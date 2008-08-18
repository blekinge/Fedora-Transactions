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

package fedora.server.journal.readerwriter.multicast.rmi;

/**
 * <p>
 * RmiJournalReceiverHelper.java
 * </p>
 * <p>
 * Utility methods that are used by multiple RMI-related classes.
 * </p>
 * 
 * @author jblake
 * @version $Id: RmiJournalReceiverHelper.java,v 1.3 2007/06/01 17:21:32 jblake
 *          Exp $
 */
public class RmiJournalReceiverHelper {

    /**
     * The writer and the receiver each use this method to figure the hash on
     * each journal entry. If the receiver calculates a different hash from the
     * one that appears on the entry, it will throw an exception.
     */
    public static String figureIndexedHash(String repositoryHash,
                                           long entryIndex) {
        return String.valueOf((entryIndex + repositoryHash).hashCode());
    }

}
