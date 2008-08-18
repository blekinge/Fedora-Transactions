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

package fedora.client.actions;

import java.awt.event.ActionEvent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import fedora.client.Administrator;
import fedora.client.objecteditor.ObjectEditorFrame;

/**
 * Launches an object viewer/editor window.
 * 
 * @author Chris Wilper
 */
public class ViewObject
        extends AbstractAction {

    private static final long serialVersionUID = 1L;

    private Set m_pids;

    private boolean m_prompt;

    public ViewObject() {
        super("Open Object...");
        m_prompt = true;
    }

    public ViewObject(String pid) {
        super("Open Object");
        m_pids = new HashSet();
        m_pids.add(pid);
    }

    public ViewObject(Set pids) {
        super("Open Objects");
        m_pids = pids;
    }

    public void actionPerformed(ActionEvent ae) {
        launch();
    }

    public void launch() {
        if (m_prompt) {
            String pid =
                    JOptionPane
                            .showInputDialog("Enter the PID of the object to open.");
            if (pid == null) {
                return;
            }
            m_pids = new HashSet();
            m_pids.add(pid);
        }
        Iterator pidIter = m_pids.iterator();
        while (pidIter.hasNext()) {
            String pid = (String) pidIter.next();
            try {
                ObjectEditorFrame editor = new ObjectEditorFrame(pid, 0);
                editor.setVisible(true);
                Administrator.getDesktop().add(editor);
                editor.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
                Administrator.showErrorDialog(Administrator.getDesktop(),
                                              "Error Opening Object",
                                              e.getClass().getName() + ": "
                                                      + e.getMessage(),
                                              e);
            }
        }
    }

}
