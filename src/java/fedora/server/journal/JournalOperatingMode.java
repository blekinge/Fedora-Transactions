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

/*
 * The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.journal;

import fedora.server.errors.InvalidStateException;
import fedora.server.errors.ServerException;
import fedora.server.journal.entry.CreatorJournalEntry;

/**
 * <p>
 * <b>Title:</b> JournalOperatingMode.java
 * </p>
 * <p>
 * <b>Description:</b> A mechanism for kicking a server from normal
 * (Journal-Creating) mode, to disabled (Read-Only) mode. Any
 * {@link CreatorJournalEntry} must call
 * {@link JournalOperatingMode#enforceCurrentMode} before performing an
 * operation that might modify the repository.
 * </p>
 * 
 * @author jblake
 * @version $Id$
 */
public enum JournalOperatingMode {
    NORMAL, READ_ONLY;

    private static JournalOperatingMode currentMode = NORMAL;

    /**
     * Set the current mode.
     */
    public static void setMode(JournalOperatingMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("Journal operating mode may not be null");
        }
        JournalOperatingMode.currentMode = mode;
    }

    /**
     * Get the current mode.
     */
    public static Object getMode() {
        return currentMode;
    }

    /**
     * If a modifying operation is attempted while we are in Read-Only mode,
     * throw an exception to prevent it. In Normal mode, do nothing.
     * 
     * @throws ServerException
     *         to prevent a modifying operation in Read-Only mode.
     */
    public static void enforceCurrentMode() throws ServerException {
        switch (currentMode) {
            case READ_ONLY:
                throw new InvalidStateException("Server is in Read-Only mode, pursuant to a Journaling error.");
            default:
        }
    }

}
