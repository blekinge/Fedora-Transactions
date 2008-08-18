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

import java.util.Comparator;
import java.util.Vector;

/**
 * ColumnComparator.
 * 
 * <p>NOTICE: Portions created by Claude Duguay are Copyright &copy; 
 * Claude Duguay, originally made available at 
 * http://www.fawcette.com/javapro/2002_08/magazine/columns/visualcomponents/
 * 
 * @author Claude Duguay
 * @author Chris Wilper
 */
public class ColumnComparator
        implements Comparator {

    protected int index;

    protected boolean ascending;

    public ColumnComparator(int index, boolean ascending) {
        this.index = index;
        this.ascending = ascending;
    }

    public int compare(Object one, Object two) {
        if (one instanceof Vector && two instanceof Vector) {
            Vector vOne = (Vector) one;
            Vector vTwo = (Vector) two;
            Object oOne = vOne.elementAt(index);
            Object oTwo = vTwo.elementAt(index);
            if (oOne instanceof Comparable && oTwo instanceof Comparable) {
                Comparable cOne = (Comparable) oOne;
                Comparable cTwo = (Comparable) oTwo;
                if (ascending) {
                    return cOne.compareTo(cTwo);
                } else {
                    return cTwo.compareTo(cOne);
                }
            }
        }
        return 1;
    }
}
