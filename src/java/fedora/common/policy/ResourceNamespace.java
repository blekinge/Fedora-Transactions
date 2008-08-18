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

import com.sun.xacml.attr.DateTimeAttribute;

/**
 * The Fedora Resource XACML namespace.
 * 
 * <pre>
 * Namespace URI    : urn:fedora:names:fedora:2.1:resource
 * </pre>
 */
public class ResourceNamespace
        extends XacmlNamespace {

    // Properties
    public final XacmlName AS_OF_DATETIME;

    public final XacmlName TICKET_ISSUED_DATETIME;

    private ResourceNamespace(XacmlNamespace parent, String localName) {
        super(parent, localName);
        AS_OF_DATETIME =
                new XacmlName(this,
                              "asOfDateTime",
                              DateTimeAttribute.identifier);
        TICKET_ISSUED_DATETIME =
                addName(new XacmlName(this,
                                      "ticketIssuedDateTime",
                                      DateTimeAttribute.identifier));

    }

    public static ResourceNamespace onlyInstance =
            new ResourceNamespace(Release2_1Namespace.getInstance(), "resource");

    static {
        init();
    }

    @SuppressWarnings("deprecation")
    private static void init() {
        onlyInstance.addNamespace(ObjectNamespace.getInstance());
        onlyInstance.addNamespace(DatastreamNamespace.getInstance());
        onlyInstance.addNamespace(DisseminatorNamespace.getInstance());
        onlyInstance.addNamespace(ServiceDefinitionNamespace.getInstance());
        onlyInstance.addNamespace(ServiceDeploymentNamespace.getInstance());
    }

    public static final ResourceNamespace getInstance() {
        return onlyInstance;
    }

}
