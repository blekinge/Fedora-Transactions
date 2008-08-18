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


package fedora.server.errors.authorization;

import fedora.server.errors.ServerException;

/**
 * Thrown when authorization is denied.
 * 
 * @author Bill Niebel
 */
public class PasswordComparisonException
        extends ServerException {

    private static final long serialVersionUID = 1L;

    public static final String BRIEF_DESC = "Authorization Permitted";

    public PasswordComparisonException(String message) {
        super(null, message, null, null, null);
    }

    public PasswordComparisonException(String message, Throwable cause) {
        super(null, message, null, null, cause);
    }

    public PasswordComparisonException(String bundleName,
                                       String code,
                                       String[] replacements,
                                       String[] details,
                                       Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}
