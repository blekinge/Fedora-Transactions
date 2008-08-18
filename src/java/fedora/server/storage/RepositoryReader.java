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

import fedora.server.Context;
import fedora.server.errors.ServerException;

/**
 * Provides context-appropriate digital object readers and the ability to 
 * list all objects (accessible in the given context) within the repository.
 * 
 * @author Chris Wilper
 */
public interface RepositoryReader {

    /**
     * Gets a digital object reader.
     * 
     * @param context
     *        The context of this request.
     * @param pid
     *        The PID of the object.
     * @return A reader.
     * @throws ServerException
     *         If anything went wrong.
     */
    public abstract DOReader getReader(boolean cachedObjectRequired,
                                       Context context,
                                       String pid) throws ServerException;

    public abstract ServiceDeploymentReader getServiceDeploymentReader(boolean cachedObjectRequired,
                                               Context context,
                                               String pid)
            throws ServerException;

    public abstract ServiceDefinitionReader getServiceDefinitionReader(boolean cachedObjectRequired,
                                             Context context,
                                             String pid) throws ServerException;

    /**
     * Gets a list of PIDs (accessible in the given context) of all objects in
     * the repository.
     */
    public String[] listObjectPIDs(Context context) throws ServerException;

}
