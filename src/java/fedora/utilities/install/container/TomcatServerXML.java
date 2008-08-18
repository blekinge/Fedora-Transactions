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

package fedora.utilities.install.container;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import fedora.utilities.XMLDocument;
import fedora.utilities.install.InstallOptions;
import fedora.utilities.install.InstallationFailedException;

public class TomcatServerXML
        extends XMLDocument {

    private static final String KEYSTORE_LOCATION =
            Tomcat.CONF + "/" + Tomcat.KEYSTORE;

    private static final String KEYSTORE_PASSWORD_DEFAULT = "changeit";

    private static final String KEYSTORE_TYPE_DEFAULT = "JKS";

    private final InstallOptions options;

    public TomcatServerXML(File serverXML, InstallOptions installOptions)
            throws FileNotFoundException, DocumentException {
        this(new FileInputStream(serverXML), installOptions);
    }

    public TomcatServerXML(InputStream serverXML, InstallOptions installOptions)
            throws FileNotFoundException, DocumentException {
        super(serverXML);
        options = installOptions;
    }

    public void update() throws InstallationFailedException {
        setHTTPPort();
        setShutdownPort();
        setSSLPort();
    }

    public void setHTTPPort() throws InstallationFailedException {
        // Note this very significant assumption: this xpath will select exactly one connector
        Element httpConnector =
                (Element) getDocument()
                        .selectSingleNode("/Server/Service[@name='Catalina']/Connector[not(@scheme='https' or contains(@protocol, 'AJP'))]");

        if (httpConnector == null) {
            throw new InstallationFailedException("Unable to set server.xml HTTP Port. XPath for Connector element failed.");
        }

        httpConnector.addAttribute("port", options
                .getValue(InstallOptions.TOMCAT_HTTP_PORT));
        httpConnector.addAttribute("enableLookups", "true"); // supports client dns/fqdn in xacml authz policies
    }

    public void setShutdownPort() throws InstallationFailedException {
        Element server =
                (Element) getDocument()
                        .selectSingleNode("/Server[@shutdown and @port]");

        if (server == null) {
            throw new InstallationFailedException("Unable to set server.xml shutdown port. XPath for Server element failed.");
        }

        server.addAttribute("port", options
                .getValue(InstallOptions.TOMCAT_SHUTDOWN_PORT));
    }

    /**
     * Sets the port and keystore information on the SSL connector if it already
     * exists; creates a new SSL connector, otherwise. Also sets the
     * redirectPort on the non-SSL connector to match.
     * 
     * @throws InstallationFailedException
     */
    public void setSSLPort() throws InstallationFailedException {
        Element httpsConnector =
                (Element) getDocument()
                        .selectSingleNode("/Server/Service[@name='Catalina']/Connector[@scheme='https' and not(contains(@protocol, 'AJP'))]");
        if (options.getBooleanValue(InstallOptions.SSL_AVAILABLE, true)) {
            if (httpsConnector == null) {
                Element service =
                        (Element) getDocument()
                                .selectSingleNode("/Server/Service[@name='Catalina']");
                httpsConnector = service.addElement("Connector");
                httpsConnector.addAttribute("maxThreads", "150");
                httpsConnector.addAttribute("minSpareThreads", "25");
                httpsConnector.addAttribute("maxSpareThreads", "75");
                httpsConnector.addAttribute("disableUploadTimeout", "true");
                httpsConnector.addAttribute("acceptCount", "100");
                httpsConnector.addAttribute("debug", "0");
                httpsConnector.addAttribute("scheme", "https");
                httpsConnector.addAttribute("secure", "true");
                httpsConnector.addAttribute("clientAuth", "false");
                httpsConnector.addAttribute("sslProtocol", "TLS");
            }
            httpsConnector.addAttribute("port", options
                    .getValue(InstallOptions.TOMCAT_SSL_PORT));
            httpsConnector.addAttribute("enableLookups", "true"); // supports client dns/fqdn in xacml authz policies

            String keystore = options.getValue(InstallOptions.KEYSTORE_FILE);
            if (keystore.equals(InstallOptions.INCLUDED)) {
                keystore = KEYSTORE_LOCATION;
            }

            addAttribute(httpsConnector,
                         "keystoreFile",
                         keystore,
                         InstallOptions.DEFAULT);
            addAttribute(httpsConnector,
                         "keystorePass",
                         options.getValue(InstallOptions.KEYSTORE_PASSWORD),
                         KEYSTORE_PASSWORD_DEFAULT);
            addAttribute(httpsConnector,
                         "keystoreType",
                         options.getValue(InstallOptions.KEYSTORE_TYPE),
                         KEYSTORE_TYPE_DEFAULT);

            // The redirectPort for the non-SSL connector should match the port on
            // the SSL connector, per:
            // http://tomcat.apache.org/tomcat-5.0-doc/ssl-howto.html
            Element httpConnector =
                    (Element) getDocument()
                            .selectSingleNode("/Server/Service[@name='Catalina']/Connector[(@scheme='http' or not(@scheme)) and not(contains(@protocol, 'AJP'))]");
            if (httpConnector != null) {
                httpConnector.addAttribute("redirectPort", options
                        .getValue(InstallOptions.TOMCAT_SSL_PORT));
            } else {
                throw new InstallationFailedException("Unable to set server.xml SSL Port. XPath for Connector element failed.");
            }
        } else if (httpsConnector != null) {
            httpsConnector.getParent().remove(httpsConnector);
        }
    }

    /**
     * Adds the attribute to the element if the attributeValue is not equal to
     * defaultValue. If attributeValue is null or equals defaultValue, remove
     * the attribute from the element if it is present.
     * 
     * @param element
     * @param attributeName
     * @param attributeValue
     * @param defaultValue
     */
    private void addAttribute(Element element,
                              String attributeName,
                              String attributeValue,
                              String defaultValue) {
        if (attributeValue == null || attributeValue.equals(defaultValue)) {
            Attribute attribute =
                    (Attribute) element.selectSingleNode(attributeName);
            if (attribute != null) {
                element.remove(attribute);
            }
        } else {
            element.addAttribute(attributeName, attributeValue);
        }
    }
}
