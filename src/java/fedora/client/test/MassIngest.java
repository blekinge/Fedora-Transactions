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

package fedora.client.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;

import fedora.client.FedoraClient;
import fedora.client.utility.ingest.AutoIngestor;

import fedora.oai.sample.RandomDCMetadataFactory;

import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;

/**
 * @author Chris Wilper
 */
public class MassIngest {

    public static FedoraAPIA APIA = null;

    public static FedoraAPIM APIM = null;

    public MassIngest(AutoIngestor ingestor,
                      File templateFile,
                      File dictFile,
                      String format,
                      int numTimes)
            throws Exception {
        // load the template file into two parts... with splitter=##SPLITTER##
        BufferedReader in = new BufferedReader(new FileReader(templateFile));
        String nextLine = "";
        StringBuffer startBuffer = new StringBuffer();
        StringBuffer endBuffer = new StringBuffer();
        boolean seenSplitter = false;
        while (nextLine != null) {
            nextLine = in.readLine();
            if (nextLine != null) {
                if (!seenSplitter) {
                    if (nextLine.startsWith("##SPLITTER##")) {
                        seenSplitter = true;
                    } else {
                        startBuffer.append(nextLine + "\n");
                    }
                } else {
                    endBuffer.append(nextLine + "\n");
                }
            }
        }
        in.close();
        String start = startBuffer.toString();
        String end = endBuffer.toString();
        RandomDCMetadataFactory dcFactory =
                new RandomDCMetadataFactory(dictFile);
        for (int i = 0; i < numTimes; i++) {
            String xml = start + dcFactory.get(2, 13) + end;
            String pid =
                    ingestor
                            .ingestAndCommit(new ByteArrayInputStream(xml
                                                     .getBytes("UTF-8")),
                                             format,
                                             "part of massingest of "
                                                     + numTimes
                                                     + " auto-generated objects.");
            int t = i + 1;
            System.out.println(pid + " " + t + "/" + numTimes);
        }

    }

    public static void showUsage(String message) {
        System.out.println("ERROR: " + message);
        System.out
                .println("Usage: MassIngest host port username password templateFile dictionaryFile format numTimes protocol");
    }

    public static void main(String[] args) throws Exception {
        try {
            if (args.length != 9) {
                MassIngest.showUsage("You must provide nine arguments.");
            } else {
                String hostName = args[0];
                int portNum = Integer.parseInt(args[1]);
                String username = args[2];
                String password = args[3];
                File dictFile = new File(args[5]);
                String format = args[6];
                // third arg==file... must exist
                File f = new File(args[4]);
                String protocol = args[8];

                // ******************************************
                // NEW: use new client utility class
                // FIXME:  Get around hardcoding the path in the baseURL
                String baseURL =
                        protocol + "://" + hostName + ":" + portNum + "/fedora";
                FedoraClient fc = new FedoraClient(baseURL, username, password);
                APIA = fc.getAPIA();
                APIM = fc.getAPIM();
                //*******************************************
                AutoIngestor autoIngestor = new AutoIngestor(APIA, APIM);
                MassIngest m =
                        new MassIngest(autoIngestor,
                                       f,
                                       dictFile,
                                       format,
                                       Integer.parseInt(args[7]));
            }
        } catch (Exception e) {
            MassIngest.showUsage(e.getClass().getName()
                    + " - "
                    + (e.getMessage() == null ? "(no detail provided)" : e
                            .getMessage()));
        }
    }

}
