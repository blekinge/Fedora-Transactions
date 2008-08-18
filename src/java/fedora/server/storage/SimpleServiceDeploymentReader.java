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

package fedora.server.storage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.Date;

import org.xml.sax.InputSource;

import fedora.server.Context;
import fedora.server.errors.DatastreamNotFoundException;
import fedora.server.errors.GeneralException;
import fedora.server.errors.MethodNotFoundException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.RepositoryConfigurationException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.UnsupportedTranslationException;
import fedora.server.storage.service.ServiceMapper;
import fedora.server.storage.translation.DOTranslator;
import fedora.server.storage.types.DeploymentDSBindSpec;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MethodDefOperationBind;
import fedora.server.storage.types.MethodParmDef;

/**
 * A ServiceDeploymentReader based on a DigitalObject.
 * 
 * @author Chris Wilper
 */
public class SimpleServiceDeploymentReader
        extends SimpleServiceAwareReader
        implements ServiceDeploymentReader {

    private final ServiceMapper serviceMapper;

    public SimpleServiceDeploymentReader(Context context,
                             RepositoryReader repoReader,
                             DOTranslator translator,
                             String exportFormat,
                             String storageFormat,
                             String encoding,
                             InputStream serializedObject)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException, ServerException {
        super(context,
              repoReader,
              translator,
              exportFormat,
              storageFormat,
              encoding,
              serializedObject);
        serviceMapper = new ServiceMapper(GetObjectPID());
    }

    /**
     * Alternate constructor for when a DigitalObject is already available for
     * some reason.
     */
    public SimpleServiceDeploymentReader(Context context,
                             RepositoryReader repoReader,
                             DOTranslator translator,
                             String exportFormat,
                             String encoding,
                             DigitalObject obj) {
        super(context, repoReader, translator, exportFormat, encoding, obj);
        serviceMapper = new ServiceMapper(GetObjectPID());
    }

    public MethodDef[] getServiceMethods(Date versDateTime)
            throws DatastreamNotFoundException, ObjectIntegrityException,
            RepositoryConfigurationException, GeneralException {
        return serviceMapper
                .getMethodDefs(new InputSource(new ByteArrayInputStream(getMethodMapDatastream(versDateTime).xmlContent)));
    }

    public MethodParmDef[] getServiceMethodParms(String methodName,
                                                 Date versDateTime)
            throws MethodNotFoundException, ServerException {
        return getParms(getServiceMethods(versDateTime), methodName);
    }

    public MethodDefOperationBind[] getServiceMethodBindings(Date versDateTime)
            throws DatastreamNotFoundException, ObjectIntegrityException,
            RepositoryConfigurationException, GeneralException {
        return serviceMapper
                .getMethodDefBindings(new InputSource(new ByteArrayInputStream(getWSDLDatastream(versDateTime).xmlContent)),
                                      new InputSource(new ByteArrayInputStream(getMethodMapDatastream(versDateTime).xmlContent)));
    }

    public DeploymentDSBindSpec getServiceDSInputSpec(Date versDateTime)
            throws DatastreamNotFoundException, ObjectIntegrityException,
            RepositoryConfigurationException, GeneralException {
        return serviceMapper
                .getDSInputSpec(new InputSource(new ByteArrayInputStream(getDSInputSpecDatastream(versDateTime).xmlContent)));
    }

    public InputStream getServiceMethodsXML(Date versDateTime)
            throws DatastreamNotFoundException, ObjectIntegrityException {
        return new ByteArrayInputStream(getMethodMapDatastream(versDateTime).xmlContent);
    }

    /**
     * Get the parms out of a particular service method definition.
     * 
     * @param methods
     * @return
     */
    private MethodParmDef[] getParms(MethodDef[] methods, String methodName)
            throws MethodNotFoundException, ServerException {
        for (MethodDef element : methods) {
            if (element.methodName.equalsIgnoreCase(methodName)) {
                return element.methodParms;
            }
        }
        throw new MethodNotFoundException("[getParms] The service deployment object, "
                + m_obj.getPid()
                + ", does not have a service method named '"
                + methodName);
    }
}
