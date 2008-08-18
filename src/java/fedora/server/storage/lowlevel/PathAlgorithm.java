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

package fedora.server.storage.lowlevel;

import java.util.Map;

import fedora.server.Server;
import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.MalformedPidException;

/**
 * @author Bill Niebel
 */
public abstract class PathAlgorithm {

    public PathAlgorithm(Map configuration) {
    };

    public abstract String get(String pid) throws LowlevelStorageException;

    public static String encode(String unencoded)
            throws LowlevelStorageException {
        try {
            int i = unencoded.indexOf("+");
            if (i != -1) {
                return Server.getPID(unencoded.substring(0, i)).toFilename()
                        + unencoded.substring(i);
            } else {
                return Server.getPID(unencoded).toFilename();
            }
        } catch (MalformedPidException e) {
            throw new LowlevelStorageException(true, e.getMessage(), e);
        }
    }

    public static String decode(String encoded) throws LowlevelStorageException {
        try {
            int i = encoded.indexOf("+");
            if (i != -1) {
                return Server.pidFromFilename(encoded.substring(0, i))
                        .toString()
                        + encoded.substring(i);
            } else {
                return Server.pidFromFilename(encoded).toString();
            }
        } catch (MalformedPidException e) {
            throw new LowlevelStorageException(true, e.getMessage(), e);
        }
    }
}
