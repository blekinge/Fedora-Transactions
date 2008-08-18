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

import java.io.OutputStream;

import org.trippi.RDFFormat;
import org.trippi.TriplestoreWriter;

import fedora.server.errors.ResourceIndexException;
import fedora.server.storage.DOReader;

/**
 * The main interface to the Fedora Resource Index. The Resource Index (RI)
 * provides read/write access to an RDF representation of all objects in the
 * Fedora repository. The information stored in the RI is derived solely from
 * information stored within the digital objects.
 * 
 * @author Edwin Shin
 * @author Chris Wilper
 */
public interface ResourceIndex
        extends TriplestoreWriter {

    /**
     * At this level, the ResourceIndex will not index anything.
     */
    public static final int INDEX_LEVEL_OFF = 0;

    /**
     * At this level the ResourceIndex will index: object properties datastreams
     * intra-object dependencies
     */
    public static final int INDEX_LEVEL_ON = 1;

    /**
     * Gets the index level of the ResourceIndex.
     * 
     * @return the current index level of the RI, which is either
     *         INDEX_LEVEL_OFF or INDEX_LEVEL_ON.
     */
    int getIndexLevel();

    /**
     * Adds the appripriate triples implied by the given object to the
     * ResourceIndex.
     * 
     * @param reader
     *        The given object to index.
     * @throws ResourceIndexException
     *         If the triples can't be added for any reason.
     */
    void addObject(DOReader reader) throws ResourceIndexException;

    /**
     * Updates any appropriate triples implied a modified object.
     * 
     * @param oldReader
     *        Pre-modification version of the oject.
     * @param newReader
     *        Post-modification version of the object.
     * @throws ResourceIndexException
     *         If the triples can't be updated for any reason.
     */
    void modifyObject(DOReader oldReader, DOReader newReader)
            throws ResourceIndexException;

    /**
     * Removes the triples implied by a given object from the ResourceIndex.
     * 
     * @param oldReader
     *        Object whose triples shall be removed from the index.
     * @throws ResourceIndexException
     *         If the triples can't be removed for any reason.
     */
    void deleteObject(DOReader oldReader) throws ResourceIndexException;

    /**
     * Exports all triples in the RI.
     * 
     * @param out
     *        the output stream to which the RDF should be written. The caller
     *        is responsible for eventually closing this stream.
     * @param format
     *        the output format (RDF_XML, TURTLE, N_TRIPLESs, etc).
     * @throws ResourceIndexException
     *         if triples in the RI cannot be serialized for any reason.
     */
    void export(OutputStream out, RDFFormat format)
            throws ResourceIndexException;

}
