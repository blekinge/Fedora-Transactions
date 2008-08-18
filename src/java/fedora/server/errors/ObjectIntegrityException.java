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

package fedora.server.errors;

/**
 * Signals that an object (serialized or deserialized) is inappropriately
 * formed in the context that it is being examined.
 * 
 * @author Chris Wilper
 */
public class ObjectIntegrityException
        extends StorageException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an ObjectIntegrityException.
     * 
     * @param message
     *        An informative message explaining what happened and (possibly) how
     *        to fix it.
     */
    public ObjectIntegrityException(String message) {
        super(null, message, null, null, null);
    }

    public ObjectIntegrityException(String message, Throwable th) {
        super(null, message, null, null, th);
    }

    public ObjectIntegrityException(String a,
                                    String message,
                                    String[] b,
                                    String[] c,
                                    Throwable th) {
        super(a, message, b, c, th);
    }

}
