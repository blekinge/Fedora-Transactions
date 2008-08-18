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
import com.sun.xacml.attr.DateTimeAttribute;
import com.sun.xacml.attr.IntegerAttribute;
import com.sun.xacml.attr.StringAttribute;

/**
 * The Fedora Object XACML namespace.
 * 
 * <pre>
 * Namespace URI    : urn:fedora:names:fedora:2.1:resource:object
 * </pre>
 */
public class ObjectNamespace
        extends XacmlNamespace {

    public final XacmlName CONTENT_MODEL;

    public final XacmlName CONTROL_GROUP;

    public final XacmlName CREATED_DATETIME;

    public final XacmlName LAST_MODIFIED_DATETIME;

    public final XacmlName NAMESPACE; //not a "patterning" error; this is the pid prefix, part before ":"	

    public final XacmlName OBJECT_TYPE;

    public final XacmlName OWNER;

    public final XacmlName PID;

    public final XacmlName STATE;

    public final XacmlName NEW_STATE;

    public final XacmlName FORMAT_URI; //for serialization

    public final XacmlName CONTEXT; //for serialization

    public final XacmlName ENCODING; //for serialization

    public final XacmlName N_PIDS;

    private ObjectNamespace(XacmlNamespace parent, String localName) {
        super(parent, localName);

        // Properties
        PID = addName(new XacmlName(this, "pid", StringAttribute.identifier));
        NAMESPACE =
                addName(new XacmlName(this,
                                      "namespace",
                                      StringAttribute.identifier)); //see declaration 
        STATE =
                addName(new XacmlName(this, "state", StringAttribute.identifier));
        NEW_STATE =
                addName(new XacmlName(this,
                                      "newState",
                                      StringAttribute.identifier));
        CONTROL_GROUP =
                addName(new XacmlName(this,
                                      "controlGroup",
                                      StringAttribute.identifier));
        OWNER =
                addName(new XacmlName(this, "owner", StringAttribute.identifier));
        CONTENT_MODEL =
                addName(new XacmlName(this,
                                      "contentModel",
                                      StringAttribute.identifier));
        CREATED_DATETIME =
                addName(new XacmlName(this,
                                      "createdDate",
                                      DateTimeAttribute.identifier));
        LAST_MODIFIED_DATETIME =
                addName(new XacmlName(this,
                                      "lastModifiedDate",
                                      DateTimeAttribute.identifier));
        OBJECT_TYPE =
                addName(new XacmlName(this,
                                      "objectType",
                                      StringAttribute.identifier));
        CONTEXT =
                addName(new XacmlName(this,
                                      "context",
                                      StringAttribute.identifier));
        ENCODING =
                addName(new XacmlName(this,
                                      "encoding",
                                      StringAttribute.identifier));
        FORMAT_URI =
                addName(new XacmlName(this,
                                      "formatUri",
                                      AnyURIAttribute.identifier));
        N_PIDS =
                addName(new XacmlName(this,
                                      "nPids",
                                      IntegerAttribute.identifier));
        // Values

    }

    public static ObjectNamespace onlyInstance =
            new ObjectNamespace(ResourceNamespace.getInstance(), "object");

    public static final ObjectNamespace getInstance() {
        return onlyInstance;
    }

}
