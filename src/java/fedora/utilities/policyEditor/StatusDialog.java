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

package fedora.utilities.policyEditor;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class StatusDialog
        extends JDialog
        implements ActionListener {

    private static final long serialVersionUID = 1L;

    private JButton button = null;

    private JPanel panel = null;

    /**
     * @param owner
     * @param title
     * @throws java.awt.HeadlessException
     */
    public StatusDialog(Frame owner, String title)
            throws HeadlessException {
        super(owner, title, true);
        getContentPane().setLayout(new BorderLayout());
        panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1, 50, 5));
        panel.add(new JLabel("Writing Policy File:"));
        getContentPane().add(panel, BorderLayout.CENTER);
        button = new JButton("Close");
        getContentPane().add(button, BorderLayout.SOUTH);
        button.setEnabled(false);
        button.addActionListener(this);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(20,
                                                                  20,
                                                                  20,
                                                                  20));
    }

    public void addText(String text) {
        panel.add(new JLabel(text));
        pack();
        validate();
    }

    public void finish() {
        button.setEnabled(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Close")) {
            setVisible(false);
            dispose();
        }
    }
}
