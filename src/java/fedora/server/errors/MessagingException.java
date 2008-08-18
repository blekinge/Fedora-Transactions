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

package fedora.server.errors;

public class MessagingException extends ServerException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
	public MessagingException(String message) {
        super(null, message, null, null, null);
    }
	
	/**
	 * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     * @param cause The underlying exception if known, null meaning unknown or
     *        none.
     */
    public MessagingException(String message, Throwable cause) {
        super(null, message, null, null, cause);
    }
	
	/**
	 * @param bundleName The bundle in which the message resides.
     * @param code The identifier for the message in the bundle, aka the key.
     * @param values Replacements for placeholders in the message, where
     *        placeholders are of the form {num} where num starts at 0,
     *        indicating the 0th (1st) item in this array.
     * @param details Identifiers for messages which provide detail on the
     *        error.  This may empty or null.
     * @param cause The underlying exception if known, null meaning unknown or
     *        none.
     */
	public MessagingException(String bundleName, String code, String[] values, String[] details, Throwable cause) {
		super(bundleName, code, values, details, cause);
	}

}
