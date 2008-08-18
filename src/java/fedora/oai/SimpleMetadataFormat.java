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

/**
 * A simple implementation of MetadataFormat that provides getters on the 
 * values passed in the constructor.
 * 
 * @author Chris Wilper
 */
public class SimpleMetadataFormat
        implements MetadataFormat {

    private final String m_prefix;

    private final String m_schemaLocation;

    private final String m_namespaceURI;

    public SimpleMetadataFormat(String prefix,
                                String schemaLocation,
                                String namespaceURI) {
        m_prefix = prefix;
        m_schemaLocation = schemaLocation;
        m_namespaceURI = namespaceURI;
    }

    public String getPrefix() {
        return m_prefix;
    }

    public String getSchemaLocation() {
        return m_schemaLocation;
    }

    public String getNamespaceURI() {
        return m_namespaceURI;
    }
}
