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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * A PrintStream that sends its output to Administrator.WATCH_AREA, the
 * JTextArea of the Tools->Advanced->STDOUT/STDERR window. This is used for
 * redirecting System.out/err output to the UI.
 * 
 * @author Chris Wilper
 */
public class WatchPrintStream
        extends PrintStream {

    /** Output is buffered here until a call to println(String) */
    private final ByteArrayOutputStream m_out;

    public WatchPrintStream(ByteArrayOutputStream out) {
        super(out);
        m_out = out;
    }

    /**
     * Every time this is called, the buffer is cleared an output is sent to the
     * JTextArea.
     */
    @Override
    public void println(String str) {
        super.println(str);
        if (Administrator.WATCH_AREA != null) {
            String buf = m_out.toString();
            m_out.reset();
            Administrator.WATCH_AREA.append(buf);
        }
    }

}