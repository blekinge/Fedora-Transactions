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

package fedora.client.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.util.Properties;

import fedora.common.Constants;

/**
 * Auto Batch Build Ingest.
 * 
 * @author Ross Wayland
 */
public class AutoBatchBuildIngest
        implements Constants {

    private final Properties batchProperties = new Properties();

    public AutoBatchBuildIngest(String objectTemplate,
                                String objectSpecificDir,
                                String objectDir,
                                String logFile,
                                String logFormat,
                                String objectFormat,
                                String host,
                                String port,
                                String username,
                                String password,
                                String protocol)
            throws Exception {

        batchProperties.setProperty("merge-objects", "yes");
        batchProperties.setProperty("ingest", "yes");
        batchProperties.setProperty("template", objectTemplate);
        batchProperties.setProperty("specifics", objectSpecificDir);
        batchProperties.setProperty("objects", objectDir);
        batchProperties.setProperty("ingested-pids", logFile);
        batchProperties.setProperty("pids-format", logFormat);
        batchProperties.setProperty("object-format", objectFormat);
        batchProperties.setProperty("server-fqdn", host);
        batchProperties.setProperty("server-port", port);
        batchProperties.setProperty("username", username);
        batchProperties.setProperty("password", password);
        batchProperties.setProperty("server-protocol", protocol);

        BatchTool batchTool = new BatchTool(batchProperties, null, null);
        batchTool.prep();
        batchTool.process();
    }

    public static final void main(String[] args) throws Exception {
        boolean errors = false;
        String objectFormat = null;
        if (args.length == 9) {
            if (!new File(args[0]).exists() && !new File(args[0]).isFile()) {
                System.out.println("Specified object template file path: \""
                        + args[0] + "\" does not exist.");
                errors = true;
            }
            if (!new File(args[1]).isDirectory()) {
                System.out.println("Specified object specific directory: \""
                        + args[1] + "\" is not directory.");
                errors = true;
            }
            if (!new File(args[2]).isDirectory()) {
                System.out.println("Specified object directory: \"" + args[2]
                        + "\" is not a directory.");
                errors = true;
            }
            if (!args[4].equals("xml") && !args[4].equals("text")) {
                System.out
                        .println("Format for log file must must be either: \""
                                + "\"xml\"  or  \"txt\"");
                errors = true;
            }
            String[] server = args[5].split(":");
            if (server.length != 2) {
                System.out.println("Specified server name does not specify "
                        + "port number: \"" + args[5] + "\" .");
                errors = true;
            }

            if (!args[8].equals("http") && !args[8].equals("https")) {
                System.out.println("Protocl must be either: \""
                        + "\"http\"  or  \"https\"");
                errors = true;
            }

            // Verify format of template file to see if it is a METS or FOXML template
            BufferedReader br = new BufferedReader(new FileReader(args[0]));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                if (line.indexOf("<foxml:") != -1) {
                    objectFormat = FOXML1_1.uri;
                    break;
                }
                if (line.indexOf("<METS:") != -1) {
                    objectFormat = METS_EXT1_1.uri;
                    break;
                }
            }
            br.close();
            br = null;

            if (objectFormat == null) {
                errors = true;
            }
            if (!errors) {
                System.out.println("\n*** Format of template files is: "
                        + objectFormat + " . Generated objects will be in "
                        + objectFormat + " format.\n");
                AutoBatchBuildIngest autoBatch =
                        new AutoBatchBuildIngest(args[0],
                                                 args[1],
                                                 args[2],
                                                 args[3],
                                                 args[4],
                                                 objectFormat,
                                                 server[0],
                                                 server[1],
                                                 args[6],
                                                 args[7],
                                                 args[8]);
            }
        } else {
            if (objectFormat == null && args.length == 9) {
                System.out.println("\nUnknown format for template file.\n"
                        + "Template file must either be METS or FOXML.\n");
            } else {
                System.out.println("\n**** Wrong Number of Arguments *****\n");
                System.out
                        .println("AutoBatchBuildIngest requires 9 arguments.");
                System.out.println("(1) - full path to object template file");
                System.out
                        .println("(2) - full path to object specific directory");
                System.out.println("(3) - full path to object directory");
                System.out.println("(4) - full path to log file");
                System.out.println("(5) - format of log file (xml or text)\n");
                System.out
                        .println("(6) - host name and port of Fedora server (host:port)");
                System.out.println("(7) - admin username of Fedora server");
                System.out
                        .println("(8) - password for admin user of Fedora server\n");
                System.out
                        .println("(9) - protocol to communicate with Fedora server (http or https)");
            }
        }
    }
}
