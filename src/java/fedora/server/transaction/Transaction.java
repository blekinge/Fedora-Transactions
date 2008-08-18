package fedora.server.transaction;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA. User: abr Date: Aug 7, 2008 Time: 2:38:34 PM To
 * change this template use File | Settings | File Templates.
 */
public class Transaction {

    private HashMap<String,ProxyObject> proxies;

    

    private String transactionToken;

    public Transaction(String transactionToken) {
        proxies = new HashMap<String, ProxyObject>();
        this.transactionToken = transactionToken;
    }

    public String getTransactionToken() {
        return transactionToken;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Transaction){
            return transactionToken.equals(((Transaction)obj).getTransactionToken());
        } else{
            return false;
        }
    }

    public int hashCode() {
        return transactionToken.hashCode();
    }

    public void registerProxy(ProxyObject proxy) {
        proxies.put(proxy.getPid(),proxy);
    }

    public Set<String> getProxyPids(){
        return proxies.keySet();
    }

    public Collection<ProxyObject> getProxies(){
        return proxies.values();
    }
}
