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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import org.w3c.dom.Element;

import fedora.common.Constants;
import fedora.common.Models;
import fedora.common.PID;
import fedora.common.rdf.RDFName;

import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ServerInitializationException;
import fedora.server.storage.DOManager;
import fedora.server.storage.DOWriter;
import fedora.server.utilities.status.ServerState;
import fedora.server.utilities.status.ServerStatusFile;

/**
 * Fedora Server.
 * 
 * @author Chris Wilper
 */
public class BasicServer
        extends Server {

    /** Logger for this class. */
    private static Logger LOG = Logger.getLogger(BasicServer.class.getName());

    public BasicServer(Element rootElement, File fedoraHomeDir)
            throws ServerInitializationException, ModuleInitializationException {
        super(rootElement, fedoraHomeDir);
    }

    @Override
    public void initServer() throws ServerInitializationException {

        String fedoraServerHost = null;
        String fedoraServerPort = null;

        // fedoraServerHost (required)
        fedoraServerHost = getParameter("fedoraServerHost");
        if (fedoraServerHost == null) {
            throw new ServerInitializationException("Parameter fedoraServerHost "
                    + "not given, but it's required.");
        }
        // fedoraServerPort (required)
        fedoraServerPort = getParameter("fedoraServerPort");
        if (fedoraServerPort == null) {
            throw new ServerInitializationException("Parameter fedoraServerPort "
                    + "not given, but it's required.");
        }

        LOG.info("Fedora Version: " + VERSION_MAJOR + "." + VERSION_MINOR);
        LOG.info("Fedora Build: " + BUILD_NUMBER);

        ServerStatusFile status = getStatusFile();
        try {
            status.append(ServerState.STARTING, "Fedora Version: "
                    + VERSION_MAJOR + "." + VERSION_MINOR);
            status.append(ServerState.STARTING, "Fedora Build: "
                    + BUILD_NUMBER);
            status.append(ServerState.STARTING, "Server Host Name: "
                    + fedoraServerHost);
            status.append(ServerState.STARTING, "Server Port: "
                    + fedoraServerPort);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerInitializationException("Unable to write to status file: "
                    + e.getMessage());
        }
    }

    /**
     * Gets the names of the roles that are required to be fulfilled by modules
     * specified in this server's configuration file.
     * 
     * @return String[] The roles.
     */
    @Override
    public String[] getRequiredModuleRoles() {
        return new String[] {DOManager.class.getName()};
    }
    
    @Override
    public void postInitServer() throws ServerInitializationException {
        // check for system objects and pre-ingest them if necessary
        DOManager doManager = (DOManager) getModule(DOManager.class.getName());
        try {
            preIngestIfNeeded(doManager, Models.CONTENT_MODEL_3_0);
            preIngestIfNeeded(doManager, Models.FEDORA_OBJECT_3_0);
            preIngestIfNeeded(doManager, Models.SERVICE_DEFINITION_3_0);
            preIngestIfNeeded(doManager, Models.SERVICE_DEPLOYMENT_3_0);
        } catch (Exception e) {
            throw new ServerInitializationException("Failed to ingest "
                                                    + "system object(s)", e);
        }
    }
    
    private void preIngestIfNeeded(DOManager doManager,
                                   RDFName objectName) throws Exception {
        PID pid = new PID(objectName.uri.substring("info:fedora/".length()));
        if (!doManager.objectExists(pid.toString())) {
            LOG.info("Pre-ingesting system object: " + pid.toString());
            InputStream xml = getStream("fedora/server/resources/"
                                        + pid.toFilename() + ".xml");
            Context context = ReadOnlyContext.getContext(null,
                                                         null,
                                                         null,
                                                         false);
            DOWriter w = doManager.getIngestWriter(USE_DEFINITIVE_STORE,
                                                   context,
                                                   xml,
                                                   Constants.FOXML1_1.uri,
                                                   "UTF-8",
                                                   false);
            try {
                w.commit("Pre-ingested by Fedora at startup");
            } finally {
                doManager.releaseWriter(w);
            }
        }
    }
    
    private InputStream getStream(String path) throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(
                    path);
        if (stream == null) {
            throw new IOException("Classloader cannot find resource: " + path);
        } else {
            return stream;
        }
    }
    

}
