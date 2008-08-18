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

package fedora.client.utility.validate.types;

/**
 * Information needed to validate an object's relationships against its content
 * model.
 * 
 * @author Jim Blake
 */
public class RelationshipInfo {

    private final String predicate;

    private final String object;

    public RelationshipInfo(String predicate, String object) {
        this.predicate = predicate;
        this.object = object;
    }

    public String getPredicate() {
        return predicate;
    }

    public String getObject() {
        return object;
    }

    public String getObjectPid() {
        if (object != null && object.startsWith("info:fedora/")) {
            return object.substring(12);
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }

        RelationshipInfo that = (RelationshipInfo) obj;
        return equivalent(predicate, that.predicate)
                && equivalent(object, that.object);
    }

    @Override
    public int hashCode() {
        return hashIt(predicate) ^ hashIt(object);
    }

    @Override
    public String toString() {
        return "RelationshipInfo[predicate='" + predicate + "', object='"
                + object + "']";
    }

    private boolean equivalent(Object obj1, Object obj2) {
        return obj1 == null ? obj2 == null : obj1.equals(obj2);
    }

    private int hashIt(Object obj) {
        return obj == null ? 0 : obj.hashCode();
    }

}
