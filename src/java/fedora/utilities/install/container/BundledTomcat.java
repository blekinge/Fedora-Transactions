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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.DocumentException;

import fedora.utilities.FileUtils;
import fedora.utilities.Zip;
import fedora.utilities.install.Distribution;
import fedora.utilities.install.FedoraHome;
import fedora.utilities.install.InstallOptions;
import fedora.utilities.install.InstallationFailedException;

/**
 * The profile for the servlet container bundled with the Fedora installer.
 * History: Release 3.0 bundled Tomcat 5.5.26. Release 2.2 bundled Tomcat
 * 5.0.28.
 * 
 * @author Edwin Shin
 */
public class BundledTomcat
        extends Tomcat {

    public BundledTomcat(Distribution dist, InstallOptions options) {
        super(dist, options);
    }

    @Override
    public void install() throws InstallationFailedException {
        super.install();
    }

    @Override
    protected void installTomcat() throws InstallationFailedException {
        System.out.println("Installing Tomcat...");
        try {
            Zip.unzip(getDist().get(Distribution.TOMCAT), System
                    .getProperty("java.io.tmpdir"));
        } catch (IOException e) {
            throw new InstallationFailedException(e.getMessage(), e);
        }
        File f =
                new File(System.getProperty("java.io.tmpdir"),
                         Distribution.TOMCAT_BASENAME);
        if (!FileUtils.move(f, getTomcatHome())) {
            throw new InstallationFailedException("Move to "
                    + getTomcatHome().getAbsolutePath() + " failed.");
        }
        FedoraHome.setScriptsExecutable(new File(getTomcatHome(), "bin"));
    }

    @Override
    protected void installServerXML() throws InstallationFailedException {
        try {
            File distServerXML = new File(getConf(), "server.xml");
            TomcatServerXML serverXML =
                    new TomcatServerXML(distServerXML, getOptions());
            serverXML.update();
            serverXML.write(distServerXML.getAbsolutePath());
        } catch (IOException e) {
            throw new InstallationFailedException(e.getMessage(), e);
        } catch (DocumentException e) {
            throw new InstallationFailedException(e.getMessage(), e);
        }
    }

    @Override
    protected void installIncludedKeystore() throws InstallationFailedException {
        String keystoreFile =
                getOptions().getValue(InstallOptions.KEYSTORE_FILE);
        if (keystoreFile == null
                || !keystoreFile.equals(InstallOptions.INCLUDED)) {
            // nothing to do
            return;
        }
        try {
            InputStream is = getDist().get(Distribution.KEYSTORE);
            File keystore = getIncludedKeystore();

            if (!FileUtils.copy(is, new FileOutputStream(keystore))) {
                throw new InstallationFailedException("Copy to "
                        + keystore.getAbsolutePath() + " failed.");
            }
        } catch (IOException e) {
            throw new InstallationFailedException(e.getMessage(), e);
        }
    }
}
