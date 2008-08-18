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

package fedora.server.utilities;

import java.lang.reflect.Method;

/**
 * A <code>Thread</code> that invokes a single method, then exits.
 * 
 * <p>This is convenient in situations where some method should run in a 
 * separate thread, but it is either inconvenient or inappropriate to
 * write a <code>Runnable</code> to do the work.
 * 
 * @author Chris Wilper
 */
public class MethodInvokerThread
        extends Thread {

    /** The object in which the method should be invoked. */
    private final Object m_object;

    /** The method. */
    private final Method m_method;

    /** The arguments to the method. */
    private final Object[] m_args;

    /** The <code>Object</code> returned by the method call, if any. */
    private Object m_returned;

    /** The <code>Throwable</code> the method call resulted in, if any. */
    private Throwable m_thrown;

    /**
     * Constructs a <code>MethodInvokerThread</code>.
     * 
     * @param targetObject
     *        The object in which the method resides.
     * @param method
     *        The <code>Method</code> to invoke.
     * @param args
     *        The arguments to the method.
     */
    public MethodInvokerThread(Object targetObject, Method method, Object[] args) {
        m_object = targetObject;
        m_method = method;
        m_args = args;
    }

    /**
     * Constructs a <code>MethodInvokerThread</code> with a name.
     * 
     * @param targetObject
     *        The object in which the method resides.
     * @param method
     *        The <code>Method</code> to invoke.
     * @param args
     *        The arguments to the method.
     * @param name
     *        The thread's name.
     */
    public MethodInvokerThread(Object targetObject,
                               Method method,
                               Object[] args,
                               String name) {
        super(name);
        m_object = targetObject;
        m_method = method;
        m_args = args;
    }

    /**
     * Constructs a <code>MethodInvokerThread</code> with a
     * <code>ThreadGroup</code> and a name.
     * 
     * @param targetObject
     *        The object in which the method resides.
     * @param method
     *        The <code>Method</code> to invoke.
     * @param args
     *        The arguments to the method.
     * @param threadGroup
     *        The <code>ThreadGroup</code> to which the thread should belong.
     * @param name
     *        The thread's name.
     */
    public MethodInvokerThread(Object targetObject,
                               Method method,
                               Object[] args,
                               ThreadGroup threadGroup,
                               String name) {
        super(threadGroup, name);
        m_object = targetObject;
        m_method = method;
        m_args = args;
    }

    /**
     * Invokes the <code>Method</code>, then exits.
     */
    @Override
    public void run() {
        try {
            m_returned = m_method.invoke(m_object, m_args);
        } catch (Throwable thrown) {
            m_thrown = thrown;
        }
    }

    /**
     * Gets the <code>Object</code> returned by the invoked
     * <code>Method</code>.
     * 
     * @return The Object, or null if the method has no return type or the
     *         method hasn't been invoked yet.
     */
    public Object getReturned() {
        return m_returned;
    }

    /**
     * Gets the <code>Throwable</code> that resulted if an error occurred
     * while trying to invoke the <code>Method</code>.
     * 
     * @return The Throwable, or null if the method's invocation did not produce
     *         a Throwable or the method hasn't been invoked yet.
     */
    public Throwable getThrown() {
        return m_thrown;
    }

}
