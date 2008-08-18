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

import java.util.Date;

import fedora.server.errors.ServerException;
import fedora.server.storage.types.Datastream;

/**
 * The standard interface for write operations on a digital object.
 * 
 * <p>A <code>DOWriter</code> instance is a handle on a Fedora digital object,
 * and is obtained via a <code>getWriter(String)</code> call on a
 * <code>DOManager</code>.
 * 
 * <p>Work with a DOWriter ends with either commit() or cancel().
 * 
 * @author Chris Wilper
 */
public interface DOWriter
        extends DOReader {

    /**
     * Sets the state of the entire digital object.
     * 
     * @param state
     *        The state.
     * @throws ServerException
     *         If any type of error occurred fulfilling the request.
     */
    public void setState(String state) throws ServerException;

    /**
     * Sets the ownerId for the digital object.
     * 
     * @param ownerId
     *        The ownerId.
     * @throws ServerException
     *         If any type of error occurred fulfilling the request.
     */
    public void setOwnerId(String ownerId) throws ServerException;

    /**
     * Sets the state for all versions of the specified datastream.
     * 
     * @param id
     *        The datastream id.
     * @param state
     *        The state.
     * @throws ServerException
     *         If any type of error occurred fulfilling the request.
     */
    public void setDatastreamState(String id, String state)
            throws ServerException;

    /**
     * Sets the indicator as to whether the datastream is subject to versioning.
     * In Fedora 2.0, the system will not operate on this indicator and all
     * datastreams will be versioned by default.
     * 
     * @param id
     *        The datastream id.
     * @param versionable
     *        a boolean indicating if versionable
     * @throws ServerException
     *         If any type of error occurred fulfilling the request.
     */
    public void setDatastreamVersionable(String id, boolean versionable)
            throws ServerException;

    /**
     * Sets the label of the digital object.
     * 
     * @param label
     *        The label.
     * @throws ServerException
     *         If any type of error occurred fulfilling the request.
     */
    public void setLabel(String label) throws ServerException;

    /**
     * Removes the entire digital object.
     * 
     * @throws ServerException
     *         If any type of error occurred fulfilling the request.
     */
    public void remove() throws ServerException;

    /**
     * Adds a datastream to the object.
     * 
     * @param datastream
     *        The datastream.
     * @throws ServerException
     *         If any type of error occurred fulfilling the request.
     */
    public void addDatastream(Datastream datastream, boolean addNewVersion)
            throws ServerException;

    /**
     * Removes a range of datastream versions from an object without leaving
     * anything behind. If any integrity checks need to be done, they should be
     * done outside of this code.
     * 
     * @param id
     *        The id of the datastream.
     * @param start
     *        The start date (inclusive) of versions to remove. If
     *        <code>null</code>, this is taken to be the smallest possible
     *        value.
     * @param end
     *        The end date (inclusive) of versions to remove. If
     *        <code>null</code>, this is taken to be the greatest possible
     *        value.
     * @throws ServerException
     *         If any type of error occurred fulfilling the request.
     */
    public Date[] removeDatastream(String id, Date start, Date end)
            throws ServerException;

    /**
     * Saves the changes thus far to the permanent copy of the digital object.
     * 
     * @param logMessage
     *        An explanation of the change(s).
     * @throws ServerException
     *         If any type of error occurred fulfilling the request.
     */
    public void commit(String logMessage) throws ServerException;

    /**
     * Marks this DOWriter handle invalid (unusable).
     */
    public void invalidate();

    /**
     * Generate a unique id for a datastream.
     */
    public String newDatastreamID();

    /**
     * Generate a unique id for a datastream version.
     */
    public String newDatastreamID(String dsID);

    /**
     * Generate a unique id for an audit record.
     */
    public String newAuditRecordID();

    /**
     * Marks whether the object has been successfully committed.
     */
    public boolean isCommitted();

    /**
     * Marks whether the object is new.
     */
    public boolean isNew();

    /**
     * Adds a RDF triple to the RELS-EXT datastream
     */
    public boolean addRelationship(String relationship,
                                   String object,
                                   boolean isLiteral,
                                   String datatype) throws ServerException;

    /**
     * Purges a RDF triple from the RELS-EXT datastream
     */
    public boolean purgeRelationship(String relationship,
                                     String object,
                                     boolean isLiteral,
                                     String datatype) throws ServerException;

}
