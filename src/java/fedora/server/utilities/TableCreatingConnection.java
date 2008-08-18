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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Iterator;

/**
 * A ConnectionWrapper that creates tables on the target database given a
 * TableSpec.
 * 
 * @author Chris Wilper
 */
public class TableCreatingConnection
        extends ConnectionWrapper {

    private final DDLConverter m_converter;

    /**
     * Constructs a TableCreatingConnection.
     * 
     * @param wrapped
     *        The wrapped connection.
     * @param converter
     *        A converter that can translate from a TableSpec to DB-specific
     *        DDL.
     */
    public TableCreatingConnection(Connection wrapped, DDLConverter converter) {
        super(wrapped);
        m_converter = converter;
    }

    /**
     * Get the DDLConverter this TableCreatingConnection works with.
     * 
     * @return The converter.
     */
    public DDLConverter getDDLConverter() {
        return m_converter;
    }

    /**
     * Creates a table in the target database.
     * <p>
     * </p>
     * This method may execute more than one update command and it ignores the
     * transaction state of the connection.
     * 
     * @param spec
     *        A description of the table to be created.
     */
    public void createTable(TableSpec spec) throws SQLException {
        Statement s = createStatement();
        Iterator<String> iter = m_converter.getDDL(spec).iterator();
        while (iter.hasNext()) {
            String updateSQL = iter.next();
            s.executeUpdate(updateSQL);
        }
    }

}
