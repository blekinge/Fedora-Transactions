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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple immutable implementation of {@link ObjectInfo}.
 * 
 * @author Jim Blake
 */
public class BasicObjectInfo
        implements ObjectInfo {

    private final String pid;

    private final Set<RelationshipInfo> relations;

    private final Map<String, DatastreamInfo> datastreamMap;

    /** Create a "stub" object with no relations, no datastreams. */
    public BasicObjectInfo(String pid) {
        this(pid,
             new HashSet<RelationshipInfo>(),
             new HashSet<DatastreamInfo>());
    }

    /** Create a full object. */
    public BasicObjectInfo(String pid,
                           Collection<RelationshipInfo> relations,
                           Collection<DatastreamInfo> datastreams) {
        if (pid == null) {
            throw new NullPointerException("'pid' may not be null");
        }
        if (relations == null) {
            throw new NullPointerException("'relations' may not be null");
        }
        if (datastreams == null) {
            throw new NullPointerException("'datastreams' may not be null");
        }

        this.pid = pid;

        this.relations =
                Collections
                        .unmodifiableSet(new HashSet<RelationshipInfo>(relations));

        Map<String, DatastreamInfo> datastreamMap =
                new HashMap<String, DatastreamInfo>();
        for (DatastreamInfo dsInfo : datastreams) {
            datastreamMap.put(dsInfo.getId(), dsInfo);
        }
        this.datastreamMap = Collections.unmodifiableMap(datastreamMap);
    }

    public String getPid() {
        return pid;
    }

    public boolean hasRelation(String relationship) {
        if (relationship == null) {
            throw new NullPointerException("'relationship' may not be null.");
        }
        for (RelationshipInfo relation : relations) {
            if (relationship.equals(relation.getPredicate())) {
                return true;
            }
        }
        return false;
    }

    public Collection<RelationshipInfo> getRelations(String relationship) {
        List<RelationshipInfo> result = new ArrayList<RelationshipInfo>();
        for (RelationshipInfo relation : relations) {
            if (relationship.equals(relation.getPredicate())) {
                result.add(relation);
            }
        }
        return result;
    }

    public Collection<String> getDatastreamIds() {
        return new HashSet<String>(datastreamMap.keySet());
    }

    public DatastreamInfo getDatastreamInfo(String dsId) {
        return datastreamMap.get(dsId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BasicObjectInfo that = (BasicObjectInfo) obj;
        return equivalent(pid, that.pid)
                && equivalent(datastreamMap, that.datastreamMap)
                && equivalent(relations, that.relations);
    }

    private boolean equivalent(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    @Override
    public int hashCode() {
        return hashIt(pid) ^ hashIt(datastreamMap) ^ hashIt(relations);
    }

    private int hashIt(Object o) {
        return o == null ? 0 : o.hashCode();
    }

    @Override
    public String toString() {
        return "BasicObjectInfo[pid='" + pid + "', relations=" + relations
                + "', datastreamMap=" + datastreamMap + "]";
    }

}
