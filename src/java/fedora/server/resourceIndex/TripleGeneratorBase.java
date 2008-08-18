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

import java.util.Date;
import java.util.Set;

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;

import fedora.common.rdf.RDFName;
import fedora.common.rdf.SimpleLiteral;
import fedora.common.rdf.SimpleTriple;

import fedora.server.errors.ResourceIndexException;
import fedora.server.utilities.DateUtility;

import static fedora.common.Constants.MODEL;
import static fedora.common.Constants.RDF_XSD;

public abstract class TripleGeneratorBase {
    // Helper methods for creating RDF components
    
    protected RDFName getStateResource(String state)
            throws ResourceIndexException {
        if (state == null) {
            throw new ResourceIndexException("State cannot be null");
        } else if (state.equals("A")) {
            return MODEL.ACTIVE;
        } else if (state.equals("D")) {
            return MODEL.DELETED;
        } else if (state.equals("I")) {
            return MODEL.INACTIVE;
        } else {
            throw new ResourceIndexException("Unrecognized state: " + state);
        }
    }

    // Helper methods for adding triples

    protected void add(SubjectNode subject,
                       RDFName predicate,
                       ObjectNode object,
                       Set<Triple> set) throws ResourceIndexException {
        set.add(new SimpleTriple(subject, predicate, object));
    }

    protected void add(SubjectNode subject,
                       RDFName predicate,
                       String lexicalValue,
                       Set<Triple> set) throws Exception {
        if (lexicalValue != null) {
            set.add(new SimpleTriple(subject, predicate, new SimpleLiteral(lexicalValue)));
        }
    }

    protected void add(SubjectNode subject,
                       RDFName predicate,
                       Date dateValue,
                       Set<Triple> set) throws Exception {
        if (dateValue != null) {
            String lexicalValue = DateUtility.convertDateToXSDString(dateValue);
            ObjectNode object = new SimpleLiteral(lexicalValue,
                                                  RDF_XSD.DATE_TIME.getURI());
            set.add(new SimpleTriple(subject, predicate, object));
        }
    }

    protected void add(SubjectNode subject,
                       RDFName predicate,
                       boolean booleanValue,
                       Set<Triple> set) throws Exception {
        add(subject, predicate, Boolean.toString(booleanValue), set);
    }
}
