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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Chris Wilper
 */
public class BasisDataStream
        extends DataStream {

    private final HashSet<InlineDataStream> m_descriptiveStreams =
            new HashSet<InlineDataStream>();

    private boolean m_internallyStored = true;

    private String m_location;

    public BasisDataStream(File tempDir, String id) {
        super(tempDir, id);
    }

    @Override
    public final int getType() {
        return DataStream.BASIS;
    }

    public void addDescriptiveStream(InlineDataStream inlineStream) {
        m_dirty = true;
        m_descriptiveStreams.add(inlineStream);
    }

    public void removeDescriptiveStream(InlineDataStream inlineStream) {
        m_dirty = true;
        m_descriptiveStreams.remove(inlineStream);
    }

    public Iterator descriptiveStreams() {
        return m_descriptiveStreams.iterator();
    }

    public boolean isInternallyStored() {
        return m_internallyStored;
    }

    public void setLocation(String location) {
        m_location = location;
        m_internallyStored = false;
        clearData();
    }

    public String getLocation() {
        return m_location;
    }

    @Override
    public void setData(InputStream in) throws IOException {
        super.setData(in);
        m_location = null;
        m_internallyStored = true;
    }

}
