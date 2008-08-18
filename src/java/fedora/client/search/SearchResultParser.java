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

package fedora.client.search;

import java.io.IOException;
import java.io.InputStream;

import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parses plain old xml results from a Fedora search request.
 */
public class SearchResultParser
        extends DefaultHandler {

    private boolean READING_PID;

    private boolean READING_TOKEN;

    private StringBuffer m_currentPID;

    private StringBuffer m_sessionToken;

    private final Set<String> m_pids = new HashSet<String>();

    public SearchResultParser(InputStream xml)
            throws IOException {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            SAXParser parser = spf.newSAXParser();
            parser.parse(xml, this);
        } catch (Exception e) {
            if (e.getMessage() != null) {
                throw new IOException(e.getMessage());
            } else {
                throw new IOException("Error parsing search result xml: "
                        + e.getClass().getName());
            }
        }
    }

    @Override
    public void startElement(String uri,
                             String localName,
                             String qName,
                             Attributes a) {
        if (localName.equals("pid")) {
            READING_PID = true;
            m_currentPID = new StringBuffer();
        } else if (localName.equals("token")) {
            READING_TOKEN = true;
            m_sessionToken = new StringBuffer();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (READING_PID) {
            m_currentPID.append(ch, start, length);
        } else if (READING_TOKEN) {
            m_sessionToken.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (localName.equals("pid")) {
            READING_PID = false;
            m_pids.add(m_currentPID.toString());
        } else if (localName.equals("token")) {
            READING_TOKEN = false;
        }
    }

    public Set<String> getPIDs() {
        return m_pids;
    }

    public String getToken() {
        if (m_sessionToken == null) {
            return null;
        }
        return m_sessionToken.toString();
    }
}