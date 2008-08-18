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

package fedora.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Bill Niebel
 */
public class BatchOutput
        extends JInternalFrame {

    private static final long serialVersionUID = 1L;

    JTextArea jTextArea = null;

    String directoryPath = null; //2003.12.03 niebel

    public BatchOutput(String title) {
        super(title, true, //resizable
              true, //TITLE_PROPERTY
              true, //maximizable
              true);//iconifiable
        setVisible(false);
        jTextArea = new JTextArea();
        jTextArea.setEditable(false);
        JScrollPane jScrollPane = new JScrollPane(jTextArea);
        getContentPane().add(jScrollPane);
        setFrameIcon(new ImageIcon(this.getClass().getClassLoader()
                .getResource("images/standard/general/New16.gif")));
        setSize(400, 400);
    }

    public JTextArea getJTextArea() {
        return jTextArea;
    }

    //2003.12.03 niebel vvvvv -- duplicate output to file	
    /**
     * extract parent directory from filepath of pidsfile, store it as directory
     * where processing progress report will be written. see also
     * BatchBuildGUI.java, BatchBuildIngestGUI.java, and BatchIngestGUI.java,
     * each of which calls setDirectoryPath()
     */
    public void setDirectoryPath(String pidsfilepath) {
        File pidsfile = new File(pidsfilepath);
        directoryPath = pidsfile.getParent();
    }

    /**
     * write processing progress report to directory stored by
     * setDirectoryPath(). contents is jTextArea.getText(); filename based on
     * current time. see also BatchThread.java, which calls flush2file().
     */
    public void flush2file() {
        StringBuffer filename = new StringBuffer();
        {
            Calendar now = Calendar.getInstance();
            filename.append(now.get(Calendar.YEAR));
            int month = now.get(Calendar.MONTH) + 1;
            filename.append((month < 10 ? "0" : "") + month);
            int day = now.get(Calendar.DAY_OF_MONTH);
            filename.append((day < 10 ? "0" : "") + day);
            int militaryHour = now.get(Calendar.HOUR_OF_DAY);
            filename
                    .append("-" + (militaryHour < 10 ? "0" : "") + militaryHour);
            int minute = now.get(Calendar.MINUTE);
            filename.append((minute < 10 ? "0" : "") + minute);
            int second = now.get(Calendar.SECOND);
            filename.append((second < 10 ? "0" : "") + second);
            int milliseconds = now.get(Calendar.MILLISECOND);
            filename
                    .append("-"
                            + (milliseconds < 10 ? "00"
                                    : milliseconds < 100 ? "0" : "")
                            + milliseconds);
            filename.append(".txt");
        }
        try {
            File file = new File(directoryPath, filename.toString());
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            PrintStream userLog = new PrintStream(fileOutputStream);
            userLog.print(jTextArea.getText());
            userLog.close();
        } catch (FileNotFoundException e) {
            jTextArea.append("COULD NOT WRITE PROCESSING REPORT TO FILE!!!\n");
        }
    }
    //2003.12.03 niebel ^^^^^
}
