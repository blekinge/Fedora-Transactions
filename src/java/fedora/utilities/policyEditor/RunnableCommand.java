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

/**
 * @author Robert Haschart
 */
public class RunnableCommand
        implements Runnable {

    protected Object parm1 = null;

    protected Object parm2 = null;

    protected Object parm3 = null;

    protected Object parm4 = null;

    protected Object result = null;

    public RunnableCommand() {
    }

    public RunnableCommand(Object p1) {
        parm1 = p1;
    }

    public RunnableCommand(Object p1, Object p2) {
        parm1 = p1;
        parm2 = p2;
    }

    public RunnableCommand(Object p1, Object p2, Object p3) {
        parm1 = p1;
        parm2 = p2;
        parm3 = p3;
    }

    public RunnableCommand(Object p1, Object p2, Object p3, Object p4) {
        parm1 = p1;
        parm2 = p2;
        parm3 = p3;
        parm4 = p4;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(String string) {
        result = string;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
    }
}
