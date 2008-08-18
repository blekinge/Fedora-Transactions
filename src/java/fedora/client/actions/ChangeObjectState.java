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

/**
 * Change an object or a group of object's state.
 * 
 * @author Chris Wilper
 */
public class ChangeObjectState
        extends AbstractAction {

    private static final long serialVersionUID = 1L;

    private final Set<String> m_pids;

    private final String m_newState;

    public ChangeObjectState(String pid, String stateString) {
        super(stateString);
        m_pids = new HashSet<String>();
        m_pids.add(pid);
        m_newState = stateString.toUpperCase().substring(0, 1);
    }

    public ChangeObjectState(Set<String> pids, String stateString) {
        super(stateString);
        m_pids = pids;
        m_newState = stateString.toUpperCase().substring(0, 1);
    }

    public void actionPerformed(ActionEvent ae) {
        String reason =
                JOptionPane
                        .showInputDialog("Enter a log message for the state change.");
        if (reason != null) {
            try {
                Iterator pidIter = m_pids.iterator();
                while (pidIter.hasNext()) {
                    String pid = (String) pidIter.next();
                    Administrator.APIM.modifyObject(pid,
                                                    m_newState,
                                                    null,
                                                    null,
                                                    reason);
                }
                String s = "s";
                if (m_pids.size() == 1) {
                    s = "";
                }
                JOptionPane.showMessageDialog(Administrator.getDesktop(),
                                              "Success.\n" + "Set state of "
                                                      + m_pids.size()
                                                      + " object" + s + " to '"
                                                      + m_newState + "'.");
            } catch (Exception e) {
                String message = e.getMessage();
                if (message == null) {
                    message = e.getClass().getName();
                }
                Administrator.showErrorDialog(Administrator.getDesktop(),
                                              "Failure Changing State",
                                              message,
                                              e);
            }
        }
    }

}
