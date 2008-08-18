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

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import fedora.server.errors.ModuleInitializationException;
import fedora.server.journal.ServerInterface;
import fedora.server.journal.helpers.JournalHelper;

/**
 * A production-oriented implementation of {@link JournalRecoveryLog}.
 * <p>
 * The name of the log file contains a time-stamp, so we can be sure that it
 * won't over-write an existing log file. Also, the name is prefixed with an '_'
 * (underscore) while the recovery is in progress, so we can see when it
 * finishes.
 * 
 * @author Jim Blake
 */
public class RenamingJournalRecoveryLog
        extends JournalRecoveryLog {

    /** Logger for this class. */
    private static final Logger LOG =
            Logger.getLogger(RenamingJournalRecoveryLog.class.getName());

    /** The name of the log file, when it is complete. */
    String fileName;

    /** The name of the log file, while it is running. */
    String tempFileName;

    private final File logFile;

    private final FileWriter writer;

    private boolean open = true;

    /**
     * @param parameters
     * @param role
     * @param server
     * @throws ModuleInitializationException
     */
    public RenamingJournalRecoveryLog(Map<String, String> parameters,
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
            String fileNameTemplate =
                    parameters.get(PARAMETER_RECOVERY_LOG_FILENAME);
            fileName =
                    JournalHelper.createTimestampedFilename(fileNameTemplate,
                                                            new Date());
            tempFileName = insertHyphenBeforeFilename(fileName);
            logFile = new File(tempFileName);
            writer = new FileWriter(logFile);

            super.logHeaderInfo(parameters);
        } catch (IOException e) {
            throw new ModuleInitializationException("Problem writing to the recovery log",
                                                    role,
                                                    e);
        }
    }

    private String insertHyphenBeforeFilename(String name) {
        int lastSlash = name.lastIndexOf(File.separatorChar);
        if (lastSlash == -1) {
            return '_' + name;
        } else {
            return name.substring(0, lastSlash + 1) + '_'
                    + name.substring(lastSlash + 1);
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
                super.log(message, writer);
                writer.flush();
            }
        } catch (IOException e) {
            LOG.error("Unable to write journal log message", e);
        }
    }

    /**
     * On the first call to this method, close the log file and rename it. Set
     * the flag so no more logging calls will be accepted.
     */
    @Override
    public synchronized void shutdown() {
        try {
            if (open) {
                open = false;
                writer.close();
                logFile.renameTo(new File(fileName));
            }
        } catch (IOException e) {
            LOG.error("Error shutting down", e);
        }
    }

    @Override
    public String toString() {
        return super.toString() + ", logFile='" + logFile.getPath() + "'";
    }

}
