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


package fedora.server.resourceIndex;

import java.io.IOException;
import java.io.InputStream;

import java.net.URI;

import java.util.HashSet;
import java.util.Set;

import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;

import org.xml.sax.InputSource;

import fedora.common.FaultException;
import fedora.common.PID;
import fedora.common.rdf.SimpleURIReference;

import fedora.server.errors.ResourceIndexException;
import fedora.server.errors.ServerException;
import fedora.server.storage.DOReader;
import fedora.server.storage.service.ServiceMapper;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.MethodDef;

import static fedora.common.Constants.MODEL;

/**
 * Generates all triples objects modeled as Fedora 3.0 Service Definitions.
 * 
 * @author Aaron Birkland
 */
public class ServiceDefinitionTripleGenerator_3_0
        extends TripleGeneratorBase
        implements TripleGenerator {

    private static final String METHODMAP_DS = "METHODMAP";

    /**
     * {@inheritDoc}
     */
    public Set<Triple> getTriplesForObject(DOReader reader)
            throws ResourceIndexException {
        Set<Triple> set = new HashSet<Triple>();

        try {
            URIReference objURI = new SimpleURIReference(
                    new URI(PID.toURI(reader.GetObjectPID())));

            /* Now add the SDef operation-specific triples */
            addMethodDefTriples(objURI, reader, set);
        } catch (Exception e) {
            throw new ResourceIndexException("Could not generate triples", e);
        }
        return set;
    }

    /**
     * Add a "defines" statement for the given sDef for each abstract method it
     * defines.
     */
    private void addMethodDefTriples(URIReference objURI,
                                     DOReader reader,
                                     Set<Triple> set)
            throws ResourceIndexException {
        try {
            for (MethodDef element : getAbstractMethods(reader)) {
                add(objURI, MODEL.DEFINES_METHOD, element.methodName, set);
            }
        } catch (ResourceIndexException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceIndexException("Error adding method def "
                    + "triples", e);
        }
    }

    private MethodDef[] getAbstractMethods(DOReader reader)
            throws ServerException {
        ServiceMapper mapper = new ServiceMapper(reader.GetObjectPID());
        Datastream methodmap = reader.GetDatastream(METHODMAP_DS, null);
        if (methodmap != null) {
            InputStream contentStream = methodmap.getContentStream();
            try {
                return mapper.getMethodDefs(new InputSource(contentStream));
            } finally {
                try {
                    contentStream.close();
                } catch (IOException e) {
                    throw new FaultException(e);
                }
            }
        } else {
            return new MethodDef[0];
        }
    }

}
