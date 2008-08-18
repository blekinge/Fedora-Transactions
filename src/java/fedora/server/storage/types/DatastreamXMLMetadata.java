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

package fedora.server.storage.types;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.w3c.dom.Document;

import org.xml.sax.SAXException;

/**
 * @author Sandy Payette
 */
public class DatastreamXMLMetadata
        extends Datastream {

    // techMD (technical metadata),
    // sourceMD (analog/digital source metadata),
    // rightsMD (intellectual property rights metadata),
    // digiprovMD (digital provenance metadata).
    // dmdSec (descriptive metadata).

    /** Technical XML metadata */
    public final static int TECHNICAL = 1;

    /** Source XML metatdata */
    public final static int SOURCE = 2;

    /** Rights XML metatdata */
    public final static int RIGHTS = 3;

    /** Digital provenance XML metadata */
    public final static int DIGIPROV = 4;

    /** Descriptive XML metadata */
    public final static int DESCRIPTIVE = 5;

    // FIXME:xml datastream contents are held in memory...this could be expensive.
    public byte[] xmlContent;

    /**
     * The class of XML metadata (TECHNICAL, SOURCE, RIGHTS, DIGIPROV, or
     * DESCRIPTIVE)
     */
    public int DSMDClass = 0;

    private final String m_encoding;

    public DatastreamXMLMetadata() {
        m_encoding = "UTF-8";
    }

    public DatastreamXMLMetadata(String encoding) {
        m_encoding = encoding;
    }

    @Override
    public Datastream copy() {
        DatastreamXMLMetadata ds = new DatastreamXMLMetadata(m_encoding);
        copy(ds);
        if (xmlContent != null) {
            ds.xmlContent = new byte[xmlContent.length];
            for (int i = 0; i < xmlContent.length; i++) {
                ds.xmlContent[i] = xmlContent[i];
            }
        }
        ds.DSMDClass = DSMDClass;
        return ds;
    }

    @Override
    public InputStream getContentStream() {
        return new ByteArrayInputStream(xmlContent);
    }

    @Override
    public InputStream getContentStreamForChecksum() {
        BufferedReader br;
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            OutputFormat fmt = new OutputFormat("XML", "UTF-8", false);
            fmt.setIndent(0);
            fmt.setLineWidth(0);
            fmt.setPreserveSpace(false);
            XMLSerializer ser = new XMLSerializer(outStream, fmt);
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlContent));
            ser.serialize(doc);

            br =
                    new BufferedReader(new InputStreamReader(new ByteArrayInputStream(outStream
                                                                     .toByteArray()),
                                                             m_encoding));
            String line;
            StringBuffer buf = new StringBuffer();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                buf = buf.append(line);
            }
            String bufStr = buf.toString();
            return new ByteArrayInputStream(bufStr.getBytes(m_encoding));
        } catch (UnsupportedEncodingException e) {
            return getContentStream();
        } catch (IOException e) {
            return getContentStream();
        } catch (ParserConfigurationException e) {
            return getContentStream();
        } catch (SAXException e) {
            return getContentStream();
        }
    }

    public InputStream getContentStreamAsDocument()
            throws UnsupportedEncodingException {
        // *with* the <?xml version="1.0" encoding="m_encoding" ?> line
        String firstLine =
                "<?xml version=\"1.0\" encoding=\"" + m_encoding + "\" ?>\n";
        byte[] firstLineBytes = firstLine.getBytes(m_encoding);
        byte[] out = new byte[xmlContent.length + firstLineBytes.length];
        for (int i = 0; i < firstLineBytes.length; i++) {
            out[i] = firstLineBytes[i];
        }
        for (int i = firstLineBytes.length; i < firstLineBytes.length
                + xmlContent.length; i++) {
            out[i] = xmlContent[i - firstLineBytes.length];
        }
        return new ByteArrayInputStream(out);
    }
}
