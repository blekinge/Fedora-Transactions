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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class InstallOptions {

    public static final String INSTALL_TYPE = "install.type";

    public static final String FEDORA_HOME = "fedora.home";

    public static final String FEDORA_SERVERHOST = "fedora.serverHost";

    public static final String APIA_AUTH_REQUIRED = "apia.auth.required";

    public static final String SSL_AVAILABLE = "ssl.available";

    public static final String APIA_SSL_REQUIRED = "apia.ssl.required";

    public static final String APIM_SSL_REQUIRED = "apim.ssl.required";

    public static final String SERVLET_ENGINE = "servlet.engine";

    public static final String USING_JBOSS = "jboss";

    public static final String TOMCAT_HOME = "tomcat.home";

    public static final String FEDORA_ADMIN_PASS = "fedora.admin.pass";

    public static final String TOMCAT_SHUTDOWN_PORT = "tomcat.shutdown.port";

    public static final String TOMCAT_HTTP_PORT = "tomcat.http.port";

    public static final String TOMCAT_SSL_PORT = "tomcat.ssl.port";

    public static final String KEYSTORE_FILE = "keystore.file";

    public static final String KEYSTORE_PASSWORD = "keystore.password";

    public static final String KEYSTORE_TYPE = "keystore.type";

    public static final String DATABASE = "database";

    public static final String DATABASE_DRIVER = "database.driver";

    public static final String DATABASE_JDBCURL = "database.jdbcURL";

    public static final String DATABASE_DRIVERCLASS =
            "database.jdbcDriverClass";

    public static final String DATABASE_USERNAME = "database.username";

    public static final String DATABASE_PASSWORD = "database.password";

    public static final String XACML_ENABLED = "xacml.enabled";

    public static final String RI_ENABLED = "ri.enabled";

    public static final String REST_ENABLED = "rest.enabled";
    
    public static final String MESSAGING_ENABLED = "messaging.enabled";
    
    public static final String MESSAGING_URI = "messaging.uri";
    
    public static final String DEPLOY_LOCAL_SERVICES = "deploy.local.services";

    public static final String UNATTENDED = "unattended";

    public static final String DATABASE_UPDATE = "database.update";

    public static final String DEFAULT = "default";

    public static final String INSTALL_QUICK = "quick";

    public static final String INSTALL_CLIENT = "client";

    public static final String INCLUDED = "included";

    public static final String MCKOI = "mckoi";

    public static final String MYSQL = "mysql";

    public static final String ORACLE = "oracle";

    public static final String POSTGRESQL = "postgresql";

    public static final String OTHER = "other";

    public static final String EXISTING_TOMCAT = "existingTomcat";

    private final Map<String, String> _map;

    private final Distribution _dist;

    /**
     * Initialize options from the given map of String values, keyed by option
     * id.
     */
    public InstallOptions(Distribution dist, Map<String, String> map)
            throws OptionValidationException {
        _dist = dist;
        _map = map;

        applyDefaults();
        validateAll();
    }

    /**
     * Initialize options interactively, via input from the console.
     */
    public InstallOptions(Distribution dist)
            throws InstallationCancelledException {
        _dist = dist;
        _map = new HashMap<String, String>();

        System.out.println();
        System.out.println("***********************");
        System.out.println("  Fedora Installation ");
        System.out.println("***********************");
        System.out.println();
        System.out
                .println("To install Fedora, please answer the following questions.");
        System.out
                .println("Enter CANCEL at any time to abort the installation.");
        System.out
                .println("Detailed installation instructions are available at:");
        System.out.println("\thttp://www.fedora.info/download/");
        System.out.println();

        inputOption(INSTALL_TYPE);
        inputOption(FEDORA_HOME);

        if (getValue(INSTALL_TYPE).equals(INSTALL_CLIENT)) {
            return;
        }

        inputOption(FEDORA_ADMIN_PASS);

        String fedoraHome =
                new File(getValue(InstallOptions.FEDORA_HOME))
                        .getAbsolutePath();
        String includedJDBCURL =
                "jdbc:mckoi:local://" + fedoraHome + "/"
                        + Distribution.MCKOI_BASENAME
                        + "/db.conf?create_or_boot=true";

        if (getValue(INSTALL_TYPE).equals(INSTALL_QUICK)) {
            // See the defaultValues defined in OptionDefinition.properties
            // for the null values below
            _map.put(FEDORA_SERVERHOST, null); // localhost
            _map.put(APIA_AUTH_REQUIRED, null); // false
            _map.put(SSL_AVAILABLE, Boolean.toString(false));
            _map.put(APIM_SSL_REQUIRED, Boolean.toString(false));
            _map.put(SERVLET_ENGINE, null); // included
            _map.put(USING_JBOSS, null); // included
            _map.put(TOMCAT_HOME, fedoraHome + File.separator + "tomcat");
            _map.put(TOMCAT_HTTP_PORT, null); // 8080
            _map.put(TOMCAT_SHUTDOWN_PORT, null); // 8005
            _map.put(XACML_ENABLED, Boolean.toString(false));
            _map.put(RI_ENABLED, null); // false
            _map.put(REST_ENABLED, null); // false
            _map.put(DATABASE, INCLUDED); // included
            _map.put(DATABASE_DRIVER, INCLUDED);
            _map.put(DATABASE_USERNAME, "fedoraAdmin");
            _map.put(DATABASE_PASSWORD, "fedoraAdmin");
            _map.put(DATABASE_JDBCURL, includedJDBCURL);
            _map.put(DATABASE_DRIVERCLASS, "com.mckoi.JDBCDriver");
            _map.put(MESSAGING_ENABLED, Boolean.toString(false));
            _map.put(DEPLOY_LOCAL_SERVICES, null); // true
            applyDefaults();
            return;
        }

        inputOption(FEDORA_SERVERHOST);
        inputOption(APIA_AUTH_REQUIRED);
        inputOption(SSL_AVAILABLE);

        boolean sslAvailable = getBooleanValue(SSL_AVAILABLE, true);
        if (sslAvailable) {
            inputOption(APIA_SSL_REQUIRED);
            inputOption(APIM_SSL_REQUIRED);
        }
        inputOption(SERVLET_ENGINE);
        if (getValue(SERVLET_ENGINE).equals(OTHER)) {
            inputOption(USING_JBOSS);
        } else {
            inputOption(TOMCAT_HOME);
            inputOption(TOMCAT_HTTP_PORT);
            inputOption(TOMCAT_SHUTDOWN_PORT);
            if (sslAvailable) {
                inputOption(TOMCAT_SSL_PORT);
                if (getValue(SERVLET_ENGINE).equals(INCLUDED)
                        || getValue(SERVLET_ENGINE).equals(EXISTING_TOMCAT)) {
                    inputOption(KEYSTORE_FILE);
                    if (!getValue(KEYSTORE_FILE).equals(INCLUDED)) {
                        inputOption(KEYSTORE_PASSWORD);
                        inputOption(KEYSTORE_TYPE);
                    }
                }
            }
        }

        // Database selection
        // Ultimately we want to provide the following properties:
        //   database, database.username, database.password, 
        //   database.driver, database.jdbcURL, database.jdbcDriverClass
        inputOption(DATABASE);

        String db = DATABASE + "." + getValue(DATABASE);

        // The following lets us use the database-specific OptionDefinition.properties
        // for the user prompts and defaults
        String driver = db + ".driver";
        String jdbcURL = db + ".jdbcURL";
        String jdbcDriverClass = db + ".jdbcDriverClass";

        if (getValue(DATABASE).equals(INCLUDED)) {
            _map.put(DATABASE_USERNAME, "fedoraAdmin");
            _map.put(DATABASE_PASSWORD, "fedoraAdmin");
            _map.put(DATABASE_DRIVER, INCLUDED);
            _map.put(DATABASE_JDBCURL, includedJDBCURL);
            _map.put(DATABASE_DRIVERCLASS, "com.mckoi.JDBCDriver");
        } else {
            boolean dbValidated = false;
            while (!dbValidated) {
                inputOption(driver);
                _map.put(DATABASE_DRIVER, getValue(driver));
                inputOption(DATABASE_USERNAME);
                inputOption(DATABASE_PASSWORD);
                inputOption(jdbcURL);
                _map.put(DATABASE_JDBCURL, getValue(jdbcURL));
                inputOption(jdbcDriverClass);
                _map.put(DATABASE_DRIVERCLASS, getValue(jdbcDriverClass));
                dbValidated = validateDatabaseConnection();
            }
        }
        
        inputOption(XACML_ENABLED);
        inputOption(RI_ENABLED);
        inputOption(REST_ENABLED);  
        inputOption(MESSAGING_ENABLED);
        if(getValue(MESSAGING_ENABLED).equals(Boolean.toString(true))) {                     
            inputOption(MESSAGING_URI);
        }            
        
        // If using an "other" servlet container, we can't automatically deploy
        // the local services, so don't even bother to ask.
        if (getValue(SERVLET_ENGINE).equals(OTHER)) {
            _map.put(DEPLOY_LOCAL_SERVICES, "false");
        } else {
            inputOption(DEPLOY_LOCAL_SERVICES);
        }
    }

    private String dashes(int len) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < len; i++) {
            out.append('-');
        }
        return out.toString();
    }

    /**
     * Get the indicated option from the console. Continue prompting until the
     * value is valid, or the user has indicated they want to cancel the
     * installation.
     */
    private void inputOption(String optionId)
            throws InstallationCancelledException {

        OptionDefinition opt = OptionDefinition.get(optionId, this);

        if (opt.getLabel() == null || opt.getLabel().length() == 0) {
            throw new InstallationCancelledException(optionId
                    + " is missing label (check OptionDefinition.properties?)");
        }
        System.out.println(opt.getLabel());
        System.out.println(dashes(opt.getLabel().length()));
        System.out.println(opt.getDescription());

        System.out.println();

        String[] valids = opt.getValidValues();
        if (valids != null) {
            System.out.print("Options : ");
            for (int i = 0; i < valids.length; i++) {
                if (i > 0) {
                    System.out.print(", ");
                }
                System.out.print(valids[i]);
            }
            System.out.println();
        }

        String defaultVal = opt.getDefaultValue();
        if (valids != null || defaultVal != null) {
            System.out.println();
        }

        boolean gotValidValue = false;

        while (!gotValidValue) {

            System.out.print("Enter a value ");
            if (defaultVal != null) {
                System.out.print("[default is " + defaultVal + "] ");
            }
            System.out.print("==> ");

            String value = readLine().trim();
            if (value.length() == 0 && defaultVal != null) {
                value = defaultVal;
            }
            System.out.println();
            if (value.equalsIgnoreCase("cancel")) {
                throw new InstallationCancelledException("Cancelled by user.");
            }

            try {
                opt.validateValue(value);
                gotValidValue = true;
                _map.put(optionId, value);
                System.out.println();
            } catch (OptionValidationException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

    }

    private String readLine() {
        try {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(System.in));
            return reader.readLine();
        } catch (Exception e) {
            throw new RuntimeException("Error: Unable to read from STDIN");
        }
    }

    /**
     * Dump all options (including any defaults that were applied) to the given
     * stream, in java properties file format. The output stream remains open
     * after this method returns.
     */
    public void dump(OutputStream out) throws IOException {

        Properties props = new Properties();
        Iterator<String> iter = _map.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            props.setProperty(key, getValue(key));
        }

        props.store(out, "Install Options");
    }

    /**
     * Get the value of the given option, or <code>null</code> if it doesn't
     * exist.
     */
    public String getValue(String name) {
        return _map.get(name);
    }

    /**
     * Get the value of the given option as an integer, or the given default
     * value if unspecified.
     * 
     * @throws NumberFormatException
     *         if the value is specified, but cannot be parsed as an integer.
     */
    public int getIntValue(String name, int defaultValue)
            throws NumberFormatException {

        String value = getValue(name);

        if (value == null) {
            return defaultValue;
        } else {
            return Integer.parseInt(value);
        }
    }

    /**
     * Get the value of the given option as a boolean, or the given default
     * value if unspecified. If specified, the value is assumed to be
     * <code>true</code> if given as "true", regardless of case. All other
     * values are assumed to be <code>false</code>.
     */
    public boolean getBooleanValue(String name, boolean defaultValue) {

        String value = getValue(name);

        if (value == null) {
            return defaultValue;
        } else {
            return value.equals("true");
        }
    }

    /**
     * Get an iterator of the names of all specified options.
     */
    public Iterator<String> getOptionNames() {
        return _map.keySet().iterator();
    }

    /**
     * Apply defaults to the options, where possible.
     */
    private void applyDefaults() {
        Iterator<String> names = getOptionNames();
        while (names.hasNext()) {
            String name = names.next();
            String val = _map.get(name);
            if (val == null || val.length() == 0) {
                OptionDefinition opt = OptionDefinition.get(name, this);
                _map.put(name, opt.getDefaultValue());
            }
        }
    }

    /**
     * Validate the options, assuming defaults have already been applied.
     * Validation for a given option might entail more than a syntax check. It
     * might check whether a given directory exists, for example.
     */
    private void validateAll() throws OptionValidationException {
        boolean unattended = getBooleanValue(UNATTENDED, false);
        Iterator<String> keys = getOptionNames();
        while (keys.hasNext()) {
            String optionId = keys.next();
            OptionDefinition opt = OptionDefinition.get(optionId, this);
            opt.validateValue(getValue(optionId), unattended);
        }
    }

    private boolean validateDatabaseConnection() {
        String database = getValue(DATABASE);
        if (database.equals(InstallOptions.INCLUDED)) {
            return true;
        }

        Database db = new Database(_dist, this);

        try {
            // validate the user input by attempting a database connection
            db.test();
            // check if we need to update old table
            if (db.usesDOTable()) {
                inputOption(DATABASE_UPDATE);
            }

            db.close();
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}
