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

/*
 * The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;

import fedora.common.Constants;
import fedora.common.Models;
import fedora.common.rdf.JRDF;

import fedora.server.errors.ServerException;
import fedora.server.storage.RDFRelationshipReader;

/**
 * A basic implementation of DigitalObject that stores things in memory.
 * 
 * @author Chris Wilper
 */
@SuppressWarnings("deprecation")
public class BasicDigitalObject
        implements DigitalObject {

    private boolean m_isNew;

    private String m_pid;

    private String m_state;

    private String m_ownerId;

    private String m_label;

    private Set<RelationshipTuple> m_rels;

    private Date m_createDate;

    private Date m_lastModDate;

    private DatastreamProcessor m_datastreamProcessor;

    private final ArrayList<AuditRecord> m_auditRecords;

    /*
     * Although not required, this will assure that datastreamsIDs may be
     * iterated in insertion order.
     */
    private final LinkedHashMap<String, List<Datastream>> m_datastreams;

    private final HashMap<String, List<Disseminator>> m_disseminators;

    private final Map<String, String> m_extProperties;

    public BasicDigitalObject() {
        m_auditRecords = new ArrayList<AuditRecord>();
        m_datastreams = new LinkedHashMap<String, List<Datastream>>();
        m_disseminators = new HashMap<String, List<Disseminator>>();
        m_extProperties = new HashMap<String, String>();
        m_datastreamProcessor = new RelationshipProcessor();
        setNew(false);
    }

    public boolean isNew() {
        return m_isNew;
    }

    public void setNew(boolean isNew) {
        m_isNew = isNew;
    }

    public String getPid() {
        return m_pid;
    }

    public void setPid(String pid) {
        m_pid = pid;
    }

    public String getState() {
        return m_state;
    }

    public void setState(String state) {
        m_state = state;
    }

    public String getOwnerId() {
        return m_ownerId;
    }

    public void setOwnerId(String owner) {
        m_ownerId = owner;
    }

    public String getLabel() {
        return m_label;
    }

    public void setLabel(String label) {
        m_label = label;
    }

    public Date getCreateDate() {
        return m_createDate;
    }

    public void setCreateDate(Date createDate) {
        m_createDate = createDate;
    }

    public Date getLastModDate() {
        return m_lastModDate;
    }

    public void setLastModDate(Date lastModDate) {
        m_lastModDate = lastModDate;
    }

    public List<AuditRecord> getAuditRecords() {
        return m_auditRecords;
    }

    public Iterator<String> datastreamIdIterator() {
        return copyOfKeysForNonEmptyLists(m_datastreams).iterator();
    }

    private static <T> Set<String> copyOfKeysForNonEmptyLists(Map<String, List<T>> map) {
        Set<String> set = new LinkedHashSet<String>();

        for (Map.Entry<String, List<T>> e : map.entrySet()) {
            if (!e.getValue().isEmpty()) {
                set.add(e.getKey());
            }
        }
        return set;
    }

    public Iterable<Datastream> datastreams(String id) {

        if (!m_datastreams.containsKey(id)) {
            return new ArrayList<Datastream>();
        }

        return Collections
                .unmodifiableList(new ArrayList<Datastream>(m_datastreams
                        .get(id)));
    }

    public void removeDatastreamVersion(Datastream ds) {
        remove(ds);
    }

    public void addDatastreamVersion(Datastream ds, boolean addNewVersion) {
        if (!addNewVersion) {
            Datastream latestCreated = null;
            long latestCreateTime = -1;
            for (Datastream d : datastreams(ds.DatastreamID)) {
                if (d.DSCreateDT.getTime() > latestCreateTime) {
                    latestCreateTime = d.DSCreateDT.getTime();
                    latestCreated = d;
                }
            }
            remove(latestCreated);
        }
        add(ds);
    }

    private void add(Datastream d) {

        /*
         * We determine the most recent datastream version by its created date.
         * If a created date has not been supplied, give it one.
         */
        if (d.DSCreateDT == null) {
            d.DSCreateDT = new Date();
        }

        String id = d.DatastreamID;
        if (!m_datastreams.containsKey(id)) {
            m_datastreams.put(id, new ArrayList<Datastream>());
        }

        m_datastreams.get(id).add(d);
        m_datastreamProcessor.processAdd(d);
    }

    private void remove(Datastream d) {
        if (d == null) return;
        List<Datastream> datastreams = m_datastreams.get(d.DatastreamID);

        if (datastreams == null) {
            return;
        }

        int size = datastreams.size();
        for (int i = 0; i < size; i++) {
            Datastream v = datastreams.get(i);
            if (d.DSVersionID.equals(v.DSVersionID)) {
                datastreams.remove(i);
                m_datastreamProcessor.processRemove(v);
                break;
            }
        }

        /* If we've removed the last version, remove the ID from the map */
        if (datastreams.size() == 0) {
            m_datastreams.remove(d.DatastreamID);
        }
    }

    @Deprecated
    public Iterator<String> disseminatorIdIterator() {
        return copyOfKeysForNonEmptyLists(m_disseminators).iterator();
    }

    @Deprecated
    public List<Disseminator> disseminators(String id) {
        ArrayList<Disseminator> ret =
                (ArrayList<Disseminator>) m_disseminators.get(id);
        if (ret == null) {
            ret = new ArrayList<Disseminator>();
            m_disseminators.put(id, ret);
        }
        return ret;
    }

    public String newDatastreamID() {
        return newID(datastreamIdIterator(), "DS");
    }

    public String newDatastreamID(String id) {
        List<String> versionIDs = new ArrayList<String>();
        Iterator<Datastream> iter = (m_datastreams.get(id)).iterator();
        while (iter.hasNext()) {
            Datastream ds = iter.next();
            versionIDs.add(ds.DSVersionID);
        }
        return newID(versionIDs.iterator(), id + ".");
    }

    public String newAuditRecordID() {
        ArrayList<String> auditIDs = new ArrayList<String>();
        Iterator<AuditRecord> iter = m_auditRecords.iterator();
        while (iter.hasNext()) {
            AuditRecord record = iter.next();
            auditIDs.add(record.id);
        }
        return newID(auditIDs.iterator(), "AUDREC");
    }

    /**
     * Sets an extended property on the object.
     * 
     * @param propName
     *        The extende property name, either a string, or URI as string.
     */
    public void setExtProperty(String propName, String propValue) {
        m_extProperties.put(propName, propValue);

    }

    /**
     * Gets an extended property value, given the property name.
     * 
     * @return The property value.
     */
    public String getExtProperty(String propName) {
        return m_extProperties.get(propName);

    }

    /**
     * Gets a Map containing all of the extended properties on the object. Map
     * key is property name.
     * 
     * @return The property Map.
     */
    public Map<String, String> getExtProperties() {
        return m_extProperties;

    }

    public boolean hasRelationship(PredicateNode predicate, ObjectNode object) {
        /* Brute force */
        return getRelationships(predicate, object).size() > 0;
    }

    public Set<RelationshipTuple> getRelationships(PredicateNode predicate,
                                                   ObjectNode object) {
        Set<RelationshipTuple> foundRels = new HashSet<RelationshipTuple>();

        if (m_rels == null) {
            readRels();
        }

        boolean basicExplicit = false;
        
        // Iterate explicit relationships, finding matches and
        // determining whether the object has an explicit basic cmodel.
        for (RelationshipTuple t : m_rels) {
            
            // Do any hasModel rels point to a basic cmodel?
            if (Constants.MODEL.HAS_MODEL.uri.equals(t.predicate)
                    && Models.isBasicModel(t.object)) {
                basicExplicit = true;
            }
            
            // Find matching relationships from those that are explicit
            if (predicate != null) {
                if (!JRDF.samePredicate(predicate, t.predicate)) {
                    continue;
                }
            }
            if (object != null) {
                if (!JRDF.sameObject(object,
                                     t.object,
                                     t.isLiteral,
                                     t.datatype,
                                     null)) {
                    continue;
                }

            }
            foundRels.add(t);
        }
       
        // If necessary, add the current basic cmodel to the set of matches
        if (!basicExplicit 
                && (predicate == null ||
                    JRDF.samePredicate(predicate, Constants.MODEL.HAS_MODEL))
                && (object == null ||
                    JRDF.sameObject(object, Models.FEDORA_OBJECT_CURRENT))) {
            foundRels.add(
                    new RelationshipTuple(Constants.FEDORA.uri + m_pid,
                                          Constants.MODEL.HAS_MODEL.uri,
                                          Models.FEDORA_OBJECT_CURRENT.uri,
                                          false,
                                          null));
        }
        
        return foundRels;
    }

    /**
     * Given an iterator of existing ids, return a new id that starts with
     * <code>start</code> and is guaranteed to be unique. This algorithm adds
     * one to the highest existing id that starts with <code>start</code>. If
     * no such existing id exists, it will return <i>start</i> + "1".
     */
    private String newID(Iterator<String> iter, String start) {
        int highest = 0;
        while (iter.hasNext()) {
            String id = iter.next();
            if (id.startsWith(start) && id.length() > start.length()) {
                try {
                    int num = Integer.parseInt(id.substring(start.length()));
                    if (num > highest) {
                        highest = num;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        int newNum = highest + 1;
        return start + newNum;
    }

    private void readRels() {
        List<Datastream> relsExtVersions = m_datastreams.get("RELS-EXT");

        if (relsExtVersions == null || relsExtVersions.size() == 0) {
            m_rels = new HashSet<RelationshipTuple>();
            return;
        }

        Datastream latestRels = relsExtVersions.get(0);

        for (Datastream v : relsExtVersions) {
            if (v.DSCreateDT.getTime() > latestRels.DSCreateDT.getTime()) {
                latestRels = v;
            }
        }

        try {
            m_rels = RDFRelationshipReader.readRelationships(latestRels);
        } catch (ServerException e) {
            throw new RuntimeException("Error reading object relationships", e);
        }
    }

    private abstract class DatastreamProcessor {

        abstract void processAdd(Datastream d);

        abstract void processRemove(Datastream d);

        boolean isLatestVersion(Datastream d) {

            List<Datastream> versions = m_datastreams.get(d.DatastreamID);

            if (versions == null || versions.size() == 0) return true;

            long created = d.DSCreateDT.getTime();

            for (Datastream v : versions) {
                if (v.DSCreateDT.getTime() > created) {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * If the latest RELS-EXT is added or removed, invalidate the relationship
     * cache so that it has to be re-read next time it is requested.
     */
    private class RelationshipProcessor
            extends DatastreamProcessor {

        private static final String RELS_EXT = "RELS-EXT";

        void processRemove(Datastream d) {
            invalidateIfLatestRelsExt(d);
        }

        void processAdd(Datastream d) {
            invalidateIfLatestRelsExt(d);
        }

        private void invalidateIfLatestRelsExt(Datastream d) {
            if (d.DatastreamID.equals(RELS_EXT) && isLatestVersion(d)) {
                m_rels = null;
            }
        }
    }
}