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

package fedora.server.utilities.rebuild;

import java.io.File;

import java.util.Map;

import fedora.server.config.ServerConfiguration;
import fedora.server.storage.types.DigitalObject;

/**
 * Interface for a class that rebuilds some aspect of the repository.
 * 
 * <p>It is expected that clients of this interface will first call init, then 
 * start, then addObject (possibly a series of times), then finish.
 * 
 * @author Chris Wilper
 */
public interface Rebuilder {

    /**
     * Get a short phrase describing what the user can do with this rebuilder.
     */
    public String getAction();

    /**
     * Initialize the rebuilder, given the server configuration.
     * 
     * @returns a map of option names to plaintext descriptions.
     */
    public Map<String, String> init(File serverBaseDir,
                                    ServerConfiguration serverConfig)
            throws Exception;

    /**
     * Returns true is the server _must_ be shut down for this rebuilder to
     * safely operate.
     */
    public boolean shouldStopServer();

    /**
     * Validate the provided options and perform any necessary startup tasks.
     */
    public void start(Map<String, String> options) throws Exception;

    /**
     * Add the data of interest for the given object.
     */
    public void addObject(DigitalObject object) throws Exception;

    /**
     * Free up any system resources associated with rebuilding.
     */
    public void finish() throws Exception;

}