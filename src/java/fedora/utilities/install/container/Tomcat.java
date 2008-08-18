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

import fedora.utilities.FileUtils;
import fedora.utilities.install.Distribution;
import fedora.utilities.install.InstallOptions;
import fedora.utilities.install.InstallationFailedException;

public abstract class Tomcat
        extends Container {

    public static final String CONF = "conf";

    public static final String KEYSTORE = "keystore";

    private final File tomcatHome;

    private final File webapps;

    private final File conf;

    private final File common_lib;

    /**
     * Target location of the included keystore file.
     */
    private final File includedKeystore;

    Tomcat(Distribution dist, InstallOptions options) {
        super(dist, options);
        tomcatHome =
                new File(getOptions().getValue(InstallOptions.TOMCAT_HOME));
        webapps = new File(tomcatHome, "webapps" + File.separator);
        conf = new File(tomcatHome, CONF + File.separator);
        common_lib =
                new File(tomcatHome, "common" + File.separator + "lib"
                        + File.separator);
        includedKeystore = new File(conf, KEYSTORE);
    }

    @Override
    public void deploy(File war) throws InstallationFailedException {
        System.out.println("Deploying " + war.getName() + "...");
        File dest = new File(webapps, war.getName());
        if (!FileUtils.copy(war, dest)) {
            throw new InstallationFailedException("Deploy failed: unable to copy "
                    + war.getAbsolutePath() + " to " + dest.getAbsolutePath());
        }
    }

    @Override
    public void install() throws InstallationFailedException {
        installTomcat();
        installServerXML();
        installIncludedKeystore();
    }

    protected abstract void installTomcat() throws InstallationFailedException;

    protected abstract void installServerXML()
            throws InstallationFailedException;

    protected abstract void installIncludedKeystore()
            throws InstallationFailedException;

    protected final File getTomcatHome() {
        return tomcatHome;
    }

    protected final File getWebapps() {
        return webapps;
    }

    protected final File getConf() {
        return conf;
    }

    protected final File getCommonLib() {
        return common_lib;
    }

    protected final File getIncludedKeystore() {
        return includedKeystore;
    }
}
