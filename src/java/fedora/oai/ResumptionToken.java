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

package fedora.oai;

import java.util.Date;

/**
 * A token that can be used to retrieve the remaining portion of an 
 * incomplete list response.
 * 
 * @author Chris Wilper
 * @see <a
 *      href="http://www.openarchives.org/OAI/openarchivesprotocol.html#FlowControl">
 *      http://www.openarchives.org/OAI/openarchivesprotocol.html#FlowControl</a>
 */
public interface ResumptionToken {

    /**
     * Get the value of the token. A null value indicates that the associated
     * list is complete.
     */
    public abstract String getValue();

    /**
     * Get the expiration date of the token. A null value indicates an unknown
     * or unprovided expiration date.
     */
    public abstract Date getExpirationDate();

    /**
     * Get the size of the list. A negative value indicates an unknown or
     * unprovided list size.
     */
    public abstract long getCompleteListSize();

    /**
     * Get the position in the list that this record starts at. A negative value
     * indicates an unknown or unprovided position.
     */
    public abstract long getCursor();

}
