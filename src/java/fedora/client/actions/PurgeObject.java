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
import fedora.client.utility.AutoPurger;

import fedora.server.utilities.StringUtility;

/**
 * @author Chris Wilper
 */
public class PurgeObject
        extends AbstractAction {

    private static final long serialVersionUID = 1L;

    private Set m_pids;

    private boolean m_prompt;

    private ObjectEditorFrame m_parent;

    public PurgeObject() {
        super("Purge Object...");
        m_prompt = true;
    }

    public PurgeObject(ObjectEditorFrame parent, String pid) {
        super("Purge Object");
        m_pids = new HashSet();
        m_pids.add(pid);
        m_parent = parent;
    }

    public PurgeObject(Set pids) {
        super("Purge Objects");
        m_pids = pids;
    }

    public PurgeObject(String pid) {
        super("Purge Object");
        m_pids = new HashSet();
        m_pids.add(pid);
    }

    public void actionPerformed(ActionEvent ae) {
        boolean failed = false;
        if (m_prompt) {
            String pid = JOptionPane.showInputDialog("Enter the PID.");
            if (pid == null) {
                return;
            }
            m_pids = new HashSet();
            m_pids.add(pid);
        }
        AutoPurger purger = null;
        try {
            purger = new AutoPurger(Administrator.APIM);
        } catch (Exception e) {
            Administrator
                    .showErrorDialog(Administrator.getDesktop(),
                                     "Purge Failure",
                                     StringUtility.prettyPrint(e.getClass()
                                             .getName()
                                             + ": " + e.getMessage(), 70, null),
                                     e);
        }
        if (purger != null) {
            Iterator pidIter = m_pids.iterator();
            if (m_pids.size() == 1) {
                String pid = (String) pidIter.next();
                // just purge one
                String reason =
                        JOptionPane
                                .showInputDialog("Why are you permanently removing "
                                        + pid + "?");
                if (reason != null) {
                    try {
                        purger.purge(pid, reason, false); // DEFAULT_FORCE_PURGE
                    } catch (Exception e) {
                        Administrator.showErrorDialog(Administrator
                                .getDesktop(), "Purge Failure", StringUtility
                                .prettyPrint(e.getClass().getName() + ": "
                                        + e.getMessage(), 70, null), e);
                        failed = true;
                    }
                    if (!failed) {
                        JOptionPane.showMessageDialog(Administrator
                                .getDesktop(), "Purge succeeded.");
                        if (m_parent != null) {
                            m_parent.dispose();
                        }
                    }
                }
            } else {
                // purge multiple
                String reason =
                        JOptionPane
                                .showInputDialog("Why are you permanently removing these objects?");
                if (reason != null) {
                    while (pidIter.hasNext()) {
                        try {
                            String pid = (String) pidIter.next();
                            purger.purge(pid, reason, false); // DEFAULT_FORCE_PURGE
                        } catch (Exception e) {
                            Administrator
                                    .showErrorDialog(Administrator.getDesktop(),
                                                     "Purge Failure",
                                                     StringUtility
                                                             .prettyPrint(e
                                                                                  .getClass()
                                                                                  .getName()
                                                                                  + ": "
                                                                                  + e
                                                                                          .getMessage(),
                                                                          70,
                                                                          null),
                                                     e);
                            failed = true;
                        }
                    }
                    if (!failed) {
                        JOptionPane.showMessageDialog(Administrator
                                .getDesktop(), "Purge of " + m_pids.size()
                                + " objects succeeded.");
                    }
                }
            }
        }
    }

}
