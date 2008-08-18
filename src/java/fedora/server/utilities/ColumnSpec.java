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

/**
 * @author Chris Wilper
 */
public class ColumnSpec {

    private final String m_name;

    private final boolean m_binary;

    private final String m_type;

    private final String m_defaultValue;

    private final boolean m_isAutoIncremented;

    private final String m_indexName;

    private final boolean m_isUnique;

    private final boolean m_isNotNull;

    private final String m_foreignTableName;

    private final String m_foreignColumnName;

    private final String m_onDeleteAction;

    public ColumnSpec(String name,
                      String type,
                      boolean binary,
                      String defaultValue,
                      boolean isAutoIncremented,
                      String indexName,
                      boolean isUnique,
                      boolean isNotNull,
                      String foreignTableName,
                      String foreignColumnName,
                      String onDeleteAction) {
        m_name = name;
        m_type = type;
        m_binary = binary;
        m_defaultValue = defaultValue;
        m_isAutoIncremented = isAutoIncremented;
        m_indexName = indexName;
        m_isUnique = isUnique;
        m_isNotNull = isNotNull;
        m_foreignTableName = foreignTableName;
        m_foreignColumnName = foreignColumnName;
        m_onDeleteAction = onDeleteAction;
    }

    public String getName() {
        return m_name;
    }

    public boolean getBinary() {
        return m_binary;
    }

    public String getType() {
        return m_type;
    }

    public String getForeignTableName() {
        return m_foreignTableName;
    }

    public String getForeignColumnName() {
        return m_foreignColumnName;
    }

    public String getOnDeleteAction() {
        return m_onDeleteAction;
    }

    public boolean isUnique() {
        return m_isUnique;
    }

    public boolean isNotNull() {
        return m_isNotNull;
    }

    public String getIndexName() {
        return m_indexName;
    }

    public boolean isAutoIncremented() {
        return m_isAutoIncremented;
    }

    public String getDefaultValue() {
        return m_defaultValue;
    }

}
