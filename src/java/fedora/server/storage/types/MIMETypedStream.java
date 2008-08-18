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

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * Data structure for holding a MIME-typed stream.
 *
 * @author Ross Wayland
 */
public class MIMETypedStream {

    private static final Logger LOG = Logger.getLogger(MIMETypedStream.class);

    public String MIMEType;

    private InputStream stream;

    public Property[] header;

    private boolean gotStream = false;

    /**
     * Constructs a MIMETypedStream.
     *
     * @param MIMEType
     *        The MIME type of the byte stream.
     * @param stream
     *        The byte stream.
     */
    public MIMETypedStream(String MIMEType,
                           InputStream stream,
                           Property[] header) {
        this.MIMEType = MIMEType;
        this.header = header;
        setStream(stream);
    }

    /**
     * Retrieves the underlying stream.
     * Caller is responsible to close the stream,
     * either by calling MIMETypedStream.close()
     * or by calling close() on the stream.
     *
     * @return The byte stream
     */
    public InputStream getStream() {
        gotStream = true;
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    /**
     * Closes the underlying stream if it's not already closed.
     *
     * In the event of an error, a warning will be logged.
     */
    public void close() {
        if (this.stream != null) {
            try {
                this.stream.close();
                this.stream = null;
            } catch (IOException e) {
                LOG.warn("Error closing stream", e);
            }
        }
    }

    /**
     * Ensures the underlying stream is closed at garbage-collection time
     * if the stream has not been retrieved. If getStream() has been called
     * the caller is responsible to close the stream.
     *
     * {@inheritDoc}
     */
    @Override
    public void finalize() {
        if(!gotStream) {
            close();
        }
    }
}
