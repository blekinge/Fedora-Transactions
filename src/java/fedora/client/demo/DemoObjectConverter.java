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

package fedora.client.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Goes through the Fedora demo objects directory and changes all occurrences
 * of strings for protocol, hostname and port number to values supplied by 
 * the user in the calling arguments. This utility is used to convert the 
 * original Fedora demo objects so that they will function correctly when 
 * the Fedora server is configured to run on a protocol, host, or port other
 * than the defaults.
 * 
 * @author Ross Wayland
 */
public class DemoObjectConverter {

    private static String fromProtocol = null;

    private static String fromHostName = null;

    private static String fromPortNum = null;

    private static String toProtocol = null;

    private static String toHostName = null;

    private static String toPortNum = null;

    /**
     * Constructor for DemoObjectConverter. Initializes class variables for
     * hostname, port number and fedoraHome.
     * 
     * @param fromProtocol
     *        The protocol for the URL to be changed from.
     * @param fromHostName
     *        The host name to be changed from.
     * @param fromPortNum
     *        The port number to be changed from.
     * @param toProtocol
     *        The protocol for the URL to be changed to.
     * @param toHostName
     *        The host name to be changed to.
     * @param toPortNum
     *        The port number ot be changed to.
     * @param fedoraHome
     *        The installation directory for Fedora.
     */
    public DemoObjectConverter(String fromProtocol,
                               String fromHostName,
                               String fromPortNum,
                               String toProtocol,
                               String toHostName,
                               String toPortNum,
                               String fedoraHome) {
        DemoObjectConverter.toProtocol = toProtocol;
        DemoObjectConverter.toHostName = toHostName;
        DemoObjectConverter.toPortNum = toPortNum;
        DemoObjectConverter.fromProtocol = fromProtocol;
        DemoObjectConverter.fromHostName = fromHostName;
        DemoObjectConverter.fromPortNum = fromPortNum;

    }

    /**
     * Converts all Fedora demo objects by substituting hostname and port numer
     * supplied in calling arguments.
     * 
     * @param fedoraHome
     *        The location Fedora is installed.
     */
    public static void convert(String fedoraHome) {
        // Define the Fedora demo directory.
        File demoDir = new File(fedoraHome, "client/demo");
        if (demoDir.exists()) {
            getFiles(demoDir);
        } else {
            System.out
                    .println("ERROR: Unable to locate Demo Object Directory: "
                            + demoDir.toString());
        }
    }

    /**
     * Recursively traverse the specified directory and process each file.
     * 
     * @param dir
     *        The directory to be traversed.
     */
    public static void getFiles(File dir) {
        File[] files = dir.listFiles();
        for (File element : files) {
            if (element.isDirectory()) {
                // Collect files to be scanned.
                getFiles(element);
            } else {
                // Make hostname and port number substitutions.
                substitute(element);
            }
        }

    }

    /**
     * Substitute the protocol, hostname and port number supplied in calling
     * arguments with those found in Fedora demo objects. Replaces the original
     * file with the edited version.
     * 
     * @param demoObject
     *        The Fedora demo object file to be edited.
     */
    public static void substitute(File demoObject) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(demoObject));
            String tempFile = demoObject.toString() + "-temp";
            OutputStream os = new FileOutputStream(new File(tempFile));
            OutputStreamWriter out = new OutputStreamWriter(os, "UTF-8");
            String nextLine = "";
            String newUrlStart;
            if (toProtocol.equalsIgnoreCase("http")
                    && (toPortNum.equals("") || toPortNum.equals("80"))) {
                newUrlStart = "http://" + toHostName + "/";
            } else if (toProtocol.equalsIgnoreCase("https")
                    && (toPortNum.equals("") || toPortNum.equals("443"))) {
                newUrlStart = "https://" + toHostName + "/";
            } else {
                newUrlStart =
                        toProtocol + "://" + toHostName + ":" + toPortNum + "/";
            }
            String a = fromProtocol + "://" + fromHostName;
            String fromURLStartNoPort = a + "/";
            String fromURLStartPort80 = a + ":80" + "/";
            String fromURLStartPort443 = a + ":443" + "/";
            String fromURLStartWithPort = a + ":" + fromPortNum + "/";

            if (fromProtocol.equalsIgnoreCase("http")
                    && (fromPortNum.equals("") || fromPortNum.equals("80"))) {
                System.out.println("searching for " + fromURLStartNoPort);
                System.out.println("searching for " + fromURLStartPort80);
                System.out.println("replacing with " + newUrlStart);
            } else if (fromProtocol.equalsIgnoreCase("https")
                    && (fromPortNum.equals("") || fromPortNum.equals("443"))) {
                System.out.println("searching for " + fromURLStartNoPort);
                System.out.println("searching for " + fromURLStartPort443);
                System.out.println("replacing with " + newUrlStart);
            } else {
                System.out.println("searching for " + fromURLStartWithPort);
                System.out.println("replacing with " + newUrlStart);
            }

            while (nextLine != null) {
                nextLine = in.readLine();
                if (nextLine != null) {
                    if (fromProtocol.equalsIgnoreCase("http")
                            && (fromPortNum.equals("") || fromPortNum
                                    .equals("80"))) {
                        nextLine =
                                nextLine.replaceAll(fromURLStartNoPort,
                                                    newUrlStart);
                        nextLine =
                                nextLine.replaceAll(fromURLStartPort80,
                                                    newUrlStart);
                    } else if (fromProtocol.equalsIgnoreCase("https")
                            && (fromPortNum.equals("") || fromPortNum
                                    .equals("443"))) {
                        nextLine =
                                nextLine.replaceAll(fromURLStartNoPort,
                                                    newUrlStart);
                        nextLine =
                                nextLine.replaceAll(fromURLStartPort443,
                                                    newUrlStart);
                    } else {
                        nextLine =
                                nextLine.replaceAll(fromURLStartWithPort,
                                                    newUrlStart);
                    }
                    out.write(nextLine + "\n");
                }
            }
            in.close();
            out.close();

            // Remove original file.
            if (demoObject.delete()) {
                File file = new File(tempFile);

                // Rename temp file with original file name.
                if (!file.renameTo(demoObject)) {
                    System.out.println("ERROR: unable to rename file: "
                            + demoObject);
                } else {
                    System.out.println("Replaced File: " + demoObject);
                }
            } else {
                System.out.println("ERROR: Unable to delete file: "
                        + demoObject);
            }
        } catch (IOException ioe) {
            System.out.println("IO ERROR: " + ioe.getMessage());
        }
    }

    /**
     * Shows argument list for application.
     * 
     * @param errMessage
     *        The message to be included with usage information.
     */
    public static void showUsage(String errMessage) {
        System.out.println("Error: " + errMessage);
        System.out.println("");
        System.out
                .println("Usage: DemoObjectConverter fromProtocol fromHostName fromPortNum "
                        + "toProtocol toHostName toPortNum fedoraHome");
    }

    public static void main(String[] args) {
        try {

            if (args.length != 7) {
                DemoObjectConverter
                        .showUsage("You must provide seven arguments.");
            } else {
                String fromProtocol = args[0];
                String fromHostName = args[1];
                String fromPortNum = args[2];
                String toProtocol = args[3];
                String toHostName = args[4];
                String toPortNum = args[5];
                String fedoraHome = args[6];

                DemoObjectConverter doc =
                        new DemoObjectConverter(fromProtocol,
                                                fromHostName,
                                                fromPortNum,
                                                toProtocol,
                                                toHostName,
                                                toPortNum,
                                                fedoraHome);
                DemoObjectConverter.convert(fedoraHome);
            }
        } catch (Exception e) {
            DemoObjectConverter.showUsage(e.getClass().getName()
                    + " - "
                    + (e.getMessage() == null ? "(no detail provided)" : e
                            .getMessage()));
        }
    }
}
