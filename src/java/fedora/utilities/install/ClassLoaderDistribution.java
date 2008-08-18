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

package fedora.utilities.install;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

public class ClassLoaderDistribution
        extends Distribution {

    private final ClassLoader _cl;

    public ClassLoaderDistribution() {
        _cl = this.getClass().getClassLoader();
    }

    public ClassLoaderDistribution(ClassLoader cl) {
        _cl = cl;
    }

    @Override
    public boolean contains(String path) {
        return _cl.getResource(rewritePath(path)) != null;
    }

    /**
     * Requested resources will automatically be prefixed with "resources/".
     */
    @Override
    public InputStream get(String path) throws IOException {
        InputStream stream = _cl.getResourceAsStream(rewritePath(path));
        if (stream == null) {
            throw new FileNotFoundException("Not found in classpath: " + path);
        } else {
            return stream;
        }
    }

    @Override
    public URL getURL(String path) {
        return _cl.getResource(rewritePath(path));
    }

    /**
     * Note: we don't check for backtracking.
     * 
     * @param path
     * @return
     */
    private static String rewritePath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        // Note, ClassLoader paths are always absolute, so , so no leading slash
        return "resources/" + path;
    }
}
