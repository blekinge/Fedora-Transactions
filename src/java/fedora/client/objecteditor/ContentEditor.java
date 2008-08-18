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

import java.awt.event.ActionListener;

import java.io.IOException;
import java.io.InputStream;

/**
 * Edit content of certain types in a JComponent.
 */
public abstract class ContentEditor
        extends ContentViewer {

    /**
     * Always returns true.
     */
    @Override
    public final boolean isEditor() {
        return true;
    }

    public void setPIDAndDSID(String pid, String dsid) {
    }

    /**
     * Called when the caller wants what is in the view to be considered "not
     * dirty" because it's been saved that way.
     */
    public abstract void changesSaved();

    /**
     * Called when the caller wants to update the view back to the data was
     * originally passed in.
     */
    public abstract void undoChanges();

    /**
     * Returns true if the content should be considered "dirty" (e.g. it has
     * changed due to some form of editing).
     */
    public abstract boolean isDirty();

    /**
     * Sets the listener that this ContentEditor will notify via
     * listener.actionPerformed(...) when any content-changing events occur that
     * could potentially affect its "dirty state" (whether going from not dirty
     * to dirty, or dirty to not dirty).
     */
    public abstract void setContentChangeListener(ActionListener listener);

    /**
     * Gets the content in its edited state.
     */
    public abstract InputStream getContent() throws IOException;

}