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

package fedora.server.journal;

import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ModuleShutdownException;
import fedora.server.management.Management;
import fedora.server.management.ManagementDelegate;

/**
 * A common interface for the <code>JournalConsumer</code> and
 * <code>JournalCreator</code> classes. These classes form the implementation
 * layer between the <code>Journaler</code> and the
 * <code>ManagementDelegate</code>.
 * 
 * @author Jim Blake
 */
public interface JournalWorker
        extends Management {

    /**
     * Called by the Journaler during post-initialization, with a reference to
     * the ManagementDelegate module.
     */
    public void setManagementDelegate(ManagementDelegate delegate)
            throws ModuleInitializationException;

    /**
     * Called when the Journaler module receives a shutdown() from the server.
     */
    public void shutdown() throws ModuleShutdownException;
}
