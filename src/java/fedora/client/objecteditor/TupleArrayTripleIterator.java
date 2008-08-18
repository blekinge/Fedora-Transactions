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


package fedora.client.objecteditor;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Triple;

import org.trippi.TripleIterator;
import org.trippi.TrippiException;

import fedora.common.rdf.SimpleLiteral;
import fedora.common.rdf.SimpleTriple;
import fedora.common.rdf.SimpleURIReference;

import fedora.server.storage.types.RelationshipTuple;

public class TupleArrayTripleIterator
        extends TripleIterator {

    int size = 0;

    int index = 0;

    ArrayList<RelationshipTuple> m_TupleArray = null;

    Map<String, String> m_map = null;

    public TupleArrayTripleIterator(ArrayList<RelationshipTuple> array,
                                    Map<String, String> map) {
        m_TupleArray = array;
        size = array.size();
        m_map = map;
    }

    public TupleArrayTripleIterator(ArrayList<RelationshipTuple> array) {
        m_TupleArray = array;
        size = array.size();
        m_map = new HashMap<String, String>();
        m_map.put("rel", "info:fedora/fedora-system:def/relations-external#");
        m_map.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    }

    @Override
    public boolean hasNext() throws TrippiException {
        return index < size;
    }

    @Override
    public Triple next() throws TrippiException {
        RelationshipTuple tuple = m_TupleArray.get(index++);
        try {
            Triple triple = new SimpleTriple(
                    new SimpleURIReference(new URI(tuple.subject)),
                    makePredicateResourceFromRel(tuple.predicate,
                                                 m_map),
                    makeObjectFromURIandLiteral(tuple.object,
                                                tuple.isLiteral,
                                                tuple.datatype));
            return triple;
        } catch (URISyntaxException e) {
            throw new TrippiException("Invalid URI in Triple", e);
        }
    }

    public static ObjectNode makeObjectFromURIandLiteral(String objURI,
                                                         boolean isLiteral,
                                                         String literalType)
            throws URISyntaxException {
        ObjectNode obj = null;
        if (isLiteral) {
            if (literalType == null || literalType.length() == 0) {
                obj = new SimpleLiteral(objURI);
            } else {
                obj = new SimpleLiteral(objURI, new URI(literalType));
            }
        } else {
            obj = new SimpleURIReference(new URI(objURI));
        }
        return obj;
    }

    public static PredicateNode makePredicateResourceFromRel(String predicate,
                                                             Map<String, String> map)
            throws URISyntaxException {
        URI predURI = makePredicateFromRel(predicate, map);
        PredicateNode node = new SimpleURIReference(predURI);
        return node;
    }

    public static URI makePredicateFromRel(String relationship, Map map)
            throws URISyntaxException {
        String predicate = relationship;
        Set keys = map.keySet();
        Iterator iter = keys.iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            if (predicate.startsWith(key + ":")) {
                predicate = predicate.replaceFirst(key + ":",
                                                   (String) map.get(key));
            }
        }

        URI retVal = null;
        retVal = new URI(predicate);
        return retVal;
    }

    @Override
    public void close() throws TrippiException {
        // TODO Auto-generated method stub

    }
}