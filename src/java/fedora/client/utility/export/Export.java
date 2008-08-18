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

package fedora.client.utility.export;

import java.io.File;
import java.io.FileOutputStream;

import java.util.StringTokenizer;

import fedora.client.FedoraClient;
import fedora.client.utility.AutoFinder;

import fedora.common.Constants;

import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.FieldSearchResult;
import fedora.server.types.gen.ObjectFields;
import fedora.server.types.gen.RepositoryInfo;

/**
 * Utility to initiate an export of one or more objects. This class provides
 * static utility methods, and it is also called by command line utilities. This
 * class calls AutoExporter.class which is responsible for making the API-M SOAP
 * calls for the export.
 * 
 * @version $Id$
 */
public class Export
        implements Constants {

    // FIXME: this isn't export-specific... it doesn't belong here
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

    public static void one(FedoraAPIA apia,
                           FedoraAPIM apim,
                           String pid,
                           String format,
                           String exportContext,
                           File dir) throws Exception {
        String suffix;
        if (format.equals(ATOM_ZIP1_1.uri)) {
            suffix = ".zip";
        } else {
            suffix = ".xml";
        }
        String fName = pid.replaceAll(":", "_") + suffix;
        File file = new File(dir, fName);
        System.out.println("Exporting " + pid + " to " + file.getPath());
        AutoExporter.export(apia,
                            apim,
                            pid,
                            format,
                            exportContext,
                            new FileOutputStream(file));
    }

    public static int multi(FedoraAPIA apia,
                            FedoraAPIM apim,
                            String format,
                            String exportContext,
                            File dir) throws Exception {
        int count = 0;

        // prepare the FieldSearch query
        FieldSearchQuery query = new FieldSearchQuery();
        query.setTerms(null);

        String[] resultFields = new String[1];
        resultFields[0] = "pid";

        // get the first chunk of search results

        FieldSearchResult result =
                AutoFinder.findObjects(apia, resultFields, 100, query);

        while (result != null) {

            ObjectFields[] ofs = result.getResultList();

            // export all objects from this chunk of search results
            for (ObjectFields element : ofs) {
                String pid = element.getPid();
                one(apia, apim, pid, format, exportContext, dir);
                count++;
            }

            // get the next chunk of search results, if any
            String token = null;
            try {
                token = result.getListSession().getToken();
            } catch (Throwable th) {
            }

            if (token != null) {
                result = AutoFinder.resumeFindObjects(apia, token);
            } else {
                result = null;
            }
        }

        return count;
    }

    /**
     * Print error message and show usage for command-line interface.
     */
    public static void badArgs(String msg) {
        System.err.println("Command: fedora-export");
        System.err.println();
        System.err.println("Summary: Exports one or more objects from a Fedora repository.");
        System.err.println();
        System.err.println("Syntax:");
        System.err.println("  fedora-export HST:PRT USR PSS PID|FTYPS FORMAT ECONTEXT PATH PROTOCOL");
        System.err.println();
        System.err.println("Where:");
        System.err.println("  HST    is the repository hostname.");
        System.err.println("  PRT    is the repository port number.");
        System.err.println("  USR    is the id of the repository user.");
        System.err.println("  PSS    is the password of repository user.");
        System.err.println("  PID    is the id of the object to export from the source repository.");
        System.err.println("  FORMAT is the XML format to export ");
        System.err.println("         ('" + FOXML1_1.uri + "',");
        System.err.println("          '" + FOXML1_0.uri + "',");
        System.err.println("          '" + METS_EXT1_1.uri + "',");
        System.err.println("          '" + METS_EXT1_0.uri + "',");
        System.err.println("          '" + ATOM1_1.uri + "',");
        System.err.println("          '" + ATOM_ZIP1_1.uri + "',");
        System.err.println("          or 'default')");
        System.err.println("  ECONTEXT is the export context (which indicates what use case");
        System.err.println("         the output should be prepared for.");
        System.err.println("         ('public', 'migrate', 'archive' or 'default')");
        System.err.println("  PATH   is the directory to export the object.");
        System.err.println("  PROTOCOL is the how to connect to repository, either http or https.");
        System.err.println();
        System.err.println("Examples:");
        System.err.println("fedora-export myrepo.com:8443 user pw demo:1 " + FOXML1_1.uri + " migrate . https");
        System.err.println();
        System.err.println("  Exports demo:1 for migration in FOXML format ");
        System.err.println("  using the secure https protocol (SSL).");
        System.err.println("  (from myrepo.com:80 to the current directory).");
        System.err.println();
        System.err.println("fedora-export myrepo.com:80 user pw DMO default default /tmp/fedoradump http");
        System.err.println();
        System.err.println("  Exports all objects in the default export format and context ");
        System.err.println("  (from myrepo.com:80 to directory /tmp/fedoradump).");
        System.err.println();
        System.err.println("ERROR  : " + msg);
        System.exit(1);
    }

    /**
     * Command-line interface for doing exports.
     */
    public static void main(String[] args) {
        try {
            // USAGE: fedora-export HST:PRT USR PSS PID|FTYPS FORMAT ECONTEXT PATH PROTOCOL
            if (args.length != 8) {
                Export.badArgs("Wrong number of arguments.");
            }
            String[] hp = args[0].split(":");
            if (hp.length != 2) {
                Export.badArgs("First arg must be of the form 'host:portnum'");
            }
            //SDP - HTTPS
            String protocol = args[7];
            if (!protocol.equals("http") && !protocol.equals("https")) {
                Export.badArgs("PROTOCOL arg must be 'http' or 'https'");
            }

            // ******************************************
            // NEW: use new client utility class
            // FIXME:  Get around hardcoding the path in the baseURL
            String baseURL =
                    protocol + "://" + hp[0] + ":" + Integer.parseInt(hp[1])
                            + "/fedora";
            FedoraClient fc = new FedoraClient(baseURL, args[1], args[2]);
            FedoraAPIA sourceRepoAPIA = fc.getAPIA();
            FedoraAPIM sourceRepoAPIM = fc.getAPIM();
            //*******************************************

            String exportFormat = args[4];
            String exportContext = args[5];
            if (!exportFormat.equals(FOXML1_1.uri)
                && !exportFormat.equals(FOXML1_0.uri)
                && !exportFormat.equals(METS_EXT1_1.uri)
                && !exportFormat.equals(METS_EXT1_0.uri)
                && !exportFormat.equals(ATOM1_1.uri)
                && !exportFormat.equals(ATOM_ZIP1_1.uri)
                && !exportFormat.equals("default")) {
                    Export.badArgs(exportFormat +" is not a valid export format.");
            }
            if (!exportContext.equals("public")
                    && !exportContext.equals("migrate")
                    && !exportContext.equals("archive")
                    && !exportContext.equals("default")) {
                Export.badArgs("ECONTEXT arg must be 'public', 'migrate', 'archive', or 'default'");
            }

            RepositoryInfo repoinfo = sourceRepoAPIA.describeRepository();
            StringTokenizer stoken =
                    new StringTokenizer(repoinfo.getRepositoryVersion(), ".");
            int majorVersion = new Integer(stoken.nextToken()).intValue();
            if (majorVersion < 2 // pre-2.0 repo
                && !exportFormat.equals(METS_EXT1_0.uri)
                && !exportFormat.equals("default")) {
                Export.badArgs("FORMAT arg must be '" + METS_EXT1_0.uri
                        + "' or 'default' for pre-2.0 repository.");
            }

            if (exportFormat.equals("default")) {
                exportFormat = null;
            }
            if (exportContext.equals("default")) {
                exportContext = null;
            }
            if (args[3].indexOf(":") == -1) {
                // assume args[3] is FTYPS... so multi-export
                int count =
                        Export.multi(sourceRepoAPIA, sourceRepoAPIM,
                                     exportFormat,
                                     exportContext,
                                     //args[4], // format
                                     //args[5], // export context
                                     new File(args[6])); // path
                System.out.print("Exported " + count + " objects.");
            } else {
                // assume args[3] is a PID...they only want to export one object

                Export.one(sourceRepoAPIA, sourceRepoAPIM, args[3], // PID
                           exportFormat,
                           exportContext,
                           //args[4], // format
                           //args[5], // export context
                           new File(args[6])); // path
                System.out.println("Exported " + args[3]);
            }
        } catch (Exception e) {
            System.err.print("Error  : ");
            if (e.getMessage() == null) {
                e.printStackTrace();
            } else {
                System.err.print(e.getMessage());
            }
        }
    }

}
