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

package fedora.server.storage.types;

import java.io.File;
import java.io.InputStream;

import fedora.common.Constants;

import fedora.server.Server;
import fedora.server.errors.InitializationException;
import fedora.server.errors.StreamIOException;
import fedora.server.storage.lowlevel.ILowlevelStorage;

/**
 * @author Chris Wilper
 * @version $Id$
 */
public class DatastreamManagedContent
        extends Datastream {
	
	/**
	 * Internal scheme to indicating that a copy should made of the resource.
	 */
	public static final String COPY_SCHEME = "copy://";
	
	public static final String TEMP_SCHEME = "temp://";
	
	public static final String UPLOADED_SCHEME = "uploaded://";
	
    private static ILowlevelStorage s_llstore;    

    public DatastreamManagedContent() {
    }

    @Override
    public Datastream copy() {
        DatastreamManagedContent ds = new DatastreamManagedContent();
        copy(ds);
        return ds;
    }

    private ILowlevelStorage getLLStore() throws Exception {
        if (s_llstore == null) {
            try {
                Server server =
                        Server.getInstance(new File(Constants.FEDORA_HOME),
                                           false);
                s_llstore =
                        (ILowlevelStorage) server
                                .getModule("fedora.server.storage.lowlevel.ILowlevelStorage");
            } catch (InitializationException ie) {
                throw new Exception("Unable to get LLStore Module: "
                        + ie.getMessage(), ie);
            }
        }
        return s_llstore;
    }

    @Override
    public InputStream getContentStream() throws StreamIOException {
        try {
            return getLLStore().retrieveDatastream(DSLocation);
        } catch (Throwable th) {
            throw new StreamIOException("[DatastreamManagedContent] returned "
                    + " the error: \"" + th.getClass().getName()
                    + "\". Reason: " + th.getMessage());
        }
    }
}
