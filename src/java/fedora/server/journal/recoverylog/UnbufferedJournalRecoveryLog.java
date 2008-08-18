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

package fedora.server.journal.recoverylog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Map;

import org.apache.log4j.Logger;

import fedora.server.errors.ModuleInitializationException;
import fedora.server.journal.ServerInterface;

/**
 * A basic implementation of RecoveryLog.
 * <p>
 * All entries are written to a log, which is flushed after each entry so the
 * log will be up to date even if the server crashes.
 * 
 * @author Jim Blake
 */
public class UnbufferedJournalRecoveryLog
        extends JournalRecoveryLog {

    /** Logger for this class. */
    private static final Logger LOG =
            Logger.getLogger(UnbufferedJournalRecoveryLog.class.getName());

    private final File logFile;

    private final FileWriter writer;

    private boolean open = true;

    /**
     * Get the name of the logfile from the server parameters and create the
     * file.
     */
    public UnbufferedJournalRecoveryLog(Map<String, String> parameters,
                                        String role,
                                        ServerInterface server)
            throws ModuleInitializationException {
        super(parameters, role, server);

        try {
            if (!parameters.containsKey(PARAMETER_RECOVERY_LOG_FILENAME)) {
                throw new ModuleInitializationException("Parameter '"
                                                                + PARAMETER_RECOVERY_LOG_FILENAME
                                                                + "' is not set.",
                                                        role);
            }
            String fileName = parameters.get(PARAMETER_RECOVERY_LOG_FILENAME);
            logFile = new File(fileName);
            writer = new FileWriter(logFile);

            super.logHeaderInfo(parameters);
        } catch (IOException e) {
            throw new ModuleInitializationException("Problem writing to the recovery log",
                                                    role,
                                                    e);
        }
    }

    /**
     * A request to log a message just writes it to the log file and flushes it
     * (if shutdown has not been called).
     */
    @Override
    public synchronized void log(String message) {
        try {
            if (open) {
                log(message, writer);
                writer.flush();
            }
        } catch (IOException e) {
            LOG.error("Error writing journal log entry", e);
        }
    }

    /**
     * On the first call to this method, close the log file. Set the flag so no
     * more logging calls will be accepted.
     */
    @Override
    public synchronized void shutdown() {
        try {
            if (open) {
                open = false;
                writer.close();
            }
        } catch (IOException e) {
            LOG.error("Error shutting down journal log", e);
        }
    }

    @Override
    public String toString() {
        return super.toString() + ", logFile='" + logFile.getPath() + "'";
    }

}
