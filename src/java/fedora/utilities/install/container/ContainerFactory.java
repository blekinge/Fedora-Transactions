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

import fedora.utilities.install.Distribution;
import fedora.utilities.install.InstallOptions;

/**
 * A static factory that returns a Container depending on InstallOptions
 */
public class ContainerFactory {

    private ContainerFactory() {
    }

    public static Container getContainer(Distribution dist,
                                         InstallOptions options) {
        String servletEngine = options.getValue(InstallOptions.SERVLET_ENGINE);
        if (servletEngine.equals(InstallOptions.INCLUDED)) {
            return new BundledTomcat(dist, options);
        } else if (servletEngine.equals(InstallOptions.EXISTING_TOMCAT)) {
            File tomcatHome =
                    new File(options.getValue(InstallOptions.TOMCAT_HOME));
            File dbcp55 =
                    new File(tomcatHome, "common/lib/naming-factory-dbcp.jar");
            File dbcp6 = new File(tomcatHome, "lib/tomcat-dbcp.jar");
            if (dbcp55.exists() || dbcp6.exists()) {
                return new ExistingTomcat(dist, options);
            } else {
                return new ExistingTomcat50(dist, options);
            }
        } else {
            return new DefaultContainer(dist, options);
        }
    }
}
