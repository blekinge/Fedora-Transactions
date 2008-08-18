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

package fedora.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Map;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Utility methods for working with Log4J.
 * 
 * @author Chris Wilper
 */
public abstract class Log4J {
   
    /**
     * Forces commons-logging to use Log4J.
     * <p>
     * This should only be called from standalone applications,
     * and must be called before any attempt is made to configure
     * or use commons-logging or Log4J.
     */
    public static void force() {
        System.setProperty("org.apache.commons.logging.LogFactory",
                           "org.apache.commons.logging.impl.Log4jFactory");
        System.setProperty("org.apache.commons.logging.Log",
                           "org.apache.commons.logging.impl.Log4JLogger");
    }
    
    /**
     * Initializes Log4J from a properties file.
     * 
     * @param propFile the Log4J properties file.
     * @param options a set of name-value pairs to use while expanding any
     *                replacement variables (e.g. ${some.name}) in the 
     *                properties file.  These may also be specified in
     *                the properties file itself.  If found, the value
     *                in the properties file will take precendence.
     * @throws IOException if configuration fails due to problems with the file.
     */
    public static void initFromPropFile(File propFile,
                                        Map<String, String> options)
            throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(propFile));
        if (options != null) {
            for (String name : options.keySet()) {
                String value = options.get(name);
                if (!props.containsKey(name)) {
                    props.setProperty(name, value);
                }
            }
        }
        PropertyConfigurator.configure(props);
    }
    
    /**
     * Initializes Log4J from an XML file.
     * 
     * @param xmlFile the Log4J xml file.
     * @throws IOException if configuration fails due to problems with the file.
     */
    public static void initFromXMLFile(File xmlFile)
            throws IOException {
        if (!xmlFile.exists()) {
            throw new FileNotFoundException(xmlFile.getPath());
        }
        DOMConfigurator.configure(xmlFile.getPath());
    }

}
