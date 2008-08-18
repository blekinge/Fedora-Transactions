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

import fedora.common.Constants;

/**
 * A data structure for holding relationships.
 * 
 * @author Robert Haschart
 */
public class RelationshipTuple
        implements Constants {

    public final String subject;

    public final String predicate;

    public final String object;

    public final boolean isLiteral;

    public final String datatype;

    public RelationshipTuple(String subject,
                             String predicate,
                             String object,
                             boolean isLiteral,
                             String datatype) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.isLiteral = isLiteral;
        this.datatype = datatype;
    }

    // TODO: Consider getting rid of this method
    public String getObjectPID() {
        if (object != null && !isLiteral && object.startsWith("info:fedora/")) {
            String PID = object.substring(12);
            return PID;
        }
        return null;
    }

    // TODO: Consider getting rid of this method
    public String getRelationship() {
        String prefixRel = RELS_EXT.uri;
        if (predicate != null && predicate.startsWith(prefixRel)) {
            String rel = "rel:" + predicate.substring(prefixRel.length());
            return rel;
        }
        String prefixModel = MODEL.uri;
        if (predicate != null && predicate.startsWith(prefixModel)) {
            String rel =
                    "fedora-model:" + predicate.substring(prefixModel.length());
            return rel;
        }
        return predicate;
    }

    @Override
    public String toString() {
        String retVal =
                "Sub: " + subject + "  Pred: " + predicate + "  Obj: ["
                        + object + ", " + isLiteral + ", " + datatype + "]";
        return retVal;
    }
    
    @Override
    public int hashCode() {
        return hc(subject)
                + hc(predicate)
                + hc(object)
                + hc(datatype);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof RelationshipTuple) {
            RelationshipTuple t = (RelationshipTuple) o;
            return eq(subject, t.subject)
                    && eq(predicate, t.predicate)
                    && eq(object, t.object)
                    && eq(datatype, t.datatype)
                    && isLiteral == t.isLiteral;
        } else {
            return false;
        }
    }
   
    // test for equality, accounting for null values
    private static boolean eq(Object a, Object b) {
        if (a == null) {
            return b == null;
        } else {
            return b != null && a.equals(b);
        }
    }
    
    // return the hashCode or 0 if null
    private static int hc(Object o) {
        if (o == null) {
            return 0;
        } else {
            return o.hashCode();
        }
    }
}
