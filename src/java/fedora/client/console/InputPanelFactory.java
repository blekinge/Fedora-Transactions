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

/**
 * @author Chris Wilper
 */
public abstract class InputPanelFactory {

    public static InputPanel getPanel(Class cl) {
        if (cl.getName().equals("java.lang.String")) {
            return new StringInputPanel();
        }
        if (cl.getName().equals("[B")) {
            return new ByteArrayInputPanel(true);
        }
        if (cl.getName().equals("boolean")) {
            return new BooleanInputPanel(true);
        }
        if (cl.getName().equals("java.lang.Boolean")) {
            return new BooleanInputPanel(false);
        }
        if (cl.getName().equals("java.util.Date")) {
            return new DateTimeInputPanel();
        }
        if (cl.getName().equals("org.apache.axis.types.NonNegativeInteger")) {
            return new NonNegativeIntegerInputPanel();
        }
        if (cl.getName().startsWith("[L")) {
            try {
                return new ArrayInputPanel(Class.forName(cl.getName()
                        .substring(2, cl.getName().length() - 1)));
            } catch (ClassNotFoundException cnfe) {
                // will fall through as unrecognized
            }
        }
        System.out.println("Unrecognized type: " + cl.getName());
        return NullInputPanel.getInstance();
    }

}
