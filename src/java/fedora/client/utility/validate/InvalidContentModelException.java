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

package fedora.client.utility.validate;

/**
 * Indicates that the content model object we are looking at is not valid --
 * e.g., missing the dataset, invalid format, etc.
 * 
 * @author Jim Blake
 */
public class InvalidContentModelException
        extends Exception {

    /** It's serializable, so give it a version ID. */
    private static final long serialVersionUID = 1L;

    private final String contentModelPid;

    public InvalidContentModelException(String contentModelPid,
                                        String message,
                                        Throwable cause) {
        super(message, cause);
        this.contentModelPid = contentModelPid;
    }

    public InvalidContentModelException(String contentModelPid, String message) {
        super(message);
        this.contentModelPid = contentModelPid;
    }

    public String getContentModelPid() {
        return contentModelPid;
    }
}
