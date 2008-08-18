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

/*
 * The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.journal.readerwriter.multicast.rmi;

import java.io.File;

import org.apache.log4j.lf5.LogLevel;
import org.apache.log4j.lf5.LogLevelFormatException;

/**
 * <p>
 * RmiJournalReceiverArguments.java
 * </p>
 * <p>
 * Parses and encapsulates the command line arguments that are provided to the
 * {@link RmiJournalReceiver}.
 * </p>
 * 
 * @author jblake
 * @version $Id: RmiJournalReceiverArguments.java,v 1.1 2007/03/07 19:30:42
 *          jblake Exp $
 */
public class RmiJournalReceiverArguments {

    private static final int DEFAULT_REGISTRY_PORT_NUMBER = 1099;

    private static final int DEFAULT_SERVER_PORT_NUMBER = 1100;

    private final File directoryPath;

    private final int registryPortNumber;

    private final int serverPortNumber;

    private final LogLevel logLevel;

    public RmiJournalReceiverArguments(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("usage: RmiJournalReceiver [fullDirectoryPath] {registryPort} {serverPort} {logLevel}");
        }
        directoryPath = parseDirectoryPath(args);
        registryPortNumber = parseRegistryPortNumber(args);
        serverPortNumber = parseServerPortNumber(args);
        logLevel = parseLogLevel(args);
    }

    private File parseDirectoryPath(String[] args) {
        File path = new File(args[0]);
        if (!path.exists()) {
            throw new IllegalArgumentException("'" + path.getAbsolutePath()
                    + "' does not exist.");
        }
        if (!path.isDirectory()) {
            throw new IllegalArgumentException("'" + path.getAbsolutePath()
                    + "' is not a directory.");
        }
        if (!path.canWrite()) {
            throw new IllegalArgumentException("Cannot write to '"
                    + path.getAbsolutePath() + "'.");
        }
        return path;
    }

    private int parseRegistryPortNumber(String[] args) {
        if (args.length < 2) {
            return DEFAULT_REGISTRY_PORT_NUMBER;
        } else {
            try {
                return Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("'" + args[1]
                        + "' is not a valid integer.");
            }
        }
    }

    private int parseServerPortNumber(String[] args) {
        if (args.length < 3) {
            return DEFAULT_SERVER_PORT_NUMBER;
        } else {
            try {
                return Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("'" + args[2]
                        + "' is not a valid integer.");
            }
        }
    }

    private LogLevel parseLogLevel(String[] args) {
        if (args.length < 4) {
            return LogLevel.WARN;
        } else {
            try {
                return LogLevel.valueOf(args[3]);
            } catch (LogLevelFormatException e) {
                throw new IllegalArgumentException("'" + args[3]
                        + "' is not a valid log level.");
            }
        }
    }

    public File getDirectoryPath() {
        return directoryPath;
    }

    public int getRegistryPortNumber() {
        return registryPortNumber;
    }

    public int getServerPortNumber() {
        return serverPortNumber;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

}
