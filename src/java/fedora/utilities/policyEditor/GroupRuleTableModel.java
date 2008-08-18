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

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class GroupRuleTableModel
        extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private final int columnCount;

    private int permitDenyOrBoth;

    private final int templatesOrRules;

    final public static int RULES = 0;

    final public static int TEMPLATES = 1;

    final public static int PERMIT = 0;

    final public static int DENY = 1;

    final public static int BOTH = 2;

    final private static String columnNames[] =
            {"Permit/Deny", "Name of User Group", "Description of User Group"};

    public GroupRuleTableModel() {
        super();
        columnCount = 3;
        permitDenyOrBoth = BOTH;
        templatesOrRules = RULES;
    }

    public GroupRuleTableModel(boolean showPermitDenyColumn,
                               int permitDenyOrBoth) {
        super();
        columnCount = showPermitDenyColumn ? 3 : 2;
        this.permitDenyOrBoth = permitDenyOrBoth;
        templatesOrRules = RULES;
    }

    public GroupRuleTableModel(int showTemplatesOrRules,
                               boolean showPermitDenyColumn,
                               int permitDenyOrBoth) {
        super();
        columnCount = showPermitDenyColumn ? 3 : 2;
        this.permitDenyOrBoth = permitDenyOrBoth;
        templatesOrRules = showTemplatesOrRules;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public Object getValueAt(int row, int column) {
        GroupRuleInfo rowinfo = getRow(row);
        if (columnCount == 3) {
            if (column == 0) {
                return rowinfo.getEffect() + " Access";
            }
            if (column == 1) {
                return rowinfo.getName();
            }
            if (column == 2) {
                return rowinfo.getDescription();
            }
        } else if (columnCount == 2) {
            if (column == 0) {
                return rowinfo.getName();
            }
            if (column == 1) {
                return rowinfo.getDescription();
            }
        }
        return null;
    }

    public void addRow(int permitOrDeny, GroupRuleInfo newgroup) {
        if (templatesOrRules == TEMPLATES) {
            return;
        }
        if (permitOrDeny == PERMIT) {
            GroupRuleInfo.permitRules.addElement(newgroup);
        } else {
            GroupRuleInfo.denyRules.addElement(newgroup);
        }
    }

    public GroupRuleInfo getRowWithParameters(int row, Vector templates) {
        int rowNum = -1;
        for (int i = 0; i < templates.size(); i++) {
            GroupRuleInfo tmp = (GroupRuleInfo) templates.elementAt(i);
            if (tmp.getNumParms() != 0) {
                rowNum++;
                if (rowNum == row) {
                    return tmp;
                }
            }
        }
        return null;
    }

    public int getRowNumWithParameters(int row, Vector templates) {
        int rowNum = -1;
        for (int i = 0; i < templates.size(); i++) {
            GroupRuleInfo tmp = (GroupRuleInfo) templates.elementAt(i);
            if (tmp.getNumParms() != 0) {
                rowNum++;
                if (rowNum == row) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int getRowCountWithParameters(Vector templates) {
        int rowNum = 0;
        for (int i = 0; i < templates.size(); i++) {
            GroupRuleInfo tmp = (GroupRuleInfo) templates.elementAt(i);
            if (tmp.getNumParms() != 0) {
                rowNum++;
            }
        }
        return rowNum;
    }

    public void deleteRowByNum(int row) {
        if (permitDenyOrBoth == PERMIT) {
            GroupRuleInfo.permitRules.removeElementAt(row);
        } else if (permitDenyOrBoth == DENY) {
            GroupRuleInfo.denyRules.removeElementAt(row);
        } else // BOTH
        {
            if (row < GroupRuleInfo.permitRules.size()) {
                GroupRuleInfo.permitRules.removeElementAt(row);
            } else {
                GroupRuleInfo.denyRules.removeElementAt(row
                        - GroupRuleInfo.permitRules.size());
            }
        }
    }

    public GroupRuleInfo getRow(int row) {
        GroupRuleInfo rowinfo;
        if (templatesOrRules == TEMPLATES) {
            if (permitDenyOrBoth == PERMIT) {
                rowinfo =
                        getRowWithParameters(row, GroupRuleInfo.permitTemplates);
            } else // if (permitDenyOrBoth == DENY)
            {
                rowinfo =
                        getRowWithParameters(row, GroupRuleInfo.denyTemplates);
            }
            return rowinfo;
        } else {
            if (permitDenyOrBoth == PERMIT) {
                rowinfo =
                        (GroupRuleInfo) GroupRuleInfo.permitRules
                                .elementAt(row);
            } else if (permitDenyOrBoth == DENY) {
                rowinfo =
                        (GroupRuleInfo) GroupRuleInfo.denyRules.elementAt(row);
            } else // BOTH
            {
                if (row < GroupRuleInfo.permitRules.size()) {
                    rowinfo =
                            (GroupRuleInfo) GroupRuleInfo.permitRules
                                    .elementAt(row);
                } else {
                    rowinfo =
                            (GroupRuleInfo) GroupRuleInfo.denyRules
                                    .elementAt(row
                                            - GroupRuleInfo.permitRules.size());
                }
            }
            return rowinfo;
        }
    }

    public int getRowNum(int row) {
        int num;
        if (templatesOrRules == TEMPLATES) {
            if (permitDenyOrBoth == PERMIT) {
                num =
                        getRowNumWithParameters(row,
                                                GroupRuleInfo.permitTemplates);
            } else // if (permitDenyOrBoth == DENY)
            {
                num = getRowNumWithParameters(row, GroupRuleInfo.denyTemplates);
            }
            return num;
        } else // not much point in this
        {
            return row;
        }
    }

    public int getRowCount() {
        if (templatesOrRules == TEMPLATES) {
            switch (permitDenyOrBoth) {
                case PERMIT:
                    return getRowCountWithParameters(GroupRuleInfo.permitTemplates);
                default:
                case DENY:
                    return getRowCountWithParameters(GroupRuleInfo.denyTemplates);
            }
        } else {
            switch (permitDenyOrBoth) {
                case PERMIT:
                    return GroupRuleInfo.permitRules.size();
                case DENY:
                    return GroupRuleInfo.denyRules.size();
                default:
                case BOTH:
                    return GroupRuleInfo.permitRules.size()
                            + GroupRuleInfo.denyRules.size();
            }
        }
    }

    public int setRowCount() {
        switch (permitDenyOrBoth) {
            case PERMIT:
                return GroupRuleInfo.permitRules.size();
            case DENY:
                return GroupRuleInfo.denyRules.size();
            default:
            case BOTH:
                return GroupRuleInfo.permitRules.size()
                        + GroupRuleInfo.denyRules.size();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public String getColumnName(int column) {
        if (columnCount == 3) {
            return columnNames[column];
        } else {
            return columnNames[column + 1];
        }
    }

    /**
     * @return Returns the permitDenyOrBoth.
     */
    public int getPermitDenyOrBoth() {
        return permitDenyOrBoth;
    }

    public boolean isPermit() {
        return permitDenyOrBoth == PERMIT;
    }

    /**
     * @param permitDenyOrBoth
     *        The permitDenyOrBoth to set.
     */
    public void setPermitDenyOrBoth(int permitDenyOrBoth) {
        if (permitDenyOrBoth != this.permitDenyOrBoth) {
            this.permitDenyOrBoth = permitDenyOrBoth;
            fireTableDataChanged();
        }
    }
}
