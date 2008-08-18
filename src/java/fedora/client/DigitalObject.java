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

package fedora.client;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Chris Wilper
 */
public class DigitalObject {

    protected HashMap basisStreams = new HashMap();

    protected HashMap inlineStreams = new HashMap();

    private boolean m_dirty = true;

    private String m_name = "Untitled";

    public DigitalObject() {
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_dirty = true;
        m_name = name;
    }

    public boolean isDirty() {
        if (m_dirty) {
            return true;
        }
        Iterator iter;
        iter = basisStreams.values().iterator();
        while (iter.hasNext()) {
            BasisDataStream basis = (BasisDataStream) iter.next();
            if (basis.isDirty()) {
                return true;
            }
        }
        iter = inlineStreams.values().iterator();
        while (iter.hasNext()) {
            InlineDataStream inline = (InlineDataStream) iter.next();
            if (inline.isDirty()) {
                return true;
            }
        }
        return false;
    }

    public void setClean() {
        m_dirty = false;
    }

    public void setAllClean() {
        m_dirty = false;
        Iterator iter;
        iter = basisStreams.values().iterator();
        while (iter.hasNext()) {
            BasisDataStream basis = (BasisDataStream) iter.next();
            basis.setClean();
        }
        iter = inlineStreams.values().iterator();
        while (iter.hasNext()) {
            InlineDataStream inline = (InlineDataStream) iter.next();
            inline.setClean();
        }
    }

}
