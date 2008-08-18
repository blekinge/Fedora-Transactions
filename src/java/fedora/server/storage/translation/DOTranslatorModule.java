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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.UnsupportedTranslationException;
import fedora.server.storage.types.DigitalObject;

/**
 * <code>DOTranslatorImpl</code> wrapped as a <code>Module</code>.
 * <p>
 * To configure the DOTranslatorImpl, this module accepts parameters with names
 * starting with <code>serializer_</code> and <code>deserializer_</code>.
 * The latter part of each parameter name assigns the name of the format
 * (typically a format URI), and the value of the parameter is a fully-qualified
 * class name, denoting a <code>DOSerializer</code> or
 * <code>DODeserializer</code>, respectively.
 * </p>
 * 
 * @author Chris Wilper
 * @version $Id$
 */
public class DOTranslatorModule
        extends Module
        implements DOTranslator {
    
    /** Logger for this class. */
    private static final Logger LOG =
            Logger.getLogger(DOTranslatorModule.class.getName());

    /** Prefix for deserializer parameter names. */
    private static final String DESER_PARAM_PREFIX = "deserializer_";

    /** Prefix for serializer parameter names. */
    private static final String SER_PARAM_PREFIX = "serializer_";

    /** The DOTranslator this module uses. */
    private DOTranslator m_wrappedTranslator;

    /**
     * Creates an instance using the standard <code>Module</code> constructor.
     */
    public DOTranslatorModule(Map<String, String> params, Server server, String role)
            throws ModuleInitializationException {
        super(params, server, role);
    }

    //---
    // Module overrides
    //---

    /**
     * {@inheritDoc}
     */
    @Override
    public void initModule() throws ModuleInitializationException {
        Map<String, DOSerializer> serMap = new HashMap<String, DOSerializer>();
        Map<String, DODeserializer> deserMap =
                new HashMap<String, DODeserializer>();
        Iterator<String> nameIter = parameterNames();
        while (nameIter.hasNext()) {
            String paramName = nameIter.next();
            if (paramName.startsWith(SER_PARAM_PREFIX)) {
                String serName = paramName.substring(SER_PARAM_PREFIX.length());
                try {
                    DOSerializer ser =
                            (DOSerializer) Class
                                    .forName(getParameter(paramName))
                                    .newInstance();
                    serMap.put(serName, ser);
                } catch (Exception e) {
                    throw new ModuleInitializationException("Can't instantiate serializer class for format "
                                                                    + serName,
                                                            getRole(),
                                                            e);
                }
            } else if (paramName.startsWith(DESER_PARAM_PREFIX)) {
                String deserName =
                        paramName.substring(DESER_PARAM_PREFIX.length());
                try {
                    DODeserializer deser =
                            (DODeserializer) Class
                                    .forName(getParameter(paramName))
                                    .newInstance();
                    deserMap.put(deserName, deser);
                } catch (Exception e) {
                    throw new ModuleInitializationException("Can't instantiate deserializer class for format "
                                                                    + deserName,
                                                            getRole(),
                                                            e);
                }
            }
        }
        m_wrappedTranslator = new DOTranslatorImpl(serMap, deserMap);
    }

    //---
    // DOTranslator implementation
    //---

    /**
     * {@inheritDoc}
     */
    public void deserialize(InputStream in,
                            DigitalObject out,
                            String format,
                            String encoding,
                            int transContext) throws ObjectIntegrityException,
            StreamIOException, UnsupportedTranslationException, ServerException {
        m_wrappedTranslator
                .deserialize(in, out, format, encoding, transContext);
    }

    /**
     * {@inheritDoc}
     */
    public void serialize(DigitalObject in,
                          OutputStream out,
                          String format,
                          String encoding,
                          int transContext) throws ObjectIntegrityException,
            StreamIOException, UnsupportedTranslationException, ServerException {
        m_wrappedTranslator.serialize(in, out, format, encoding, transContext);
    }

}