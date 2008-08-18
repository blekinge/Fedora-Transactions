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

package fedora.server.journal.helpers;

import java.io.IOException;
import java.io.InputStream;

import com.oreilly.servlet.Base64Encoder;

/**
 * Wraps an InputStream with a Base64 encoder, so when you "read" from the
 * stream, you don't get bytes from the InputStream, you get Strings of
 * Base64-encoded characters.
 * <p>
 * Base64 encoding is defined in Internet RFC 3548, found at
 * http://tools.ietf.org/html/rfc3548 (among other places).
 * 
 * @author Jim Blake
 */
public class EncodingBase64InputStream {

    public static final int DEFAULT_BUFFER_SIZE = 1024;

    private final InputStream stream;

    private final byte[] buffer;

    private boolean open = true;

    private int bytesInBuffer = 0;

    private boolean innerStreamHasMoreData = true;

    /**
     * @param stream
     *        the source of data bytes to be encoded.
     */
    public EncodingBase64InputStream(InputStream stream) {
        this(stream, DEFAULT_BUFFER_SIZE);
    }

    /**
     * @param stream
     *        the source of data bytes to be encoded.
     * @param bufferSize
     *        the maximum number of bytes to read at one time.
     * @throws IllegalArgumentException
     *         if bufferSize is not between 10 and 1,000,000.
     */
    public EncodingBase64InputStream(InputStream stream, int bufferSize) {
        if (bufferSize < 10 || bufferSize > 1000000) {
            throw new IllegalArgumentException("Buffer size must be between 10 and 1,000,000. Cannot be "
                    + bufferSize);
        }
        buffer = new byte[bufferSize];
        this.stream = stream;
    }

    /**
     * Read encoded data from the stream. Data is read in 3-byte multiples and
     * encoded into 4-character sequences, per the Base64 specification. As many
     * bytes as possible will be read, limited by the amount of data available
     * and by the limitation of maxStringLength on the size of the resulting
     * encoded String. Since the smallest unit of encoded data is 4 characters,
     * maxStringLength must not be less than 4.
     * 
     * @param maxStringLength
     *        the resulting String will be no longer than this.
     * @return a String that is no longer than maxStringLength, or null if no
     *         data remains to be read.
     * @throws IllegalArgumentException
     *         if maxStringLength is less than 4.
     * @throws IllegalStateException
     *         if called after the stream is closed.
     * @throws IOException
     *         from inner InputStream.
     */
    public String read(int maxStringLength) throws IOException {
        if (maxStringLength < 4) {
            throw new IllegalArgumentException("maxStringLength must be 4 or more, not "
                    + maxStringLength);
        }

        if (!open) {
            throw new IllegalStateException("Stream has already been closed.");
        }

        int bytesRequestedForEncoding = maxStringLength / 4 * 3;

        if (bytesRequestedForEncoding > bytesInBuffer) {
            readMoreBytesFromStream();
        }

        if (bytesInBuffer == 0 && !innerStreamHasMoreData) {
            return null;
        }

        int bytesToEncode = Math.min(bytesRequestedForEncoding, bytesInBuffer);
        String result = encodeBytesFromBuffer(bytesToEncode);
        return result;
    }

    /**
     * Close the InputStream, and prevent any further reads.
     * 
     * @throws IOException
     *         from the inner InputStream
     */
    public void close() throws IOException {
        open = false;
        stream.close();
    }

    /**
     * Fill the buffer with more data from the InputStream, if there is any.
     * 
     * @throws IOException
     *         from the inner InputStream
     */
    private void readMoreBytesFromStream() throws IOException {
        if (!innerStreamHasMoreData) {
            return;
        }

        int bufferSpaceAvailable = buffer.length - bytesInBuffer;
        if (bufferSpaceAvailable <= 0) {
            return;
        }

        int bytesRead =
                stream.read(buffer, bytesInBuffer, bufferSpaceAvailable);

        if (bytesRead == -1) {
            innerStreamHasMoreData = false;
        } else {
            bytesInBuffer += bytesRead;
        }
    }

    /**
     * Encode a group of bytes and remove them from the buffer. If the input
     * stream has more data, we need to limit the encoding to a multiple of 3,
     * to avoid prematurely padding the result with equals signs.
     * 
     * @param howMany
     *        how many bytes should be encoded and remove from the buffer.
     * @return the Base64-encoded characters.
     */
    private String encodeBytesFromBuffer(int howMany) {
        String result;

        if (innerStreamHasMoreData) {
            howMany = howMany - howMany % 3;
        }

        if (howMany == 0) {
            return "";
        }

        byte[] encodeBuffer = new byte[howMany];
        System.arraycopy(buffer, 0, encodeBuffer, 0, howMany);
        result = Base64Encoder.encode(encodeBuffer);

        bytesInBuffer -= howMany;
        if (bytesInBuffer != 0) {
            System.arraycopy(buffer, howMany, buffer, 0, bytesInBuffer);
        }

        return result;
    }

}
