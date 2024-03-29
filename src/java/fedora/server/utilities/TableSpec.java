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

import java.io.IOException;
import java.io.InputStream;

import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import fedora.server.errors.InconsistentTableSpecException;

/**
 * A holder of table specification information that helps in producing 
 * RDBMS-generic DDL "CREATE TABLE" commands.
 * 
 * <p>An application constructs a TableSpec without regard to the underlying
 * database kind, and then the TableSpec is converted to a DB-specific CREATE
 * TABLE DDL command by a DDLConverter before the command is issued via JDBC.
 * 
 * @author Chris Wilper
 */
public class TableSpec {

    private final String m_name;

    private final List<ColumnSpec> m_columnSpecs;

    private final String m_primaryColumnName;

    private String m_type;

    /**
     * Constructs a TableSpec given a name, a set of ColumnSpecs, and the name
     * of the primary key column.
     * 
     * @param name
     *        The table name.
     * @param columnSpecs
     *        ColumnSpec objects describing columns in the table.
     * @param primaryColumnName
     *        The column that is the primary key for the table.
     * @throws InconsistentTableSpecException
     *         if inconsistencies are detected in table specifications.
     */
    public TableSpec(String name, List<ColumnSpec> columnSpecs, String primaryColumnName)
            throws InconsistentTableSpecException {
        m_name = name;
        m_columnSpecs = columnSpecs;
        m_primaryColumnName = primaryColumnName;
        assertConsistent();
    }

    /**
     * Constructs a TableSpec given a name, a set of ColumnSpecs, the name of
     * the primary key column, and a table type. Table type specification is not
     * supported by all RDBMS software, and is usually software-specific. When a
     * tableSpec is used to create a table, if the type is understood it is
     * used. Otherwise it is ignored.
     * 
     * @param name
     *        The table name.
     * @param columnSpecs
     *        ColumnSpec objects describing columns in the table.
     * @param primaryColumnName
     *        The column that is the primary key for the table.
     * @param type
     *        The table type.
     * @throws InconsistentTableSpecException
     *         if inconsistencies are detected in table specifications.
     */
    public TableSpec(String name,
                     List<ColumnSpec> columnSpecs,
                     String primaryColumnName,
                     String type)
            throws InconsistentTableSpecException {
        m_name = name;
        m_columnSpecs = columnSpecs;
        m_primaryColumnName = primaryColumnName;
        m_type = type;
        assertConsistent();
    }

    /**
     * Gets a TableSpec for each table element in the stream, where the stream
     * contains a valid XML document containing one or more table elements,
     * wrapped in the root element.
     * 
     * <p>
     * Input is of the form:
     * 
     * <pre>
     * &lt;database&gt;
     *   &lt;table name=&quot;<i>tableName</i>&quot; primaryKey=&quot;<i>primaryColumnName</i>&quot; type=&quot;<i>tableType</i>&quot;&gt;
     *     &lt;column name=&quot;<i>columnName</i>&quot;
     *                type=&quot;<i>typeSpec</i>&quot;
     *                autoIncrement=&quot;<i>isAutoIncremented</i>&quot;
     *                index=&quot;<i>indexName</i>&quot;
     *                notNull=&quot;<i>isNotNull</i>&quot;
     *                unique=&quot;<i>isUnique</i>&quot;
     *                default=&quot;<i>defaultValue</i>&quot;
     *                foreignKey=&quot;<i>foreignTableName.columnName onDeleteAction</i>&quot;/&gt;
     *   &lt;/table&gt;
     * &lt;/database&gt;
     * </pre>
     * 
     * About the attributes:
     * <ul>
     * <li> <b>tableName</b> - The desired name of the table.
     * <li> <b>primaryColumnName</b> - Identifies column that is the primary
     * key for the table. A column that is a primary key must be notNull, and
     * can't be a foreign key.
     * <li> <b>type</b> - The table type, which is RDBMS-specific. See
     * TableSpec(String, Set, String, String) for detail.
     * <li> <b>columnName</b> - The name of the column.
     * <li> <b>typeSpec</b> - The value type of the column. For instance,
     * varchar(255). This is not checked for validity. See <a
     * href="ColumnSpec.html">ColumnSpec javadoc</a> for detail.
     * <li> <b>isAutoIncremented</b> - (true|false) Whether values in the
     * column should be automatically generated by the database upon insert.
     * This requires that the type be some numeric variant, and is
     * RDBMS-specific. NUMERIC will generally work.
     * <li> <b>indexName</b> - Specifies that an index should be created on
     * this column and provides the column name.
     * <li> <b>isNotNull</b> - (true|false) Whether input should be limited to
     * actual values.
     * <li> <b>isUnique</b> - (true|false) Whether input should be limited such
     * that all values in the column are unique.
     * <li> <b>default</b> - The value to be used when inserts don't specify a
     * value for the column. This cannot be specified with autoIncrement true.
     * <li> <b>foreignTableName.column</b> - Specifies that this is a foreign
     * key column and identifies the (primary key) column in the database
     * containing the rows that values in this column refer to. This cannot be
     * specified with autoIncrement true.
     * <li> <b>onDeleteAction</b> - Optionally specifies a "CASCADE" or "SET
     * NULL" action to be taken on matching rows in this table when a row from
     * the parent (foreign) table is deleted. If "CASCADE", matching rows in
     * this table are automatically deleted. If "SET NULL", this column's value
     * will be set to NULL for all matching rows. This value is not checked for
     * validity.
     * </ul>
     * 
     * @param in
     *        The xml-encoded table specs.
     * @return TableSpec objects.
     * @throws InconsistentTableSpecException
     *         if inconsistencies are detected in table specifications.
     * @throws IOException
     *         if an IO error occurs.
     */
    public static List<TableSpec> getTableSpecs(InputStream in)
            throws InconsistentTableSpecException, IOException {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            SAXParser parser = spf.newSAXParser();
            TableSpecDeserializer tsd = new TableSpecDeserializer();
            parser.parse(in, tsd);
            tsd.assertTableSpecsConsistent();
            return tsd.getTableSpecs();
        } catch (InconsistentTableSpecException itse) {
            throw itse;
        } catch (Exception e) {
            throw new IOException("Error parsing XML: " + e.getMessage());
        }
    }

    /**
     * Ensures that the TableSpec is internally consistent.
     * 
     * @throws InconsistentTableSpecException
     *         If it's inconsistent.
     */
    public void assertConsistent() throws InconsistentTableSpecException {
        if (1 == 2) {
            throw new InconsistentTableSpecException("hmm");
        }
    }

    /**
     * Gets the name of the table.
     * 
     * @return The name.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Gets the name of the primary key column.
     * 
     * @return The name.
     */
    public String getPrimaryColumnName() {
        return m_primaryColumnName;
    }

    /**
     * Gets the type of the table.
     * 
     * @return The name.
     */
    public String getType() {
        return m_type;
    }

    /**
     * Gets an iterator over the columns.
     * 
     * @return An Iterator over ColumnSpec objects.
     */
    public Iterator<ColumnSpec> columnSpecIterator() {
        return m_columnSpecs.iterator();
    }

}
