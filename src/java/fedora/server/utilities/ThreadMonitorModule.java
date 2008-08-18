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

package fedora.server.utilities;

import java.util.Map;

import org.apache.log4j.Logger;

import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.ModuleInitializationException;

/**
 * Module wrapper for ThreadMonitorImpl.
 * 
 * @author Chris Wilper
 */
public class ThreadMonitorModule
        extends Module
        implements ThreadMonitor {

    /** Logger for this class. */
    private static final Logger LOG =
            Logger.getLogger(ThreadMonitorModule.class.getName());

    private ThreadMonitorImpl m_wrappedMonitor;

    private boolean m_active = false;

    public ThreadMonitorModule(Map<String, String> params, Server server, String role)
            throws ModuleInitializationException {
        super(params, server, role);
    }

    @Override
    public void initModule() throws ModuleInitializationException {
        String active = getParameter("active");
        String pollInterval = getParameter("pollInterval");
        String onlyMemory = getParameter("onlyMemory");
        if (active != null
                && (active.toLowerCase().equals("yes") || active.toLowerCase()
                        .equals("true"))) {
            m_active = true;
            if (pollInterval == null) {
                LOG
                        .info("pollInterval unspecified, defaulting to 10,000 milliseconds.");
                pollInterval = "10000";
            }
            try {
                int pi = Integer.parseInt(pollInterval);
                if (pi < 0) {
                    throw new NumberFormatException();
                }
                boolean onlyMem = false;
                if (onlyMemory.equalsIgnoreCase("yes")
                        || onlyMemory.equalsIgnoreCase("true")) {
                    onlyMem = true;
                }
                m_wrappedMonitor = new ThreadMonitorImpl(pi, onlyMem);
            } catch (NumberFormatException nfe) {
                throw new ModuleInitializationException("Badly formed parameter: pollInterval: must be a nonnegative integer.",
                                                        getRole());
            }
        }
    }

    @Override
    public void shutdownModule() {
        if (m_active) {
            m_wrappedMonitor.requestStop();
        }
    }

    public void run() {
        if (m_active) {
            m_wrappedMonitor.run();
        }
    }

    public void requestStop() {
        if (m_active) {
            m_wrappedMonitor.requestStop();
        }
    }

}
