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

package fedora.server.test;

import java.io.File;

import java.util.HashMap;

import junit.framework.TestCase;

import fedora.common.Constants;
import fedora.common.Models;

import fedora.server.Server;
import fedora.server.storage.ServiceDefinitionReader;
import fedora.server.storage.ServiceDeploymentReader;
import fedora.server.storage.DOReader;
import fedora.server.storage.DirectoryBasedRepositoryReader;
import fedora.server.storage.translation.DOTranslatorImpl;
import fedora.server.storage.translation.METSFedoraExt1_1DODeserializer;
import fedora.server.storage.translation.METSFedoraExt1_1DOSerializer;

/**
 * Tests the implementation of the RepositoryReader interface,
 * DirectoryBasedRepositoryReader.
 * 
 * @author Chris Wilper
 */
public class RepositoryReaderTest
        extends TestCase
        implements Constants {

    private final File m_repoDir;

    private DirectoryBasedRepositoryReader m_repoReader;

    public RepositoryReaderTest(String fedoraHome, String label) {
        super(label);
        m_repoDir = new File(new File(fedoraHome), "demo");
    }

    @Override
    public void setUp() {
        try {
            String mets = METS_EXT1_1.uri;
            HashMap sers = new HashMap();
            sers.put(mets, new METSFedoraExt1_1DOSerializer());
            HashMap desers = new HashMap();
            desers.put(mets, new METSFedoraExt1_1DODeserializer());
            DOTranslatorImpl translator = new DOTranslatorImpl(sers, desers);
            m_repoReader =
                    new DirectoryBasedRepositoryReader(m_repoDir,
                                                       translator,
                                                       mets,
                                                       mets,
                                                       "UTF-8");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    public void testList() {
        try {
            String[] pids = m_repoReader.listObjectPIDs(null);
            System.out.println("Repository has " + pids.length + " objects.");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    public void testGetReader() {
        try {
            String[] pids = m_repoReader.listObjectPIDs(null);
            for (String element : pids) {
                DOReader r =
                        m_repoReader.getReader(Server.USE_DEFINITIVE_STORE,
                                               null,
                                               element);
                System.out.println(r.GetObjectPID() + " found via DOReader.");
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    public void testGetSDefReader() {
        try {
            String[] pids = m_repoReader.listObjectPIDs(null);
            for (String element : pids) {
                DOReader r =
                        m_repoReader.getReader(Server.USE_DEFINITIVE_STORE,
                                               null,
                                               element);
                if (r.hasRelationship(MODEL.HAS_MODEL,
                                      Models.SERVICE_DEPLOYMENT_3_0)) {
                    ServiceDefinitionReader dr =
                            m_repoReader
                                    .getServiceDefinitionReader(Server.USE_DEFINITIVE_STORE,
                                                                null,
                                                                element);
                    System.out.println(dr.GetObjectPID()
                            + " found via getSDefReader.");
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    public void testGetSDepReader() {
        try {
            String[] pids = m_repoReader.listObjectPIDs(null);
            for (String element : pids) {
                DOReader r =
                        m_repoReader.getReader(Server.USE_DEFINITIVE_STORE,
                                               null,
                                               element);
                if (r.hasRelationship(MODEL.HAS_MODEL,
                                      Models.SERVICE_DEPLOYMENT_3_0)) {
                    ServiceDeploymentReader mr =
                            m_repoReader
                                    .getServiceDeploymentReader(Server.USE_DEFINITIVE_STORE,
                                                                null,
                                                                element);
                    System.out.println(mr.GetObjectPID()
                            + " found via getSDepReader.");
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    public static void main(String[] args) {
        RepositoryReaderTest test =
                new RepositoryReaderTest(Constants.FEDORA_HOME,
                                         "Testing DirectoryBasedRepositoryReader");
        test.setUp();
        test.testList();
        test.testGetReader();
        test.testGetSDefReader();
        test.testGetSDepReader();
    }

}