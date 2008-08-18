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
 * A simple implementation of ResumptionToken that provides getters on the 
 * values passed in the constructor.
 * 
 * @author Chris Wilper
 */
public class SimpleResumptionToken
        implements ResumptionToken {

    private final String m_value;

    private final Date m_expirationDate;

    private final long m_completeListSize;

    private final long m_cursor;

    public SimpleResumptionToken(String value,
                                 Date expirationDate,
                                 long completeListSize,
                                 long cursor) {
        m_value = value;
        m_expirationDate = expirationDate;
        m_completeListSize = completeListSize;
        m_cursor = cursor;
    }

    public String getValue() {
        return m_value;
    }

    public Date getExpirationDate() {
        return m_expirationDate;
    }

    public long getCompleteListSize() {
        return m_completeListSize;
    }

    public long getCursor() {
        return m_cursor;
    }

}
