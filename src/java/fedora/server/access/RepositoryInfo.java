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

package fedora.server.access;

/**
 * Data structure to contain a key information about the repository.
 * 
 * <p>This information is the return value of the API-A 
 * <code>describeRepository</code> request.
 * 
 * @author Sandy Payette
 */
public class RepositoryInfo {

    public String repositoryName = null;

    public String repositoryBaseURL = null;

    public String repositoryVersion = null;

    public String repositoryPIDNamespace = null;

    public String defaultExportFormat = null;

    public String OAINamespace = null;

    public String[] adminEmailList = new String[0];

    public String samplePID = null;

    public String sampleOAIIdentifer = null;

    public String sampleSearchURL = null;

    public String sampleAccessURL = null;

    public String sampleOAIURL = null;

    public String[] retainPIDs = new String[0];
}
