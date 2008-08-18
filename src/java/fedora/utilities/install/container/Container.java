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

/**
 * 
 */

package fedora.utilities.install.container;

import java.io.File;

import fedora.utilities.install.Distribution;
import fedora.utilities.install.InstallOptions;
import fedora.utilities.install.InstallationFailedException;

/**
 * @author Edwin Shin
 */
public abstract class Container {

    private final Distribution dist;

    private final InstallOptions options;

    public Container(Distribution dist, InstallOptions options) {
        this.dist = dist;
        this.options = options;
    }

    public abstract void deploy(File war) throws InstallationFailedException;

    public abstract void install() throws InstallationFailedException;

    protected final Distribution getDist() {
        return dist;
    }

    protected final InstallOptions getOptions() {
        return options;
    }
}
