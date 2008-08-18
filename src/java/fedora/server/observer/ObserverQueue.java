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

package fedora.server.observer;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A threaded FIFO queue to improve performance of updates to Observers.
 * 
 * @author Edwin Shin
 * @version $Id$
 * @see <a
 *      href="http://www.javaworld.com/javaworld/javatips/jw-javatip29.html">http://www.javaworld.com/javaworld/javatips/jw-javatip29.html</a>
 */
public class ObserverQueue
        implements Publisher, Subscriber, Runnable {

    private final Set<Subscriber> subscribers;

    private BlockingQueue<Object> messages;

    public ObserverQueue() {
        subscribers = new CopyOnWriteArraySet<Subscriber>();
        messages = new LinkedBlockingQueue<Object>();
    }

    public void update(Publisher o, Object arg) {
        messages.add(arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        while (true) {
            Object obj;
            try {
                obj = messages.take();
                notifySubscribers(obj);
            } catch (InterruptedException e) {
                // restore interrupted status
                Thread.currentThread().interrupt();
            }
        }
    }

    public void addSubscriber(Subscriber obs) {
        subscribers.add(obs);
    }

    public void notifySubscribers() {
        notifySubscribers(null);
    }

    /**
     * Notify subscribers. Notifications are issued using a copy of the
     * subscriber list. Therefore, Subscribers should not assume that a
     * notification will not be received even after calling removeSubscriber.
     * {@inheritDoc}
     */
    public void notifySubscribers(Object o) {
        for (Subscriber subscriber : subscribers) {
            subscriber.update(this, o);
        }
    }

    public void removeSubscriber(Subscriber obs) {
        subscribers.remove(obs);
    }

}
