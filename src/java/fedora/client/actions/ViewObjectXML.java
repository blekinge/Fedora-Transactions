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

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;

import java.io.ByteArrayOutputStream;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

import fedora.client.Administrator;
import fedora.client.utility.export.AutoExporter;

/**
 * @author Chris Wilper
 */
public class ViewObjectXML
        extends AbstractAction {

    private static final long serialVersionUID = 1L;

    private Set m_pids;

    private boolean m_prompt;

    private JPanel m_viewPane;

    public ViewObjectXML() {
        super("View Object XML...");
        m_prompt = true;
    }

    public ViewObjectXML(String pid) {
        super("View Object XML");
        m_pids = new HashSet();
        m_pids.add(pid);
    }

    public ViewObjectXML(String pid, JPanel viewPane) {
        super("View Object XML");
        m_pids = new HashSet();
        m_pids.add(pid);
        m_viewPane = viewPane;
    }

    public ViewObjectXML(Set pids) {
        super("View Objects XML");
        m_pids = pids;
    }

    public void actionPerformed(ActionEvent ae) {
        if (m_prompt) {
            String pid = JOptionPane.showInputDialog("Enter the PID.");
            if (pid == null) {
                return;
            }
            m_pids = new HashSet();
            m_pids.add(pid);
        }
        AutoExporter exporter = null;
        try {
            //exporter=new AutoExporter(
            //	Administrator.getProtocol(), Administrator.getHost(), 
            //	Administrator.getPort(), Administrator.getUser(), Administrator.getPass());            	
            exporter = new AutoExporter(Administrator.APIA, Administrator.APIM);
        } catch (Exception e) {
            Administrator.showErrorDialog(Administrator.getDesktop(),
                                          "View Failure",
                                          e.getClass().getName() + ": "
                                                  + e.getMessage(),
                                          e);
        }
        if (exporter != null) {
            Iterator pidIter = m_pids.iterator();
            while (pidIter.hasNext()) {
                try {
                    String pid = (String) pidIter.next();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    exporter.getObjectXML(pid, out);
                    JTextComponent textEditor = new JTextArea();
                    textEditor.setFont(new Font("monospaced", Font.PLAIN, 12));
                    textEditor.setText(new String(out.toString("UTF-8")));
                    textEditor.setCaretPosition(0);
                    textEditor.setEditable(false);
                    if (m_viewPane == null) {
                        JInternalFrame viewFrame =
                                new JInternalFrame("Viewing " + pid,
                                                   true,
                                                   true,
                                                   true,
                                                   true);
                        viewFrame
                                .setFrameIcon(new ImageIcon(this
                                        .getClass()
                                        .getClassLoader()
                                        .getResource("images/standard/general/Edit16.gif")));
                        viewFrame.getContentPane()
                                .add(new JScrollPane(textEditor));
                        viewFrame.setSize(720, 520);
                        viewFrame.setVisible(true);
                        Administrator.getDesktop().add(viewFrame);
                        try {
                            viewFrame.setSelected(true);
                        } catch (java.beans.PropertyVetoException pve) {
                        }
                    } else {
                        m_viewPane.removeAll();
                        m_viewPane.setLayout(new BorderLayout());
                        m_viewPane.add(new JScrollPane(textEditor),
                                       BorderLayout.CENTER);
                        m_viewPane.validate();
                    }
                } catch (Exception e) {
                    Administrator.showErrorDialog(Administrator.getDesktop(),
                                                  "View Failure",
                                                  e.getClass().getName() + ": "
                                                          + e.getMessage(),
                                                  e);
                }
            }
        }
    }

}
