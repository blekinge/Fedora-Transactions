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

package fedora.server.resourceIndex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jrdf.graph.Triple;

import fedora.server.errors.ResourceIndexException;
import fedora.server.errors.ServerException;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.RelationshipTuple;

import static fedora.common.Constants.MODEL;
import static fedora.common.Models.CONTENT_MODEL_3_0;
import static fedora.common.Models.FEDORA_OBJECT_3_0;
import static fedora.common.Models.SERVICE_DEFINITION_3_0;
import static fedora.common.Models.SERVICE_DEPLOYMENT_3_0;

/**
 * Generates an object's triples based upon its declared content models.
 * <p>
 * For each content model in the object, will see if there is a
 * {@link TripleGenerator} for that model. Returns the union of all triples
 * created by these generators.
 * </p>
 * 
 * @author Aaron Birkland
 */
public class ModelBasedTripleGenerator
        implements TripleGenerator {

    /**
     * Map of model-specific triple generators. Right now, this is entirely
     * static. Change that if the need arises...
     */
    private static final Map<String, Class<? extends TripleGenerator>> m_generatorClasses =
            new HashMap<String, Class<? extends TripleGenerator>>();

    /* hard coded for now... */
    static {
        m_generatorClasses.put(FEDORA_OBJECT_3_0.uri,
                               FedoraObjectTripleGenerator_3_0.class);
        m_generatorClasses.put(SERVICE_DEFINITION_3_0.uri,
                               ServiceDefinitionTripleGenerator_3_0.class);
        m_generatorClasses.put(SERVICE_DEPLOYMENT_3_0.uri,
                               ServiceDeploymentTripleGenerator.class);
        m_generatorClasses.put(CONTENT_MODEL_3_0.uri,
                               ContentModelTripleGenerator_3_0.class);
    }

    /** Contains the initialized triple generators for each model */
    private Map<String, TripleGenerator> m_generators =
            new HashMap<String, TripleGenerator>();

    /**
     * Create a ModelBasedTripleGenerator.
     */
    public ModelBasedTripleGenerator() {
        for (String modelID : m_generatorClasses.keySet()) {
            Class<? extends TripleGenerator> genClass =
                    m_generatorClasses.get(modelID);

            try {
                TripleGenerator generator = genClass.newInstance();
                m_generators.put(modelID, generator);
            } catch (Exception e) {
                throw new RuntimeException("Could not instantiate triple generator "
                                                   + genClass.getName()
                                                   + " for model " + modelID,
                                           e);
            }
        }
    }

    /**
     * Gets all triples implied by the object's models.
     * 
     * @param reader
     *        Reads the current object
     * @return Set of all triples implied by the object's models.
     */
    public Set<Triple> getTriplesForObject(DOReader reader)
            throws ResourceIndexException {

        Set<Triple> objectTriples = new HashSet<Triple>();

        try {
            for (RelationshipTuple modelRel : reader
                    .getRelationships(MODEL.HAS_MODEL, null)) {

                if (m_generators.containsKey(modelRel.object)) {
                    objectTriples.addAll(m_generators.get(modelRel.object)
                            .getTriplesForObject(reader));
                }
            }
        } catch (ServerException e) {
            throw new ResourceIndexException("Could not read object's content model",
                                             e);
        }

        return objectTriples;
    }
}
