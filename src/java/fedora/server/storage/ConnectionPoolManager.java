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

import fedora.server.errors.ConnectionPoolNotFoundException;

/**
 * Interface that defines a <code>Module</code> to facilitate the acquisition
 * of JDBC connection pools for database access.
 * 
 * @author Ross Wayland
 */
public interface ConnectionPoolManager {

    /**
     * Gets the specified connection pool.
     * 
     * @param poolName
     *        The name of the specified connection pool.
     * @return The named connection pool.
     * @throws ConnectionPoolNotFoundException
     *         If the specified connection pool cannot be found.
     */
    public ConnectionPool getPool(String poolName)
            throws ConnectionPoolNotFoundException;

    /**
     * Gets the default Connection Pool. Overrides
     * <code>getPool(String poolName)</code> to return the default connection
     * pool when no specific pool name is provided as an argument.
     * 
     * @return The default connection pool.
     * @throws ConnectionPoolNotFoundException
     *         If the default connection pool cannot be found.
     */
    public ConnectionPool getPool() throws ConnectionPoolNotFoundException;
}
