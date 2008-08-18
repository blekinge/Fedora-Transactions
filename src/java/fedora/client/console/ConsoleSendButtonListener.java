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

package fedora.client.console;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import fedora.client.Administrator;

/**
 * @author Chris Wilper
 */
public class ConsoleSendButtonListener
        implements ActionListener {

    private final Administrator m_mainFrame;

    private final ComboBoxModel m_model;

    private final Console m_console;

    public ConsoleSendButtonListener(ComboBoxModel model,
                                     Administrator mainFrame,
                                     Console console) {
        m_model = model;
        m_mainFrame = mainFrame;
        m_console = console;
    }

    public void actionPerformed(ActionEvent event) {
        ConsoleCommand command = (ConsoleCommand) m_model.getSelectedItem();
        JDialog jd = new JDialog(m_mainFrame, "Send Command", true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        ConsoleCommandInvoker invoker =
                new ConsoleCommandInvoker(command, m_console);
        invoker.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        panel.add(invoker, BorderLayout.CENTER);
        JPanel okCancelPanel = new JPanel();
        okCancelPanel.setLayout(new BorderLayout());
        JButton okButton = new JButton("OK");
        InvokeDialogListener listener = new InvokeDialogListener(jd, invoker);
        okButton.addActionListener(listener);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(listener);
        okCancelPanel.add(cancelButton, BorderLayout.WEST);
        okCancelPanel.add(okButton, BorderLayout.EAST);
        panel.add(okCancelPanel, BorderLayout.SOUTH);
        jd.getContentPane().add(panel, BorderLayout.CENTER);
        jd.pack();
        jd.setLocation(m_mainFrame
                .getCenteredPos(jd.getWidth(), jd.getHeight()));
        jd.setVisible(true);
    }
}
