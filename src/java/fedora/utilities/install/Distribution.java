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

package fedora.utilities.install;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.Properties;

public abstract class Distribution {

    public static final String FEDORA_WAR = "fedora.war";

    public static final String IMAGEMANIP_WAR = "imagemanip.war";

    public static final String SAXON_WAR = "saxon.war";

    public static final String FOP_WAR = "fop.war";

    public static final String DEMO_WAR = "fedora-demo.war";

    public static final String FEDORA_HOME = "fedorahome.zip";

    public static final String KEYSTORE = "keystore";

    public static final String TRUSTSTORE = "truststore";

    public static final String DBSPEC = "DefaultDOManager.dbspec";

    public static final String TOMCAT;

    public static final String MCKOI;

    public static final String JDBC_MYSQL;

    public static final String JDBC_MCKOI;

    public static final String JDBC_POSTGRESQL;

    public static final String TOMCAT_BASENAME;

    public static final String MCKOI_BASENAME;

    public static final String COMMONS_COLLECTIONS;

    public static final String COMMONS_DBCP;

    public static final String COMMONS_POOL;

    public static final String LOG4J;

    private static Properties PROPS;
    static {
        // an up to date install.properties should be provided by the buildfile
        String path = "resources/install.properties";
        InputStream in =
                OptionDefinition.class.getClassLoader()
                        .getResourceAsStream(path);
        PROPS = new Properties();
        try {
            PROPS.load(in);
        } catch (Exception e) {
            System.err.println("ERROR: Unable to load required resource: "
                    + path);
            System.exit(1);
        }
        TOMCAT = PROPS.getProperty("install.tomcat");
        MCKOI = PROPS.getProperty("install.mckoi");
        JDBC_MCKOI = PROPS.getProperty("install.jdbc.mckoi");
        JDBC_MYSQL = PROPS.getProperty("install.jdbc.mysql");
        JDBC_POSTGRESQL = PROPS.getProperty("install.jdbc.postgresql");
        TOMCAT_BASENAME = PROPS.getProperty("install.tomcat.basename");
        MCKOI_BASENAME = PROPS.getProperty("install.mckoi.basename");

        COMMONS_COLLECTIONS = PROPS.getProperty("install.commons.collections");
        COMMONS_DBCP = PROPS.getProperty("install.commons.dbcp");
        COMMONS_POOL = PROPS.getProperty("install.commons.pool");

        LOG4J = PROPS.getProperty("install.log4j");
    }

    public abstract boolean contains(String path);

    public abstract InputStream get(String path) throws IOException;

    public abstract URL getURL(String path);

}
