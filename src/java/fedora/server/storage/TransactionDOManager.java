package fedora.server.storage;

import fedora.server.errors.ServerException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.Context;
import fedora.server.Server;
import fedora.server.storage.types.DigitalObject;
import fedora.server.transaction.ProxyObject;
import fedora.server.transaction.exceptions.ObjectInTransactionException;
import fedora.server.transaction.Transaction;
import fedora.server.transaction.exceptions.InvalidPidException;
import fedora.server.transaction.exceptions.InvalidTokenException;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by IntelliJ IDEA. User: abr Date: Aug 7, 2008 Time: 3:08:52 PM To
 * change this template use File | Settings | File Templates.
 */
public class TransactionDOManager extends DefaultDOManager{



    private HashMap<String,Transaction> transactions;

    private HashMap<String, ProxyObject> proxiedObjects;



    /**
     * Creates a new DefaultDOManager.
     */
    public TransactionDOManager(Map<String, String> moduleParameters,
                                Server server, String role)
            throws ModuleInitializationException {
        super(moduleParameters, server, role);
        proxiedObjects = new HashMap<String, ProxyObject>();
        transactions = new HashMap<String, Transaction>();


    }




    private Transaction getTransaction(String transactionToken)
            throws InvalidTokenException {
        if (transactionToken != null &&
            transactions.containsKey(transactionToken)){
            return transactions.get(transactionToken);
        } else {
            throw new InvalidTokenException("The transaction token was not found");
        }
    }

    private ProxyObject getProxyFromTransaction(String pid, String transactionToken)
            throws InvalidTokenException, ObjectInTransactionException,
            InvalidPidException {

        Transaction trans = getTransaction(transactionToken);

        ProxyObject proxy = proxiedObjects.get(pid);
        if (proxy != null){
            if (proxy.getToken().equals(trans)){
                return proxy;
            }else{
                throw new ObjectInTransactionException("Object not in use by this transaction");
            }
        }else{
            throw new InvalidPidException("The pid was not proxied");
        }

    }

    /**
     * Register a new transaction
     * @return the transaction token
     */
    public String registerTransaction(Context context){


        String uuid = UUID.randomUUID().toString();

        transactions.put(uuid, new Transaction(uuid));

        return uuid;
    }


    public void abortTransaction(Context context,String transactionToken)
            throws InvalidTokenException, ServerException {//restore all the objects from the proxies

        Transaction trans = getTransaction(transactionToken);

        for (ProxyObject proxy :trans.getProxies()){
            DigitalObject o = proxy.getObject();
            super.doCommit(false,context,o,"Object restored because transaction aborted",false);
            proxiedObjects.remove(proxy.getPid());
        }
        transactions.remove(transactionToken);

    }

    public void finalizeTransaction(Context context, String transactionToken)
            throws InvalidTokenException, ObjectInTransactionException {//drop all the proxies
        Transaction trans = getTransaction(transactionToken);

        Set<String> pids = trans.getProxyPids();

        for (String pid : pids) {
            unproxyObject(pid,transactionToken);
        }
        transactions.remove(transactionToken);
    }

    public void proxyObject(Context context,String pid, String transactionToken)
            throws ServerException, IOException, InvalidTokenException {
        Transaction trans = getTransaction(transactionToken);


        if (proxiedObjects.containsKey(pid)){//allready proxied, cannot be done again
            ProxyObject object = proxiedObjects.get(pid);
            if (!object.getToken().equals(trans)) {
                //but if it is the same transaction we do not throw an exception
                throw new ObjectInTransactionException("The pid was already locked by another thread");
            }
        }else{//just proxy the object
            DOReader reader = super.getReader(false,context,pid);
            DigitalObject object = reader.getObject();
            ProxyObject proxy = new ProxyObject(object, pid, trans);
            proxiedObjects.put(pid,proxy);
        }

    }

    public void unproxyObject(String pid, String transactionToken)
            throws ObjectInTransactionException, InvalidTokenException {

        if (transactions.containsKey(transactionToken)){
            Transaction trans = transactions.get(transactionToken);
            if (proxiedObjects.containsKey(pid)){
                ProxyObject proxy = proxiedObjects.get(pid);
                if (proxy.getToken().equals(trans)){
                    proxiedObjects.remove(pid);
                }else{
                    throw new ObjectInTransactionException("The specified transaction does not own the specified object");
                }
            } else {
                throw new ObjectInTransactionException("The object was never proxied in the first place");
            }

        } else{
            throw new InvalidTokenException("The transaction token was invalid");
        }
    }


    /**
     * Gets a reader on an an existing digital object.
     */
    @Override public DOReader getReader(boolean cachedObjectRequired,
                                        Context context, String pid)
            throws ServerException {

        if (proxiedObjects.containsKey(pid)){

            ProxyObject proxy = proxiedObjects.get(pid);

            return new SimpleDOReader(
                    context,this,
                    super.getTranslator(),
                    super.getDefaultExportFormat(),
                    super.getStorageCharacterEncoding(),
                    proxy.getObject());

        }else{
            return super.getReader(cachedObjectRequired, context, pid);
        }
    }

    /**
     * Gets a reader on an an existing digital object.
     */
    public DOReader getReader(boolean cachedObjectRequired,
                              Context context, String pid,
                              String transactionToken)
            throws ServerException, InvalidTokenException {



        Transaction trans = getTransaction(transactionToken);



        if (proxiedObjects.containsKey(pid)){

            ProxyObject proxy = proxiedObjects.get(pid);

            Transaction proxytrans = proxy.getToken();

            if (trans.equals(proxytrans)){//get the real object
                return super.getReader(cachedObjectRequired, context,pid);
            } else { //get the proxy object
                return new SimpleDOReader(
                        context,this,
                        super.getTranslator(),
                        super.getDefaultExportFormat(),
                        super.getStorageCharacterEncoding(),
                        proxy.getObject());
            }
        } else{//object not proxied, just give the real one
            return super.getReader(cachedObjectRequired, context,pid);
        }
    }



    public DOWriter getWriter(boolean cachedObjectRequired,
                              Context context, String pid,
                              String transactionToken)
            throws ServerException, InvalidTokenException, IOException {

        Transaction trans = getTransaction(transactionToken);



        if (proxiedObjects.containsKey(pid)){//already proxied
            ProxyObject proxy = proxiedObjects.get(pid);

            if (proxy.getToken().equals(trans)){//ours
                return super.getWriter(cachedObjectRequired, context, pid);
            } else { //someone elses
                throw new ObjectInTransactionException("Object is being edited by another transaction");
            }
        } else { //nobody is using it, so we claim it

            proxyObject(context,pid,transactionToken);
            return super.getWriter(cachedObjectRequired, context, pid);

        }
    }

    /**
     * Gets a writer on an an existing object.
     */
    @Override public DOWriter getWriter(boolean cachedObjectRequired,
                                        Context context, String pid)
            throws ServerException {
        if (proxiedObjects.containsKey(pid)){
            throw new ObjectInTransactionException("Object is being edited by another transaction");
        }
        return super.getWriter(cachedObjectRequired, context, pid);
    }




}