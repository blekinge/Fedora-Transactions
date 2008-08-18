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

package fedora.common.policy;

import com.sun.xacml.attr.IntegerAttribute;
import com.sun.xacml.attr.StringAttribute;

/**
 * The Fedora HTTP Request XACML namespace.
 * 
 * <pre>
 * Namespace URI    : urn:fedora:names:fedora:2.1:environment:httpRequest
 * </pre>
 */
public class HttpRequestNamespace
        extends XacmlNamespace {

    // Properties
    public final XacmlName MESSAGE_PROTOCOL;

    public final XacmlName PROTOCOL;

    public final XacmlName SCHEME;

    public final XacmlName SECURITY;

    public final XacmlName AUTHTYPE;

    public final XacmlName METHOD;

    public final XacmlName SESSION_ENCODING;

    public final XacmlName SESSION_STATUS;

    public final XacmlName CONTENT_LENGTH;

    public final XacmlName CONTENT_TYPE;

    public final XacmlName CLIENT_FQDN;

    public final XacmlName CLIENT_IP_ADDRESS;

    public final XacmlName SERVER_FQDN;

    public final XacmlName SERVER_IP_ADDRESS;

    public final XacmlName SERVER_PORT;

    // Values of MESSAGE_PROTOCOL	 
    public final XacmlName SOAP;

    public final XacmlName REST;

    // Values of SECURITY	 
    public final XacmlName SECURE;

    public final XacmlName INSECURE;

    private HttpRequestNamespace(XacmlNamespace parent, String localName) {
        super(parent, localName);
        AUTHTYPE =
                addName(new XacmlName(this,
                                      "authType",
                                      StringAttribute.identifier));
        CLIENT_FQDN =
                addName(new XacmlName(this,
                                      "clientFqdn",
                                      StringAttribute.identifier));
        CLIENT_IP_ADDRESS =
                addName(new XacmlName(this,
                                      "clientIpAddress",
                                      StringAttribute.identifier));
        CONTENT_LENGTH =
                addName(new XacmlName(this,
                                      "contentLength",
                                      IntegerAttribute.identifier));
        CONTENT_TYPE =
                addName(new XacmlName(this,
                                      "contentType",
                                      StringAttribute.identifier));
        MESSAGE_PROTOCOL = addName(new XacmlName(this, "messageProtocol"));
        SOAP = addName(new XacmlName(this, "messageProtocol-soap"));
        REST = addName(new XacmlName(this, "messageProtocol-rest"));
        METHOD =
                addName(new XacmlName(this,
                                      "method",
                                      StringAttribute.identifier));
        PROTOCOL =
                addName(new XacmlName(this,
                                      "protocol",
                                      StringAttribute.identifier));
        SCHEME =
                addName(new XacmlName(this,
                                      "scheme",
                                      StringAttribute.identifier));
        SECURITY =
                addName(new XacmlName(this,
                                      "security",
                                      StringAttribute.identifier));
        SECURE = addName(new XacmlName(this, "security-secure"));
        INSECURE = addName(new XacmlName(this, "security-insecure"));
        SERVER_FQDN =
                addName(new XacmlName(this,
                                      "serverFqdn",
                                      StringAttribute.identifier));
        SERVER_IP_ADDRESS =
                addName(new XacmlName(this,
                                      "serverIpAddress",
                                      StringAttribute.identifier));
        SERVER_PORT =
                addName(new XacmlName(this,
                                      "serverPort",
                                      StringAttribute.identifier));
        SESSION_ENCODING =
                addName(new XacmlName(this,
                                      "sessionEncoding",
                                      StringAttribute.identifier));
        SESSION_STATUS =
                addName(new XacmlName(this,
                                      "sessionStatus",
                                      StringAttribute.identifier));
    }

    public static HttpRequestNamespace onlyInstance =
            new HttpRequestNamespace(EnvironmentNamespace.getInstance(),
                                     "httpRequest");

    public static final HttpRequestNamespace getInstance() {
        return onlyInstance;
    }

}
