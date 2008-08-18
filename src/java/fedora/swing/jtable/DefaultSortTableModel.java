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

import java.util.Collections;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * DefaultSortTableModel.
 * 
 * <p>NOTICE: Portions created by Claude Duguay are Copyright &copy; 
 * Claude Duguay, originally made available at 
 * http://www.fawcette.com/javapro/2002_08/magazine/columns/visualcomponents/
 * 
 * @author Claude Duguay
 * @author Chris Wilper
 */
public class DefaultSortTableModel
        extends DefaultTableModel
        implements SortTableModel {

    private static final long serialVersionUID = 1L;

    public DefaultSortTableModel() {
    }

    public DefaultSortTableModel(int rows, int cols) {
        super(rows, cols);
    }

    public DefaultSortTableModel(Object[][] data, Object[] names) {
        super(data, names);
    }

    public DefaultSortTableModel(Object[] names, int rows) {
        super(names, rows);
    }

    public DefaultSortTableModel(Vector names, int rows) {
        super(names, rows);
    }

    public DefaultSortTableModel(Vector data, Vector names) {
        super(data, names);
    }

    public boolean isSortable(int col) {
        // return true; // FIXME: columns can't be sorted till the
        // how-do-i-get-the-pid-if-its-not-part-of-the-table-model-and-the-model-has-been-sorted
        // problem is solved
        return false;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void sortColumn(int col, boolean ascending) {
        Collections.sort(getDataVector(), new ColumnComparator(col, ascending));
    }
}
