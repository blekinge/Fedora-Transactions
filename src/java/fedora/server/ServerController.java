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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fedora.common.Constants;

import fedora.server.errors.authorization.AuthzDeniedException;
import fedora.server.errors.authorization.AuthzOperationalException;
import fedora.server.errors.authorization.AuthzPermittedException;
import fedora.server.errors.servletExceptionExtensions.BadRequest400Exception;
import fedora.server.errors.servletExceptionExtensions.Continue100Exception;
import fedora.server.errors.servletExceptionExtensions.Forbidden403Exception;
import fedora.server.errors.servletExceptionExtensions.InternalError500Exception;
import fedora.server.errors.servletExceptionExtensions.Ok200Exception;
import fedora.server.errors.servletExceptionExtensions.Unavailable503Exception;
import fedora.server.security.Authorization;
import fedora.server.utilities.status.ServerState;
import fedora.server.utilities.status.ServerStatusFile;

/**
 * Server Controller.
 * 
 * @author Chris Wilper
 */
public class ServerController
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Server s_server;

    private ServerStatusFile _status;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String actionLabel = "server control";
        String action = request.getParameter("action");
        String requestInfo =
                "Got controller '" + action + "' request from "
                        + request.getRemoteAddr();
        if (action == null) {
            throw new BadRequest400Exception(request,
                                             actionLabel,
                                             "no action",
                                             new String[0]);
        }

        if (action.equals("status")) {
            actionLabel = "getting server status";
            Context context =
                    ReadOnlyContext.getContext(Constants.HTTP_REQUEST.REST.uri,
                                               request);
            File fedoraHome = new File(Constants.FEDORA_HOME);
            if (!Server.hasInstance(fedoraHome)) {
                throw new Unavailable503Exception(request,
                                                  actionLabel,
                                                  "server not available",
                                                  new String[0]);
            }
            Server server = null;
            try {
                server = Server.getInstance(fedoraHome, false);
            } catch (Throwable t) {
                throw new InternalError500Exception(request,
                                                    actionLabel,
                                                    "error performing action0",
                                                    new String[0]);
            }
            if (server == null) {
                throw new InternalError500Exception(request,
                                                    actionLabel,
                                                    "error performing action1",
                                                    new String[0]);
            }
            try {
                server.status(context);
            } catch (AuthzOperationalException aoe) {
                throw new Forbidden403Exception(request,
                                                actionLabel,
                                                "authorization failed",
                                                new String[0]);
            } catch (AuthzDeniedException ade) {
                throw new Forbidden403Exception(request,
                                                actionLabel,
                                                "authorization denied",
                                                new String[0]);
            } catch (AuthzPermittedException ape) {
                throw new Continue100Exception(request,
                                               actionLabel,
                                               "authorization permitted",
                                               new String[0]);
            } catch (Throwable t) {
                throw new InternalError500Exception(request,
                                                    actionLabel,
                                                    "error performing action2",
                                                    new String[0]);
            }
            throw new Ok200Exception(request,
                                     actionLabel,
                                     "server running",
                                     new String[0]);
        }
        if (action.equals("reloadPolicies")) {
            actionLabel = "reloading repository policies";
            Context context =
                    ReadOnlyContext.getContext(Constants.HTTP_REQUEST.REST.uri,
                                               request);
            File fedoraHome = new File(Constants.FEDORA_HOME);
            if (!Server.hasInstance(fedoraHome)) {
                throw new Unavailable503Exception(request,
                                                  actionLabel,
                                                  "server not available",
                                                  new String[0]);
            }
            Server server = null;
            try {
                server = Server.getInstance(fedoraHome, false);
            } catch (Throwable t) {
                throw new InternalError500Exception(request,
                                                    actionLabel,
                                                    "error performing action0",
                                                    new String[0]);
            }
            if (server == null) {
                throw new InternalError500Exception(request,
                                                    actionLabel,
                                                    "error performing action1",
                                                    new String[0]);
            }
            Authorization authModule = null;
            authModule =
                    (Authorization) server
                            .getModule("fedora.server.security.Authorization");
            if (authModule == null) {
                throw new InternalError500Exception(request,
                                                    actionLabel,
                                                    "error performing action2",
                                                    new String[0]);
            }
            try {
                authModule.reloadPolicies(context);
            } catch (AuthzOperationalException aoe) {
                throw new Forbidden403Exception(request,
                                                actionLabel,
                                                "authorization failed",
                                                new String[0]);
            } catch (AuthzDeniedException ade) {
                throw new Forbidden403Exception(request,
                                                actionLabel,
                                                "authorization denied",
                                                new String[0]);
            } catch (AuthzPermittedException ape) {
                throw new Continue100Exception(request,
                                               actionLabel,
                                               "authorization permitted",
                                               new String[0]);
            } catch (Throwable t) {
                throw new InternalError500Exception(request,
                                                    actionLabel,
                                                    "error performing action2",
                                                    new String[0]);
            }
            throw new Ok200Exception(request,
                                     actionLabel,
                                     "server running",
                                     new String[0]);
        }

        throw new BadRequest400Exception(request, actionLabel, "bad action:  "
                + action, new String[0]);
    }

    @Override
    public void init() throws ServletException {

        // make sure fedora.home is defined first
        File fedoraHomeDir = getFedoraHomeDir();

        // FIXME: This is set here for legacy server code that still picks up
        // these values from fedora.home.  When the code is updated, this
        // can be removed.
        System.setProperty("fedora.home", fedoraHomeDir.getPath());

        // get file for writing startup status
        try {
            _status = new ServerStatusFile(new File(fedoraHomeDir, "server"));
        } catch (Throwable th) {
            failStartup("Error initializing server status file", th);
        }

        try {
            // Start the Fedora instance
            _status.append(ServerState.STARTING,
                           "Starting Fedora Server instance");
            s_server = Server.getInstance(fedoraHomeDir);
            _status.append(ServerState.STARTED, null);
        } catch (Throwable th) {
            String msg = "Fedora startup failed";
            try {
                _status.appendError(ServerState.STARTUP_FAILED, th);
            } catch (Exception e) {
            }
            failStartup(msg, th);
        }
    }

    /**
     * Validates and returns the value of FEDORA_HOME.
     * 
     * @return the FEDORA_HOME directory.
     * @throws ServletException
     *         if FEDORA_HOME (or fedora.home) was not set, does not denote an
     *         existing directory, or is not writable by the current user.
     */
    private File getFedoraHomeDir() throws ServletException {

        String fedoraHome = Constants.FEDORA_HOME;
        if (fedoraHome == null) {
            failStartup("Neither the FEDORA_HOME environment variable, "
                    + "nor the fedora.home system property was set", null);
        }
        File fedoraHomeDir = new File(fedoraHome);
        if (!fedoraHomeDir.isDirectory()) {
            failStartup("The FEDORA_HOME directory, " + fedoraHomeDir.getPath()
                    + " does not exist", null);
        }
        File writeTest = new File(fedoraHomeDir, "writeTest.tmp");
        String writeErrorMessage =
                "The FEDORA_HOME directory, " + fedoraHomeDir.getPath()
                        + " is not writable by " + "the current user, "
                        + System.getProperty("user.name");
        try {
            writeTest.createNewFile();
            if (!writeTest.exists()) {
                throw new IOException("");
            }
            writeTest.delete();
        } catch (IOException e) {
            failStartup(writeErrorMessage, null);
        }

        return fedoraHomeDir;
    }

    /**
     * Prints a "FEDORA STARTUP ERROR" to STDERR along with the stacktrace of
     * the Throwable (if given) and finally, throws a ServletException.
     */
    private void failStartup(String message, Throwable th)
            throws ServletException {
        System.err.println("\n**************************");
        System.err.println("** FEDORA STARTUP ERROR **");
        System.err.println("**************************\n");
        System.err.println(message);
        if (th == null) {
            System.err.println();
            throw new ServletException(message);
        } else {
            th.printStackTrace();
            System.err.println();
            throw new ServletException(message, th);
        }
    }

    @Override
    public void destroy() {

        if (s_server != null) {
            try {
                _status.append(ServerState.STOPPING,
                               "Shutting down Fedora Server and modules");
                s_server.shutdown(null);
                _status.append(ServerState.STOPPED, "Shutdown Successful");
            } catch (Throwable th) {
                try {
                    _status.appendError(ServerState.STOPPED_WITH_ERR, th);
                } catch (Exception e) {
                }
            }
            s_server = null;
        }
    }

}
