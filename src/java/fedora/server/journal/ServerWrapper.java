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

package fedora.server.journal;

import fedora.server.Server;
import fedora.server.errors.ServerException;
import fedora.server.management.ManagementDelegate;
import fedora.server.storage.DOManager;

/**
 * Wrap a Server in an object that implements an interface, so it can be passed
 * to the JournalWorker classes and their dependents.
 * <p>
 * It's also easy to mock, for unit tests.
 * 
 * @author Jim Blake
 */
public class ServerWrapper
        implements ServerInterface {

    private final Server server;

    public ServerWrapper(Server server) {
        this.server = server;
    }

    public boolean hasInitialized() {
        return server.hasInitialized();
    }

    public ManagementDelegate getManagementDelegate() {
        return (ManagementDelegate) server
                .getModule("fedora.server.management.ManagementDelegate");
    }

    public String getRepositoryHash() throws ServerException {
        DOManager doManager =
                (DOManager) server.getModule("fedora.server.storage.DOManager");
        return doManager.getRepositoryHash();
    }

}
