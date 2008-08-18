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

package fedora.common.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

/**
 * An InputStream from an HttpMethod. When this InputStream is close()d, the
 * underlying http connection is automatically released.
 */
public class HttpInputStream
        extends InputStream {

    private final HttpClient m_client;

    private final HttpMethod m_method;

    private final String m_url;

    private int m_code;

    private InputStream m_in;

    public HttpInputStream(HttpClient client, HttpMethod method, String url)
            throws IOException {
        m_client = client;
        m_method = method;
        m_url = url;
        try {
            m_code = m_client.executeMethod(m_method);
            m_in = m_method.getResponseBodyAsStream();
            if (m_in == null) {
                m_in = new ByteArrayInputStream(new byte[0]);
            }
        } catch (IOException e) {
            m_method.releaseConnection();
            throw e;
        }
    }

    /**
     * Get the http method name (GET or POST).
     */
    public String getMethodName() {
        return m_method.getName();
    }

    /**
     * Get the original URL of the http request this InputStream is based on.
     */
    public String getURL() {
        return m_url;
    }

    /**
     * Get the http status code.
     */
    public int getStatusCode() {
        return m_code;
    }

    /**
     * Get the "reason phrase" associated with the status code.
     */
    public String getStatusText() {
        return m_method.getStatusLine().getReasonPhrase();
    }

    /**
     * Get a specific response header.
     */
    public Header getResponseHeader(String name) {
        return m_method.getResponseHeader(name);
    }

    /**
     * Get a response header value string, or <code>defaultValue</code> if the
     * header is undefined or empty.
     */
    public String getResponseHeaderValue(String name, String defaultValue) {
        Header header = m_method.getResponseHeader(name);
        if (header == null) {
            return defaultValue;
        } else {
            String value = header.getValue();
            if (value == null || value.length() == 0) {
                return defaultValue;
            } else {
                return header.getValue();
            }
        }
    }

    /**
     * Get all response headers.
     */
    public Header[] getResponseHeaders() {
        return m_method.getResponseHeaders();
    }

    /**
     * Automatically close on garbage collection.
     */
    @Override
    public void finalize() {
        try {
            close();
        } catch (Exception e) {
        }
    }

    //////////////////////////////////////////////////////////////////////////
    /////////////////// Methods from java.io.InputStream /////////////////////
    //////////////////////////////////////////////////////////////////////////

    @Override
    public int read() throws IOException {
        return m_in.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return m_in.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return m_in.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return m_in.skip(n);
    }

    @Override
    public int available() throws IOException {
        return m_in.available();
    }

    @Override
    public void mark(int readlimit) {
        m_in.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        m_in.reset();
    }

    @Override
    public boolean markSupported() {
        return m_in.markSupported();
    }

    /**
     * Release the underlying http connection and close the InputStream.
     */
    @Override
    public void close() throws IOException {
        m_method.releaseConnection();
        m_in.close();
    }
}