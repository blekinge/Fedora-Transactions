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

import com.sun.xacml.attr.AnyURIAttribute;
import com.sun.xacml.attr.StringAttribute;

/**
 * The Fedora Service Deployment XACML namespace.
 * 
 * <pre>
 * Namespace URI    : urn:fedora:names:fedora:2.1:resource:sdep
 * </pre>
 */
public class ServiceDeploymentNamespace
        extends XacmlNamespace {

    public final XacmlName PID;

    public final XacmlName NAMESPACE;

    public final XacmlName STATE;

    public final XacmlName LOCATION;

    public final XacmlName NEW_PID;

    public final XacmlName NEW_NAMESPACE;

    private ServiceDeploymentNamespace(XacmlNamespace parent, String localName) {
        super(parent, localName);
        PID = addName(new XacmlName(this, "pid", StringAttribute.identifier));
        NEW_PID =
                addName(new XacmlName(this,
                                      "newPid",
                                      StringAttribute.identifier));
        NAMESPACE =
                addName(new XacmlName(this,
                                      "namespace",
                                      StringAttribute.identifier));
        NEW_NAMESPACE =
                addName(new XacmlName(this,
                                      "newNamespace",
                                      StringAttribute.identifier));
        LOCATION =
                addName(new XacmlName(this,
                                      "location",
                                      AnyURIAttribute.identifier));
        STATE =
                addName(new XacmlName(this, "state", StringAttribute.identifier));
    }

    public static ServiceDeploymentNamespace onlyInstance =
            new ServiceDeploymentNamespace(ResourceNamespace.getInstance(),
                                           "sdep");

    public static final ServiceDeploymentNamespace getInstance() {
        return onlyInstance;
    }

}
