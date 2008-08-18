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

import java.io.InputStream;

import fedora.common.http.HttpInputStream;
import fedora.common.http.WebClient;

import fedora.server.errors.StreamIOException;

/**
 * Referenced Content.
 * 
 * @author Chris Wilper
 */
public class DatastreamReferencedContent
        extends Datastream {

    private static WebClient s_http;

    static {
        s_http = new WebClient();
    }

    public DatastreamReferencedContent() {
    }

    @Override
    public Datastream copy() {
        DatastreamReferencedContent ds = new DatastreamReferencedContent();
        copy(ds);
        return ds;
    }

    /**
     * Gets an InputStream to the content of this externally-referenced
     * datastream.
     * 
     * <p>The DSLocation of this datastream must be non-null before invoking 
     * this method.
     * 
     * <p>If successful, the DSMIME type is automatically set based on the web
     * server's response header. If the web server doesn't send a valid
     * Content-type: header, as a last resort, the content-type is guessed by
     * using a map of common extensions to mime-types.
     * 
     * <p>If the content-length header is present in the response, DSSize will 
     * be set accordingly.
     */
    @Override
    public InputStream getContentStream() throws StreamIOException {

        HttpInputStream contentStream = null;
        try {
            contentStream = s_http.get(DSLocation, true);
            DSSize =
                    new Long(contentStream
                            .getResponseHeaderValue("content-length", "0"))
                            .longValue();
        } catch (Throwable th) {
            throw new StreamIOException("Error getting content stream", th);
        }
        return contentStream;
    }

}
