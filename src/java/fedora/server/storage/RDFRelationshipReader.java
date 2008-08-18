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


package fedora.server.storage;

import java.io.InputStream;

import java.net.URI;

import java.util.HashSet;
import java.util.Set;

import org.jrdf.graph.Literal;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.Triple;

import org.trippi.RDFFormat;
import org.trippi.TripleIterator;
import org.trippi.TrippiException;

import fedora.server.errors.GeneralException;
import fedora.server.errors.ServerException;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.RelationshipTuple;

public abstract class RDFRelationshipReader {

    public static Set<RelationshipTuple> readRelationships(Datastream ds)
            throws ServerException {

        if (ds == null) {
            return new HashSet<RelationshipTuple>();
        }

        try {
            return readRelationships(ds.getContentStream());
        } catch (TrippiException e) {
            throw new GeneralException(e.getMessage(), e);
        }
    }

    public static Set<RelationshipTuple> readRelationships(InputStream dsContent)
            throws TrippiException {
        Set<RelationshipTuple> tuples = new HashSet<RelationshipTuple>();

        TripleIterator iter = null;
        try {
            iter = TripleIterator.fromStream(dsContent, RDFFormat.RDF_XML);
            Triple triple;
            ObjectNode objectNode;
            boolean isLiteral;
            URI datatypeURI;
            String subject, predicate, object, datatype;
            while (iter.hasNext()) {
                triple = iter.next();

                subject = triple.getSubject().toString();
                predicate = triple.getPredicate().toString();
                objectNode = triple.getObject();
                isLiteral = objectNode instanceof Literal;
                datatype = null;
                if (isLiteral) {
                    object = ((Literal) objectNode).getLexicalForm();
                    datatypeURI = ((Literal) objectNode).getDatatypeURI();
                    if (datatypeURI != null) {
                        datatype = datatypeURI.toString();
                    }
                } else {
                    object = triple.getObject().toString();
                }
                tuples.add(new RelationshipTuple(subject,
                                                 predicate,
                                                 object,
                                                 isLiteral,
                                                 datatype));
            }
        } finally {
            if (iter != null) {
                iter.close();
            }
        }
        return tuples;
    }
}
