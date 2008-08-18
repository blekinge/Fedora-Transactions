/*
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Apache License, Version 2.0 (the "License"); you may not use 
 * this file except in compliance with the License. You may obtain a copy of 
 * the License at <a href="http://www.fedora-commons.org/licenses">
 * http://www.fedora-commons.org/licenses.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.</p>
 * <p>Copyright &copy; 2008 Fedora Commons, Inc.<br />
 * <p>Copyright &copy; 2002-2007 The Rector and Visitors of the University of 
 * Virginia and Cornell University<br /> 
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */

/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */
package fedora.server.messaging;

import java.io.File;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import fedora.common.Constants;

import fedora.server.Server;
import fedora.server.proxy.AbstractInvocationHandler;

/**
 * A {@link java.lang.reflect.InvocationHandler InvocationHandler} responsible
 * for sending notifications via {@link Messaging Messaging}.
 * 
 * @author Edwin Shin
 * @version $Id$
 */
public class NotificationInvocationHandler
        extends AbstractInvocationHandler {
    
    /** Logger for this class. */
    private static Logger LOG = 
            Logger.getLogger(MessagingModule.class.getName());
    
    private Messaging messaging;
    private boolean attemptedToLoad = false;
    
    private final ExecutorService exec = Executors.newCachedThreadPool();
    
    /**
     * Note: Setting of <code>messaging</code> does not take place in this
     * constructor because the construction of the Management proxy chain (of 
     * which this class is intended to be a part) takes place in 
     * ManagementModule.postInit(), i.e., prior to completion of Server 
     * initialization.
     */
    public NotificationInvocationHandler() {};
    
    /**
     * This constructor is intended for testing.
     * @param messaging
     */
    public NotificationInvocationHandler(Messaging messaging) {
        if (messaging != null) {
            this.messaging = messaging;
            attemptedToLoad = true;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        Object returnValue = null;
        
        try {
            returnValue = method.invoke(target, args);
        } catch(InvocationTargetException ite) {
            throw ite.getTargetException();
        }
        
        if (attemptedToLoad == false) {
            Server server = Server.getInstance(new File(Constants.FEDORA_HOME), false);
            messaging = (MessagingModule)server.getModule("fedora.server.messaging.Messaging");
            if (messaging == null) {
                LOG.warn("Unable to load MessagingModule.");
            }
            attemptedToLoad = true;
        }
        
        if (messaging != null && !exec.isShutdown()) {
            exec.submit(new Notifier(method, args, returnValue));
        }
        
        return returnValue;
    }
    
    @Override
    public void close() {
        exec.shutdown();
    }
    
    class Notifier implements Callable<Void> {
        private Method method;
        private Object[] args;
        private Object returnValue;
        
        public Notifier(Method method, Object[] args, Object returnValue) {
            this.method = method;
            this.args = args;
            this.returnValue = returnValue;
        }
        
        public Void call() throws Exception {
            FedoraMethod fm = new FedoraMethod(method, args, returnValue);
            messaging.send(fm);
            return null;
        }
    }
}
