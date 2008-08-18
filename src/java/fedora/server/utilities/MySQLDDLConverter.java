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

package fedora.server.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A DDLConverter that works with MySQL.
 * 
 * @author Chris Wilper
 */
public class MySQLDDLConverter
        implements DDLConverter {

    public MySQLDDLConverter() {
    }

    public boolean supportsTableType() {
        return true;
    }

    public String getDropDDL(String command) {
        String[] parts = command.split(" ");
        String tableName = parts[2];
        return "DROP TABLE " + tableName;
    }

    public List<String> getDDL(TableSpec spec) {
        StringBuffer out = new StringBuffer();
        StringBuffer end = new StringBuffer();
        out.append("CREATE TABLE " + spec.getName() + " (\n");
        Iterator<ColumnSpec> csi = spec.columnSpecIterator();
        int csNum = 0;
        while (csi.hasNext()) {
            if (csNum > 0) {
                out.append(",\n");
            }
            csNum++;
            ColumnSpec cs = csi.next();
            out.append("  ");
            out.append(cs.getName());
            out.append(' ');
            if (cs.getType().equalsIgnoreCase("text")) {
                if (cs.getBinary()) {
                    out.append("blob");
                } else {
                    out.append(cs.getType());
                }
            } else {
                out.append(cs.getType());
                if (cs.getType().toLowerCase().startsWith("varchar")) {
                    if (cs.getBinary()) {
                        out.append(" BINARY");
                    }
                }
            }
            if (cs.isNotNull()) {
                out.append(" NOT NULL");
            }
            if (cs.isAutoIncremented()) {
                out.append(" auto_increment");
            }
            if (cs.getDefaultValue() != null) {
                out.append(" default '");
                out.append(cs.getDefaultValue());
                out.append("'");
            }
            if (cs.isUnique()) {
                if (!end.toString().equals("")) {
                    end.append(",\n");
                }
                end.append("  UNIQUE KEY ");
                end.append(cs.getName());
                end.append(" (");
                end.append(cs.getName());
                end.append(")");
            }
            if (cs.getIndexName() != null) {
                if (!end.toString().equals("")) {
                    end.append(",\n");
                }
                end.append("  KEY ");
                end.append(cs.getIndexName());
                end.append(" (");
                end.append(cs.getName());
                end.append(")");
            }
            if (cs.getForeignTableName() != null) {
                if (!end.toString().equals("")) {
                    end.append(",\n");
                }
                end.append("  FOREIGN KEY ");
                end.append(cs.getName());
                end.append(" (");
                end.append(cs.getName());
                end.append(") REFERENCES ");
                end.append(cs.getForeignTableName());
                end.append(" (");
                end.append(cs.getForeignColumnName());
                end.append(")");
                if (cs.getOnDeleteAction() != null) {
                    end.append(" ON DELETE ");
                    end.append(cs.getOnDeleteAction());
                }
            }
        }
        if (spec.getPrimaryColumnName() != null) {
            out.append(",\n  PRIMARY KEY (");
            out.append(spec.getPrimaryColumnName());
            out.append(")");
        }
        if (!end.toString().equals("")) {
            out.append(",\n");
            out.append(end);
        }
        out.append("\n");
        out.append(")");
        if (spec.getType() != null) {
            out.append(" TYPE=" + spec.getType());
        }
        ArrayList<String> l = new ArrayList<String>();
        l.add(out.toString());
        return l;
    }

}
