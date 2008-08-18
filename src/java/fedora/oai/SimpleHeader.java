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
import java.util.Set;

/**
 * A simple implementation of Header that provides getters on the values 
 * passed in the constructor.
 * 
 * @author Chris Wilper
 */
public class SimpleHeader
        implements Header {

    private final String m_identifier;

    private final Date m_datestamp;

    private final Set m_setSpecs;

    private final boolean m_isAvailable;

    public SimpleHeader(String identifier,
                        Date datestamp,
                        Set setSpecs,
                        boolean isAvailable) {
        m_identifier = identifier;
        m_datestamp = datestamp;
        m_setSpecs = setSpecs;
        m_isAvailable = isAvailable;
    }

    public String getIdentifier() {
        return m_identifier;
    }

    public Date getDatestamp() {
        return m_datestamp;
    }

    public Set getSetSpecs() {
        return m_setSpecs;
    }

    public boolean isAvailable() {
        return m_isAvailable;
    }

}
