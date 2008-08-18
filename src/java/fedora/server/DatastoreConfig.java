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

package fedora.server;

import java.util.Map;

/**
 * A holder of configuration name-value pairs for a datastore.
 *
 * <p>A datastore is a system for retrieving and storing information. This
 * class is a convenient placeholder for the configuration values of such a 
 * system.
 *
 * <p>Configuration values for datastores are set in the server configuration
 * file. (see fedora-config.xsd)
 * 
 * @author Chris Wilper
 */
public class DatastoreConfig
        extends Parameterized {

    /**
     * Creates and initializes the <code>DatastoreConfig</code>.
     * 
     * <p>When the server is starting up, this is invoked as part of the
     * initialization process.
     * 
     * @param parameters
     *        A pre-loaded Map of name-value pairs comprising the intended
     *        configuration for the datastore.
     */
    public DatastoreConfig(Map parameters) {
        super(parameters);
    }

}
