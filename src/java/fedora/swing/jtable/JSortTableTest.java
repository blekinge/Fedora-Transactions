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

package fedora.swing.jtable;

import java.awt.Dimension;
import java.awt.GridLayout;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * JSortTableTest.
 * 
 * <p>NOTICE: Portions created by Claude Duguay are Copyright &copy; 
 * Claude Duguay, originally made available at 
 * http://www.fawcette.com/javapro/2002_08/magazine/columns/visualcomponents/
 * 
 * @author Claude Duguay
 * @author Chris Wilper
 */
public class JSortTableTest
        extends JPanel {

    private static final long serialVersionUID = 1L;

    public JSortTableTest() {
        setLayout(new GridLayout());
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        setPreferredSize(new Dimension(400, 400));
        add(new JScrollPane(new JSortTable(makeModel())));
    }

    protected SortTableModel makeModel() {
        Vector data = new Vector();
        for (int i = 0; i < 25; i++) {
            Vector row = new Vector();
            for (int j = 0; j < 5; j++) {
                row.add(new Integer((int) (Math.random() * 256)));
            }
            data.add(row);
        }

        Vector names = new Vector();
        names.add("One");
        names.add("Two");
        names.add("Three");
        names.add("Four");
        names.add("Five");

        return new DefaultSortTableModel(data, names);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("JSortTable Test");
        frame.getContentPane().setLayout(new GridLayout());
        frame.getContentPane().add(new JSortTableTest());
        frame.pack();
        frame.setVisible(true);
    }
}
