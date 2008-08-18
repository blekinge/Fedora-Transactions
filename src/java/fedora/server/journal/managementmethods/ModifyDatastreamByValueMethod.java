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

package fedora.server.journal.managementmethods;

import fedora.server.errors.ServerException;
import fedora.server.journal.JournalException;
import fedora.server.journal.entry.JournalEntry;
import fedora.server.management.ManagementDelegate;

/**
 * Adapter class for Management.modifyDatastreamByValue().
 * 
 * @author Jim Blake
 */
public class ModifyDatastreamByValueMethod
        extends ManagementMethod {

    public ModifyDatastreamByValueMethod(JournalEntry parent) {
        super(parent);
    }

    @Override
    public Object invoke(ManagementDelegate delegate) throws ServerException,
            JournalException {
        return delegate.modifyDatastreamByValue(parent.getContext(), parent
                .getStringArgument(ARGUMENT_NAME_PID), parent
                .getStringArgument(ARGUMENT_NAME_DS_ID), parent
                .getStringArrayArgument(ARGUMENT_NAME_ALT_IDS), parent
                .getStringArgument(ARGUMENT_NAME_DS_LABEL), parent
                .getStringArgument(ARGUMENT_NAME_MIME_TYPE), parent
                .getStringArgument(ARGUMENT_NAME_FORMAT_URI), parent
                .getStreamArgument(ARGUMENT_NAME_DS_CONTENT), parent
                .getStringArgument(ARGUMENT_NAME_CHECKSUM_TYPE), parent
                .getStringArgument(ARGUMENT_NAME_CHECKSUM), parent
                .getStringArgument(ARGUMENT_NAME_LOG_MESSAGE), parent
                .getBooleanArgument(ARGUMENT_NAME_FORCE));
    }

}
