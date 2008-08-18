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

package fedora.server.journal.entry;

import fedora.server.errors.ServerException;
import fedora.server.journal.JournalException;
import fedora.server.journal.recoverylog.JournalRecoveryLog;
import fedora.server.management.ManagementDelegate;

/**
 * The JournalEntry to use when consuming a journal file.
 * <p>
 * Before invoking a method, write the entry to the recovery log. After invoking
 * the method, log a completion message.
 * 
 * @author Jim Blake
 */
public class ConsumerJournalEntry
        extends JournalEntry {

    private String identifier = "no identifier";

    public ConsumerJournalEntry(String methodName, JournalEntryContext context) {
        super(methodName, context);
    }

    public void invokeMethod(ManagementDelegate delegate,
                             JournalRecoveryLog recoveryLog)
            throws ServerException, JournalException {
        recoveryLog.log(this);
        super.getMethod().invoke(delegate);
        recoveryLog.log("Call complete:" + super.getMethodName());
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "ConsumerJournalEntry[identifier=" + identifier
                + ", methodName=" + getMethodName() + ", context="
                + getContext() + "]";
    }

}
