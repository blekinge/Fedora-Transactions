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

package fedora.client.batch;

import java.awt.Font;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

import fedora.client.Administrator;

import fedora.common.Constants;

import fedora.server.utilities.StreamUtility;

/**
 * A GUI interface for entering info required to perform a batch modify that
 * consists of a file containing the modify directives to be processed. A log
 * file is generated and saved in the client logs directory detailing the
 * events that occured during processing.
 * 
 * @author Ross Wayland
 */
public class BatchModify {

    private static String s_rootName = null;

    private static String s_logPath = null;

    private static PrintStream s_log = null;

    /**
     * Constructor for the class.
     */
    public BatchModify() {
        InputStream in = null;
        BatchModifyParser bmp = null;
        File file = null;
        long st = System.currentTimeMillis();
        long et = 0;

        try {
            JFileChooser browse = new JFileChooser(Administrator.getLastDir());
            browse.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int returnVal = browse.showOpenDialog(Administrator.getDesktop());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = browse.getSelectedFile();
                int n =
                        JOptionPane
                                .showConfirmDialog(Administrator.getDesktop(),
                                                   "Process modify directives in file: \n"
                                                           + file
                                                                   .getAbsolutePath()
                                                           + " ?\n",
                                                   "Run Batch Modify?",
                                                   JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    Administrator.setLastDir(file);
                    openLog("modify-batch");
                    in = new FileInputStream(file);
                    bmp =
                            new BatchModifyParser(Administrator.UPLOADER,
                                                  Administrator.APIM,
                                                  Administrator.APIA,
                                                  in,
                                                  s_log);
                }
            }
        } catch (Exception e) {
            Administrator
                    .showErrorDialog(Administrator.getDesktop(),
                                     "Error in Parsing Directives File.",
                                     e.getClass().getName()
                                             + " - "
                                             + (e.getMessage() == null ? "(no detail provided)"
                                                     : e.getMessage()),
                                     e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (s_log != null && bmp != null) {
                    et = System.currentTimeMillis();
                    if (bmp.getFailedCount() == -1) {
                        JOptionPane
                                .showMessageDialog(Administrator.getDesktop(),
                                                   bmp.getSucceededCount()
                                                           + " Modify Directives successfully processed.\n"
                                                           + "Parser Error.\n"
                                                           + "An Unknown number of Modify Directives were not processed.\n"
                                                           + "See log file for details of how many directives were\n"
                                                           + "processed before the fatal error occurred.\n"
                                                           + "Time elapsed: "
                                                           + getDuration(et
                                                                   - st));
                        s_log.println("  <summary>");
                        s_log
                                .println("    "
                                        + StreamUtility
                                                .enc(bmp.getSucceededCount()
                                                        + " modify directives successfully processed.\n"
                                                        + "    Parser error encountered.\n"
                                                        + "    An unknown number of modify directives were not processed.\n"
                                                        + "    Time elapsed: "
                                                        + getDuration(et - st)));
                        s_log.println("  </summary>");
                    } else {
                        JOptionPane.showMessageDialog(Administrator
                                .getDesktop(), bmp.getSucceededCount()
                                + " modify directives successfully ingested.\n"
                                + bmp.getFailedCount()
                                + " modify directives failed.\n"
                                + "See log file for details.\n"
                                + "Time elapsed: " + getDuration(et - st));
                        s_log.println("  <summary>");
                        s_log
                                .println("    "
                                        + StreamUtility
                                                .enc(bmp.getSucceededCount()
                                                        + " modify directives successfully processed.\n    "
                                                        + bmp.getFailedCount()
                                                        + " modify directives failed.\n"
                                                        + "    Time elapsed: "
                                                        + getDuration(et - st)));
                        s_log.println("  </summary>");
                    }
                    closeLog();
                    int n =
                            JOptionPane
                                    .showConfirmDialog(Administrator
                                                               .getDesktop(),
                                                       "A detailed log file was created at\n"
                                                               + s_logPath
                                                               + "\n\n"
                                                               + "View it now?",
                                                       "View Modify Batch Log?",
                                                       JOptionPane.YES_NO_OPTION);
                    if (n == JOptionPane.YES_OPTION) {
                        JTextComponent textEditor = new JTextArea();
                        textEditor.setFont(new Font("monospaced",
                                                    Font.PLAIN,
                                                    12));
                        textEditor.setText(fileAsString(s_logPath));
                        textEditor.setCaretPosition(0);
                        textEditor.setEditable(false);
                        JInternalFrame viewFrame =
                                new JInternalFrame("Viewing " + s_logPath,
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
                        s_log = null;
                        try {
                            viewFrame.setSelected(true);
                        } catch (java.beans.PropertyVetoException pve) {
                            // ignore
                        }
                    }
                }
            } catch (Exception e) {
                Administrator
                        .showErrorDialog(Administrator.getDesktop(),
                                         "Error",
                                         e.getClass().getName()
                                                 + " - "
                                                 + (e.getMessage() == null ? "(no detail provided)"
                                                         : e.getMessage()),
                                         e);
            }
        }
    }

    /**
     * Convert the duration time from milliseconds to standard hours, minutes,
     * and seconds format.
     * 
     * @param millis
     *        The time interval to convert in miliseconds.
     * @return A string with the converted time.
     */
    private static String getDuration(long millis) {
        long tsec = millis / 1000;
        long h = tsec / 60 / 60;
        long m = (tsec - h * 60 * 60) / 60;
        long s = tsec - h * 60 * 60 - m * 60;
        StringBuffer out = new StringBuffer();
        if (h > 0) {
            out.append(h + " hour");
            if (h > 1) {
                out.append('s');
            }
        }
        if (m > 0) {
            if (h > 0) {
                out.append(", ");
            }
            out.append(m + " minute");
            if (m > 1) {
                out.append('s');
            }
        }
        if (s > 0 || h == 0 && m == 0) {
            if (h > 0 || m > 0) {
                out.append(", ");
            }
            out.append(s + " second");
            if (s != 1) {
                out.append('s');
            }
        }
        return out.toString();
    }

    /**
     * Initializes the log file for writing.
     * 
     * @param rootName
     *        The name of the root element for the xml log file.
     * @throws Exception
     *         If any type of error occurs in trying to open the log file for
     *         writing.
     */
    private static void openLog(String rootName) throws Exception {
        s_rootName = rootName;
        String fileName =
                s_rootName + "-" + System.currentTimeMillis() + ".xml";
        File outFile;
        String fedoraHome = Constants.FEDORA_HOME;
        if (fedoraHome == null) {
            // to current dir
            outFile = new File(fileName);
        } else {
            // to client/log
            File logDir =
                    new File(new File(new File(fedoraHome), "client"), "logs");
            if (!logDir.exists()) {
                logDir.mkdir();
            }
            outFile = new File(logDir, fileName);

        }
        s_logPath = outFile.getPath();
        s_log = new PrintStream(new FileOutputStream(outFile), true, "UTF-8");
        s_log.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        s_log.println("<" + s_rootName + ">");
    }

    /**
     * Closes the log file.
     * 
     * @throws Exception
     *         If any type of error occurs in closing the log file.
     */
    private static void closeLog() throws Exception {
        s_log.println("</" + s_rootName + ">");
        s_log.close();
    }

    /**
     * Converts file into string.
     * 
     * @param path
     *        The absolute file path of the file.
     * @return The contents of the file as a string.
     * @throws Exception
     *         If any type of error occurs during the conversion.
     */
    private static String fileAsString(String path) throws Exception {
        StringBuffer buffer = new StringBuffer();
        InputStream fis = new FileInputStream(path);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        Reader in = new BufferedReader(isr);
        int ch;
        while ((ch = in.read()) > -1) {
            buffer.append((char) ch);
        }
        in.close();
        return buffer.toString();
    }

}
