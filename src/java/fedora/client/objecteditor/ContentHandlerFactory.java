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

import java.util.HashMap;

/**
 * Provides type-appropriate ContentViewer and ContentEditor instances.
 */
public abstract class ContentHandlerFactory {

    private static HashMap s_viewers;

    private static HashMap s_editors;

    static {
        s_viewers = new HashMap();
        s_editors = new HashMap();
    }

    /**
     * Registers a viewer or editor with the factory. Before the factory is
     * used, all needed editors and viewers should be registered (in that
     * order). Order is important here because all editors are considered
     * viewers by default. If a separate viewer is registered after an editor
     * that handles the same type, that viewer will be the one provided via
     * getViewer. In general, the last viewers/editors passed in will have the
     * most precendence.
     */
    public static void register(ContentViewer handler) {
        if (handler.isEditor()) {
            String[] types = handler.getTypes();
            for (String element : types) {
                s_editors.put(element, handler);
            }
        }
        String[] types = handler.getTypes();
        for (String element : types) {
            s_viewers.put(element, handler);
        }
    }

    /**
     * Can the factory provide a viewer for the given type?
     */
    public static boolean hasViewer(String type) {
        return s_viewers.containsKey(type) || type.endsWith("+xml")
                && s_viewers.containsKey("text/xml");
    }

    /**
     * If a viewer would be provided for the given type, is that viewer also an
     * editor?
     */
    public static boolean viewerIsEditor(String type) {
        Object viewer = s_viewers.get(type);
        if (viewer != null) {
            return viewer instanceof ContentEditor;
        } else {
            return false;
        }
    }

    /**
     * Can the factory provide an editor for the given type?
     */
    public static boolean hasEditor(String type) {
        return s_editors.containsKey(type) || type.endsWith("+xml")
                && s_editors.containsKey("text/xml");
    }

    /**
     * Get a viewer for the given type, initialized with the given data. This
     * should only be called if the caller knows there is a viewer for the type.
     */
    public static ContentViewer getViewer(String type, InputStream data)
            throws IOException {
        ContentViewer viewer = (ContentViewer) s_viewers.get(type);
        if (viewer == null && type.endsWith("+xml")) {
            viewer = (ContentViewer) s_viewers.get("text/xml");
        }
        return viewer.newInstance(type, data, true);
    }

    /**
     * Get an editor for the given type, initialized with the given data. This
     * should only be called if the caller knows there is an editor for the
     * type.
     */
    public static ContentEditor getEditor(String type, InputStream data)
            throws IOException {
        ContentEditor editor = (ContentEditor) s_editors.get(type);
        if (editor == null && type.endsWith("+xml")) {
            editor = (ContentEditor) s_editors.get("text/xml");
        }
        return (ContentEditor) editor.newInstance(type, data, false);
    }

}