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

import java.io.PrintStream;

import java.util.Properties;

import javax.swing.JTextArea;

import fedora.client.batch.BatchTool;

/**
 * @author Bill Niebel
 */
public class BatchThread
        extends Thread {

    private String leadText = "";

    private Properties properties = null;

    private final Properties nullProperties = null;

    private BatchOutput batchOutput = null;

    private JTextArea jTextArea = null;

    private PrintStream originalOut = null;

    private PrintStream originalErr = null;

    private PrintStream printStream = null;

    public BatchThread(BatchOutput batchOutput,
                       JTextArea jTextArea,
                       String leadText)
            throws Exception {
        this.batchOutput = batchOutput;
        this.jTextArea = jTextArea;
        this.leadText = leadText;
        BatchOutputCatcher batchOutputCatcher =
                new BatchOutputCatcher(jTextArea);
        printStream = new PrintStream(batchOutputCatcher, true);
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void run() {
        try {
            jTextArea.setText(leadText + "\n");
            originalOut = System.out;
            originalErr = System.err;
            System.setOut(printStream);
            System.setErr(printStream);
            batchOutput.setVisible(true);
            BatchTool batchTool =
                    new BatchTool(properties, nullProperties, nullProperties);
            batchTool.prep();
            batchTool.process();
        } catch (Exception e) {
        } finally {
            System.setOut(originalOut);
            originalOut = null;
            System.setErr(originalErr);
            originalErr = null;
            batchOutput.flush2file(); //2003.12.03 niebel -- duplicate output to file
        }
    }
}
