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

package fedora.client.datastream;

import java.net.MalformedURLException;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import fedora.client.FedoraClient;

import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.Datastream;

/**
 * @author Chris Wilper
 */
public class DatastreamConduit {

    private final FedoraAPIM m_apim;

    public DatastreamConduit(FedoraAPIM apim)
            throws MalformedURLException, ServiceException {
        m_apim = apim;
    }

    public static Datastream getDatastream(FedoraAPIM skeleton,
                                           String pid,
                                           String dsId,
                                           String asOfDateTime)
            throws RemoteException {
        return skeleton.getDatastream(pid, dsId, asOfDateTime);
    }

    public Datastream getDatastream(String pid, String dsId, String asOfDateTime)
            throws RemoteException {
        return getDatastream(m_apim, pid, dsId, asOfDateTime);
    }

    public static Datastream[] getDatastreams(FedoraAPIM skeleton,
                                              String pid,
                                              String asOfDateTime,
                                              String state)
            throws RemoteException {
        return skeleton.getDatastreams(pid, asOfDateTime, state);
    }

    public Datastream[] getDatastreams(String pid,
                                       String asOfDateTime,
                                       String state) throws RemoteException {
        return getDatastreams(m_apim, pid, asOfDateTime, state);
    }

    public static void modifyDatastreamByReference(FedoraAPIM skeleton,
                                                   String pid,
                                                   String dsId,
                                                   String[] altIDs,
                                                   String dsLabel,
                                                   String mimeType,
                                                   String formatURI,
                                                   String location,
                                                   String checksumType,
                                                   String checksum,
                                                   String logMessage,
                                                   boolean force)
            throws RemoteException {
        skeleton.modifyDatastreamByReference(pid,
                                             dsId,
                                             altIDs,
                                             dsLabel,
                                             mimeType,
                                             formatURI,
                                             location,
                                             checksumType,
                                             checksum,
                                             logMessage,
                                             force);
    }

    public void modifyDatastreamByReference(String pid,
                                            String dsId,
                                            String[] altIDs,
                                            String dsLabel,
                                            String mimeType,
                                            String formatURI,
                                            String location,
                                            String checksumType,
                                            String checksum,
                                            String logMessage,
                                            boolean force)
            throws RemoteException {
        modifyDatastreamByReference(m_apim,
                                    pid,
                                    dsId,
                                    altIDs,
                                    dsLabel,
                                    mimeType,
                                    formatURI,
                                    location,
                                    checksumType,
                                    checksum,
                                    logMessage,
                                    force);
    }

    public static void modifyDatastreamByValue(FedoraAPIM skeleton,
                                               String pid,
                                               String dsId,
                                               String[] altIDs,
                                               String dsLabel,
                                               String mimeType,
                                               String formatURI,
                                               byte[] content,
                                               String checksumType,
                                               String checksum,
                                               String logMessage,
                                               boolean force)
            throws RemoteException {
        skeleton.modifyDatastreamByValue(pid,
                                         dsId,
                                         altIDs,
                                         dsLabel,
                                         mimeType,
                                         formatURI,
                                         content,
                                         checksumType,
                                         checksum,
                                         logMessage,
                                         force);
    }

    public void modifyDatastreamByValue(String pid,
                                        String dsId,
                                        String[] altIDs,
                                        String dsLabel,
                                        boolean versionable,
                                        String mimeType,
                                        String formatURI,
                                        byte[] content,
                                        String state,
                                        String checksumType,
                                        String checksum,
                                        String logMessage,
                                        boolean force) throws RemoteException {
        modifyDatastreamByValue(m_apim,
                                pid,
                                dsId,
                                altIDs,
                                dsLabel,
                                mimeType,
                                formatURI,
                                content,
                                checksumType,
                                checksum,
                                logMessage,
                                force);
    }

    public static String[] purgeDatastream(FedoraAPIM skeleton,
                                           String pid,
                                           String dsId,
                                           String startDT,
                                           String endDT,
                                           String logMessage,
                                           boolean force)
            throws RemoteException {
        return skeleton.purgeDatastream(pid,
                                        dsId,
                                        startDT,
                                        endDT,
                                        logMessage,
                                        force);
    }

    public String[] purgeDatastream(String pid,
                                    String dsId,
                                    String startDT,
                                    String endDT,
                                    String logMessage,
                                    boolean force) throws RemoteException {
        return purgeDatastream(m_apim,
                               pid,
                               dsId,
                               startDT,
                               endDT,
                               logMessage,
                               force);
    }

    public static Datastream[] getDatastreamHistory(FedoraAPIM skeleton,
                                                    String pid,
                                                    String dsId)
            throws RemoteException {
        return skeleton.getDatastreamHistory(pid, dsId);
    }

    public Datastream[] getDatastreamHistory(String pid, String dsId)
            throws RemoteException {
        return getDatastreamHistory(m_apim, pid, dsId);
    }

    public static void showUsage(String errMessage) {
        System.out.println("Error: " + errMessage);
        System.out.println("");
        System.out
                .println("Usage: fedora-dsinfo host port username password pid protocol");
        System.out.println("Note: protocol must be either http or https.");
    }

    public static void main(String[] args) {
        try {
            if (args.length != 6) {
                DatastreamConduit.showUsage("You must provide six arguments.");
            } else {
                String hostName = args[0];
                int portNum = Integer.parseInt(args[1]);
                String username = args[2];
                String password = args[3];
                String pid = args[4];
                String protocol = args[5];

                // ******************************************
                // NEW: use new client utility class
                // FIXME:  Get around hardcoding the path in the baseURL
                String baseURL =
                        protocol + "://" + hostName + ":" + portNum + "/fedora";
                FedoraClient fc = new FedoraClient(baseURL, username, password);
                FedoraAPIM sourceRepoAPIM = fc.getAPIM();
                //*******************************************
                DatastreamConduit c = new DatastreamConduit(sourceRepoAPIM);

                Datastream[] datastreams = c.getDatastreams(pid, null, null);
                for (Datastream ds : datastreams) {
                    System.out.println("   Datastream : " + ds.getID());
                    System.out.println("Control Group : "
                            + ds.getControlGroup().toString());
                    System.out.println("  Versionable : " + ds.isVersionable());
                    System.out.println("    Mime Type : " + ds.getMIMEType());
                    System.out.println("   Format URI : " + ds.getFormatURI());
                    String[] altIDs = ds.getAltIDs();
                    if (altIDs != null) {
                        for (String element : altIDs) {
                            System.out.println(" Alternate ID : " + element);
                        }
                    }
                    System.out.println("        State : " + ds.getState());
                    // print version id, create date, and label for each version
                    Datastream[] versions =
                            c.getDatastreamHistory(pid, ds.getID());
                    for (Datastream ver : versions) {
                        System.out.println("      VERSION : "
                                + ver.getVersionID());
                        System.out.println("        Created : "
                                + ver.getCreateDate());
                        System.out.println("          Label : "
                                + ver.getLabel());
                        System.out.println("       Location : "
                                + ver.getLocation());
                    }
                    System.out.println("");
                }
            }
        } catch (Exception e) {
            DatastreamConduit.showUsage(e.getClass().getName()
                    + " - "
                    + (e.getMessage() == null ? "(no detail provided)" : e
                            .getMessage()));
        }
    }

}
