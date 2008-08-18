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

package fedora.common;

/**
 * An unchecked exception that signals an unrecoverable error.
 * <p>
 * This type of exception is usually not caught, except at the fault barrier of
 * the application.
 * </p>
 * 
 * @see <a
 *      href="http://dev2dev.bea.com/pub/a/2006/11/effective-exceptions.html">
 *      Effective Java Exceptions</a>
 * @author Chris Wilper
 */
public class FaultException
        extends RuntimeException {

    /** Version of this class. */
    private static final long serialVersionUID = 0L;

    /**
     * Creates an instance with a detail message.
     * 
     * @param message
     *        the detail message.
     */
    public FaultException(String message) {
        super(message);
    }

    /**
     * Creates an instance with no detail message and cause.
     * 
     * @param cause
     *        the cause.
     */
    public FaultException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates an instance with a detail message and cause.
     * 
     * @param message
     *        the detail message.
     * @param cause
     *        the cause.
     */
    public FaultException(String message, Throwable cause) {
        super(message, cause);
    }

}
