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

package fedora.client.utility;

import java.io.IOException;

import java.net.MalformedURLException;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import fedora.client.FedoraClient;

import fedora.server.management.FedoraAPIM;

/**
 * @author Chris Wilper
 */
public class AutoPurger {

    private final FedoraAPIM m_apim;

    public AutoPurger(FedoraAPIM apim)
            throws MalformedURLException, ServiceException {
        m_apim = apim;
    }

    public void purge(String pid, String logMessage, boolean force)
            throws RemoteException, IOException {
        purge(m_apim, pid, logMessage, force);
    }

    public static void purge(FedoraAPIM skeleton,
                             String pid,
                             String logMessage,
                             boolean force) throws RemoteException, IOException {
        skeleton.purgeObject(pid, logMessage, force);
    }

    /**
     * Print error message and show usage for command-line interface.
     */
    public static void showUsage(String msg) {
        System.err.println("Command: fedora-purge");
        System.err.println();
        System.err
                .println("Summary: Purges an object from the Fedora repository.");
        System.err.println();
        System.err.println("Syntax:");
        System.err.println("  fedora-purge HST:PRT USR PSS PID PROTOCOL [LOG]");
        System.err.println();
        System.err.println("Where:");
        System.err.println("  HST  is the target repository hostname.");
        System.err.println("  PRT  is the target repository port number.");
        System.err.println("  USR  is the id of the target repository user.");
        System.err
                .println("  PSS  is the password of the target repository user.");
        System.err
                .println("  PID  is the id of the object to purge from the target repository.");
        System.err
                .println("  PROTOCOL  is the protocol to communicate with repository (http or https)");
        System.err.println("  LOG  is a log message.");
        System.err.println();
        System.err.println("Example:");
        System.err
                .println("fedora-purge myrepo.com:8443 jane janepw demo:5 https \"my message\"");
        System.err.println();
        System.err.println("  Purges the object whose PID is demo:5 from the");
        System.err
                .println("  target repository at myrepo.com:8443 using the secure https protocol (SSL)");
        System.err.println();
        System.err.println("ERROR  : " + msg);
        System.exit(1);
    }

    public static void main(String[] args) {
        try {
            if (args.length != 6) {
                AutoPurger.showUsage("You must provide six arguments.");
            } else {
                String[] hp = args[0].split(":");
                String hostName = hp[0];
                int portNum = Integer.parseInt(hp[1]);
                String pid = args[3];
                String protocol = args[4];
                String logMessage = args[5];

                // ******************************************
                // NEW: use new client utility class
                // FIXME:  Get around hardcoding the path in the baseURL
                String baseURL =
                        protocol + "://" + hostName + ":" + portNum + "/fedora";
                FedoraClient fc = new FedoraClient(baseURL, args[1], args[2]);
                AutoPurger a = new AutoPurger(fc.getAPIM());
                //*******************************************
                a.purge(pid, logMessage, false); // DEFAULT_FORCE_PURGE
            }
        } catch (Exception e) {
            AutoPurger.showUsage(e.getClass().getName()
                    + " - "
                    + (e.getMessage() == null ? "(no detail provided)" : e
                            .getMessage()));
        }
    }

}
