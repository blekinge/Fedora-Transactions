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
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Properties;

public class OptionDefinition {

    private static Properties _PROPS;

    private final String _id;

    private final String _label;

    private final String _description;

    private final String[] _validValues;

    private final String _defaultValue;

    private final InstallOptions _options;

    static {
        String path = "fedora/utilities/install/OptionDefinition.properties";
        try {
            InputStream in =
                    OptionDefinition.class.getClassLoader()
                            .getResourceAsStream(path);
            _PROPS = new Properties();
            _PROPS.load(in);
        } catch (Exception e) {
            System.err.println("ERROR: Unable to load required resource: "
                    + path);
            System.exit(1);
        }
    }

    private OptionDefinition(String id,
                             String label,
                             String description,
                             String[] validValues,
                             String defaultValue,
                             InstallOptions options) {
        _id = id;
        _label = label;

        _description = description;

        _validValues = validValues;

        _defaultValue = defaultValue;

        _options = options;
    }

    /**
     * Get the definition of the identified option, or <code>null</code> if no
     * such definition exists.
     */
    public static OptionDefinition get(String id, InstallOptions options) {
        String label = _PROPS.getProperty(id + ".label");
        String description = _PROPS.getProperty(id + ".description");
        String[] validValues = getArray(id + ".validValues");
        String defaultValue = _PROPS.getProperty(id + ".defaultValue");

        // Use the environment variable FEDORA_HOME as the default, if defined
        if (id.equals(InstallOptions.FEDORA_HOME)) {
            String eFH = System.getenv("FEDORA_HOME");
            if (eFH != null && eFH.length() != 0) {
                defaultValue = eFH;
            }
        }

        // Use CATALINA_HOME as the default, if defined.
        if (id.equals(InstallOptions.TOMCAT_HOME)) {
            String eCH = System.getenv("CATALINA_HOME");
            if (eCH != null && eCH.length() != 0) {
                defaultValue = eCH;
            } else if (options.getValue(InstallOptions.SERVLET_ENGINE)
                    .equals(InstallOptions.INCLUDED)) {
                defaultValue =
                        options.getValue(InstallOptions.FEDORA_HOME)
                                + File.separator + "tomcat";
            }
        }
        return new OptionDefinition(id,
                                    label,
                                    description,
                                    validValues,
                                    defaultValue,
                                    options);
    }

    private static String[] getArray(String propName) {

        String value = _PROPS.getProperty(propName);
        if (value == null) {
            return null;
        } else {
            return value.trim().split(" ");
        }
    }

    public String getId() {
        return _id;
    }

    public String getLabel() {
        return _label;
    }

    public String getDescription() {
        return _description;
    }

    public String[] getValidValues() {
        return _validValues;
    }

    public String getDefaultValue() {
        return _defaultValue;
    }

    public void validateValue(String value) throws OptionValidationException {
        validateValue(value, false);
    }

    public void validateValue(String value, boolean unattended)
            throws OptionValidationException {
        if (value.length() == 0) {
            throw new OptionValidationException("Must specify a value", _id);
        }
        String[] valids = getValidValues();
        if (valids != null) {
            boolean isValid = false;
            for (String element : valids) {
                if (element.equals(value)) {
                    isValid = true;
                }
            }
            if (!isValid) {
                throw new OptionValidationException("Not a valid value: "
                        + value, _id);
            }
        } else {
            if (_id.equals(InstallOptions.FEDORA_HOME)) {
                File dir = new File(value);
                if (dir.isDirectory()) {
                    if (dir.listFiles().length != 0) {
                        if (unattended) {
                            System.out
                                    .println("WARNING: Overwriting existing directory: "
                                            + dir.getAbsolutePath());
                        } else {
                            System.out.println("WARNING: "
                                    + dir.getAbsolutePath() + " is not empty.");
                            System.out
                                    .print("WARNING: Overwrite? (yes or no) [default is no] ==> ");
                            String confirm = readLine().trim();
                            if (confirm.length() == 0
                                    || confirm.equalsIgnoreCase("no")) {
                                throw new OptionValidationException("Directory is not empty; delete it or choose another",
                                                                    _id);
                            }
                        }
                    }
                } else {
                    // must be creatable
                    boolean created = dir.mkdirs();
                    if (!created) {
                        throw new OptionValidationException("Unable to create specified directory",
                                                            _id);
                    } else {
                        dir.delete();
                    }
                }
                printEnvWarning("FEDORA_HOME", value);
            } else if (_id.equals(InstallOptions.TOMCAT_HOME)) {
                printEnvWarning("CATALINA_HOME", value);
                File dir = new File(value);
                if (dir.exists()
                        && _options.getValue(InstallOptions.SERVLET_ENGINE)
                                .equals(InstallOptions.EXISTING_TOMCAT)) {
                    // must have webapps subdir
                    File webapps = new File(dir, "webapps");
                    if (!webapps.exists()) {
                        throw new OptionValidationException("Directory exists but does not contain a webapps subdirectory",
                                                            _id);
                    }
                } else if (!dir.exists()) {
                    // must be creatable
                    boolean created = dir.mkdirs();
                    if (!created) {
                        throw new OptionValidationException("Unable to create specified directory",
                                                            _id);
                    } else {
                        dir.delete();
                    }
                }
            } else if (_id.equals(InstallOptions.TOMCAT_SHUTDOWN_PORT)) {
                validatePort(value);
            } else if (_id.equals(InstallOptions.TOMCAT_HTTP_PORT)) {
                validatePort(value);
            } else if (_id.equals(InstallOptions.TOMCAT_SSL_PORT)) {
                validatePort(value);
            } else if (_id.equals(InstallOptions.KEYSTORE_FILE)) {
                if (!(value.equals(InstallOptions.INCLUDED) || value
                        .equals(InstallOptions.DEFAULT))) {
                    validateExistingFile(value);
                }
            } else if (_id.startsWith(InstallOptions.DATABASE)
                    && _id.endsWith(".driver")) {
                if (!value.equals(InstallOptions.INCLUDED)) {
                    validateExistingFile(value);
                }
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

    private void validateExistingFile(String val)
            throws OptionValidationException {
        File f = new File(val);
        if (!f.exists()) {
            throw new OptionValidationException("No such file", _id);
        }
    }

    private void validatePort(String val) throws OptionValidationException {
        try {
            int port = Integer.parseInt(val);
            if (port < 0 || port > 65535) {
                throw new OptionValidationException("Not a valid port number",
                                                    _id);
            }
        } catch (NumberFormatException e) {
            throw new OptionValidationException("Not an integer", _id);
        }
    }

    private void printEnvWarning(String envVarName, String value) {
        String env = System.getenv(envVarName);
        if (env == null || env.length() == 0) {
            System.out.println("WARNING: The environment variable, "
                    + envVarName + ", is not defined");
            System.out.println("WARNING: Remember to define the " + envVarName
                    + " environment variable");
            System.out.println("WARNING: before starting Fedora.");
        } else if (!env.equals(value)) {
            System.out.println("WARNING: The environment variable, "
                    + envVarName + ", is defined as");
            System.out.println("WARNING:   " + env);
            System.out.println("WARNING: but you entered ");
            System.out.println("WARNING:   " + value);
            System.out
                    .println("WARNING: Please ensure you have correctly defined "
                            + envVarName);
            System.out.println("WARNING: before starting Fedora.");
        }
    }
}
