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

package fedora.utilities.install.container;

import java.io.File;

import fedora.utilities.install.InstallOptions;

/**
 * Options for the Fedora web.xml file.
 * 
 * @author Edwin Shin
 */
public class WebXMLOptions {

    private boolean apiaAuth;

    private boolean apiaSSL;

    private boolean apimSSL;

    private boolean restAPI;

    private File fedoraHome;

    public WebXMLOptions() {
    }

    public WebXMLOptions(InstallOptions installOptions) {
        apiaAuth =
                installOptions
                        .getBooleanValue(InstallOptions.APIA_AUTH_REQUIRED,
                                         false);
        apiaSSL =
                installOptions
                        .getBooleanValue(InstallOptions.APIA_SSL_REQUIRED,
                                         false);
        apimSSL =
                installOptions
                        .getBooleanValue(InstallOptions.APIM_SSL_REQUIRED,
                                         false);
        restAPI =
                installOptions.getBooleanValue(InstallOptions.REST_ENABLED,
                                               false);
        fedoraHome =
                new File(installOptions.getValue(InstallOptions.FEDORA_HOME));
    }

    public boolean requireApiaAuth() {
        return apiaAuth;
    }

    public void setApiaAuth(boolean apiaAuth) {
        this.apiaAuth = apiaAuth;
    }

    public boolean requireApiaSSL() {
        return apiaSSL;
    }

    public void setApiaSSL(boolean apiaSSL) {
        this.apiaSSL = apiaSSL;
    }

    public boolean requireApimSSL() {
        return apimSSL;
    }

    public void setApimSSL(boolean apimSSL) {
        this.apimSSL = apimSSL;
    }

    public boolean enableRestAPI() {
        return restAPI;
    }

    public void setRestAPI(boolean restAPI) {
        this.restAPI = restAPI;
    }

    public File getFedoraHome() {
        return fedoraHome;
    }

    public void setFedoraHome(File fedoraHome) {
        this.fedoraHome = fedoraHome;
    }
}
