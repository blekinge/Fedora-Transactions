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

package fedora.server.storage.translation;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.util.Map;

import org.apache.log4j.Logger;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.UnsupportedTranslationException;
import fedora.server.storage.types.DigitalObject;

/**
 * A threadsafe <code>DOTranslator</code> that uses a map of serializers and a
 * map of deserializers to do its job.
 * 
 * @author Chris Wilper
 */
public class DOTranslatorImpl
        implements DOTranslator {

    /** Logger for this class. */
    private static final Logger LOG =
            Logger.getLogger(DOTranslatorImpl.class.getName());

    /** The DOSerializer map, keyed by format string. */
    private final Map<String, DOSerializer> m_serializers;

    /** The DODeserializer map, keyed by format string. */
    private final Map<String, DODeserializer> m_deserializers;

    /**
     * Creates an instance.
     * 
     * @param serializers
     *        the DOSerializer map, keyed by format string.
     * @param deserializers
     *        the DODeserializer map, keyed by format string.
     */
    public DOTranslatorImpl(Map<String, DOSerializer> serializers,
                            Map<String, DODeserializer> deserializers) {
        m_serializers = serializers;
        m_deserializers = deserializers;
    }

    //---
    // DOTranslator implementation
    //---

    /**
     * {@inheritDoc}
     */
    public void deserialize(InputStream in,
                            DigitalObject obj,
                            String format,
                            String encoding,
                            int transContext) throws ObjectIntegrityException,
            StreamIOException, UnsupportedTranslationException, ServerException {
        try {
            LOG.debug("Grabbing deserializer for: " + format);
            DODeserializer des = m_deserializers.get(format);
            if (des == null) {
                throw new UnsupportedTranslationException("No deserializer exists for format: "
                        + format);
            }
            DODeserializer newDes = des.getInstance();
            newDes.deserialize(in, obj, encoding, transContext);
        } catch (UnsupportedEncodingException uee) {
            throw new UnsupportedTranslationException("Deserializer for format: "
                    + format + " does not support encoding: " + encoding);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void serialize(DigitalObject obj,
                          OutputStream out,
                          String format,
                          String encoding,
                          int transContext) throws ObjectIntegrityException,
            StreamIOException, UnsupportedTranslationException, ServerException {
        try {
            LOG.debug("Grabbing serializer for: " + format);
            DOSerializer ser = m_serializers.get(format);
            if (ser == null) {
                throw new UnsupportedTranslationException("No serializer exists for format: "
                        + format);
            }
            DOSerializer newSer = ser.getInstance();
            newSer.serialize(obj, out, encoding, transContext);
        } catch (UnsupportedEncodingException uee) {
            throw new UnsupportedTranslationException("Serializer for format: "
                    + format + " does not support encoding: " + encoding);
        }
    }

}
