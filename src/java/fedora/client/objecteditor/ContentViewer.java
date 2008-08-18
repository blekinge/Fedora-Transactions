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

package fedora.client.objecteditor;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.JComponent;

/**
 * Views content of certain types in a JComponent.
 */
public abstract class ContentViewer {

    /**
     * Tells whether this component just does viewing or can do editing as well.
     * Implementers will simply subclass either ContentViewer or ContentEditor,
     * and this method will automatically return the correct value.
     */
    public boolean isEditor() {
        return false;
    }

    /**
     * Get the JComponent.
     */
    public abstract JComponent getComponent();

    /**
     * Gets a new instance of this class, which has been constructed and
     * immediately thereafter, init(...)'ed. Intended to be called by
     * ContentHandlerFactory only.
     */
    protected final ContentViewer newInstance(String type,
                                              InputStream data,
                                              boolean viewOnly)
            throws IOException {
        ContentViewer instance = null;
        try {
            instance = this.getClass().newInstance();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        instance.init(type, data, viewOnly);
        return instance;
    }

    /**
     * Returns a list of content types that this component can handle. This will
     * usually be a list of MIME Types, but may also include other notions of
     * type known to be understood by the users of ContentHandlerFactory.
     */
    public abstract String[] getTypes();

    /**
     * Initializes the handler. This should only be called once per instance,
     * and is guaranteed to have been called when this component is provided by
     * the ContentHandlerFactory. The viewOnly parameter signals to
     * ContentEditor implementations that editing capabilities are not desired
     * by the caller.
     */
    public abstract void init(String type, InputStream data, boolean viewOnly)
            throws IOException;

    /**
     * Re-initializes the handler given new input data. The old data can be
     * discarded.
     */
    public abstract void setContent(InputStream data) throws IOException;

}