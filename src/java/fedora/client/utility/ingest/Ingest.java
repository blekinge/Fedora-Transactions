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

package fedora.client.utility.ingest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;

import java.util.Arrays;
import java.util.StringTokenizer;

import fedora.client.FedoraClient;
import fedora.client.utility.AutoFinder;
import fedora.client.utility.export.AutoExporter;

import fedora.common.Constants;

import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.ComparisonOperator;
import fedora.server.types.gen.Condition;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.FieldSearchResult;
import fedora.server.types.gen.ObjectFields;
import fedora.server.types.gen.RepositoryInfo;

import fedora.utilities.FileComparator;

/**
 * Initiates ingest of one or more objects. This class provides static utility
 * methods, and it is also called by command line utilities.
 * 
 * @version $Id$
 */
public class Ingest
        implements Constants {

    public static String LAST_PATH;

    private static FileComparator _FILE_COMPARATOR = new FileComparator();

    // if logMessage is null, will use original path in logMessage
    public static String oneFromFile(File file,
                                     String ingestFormat,
                                     FedoraAPIA targetRepoAPIA,
                                     FedoraAPIM targetRepoAPIM,
                                     String logMessage) throws Exception {
        LAST_PATH = file.getPath();
        String pid =
                AutoIngestor.ingestAndCommit(targetRepoAPIA,
                                             targetRepoAPIM,
                                             new FileInputStream(file),
                                             ingestFormat,
                                             getMessage(logMessage, file));
        return pid;
    }

    /***************************************************************************
     * Ingest from directory
     **************************************************************************/

    public static void multiFromDirectory(File dir,
                                           String ingestFormat,
                                           FedoraAPIA targetRepoAPIA,
                                           FedoraAPIM targetRepoAPIM,
                                           String logMessage,
                                           PrintStream log,
                                           IngestCounter c) throws Exception {
        File[] files = dir.listFiles();
        if (files == null) {
            throw new RuntimeException("Could not read files from directory " + dir.getPath());
        }
        
        Arrays.sort(files, _FILE_COMPARATOR);
        for (File element : files) {
            if (!element.isHidden() && !element.getName().startsWith(".")) {
                if (element.isDirectory()) {
                    multiFromDirectory(element,
                                       ingestFormat,
                                       targetRepoAPIA,
                                       targetRepoAPIM,
                                       logMessage,
                                       log,
                                       c);
                } else {
                    try {
                        String pid =
                                oneFromFile(element,
                                            ingestFormat,
                                            targetRepoAPIA,
                                            targetRepoAPIM,
                                            logMessage);
                        c.successes++;
                        IngestLogger.logFromFile(log, element, pid);
                    } catch (Exception e) {
                        // failed... just log it and continue
                        c.failures++;
                        IngestLogger.logFailedFromFile(log, element, e);
                    }
                }
            }
        }
    }

    private static boolean matches(File file, String searchString)
            throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(file));
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.indexOf(searchString) != -1) {
                    return true;
                }
            }
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
        return false;
    }

    /***************************************************************************
     * Ingest from repository
     **************************************************************************/

    // if logMessage is null, will make informative one up
    public static String oneFromRepository(FedoraAPIA sourceRepoAPIA,
                                           FedoraAPIM sourceRepoAPIM,
                                           String sourceExportFormat,
                                           String pid,
                                           FedoraAPIA targetRepoAPIA,
                                           FedoraAPIM targetRepoAPIM,
                                           String logMessage) throws Exception {

        // EXPORT from source repository
        // The export context is set to "migrate" since the intent
        // of ingest from repository is to migrate an object from
        // one repository to another.  The "migrate" option will
        // ensure that URLs that were relative to the "exporting"
        // repository are made relative to the "importing" repository.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        AutoExporter.export(sourceRepoAPIA,
                            sourceRepoAPIM,
                            pid,
                            sourceExportFormat,
                            "migrate",
                            out);

        // Convert old format values to URIs for ingest
        String ingestFormat = sourceExportFormat;
        if (sourceExportFormat.equals(METS_EXT1_0_LEGACY)) {
            ingestFormat = METS_EXT1_0.uri;
        } else if (sourceExportFormat.equals(FOXML1_0_LEGACY)) {
            ingestFormat = FOXML1_0.uri;
        }

        // INGEST into target repository
        String realLogMessage = logMessage;
        if (realLogMessage == null) {
            realLogMessage = "Ingested from source repository with pid " + pid;
        }
        return AutoIngestor.ingestAndCommit(targetRepoAPIA,
                                            targetRepoAPIM,
                                            new ByteArrayInputStream(out
                                                    .toByteArray()),
                                            ingestFormat,
                                            realLogMessage);
    }

    public static void multiFromRepository(String sourceProtocol,
                                           String sourceHost,
                                           int sourcePort,
                                           FedoraAPIA sourceRepoAPIA,
                                           FedoraAPIM sourceRepoAPIM,
                                           String sourceExportFormat,
                                           FedoraAPIA targetRepoAPIA,
                                           FedoraAPIM targetRepoAPIM,
                                           String logMessage,
                                           PrintStream log,
                                           IngestCounter c) throws Exception {
        // prepare the FieldSearch query
        FieldSearchQuery query = new FieldSearchQuery();
        Condition cond = new Condition();
        cond.setProperty("pid");
        cond.setOperator(ComparisonOperator.fromValue("has"));
        Condition[] conditions = new Condition[1];
        conditions[0] = cond;
        query.setConditions(conditions);
        query.setTerms(null);

        String[] resultFields = new String[1];
        resultFields[0] = "pid";

        // get the first chunk of search results
        FieldSearchResult result =
                AutoFinder
                        .findObjects(sourceRepoAPIA, resultFields, 100, query);

        while (result != null) {

            ObjectFields[] ofs = result.getResultList();

            // ingest all objects from this chunk of search results
            for (ObjectFields element : ofs) {
                String pid = element.getPid();
                try {
                    String newPID =
                            oneFromRepository(sourceRepoAPIA,
                                              sourceRepoAPIM,
                                              sourceExportFormat,
                                              pid,
                                              targetRepoAPIA,
                                              targetRepoAPIM,
                                              logMessage);
                    c.successes++;
                    IngestLogger.logFromRepos(log, pid, newPID);
                } catch (Exception e) {
                    // failed... just log it and continue
                    c.failures++;
                    IngestLogger.logFailedFromRepos(log, pid, e);
                }
            }

            // get the next chunk of search results, if any
            String token = null;
            try {
                token = result.getListSession().getToken();
            } catch (Throwable th) {
            }

            if (token != null) {
                result = AutoFinder.resumeFindObjects(sourceRepoAPIA, token);
            } else {
                result = null;
            }
        }

    }

    /**
     * Determine the default export format of the source repository.
     * For backward compatibility:
     *   with pre-2.0 repositories assume the "metslikefedora1" format
     */
    public static String getExportFormat(RepositoryInfo repoinfo) throws Exception {
        String sourceExportFormat = null;
        StringTokenizer stoken =
            new StringTokenizer(repoinfo.getRepositoryVersion(), ".");
        int majorVersion = new Integer(stoken.nextToken()).intValue();
        if (majorVersion < 2) {
            sourceExportFormat = METS_EXT1_0_LEGACY;
        } else {
            sourceExportFormat = repoinfo.getDefaultExportFormat();
        }

        return sourceExportFormat;
    }

    private static String getMessage(String logMessage, File file) {
        if (logMessage != null) {
            return logMessage;
        }
        return "Ingested from local file " + file.getPath();
    }

    // FIXME: this isn't ingest-specific... it doesn't belong here
    public static String getDuration(long millis) {
        long tsec = millis / 1000;
        long h = tsec / 60 / 60;
        long m = (tsec - h * 60 * 60) / 60;
        long s = tsec - h * 60 * 60 - m * 60;
        StringBuffer out = new StringBuffer();
        if (h > 0) {
            out.append(h + " hour");
            if (h > 1) {
                out.append('s');
            }
        }
        if (m > 0) {
            if (h > 0) {
                out.append(", ");
            }
            out.append(m + " minute");
            if (m > 1) {
                out.append('s');
            }
        }
        if (s > 0 || h == 0 && m == 0) {
            if (h > 0 || m > 0) {
                out.append(", ");
            }
            out.append(s + " second");
            if (s != 1) {
                out.append('s');
            }
        }
        return out.toString();
    }

    /**
     * Print error message and show usage for command-line interface.
     */
    public static void badArgs(String msg) {
        System.err.println("Command: fedora-ingest");
        System.err.println();
        System.err.println("Summary: Ingests one or more objects into a Fedora repository, from either");
        System.err.println("         the local filesystem or another Fedora repository.");
        System.err.println();
        System.err.println("Syntax:");
        System.err.println("  fedora-ingest f[ile] INPATH FORMAT THST:TPRT TUSR TPSS TPROTOCOL [LOG]");
        System.err.println("  fedora-ingest d[ir] INPATH FORMAT THST:TPRT TUSR TPSS TPROTOCOL [LOG]");
        System.err.println("  fedora-ingest r[epos] SHST:SPRT SUSR SPSS PID|* THST:TPRT TUSR TPSS SPROTOCOL TPROTOCOL [LOG]");
        System.err.println();
        System.err.println("Where:");
        System.err.println("  INPATH     is the local file or directory name that is ingest source.");
        System.err.println("  FORMAT     is a string value which indicates the XML format of the ingest file(s)");
        System.err.println("             ('" + FOXML1_1.uri + "',");
        System.err.println("              '" + FOXML1_0.uri + "',");
        System.err.println("              '" + METS_EXT1_1.uri + "',");
        System.err.println("              '" + METS_EXT1_0.uri + "',");
        System.err.println("              '" + ATOM1_1.uri + "',");
        System.err.println("              or '" + ATOM_ZIP1_1.uri + "')");
        System.err.println("  PID        is the id of the object to ingest from the source repository.");
        System.err.println("  SHST/THST  is the source or target repository's hostname.");
        System.err.println("  SPRT/TPRT  is the source or target repository's port number.");
        System.err.println("  SUSR/TUSR  is the id of the source or target repository user.");
        System.err.println("  SPSS/TPSS  is the password of the source or target repository user.");
        System.err.println("  SPROTOCOL  is the protocol to communicate with source repository (http or https)");
        System.err.println("  TPROTOCOL  is the protocol to communicate with target repository (http or https)");
        System.err.println("  LOG        is the optional log message.  If unspecified, the log message");
        System.err.println("             will indicate the source filename or repository of the object(s).");
        System.err.println();
        System.err.println("Examples:");
        System.err.println("fedora-ingest f obj1.xml " + FOXML1_1.uri + " myrepo.com:8443 jane jpw https");
        System.err.println();
        System.err.println("  Ingests obj1.xml (encoded in FOXML 1.1 format) from the");
        System.err.println("  current directory into the repository at myrepo.com:80");
        System.err.println("  as user 'jane' with password 'jpw' using the secure https protocol (SSL).");
        System.err.println("  The logmessage will be system-generated, indicating");
        System.err.println("  the source path+filename.");
        System.err.println();
        System.err.println("fedora-ingest d c:\\archive " + FOXML1_1.uri + " myrepo.com:80 jane janepw http \"\"");
        System.err.println();
        System.err.println("  Traverses entire directory structure of c:\\archive, and ingests any file.");
        System.err.println("  It assumes all files will be in the FOXML 1.1 format");
        System.err.println("  and will fail on ingests of files that are not of this format.");
        System.err.println("  All log messages will be the quoted string.");
        System.err.println();
        System.err.println("fedora-ingest r jrepo.com:8081 mike mpw demo:1 myrepo.com:8443 jane jpw http https \"\"");
        System.err.println();
        System.err.println("  Ingests the object whose pid is 'demo:1' from the source repository");
        System.err.println("  'srcrepo.com:8081' into the target repository 'myrepo.com:80'.");
        System.err.println("  The object will be exported from the source repository in the default");
        System.err.println("  export format configured at the source.");
        System.err.println("  All log messages will be empty.");
        System.err.println();
        System.err.println("fedora-ingest r jrepo.com:8081 mike mpw O myrepo.com:8443 jane jpw http https \"\"");
        System.err.println();
        System.err.println("  Same as above, but ingests all data objects (type O).");
        System.err.println();
        System.err.println("ERROR  : " + msg);
        System.exit(1);
    }

    private static void summarize(IngestCounter counter, File logFile) {
        System.out.println();
        if (counter.failures > 0) {
            System.out.println("WARNING: " + counter.failures + " of "
                    + counter.getTotal() + " objects failed.  Check log.");
        } else {
            System.out.println("SUCCESS: All " + counter.getTotal()
                    + " objects were ingested.");
        }
        System.out.println();
        System.out.println("A detailed log is at " + logFile.getPath());
    }

    /**
     * Command-line interface for doing ingests.
     */
    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                Ingest.badArgs("No arguments entered!");
            }
            PrintStream log = null;
            File logFile = null;
            String logRootName = null;
            IngestCounter counter = new IngestCounter();
            char kind = args[0].toLowerCase().charAt(0);
            if (kind == 'f') {
                // USAGE: fedora-ingest f[ile] INPATH FORMAT THST:TPRT TUSR TPSS PROTOCOL [LOG]
                if (args.length < 7 || args.length > 8) {
                    Ingest
                            .badArgs("Wrong number of arguments for file ingest.");
                    System.out
                            .println("USAGE: fedora-ingest f[ile] INPATH FORMAT THST:TPRT TUSR TPSS PROTOCOL [LOG]");
                }
                File f = new File(args[1]);
                String ingestFormat = args[2];
                String logMessage = null;
                if (args.length == 8) {
                    logMessage = args[7];
                }

                String protocol = args[6];
                String[] hp = args[3].split(":");

                // ******************************************
                // NEW: use new client utility class
                // FIXME:  Get around hardcoding the path in the baseURL
                String baseURL =
                        protocol + "://" + hp[0] + ":"
                                + Integer.parseInt(hp[1]) + "/fedora";
                FedoraClient fc = new FedoraClient(baseURL, args[4], args[5]);
                FedoraAPIA targetRepoAPIA = fc.getAPIA();
                FedoraAPIM targetRepoAPIM = fc.getAPIM();
                //*******************************************

                String pid =
                        Ingest.oneFromFile(f,
                                           ingestFormat,
                                           targetRepoAPIA,
                                           targetRepoAPIM,
                                           logMessage);
                if (pid == null) {
                    System.out.print("ERROR: ingest failed for file: "
                            + args[1]);
                } else {
                    System.out.println("Ingested PID: " + pid);
                }
            } else if (kind == 'd') {
                // USAGE: fedora-ingest d[ir] INPATH FORMAT THST:TPRT TUSR TPSS PROTOCOL [LOG]
                if (args.length < 7 || args.length > 8) {
                    Ingest.badArgs("Wrong number of arguments (" + args.length
                            + ") for directory ingest.");
                    System.out
                            .println("USAGE: fedora-ingest d[ir] INPATH FORMAT THST:TPRT TUSR TPSS PROTOCOL [LOG]");
                }
                File d = new File(args[1]);
                String ingestFormat = args[2];
                String logMessage = null;
                if (args.length == 8) {
                    logMessage = args[7];
                }

                String protocol = args[6];
                String[] hp = args[3].split(":");

                // ******************************************
                // NEW: use new client utility class
                // FIXME:  Get around hardcoding the path in the baseURL
                String baseURL =
                        protocol + "://" + hp[0] + ":"
                                + Integer.parseInt(hp[1]) + "/fedora";
                FedoraClient fc = new FedoraClient(baseURL, args[4], args[5]);
                FedoraAPIA targetRepoAPIA = fc.getAPIA();
                FedoraAPIM targetRepoAPIM = fc.getAPIM();
                //*******************************************

                logRootName = "ingest-from-dir";
                logFile = IngestLogger.newLogFile(logRootName);
                log =
                        new PrintStream(new FileOutputStream(logFile),
                                        true,
                                        "UTF-8");
                IngestLogger.openLog(log, logRootName);
                Ingest.multiFromDirectory(d,
                                          ingestFormat,
                                          targetRepoAPIA,
                                          targetRepoAPIM,
                                          logMessage,
                                          log,
                                          counter);
                IngestLogger.closeLog(log, logRootName);
                summarize(counter, logFile);
            } else if (kind == 'r') {
                // USAGE: fedora-ingest r[epos] SHST:SPRT SUSR SPSS PID|* THST:TPRT TUSR TPSS SPROTOCOL TPROTOCOL [LOG]
                if (args.length < 10 || args.length > 11) {
                    Ingest
                            .badArgs("Wrong number of arguments for repository ingest.");
                }
                String logMessage = null;
                if (args.length == 11) {
                    logMessage = args[10];
                }
                //Source repository
                String[] shp = args[1].split(":");
                String source_host = shp[0];
                String source_port = shp[1];
                String source_user = args[2];
                String source_password = args[3];
                String source_protocol = args[8];

                // ******************************************
                // NEW: use new client utility class
                // FIXME:  Get around hardcoding the path in the baseURL
                String sourceBaseURL =
                        source_protocol + "://" + source_host + ":"
                                + Integer.parseInt(source_port) + "/fedora";
                FedoraClient sfc =
                        new FedoraClient(sourceBaseURL,
                                         source_user,
                                         source_password);
                FedoraAPIA sourceRepoAPIA = sfc.getAPIA();
                FedoraAPIM sourceRepoAPIM = sfc.getAPIM();
                //*******************************************

                //Target repository
                String[] thp = args[5].split(":");
                String target_host = thp[0];
                String target_port = thp[1];
                String target_user = args[6];
                String target_password = args[7];
                String target_protocol = args[9];

                // ******************************************
                // NEW: use new client utility class
                // FIXME:  Get around hardcoding the path in the baseURL
                String targetBaseURL =
                        target_protocol + "://" + target_host + ":"
                                + Integer.parseInt(target_port) + "/fedora";
                FedoraClient tfc =
                        new FedoraClient(targetBaseURL,
                                         target_user,
                                         target_password);
                FedoraAPIA targetRepoAPIA = tfc.getAPIA();
                FedoraAPIM targetRepoAPIM = tfc.getAPIM();
                //*******************************************

                // Determine export format
                RepositoryInfo repoinfo = sourceRepoAPIA.describeRepository();
                System.out.println("Ingest: exporting from a source repo version "
                                 + repoinfo.getRepositoryVersion());
                String sourceExportFormat = getExportFormat(repoinfo);
                System.out.println("Ingest: source repo is using "
                        + sourceExportFormat + " export format.");

                if (args[4].indexOf(":") != -1) {
                    // single object
                    String successfulPID =
                            Ingest.oneFromRepository(sourceRepoAPIA,
                                                     sourceRepoAPIM,
                                                     sourceExportFormat,
                                                     args[4],
                                                     targetRepoAPIA,
                                                     targetRepoAPIM,
                                                     logMessage);
                    if (successfulPID == null) {
                        System.out
                                .print("ERROR: ingest from repo failed for PID="
                                        + args[4]);
                    } else {
                        System.out.println("Ingested PID: " + successfulPID);
                    }
                } else {
                    // multi-object
                    //hp=args[1].split(":");
                    logRootName = "ingest-from-repository";
                    logFile = IngestLogger.newLogFile(logRootName);
                    log =
                            new PrintStream(new FileOutputStream(logFile),
                                            true,
                                            "UTF-8");
                    IngestLogger.openLog(log, logRootName);
                    Ingest.multiFromRepository(source_protocol,
                                               source_host,
                                               Integer.parseInt(source_port),
                                               sourceRepoAPIA,
                                               sourceRepoAPIM,
                                               sourceExportFormat,
                                               targetRepoAPIA,
                                               targetRepoAPIM,
                                               logMessage,
                                               log,
                                               counter);
                    IngestLogger.closeLog(log, logRootName);
                    summarize(counter, logFile);
                }

            } else {
                Ingest.badArgs("First argument must start with f, d, or r.");
            }
        } catch (Exception e) {
            System.err.print("Error  : ");
            if (e.getMessage() == null) {
                e.printStackTrace();
            } else {
                System.err.print(e.getMessage());
            }
            System.err.println();
            if (Ingest.LAST_PATH != null) {
                System.out.println("(Last attempted file was "
                        + Ingest.LAST_PATH + ")");
            }
        }
    }

}
