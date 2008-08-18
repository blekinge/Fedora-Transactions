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

import javax.media.jai.JAI;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import com.sun.media.jai.codec.MemoryCacheSeekableStream;

/**
 * Views content of common image formats in a JComponent.
 */
public class ImageContentViewer
        extends ContentViewer {

    private JLabel m_label;

    private JScrollPane m_pane;

    private boolean s_registered;

    public ImageContentViewer() {
        if (!s_registered) {
            ContentHandlerFactory.register(this);
            s_registered = true;
        }
    }

    /**
     * Get the JComponent.
     */
    @Override
    public JComponent getComponent() {
        return m_pane;
    }

    /**
     * Returns a list of content types that this component can handle. This will
     * usually be a list of MIME Types, but may also include other notions of
     * type known to be understood by the users of ContentHandlerFactory.
     */
    @Override
    public String[] getTypes() {
        return new String[] {"image/gif", "image/jpeg", "image/tiff",
                "image/bmp", "image/x-ms-bmp", "image/x-bitmap", "image/png"};
    }

    /**
     * Initializes the handler. This should only be called once per instance,
     * and is guaranteed to have been called when this component is provided by
     * the ContentHandlerFactory. The viewOnly parameter signals to
     * ContentEditor implementations that editing capabilities are not desired
     * by the caller.
     */
    @Override
    public void init(String type, InputStream data, boolean viewOnly)
            throws IOException {
        setContent(data);
    }

    /**
     * Re-initializes the handler given new input data. The old data can be
     * discarded.
     */
    @Override
    public void setContent(InputStream data) throws IOException {
        try {
            ImageIcon image =
                    new ImageIcon(JAI
                            .create("stream",
                                    new MemoryCacheSeekableStream(data))
                            .getAsBufferedImage());
            if (m_pane == null) {
                m_label = new JLabel(image);
                m_pane = new JScrollPane(m_label);
            } else {
                m_label.setIcon(image);
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

}