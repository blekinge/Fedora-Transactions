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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import fedora.server.utilities.MethodInvokerThread;

/**
 * @author Chris Wilper
 */
public class InvokeDialogListener
        implements ActionListener {

    private final JDialog m_dialog;

    private final ConsoleCommandInvoker m_invoker;

    public InvokeDialogListener(JDialog dialog, ConsoleCommandInvoker invoker) {
        m_dialog = dialog;
        m_invoker = invoker;
    }

    public void actionPerformed(ActionEvent event) {
        m_dialog.setVisible(false);
        if (event.getActionCommand().equals("OK")) {
            try {
                MethodInvokerThread th =
                        new MethodInvokerThread(m_invoker,
                                                m_invoker
                                                        .getClass()
                                                        .getMethod("invoke",
                                                                   new Class[0]),
                                                new Object[0]);
                th.start();
            } catch (NoSuchMethodException nsme) {
                System.out
                        .println("No such method as invoke()? This Shouldnt happen!");
            }
        }
    }
}
