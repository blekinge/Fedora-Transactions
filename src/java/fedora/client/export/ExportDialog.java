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

package fedora.client.export;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import fedora.client.Administrator;
import fedora.client.utility.export.Export;

/**
 * Class to initiate an interactive export dialog for use by Fedora
 * Administrator. This class calls AutoExporter.class which is reponsible for
 * making the API-M SOAP calls for the export.
 * 
 * @author Chris Wilper
 */
public class ExportDialog {

    public static int ONE = 0;

    public static int MULTI = 1;

    // launch interactively via Administrator.java
    public ExportDialog(int kind) {
        try {
            JFileChooser browse = new JFileChooser(Administrator.getLastDir());
            browse.setDialogTitle("Export to which directory?");
            browse.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = browse.showOpenDialog(Administrator.getDesktop());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = browse.getSelectedFile();
                Administrator.setLastDir(file.getParentFile());
                ExportOptionsDialog optsDialog =
                        new ExportOptionsDialog("Select Options for Export");
                if (optsDialog.getFormatSelection() != null) {
                    if (kind == ONE) {
                        String pid =
                                JOptionPane
                                        .showInputDialog("Enter the PID of the object to export.");
                        if (pid != null && !pid.equals("")) {
                            Export.one(Administrator.APIA,
                                       Administrator.APIM,
                                       pid,
                                       optsDialog.getFormatSelection(),
                                       optsDialog.getContextSelection(),
                                       file);
                            JOptionPane.showMessageDialog(Administrator
                                    .getDesktop(), "Export succeeded.  PID='"
                                    + pid + "'.");
                        }
                    } else {

                        long st = System.currentTimeMillis();
                        int count =
                                Export.multi(Administrator.APIA,
                                             Administrator.APIM,
                                             optsDialog.getFormatSelection(),
                                             optsDialog.getContextSelection(),
                                             file);
                        long et = System.currentTimeMillis();
                        JOptionPane.showMessageDialog(Administrator
                                .getDesktop(), "Export of " + count
                                + " objects finished.\n" + "Time elapsed: "
                                + Export.getDuration(et - st));
                    }
                }
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg == null) {
                msg = e.getClass().getName();
            }
            Administrator.showErrorDialog(Administrator.getDesktop(),
                                          "Export Failure",
                                          msg,
                                          e);
        }
    }
}