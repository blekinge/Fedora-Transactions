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
import com.sun.xacml.attr.StringAttribute;

/**
 * The Fedora Disseminator XACML namespace.
 * 
 * <pre>
 * Namespace URI    : urn:fedora:names:fedora:2.1:resource:disseminator
 * </pre>
 */
@Deprecated
public class DisseminatorNamespace
        extends XacmlNamespace {

    // Properties
    public final XacmlName ID;

    public final XacmlName STATE;

    public final XacmlName METHOD;

    public final XacmlName AS_OF_DATETIME;

    public final XacmlName NEW_STATE;

    // Values

    private DisseminatorNamespace(XacmlNamespace parent, String localName) {
        super(parent, localName);
        ID = addName(new XacmlName(this, "id", StringAttribute.identifier));
        STATE =
                addName(new XacmlName(this, "state", StringAttribute.identifier));
        METHOD =
                addName(new XacmlName(this,
                                      "method",
                                      StringAttribute.identifier));
        AS_OF_DATETIME =
                addName(new XacmlName(this,
                                      "asOfDateTime",
                                      DateTimeAttribute.identifier));
        NEW_STATE =
                addName(new XacmlName(this,
                                      "newState",
                                      StringAttribute.identifier));
    }

    public static DisseminatorNamespace onlyInstance =
            new DisseminatorNamespace(ResourceNamespace.getInstance(),
                                      "disseminator");

    public static final DisseminatorNamespace getInstance() {
        return onlyInstance;
    }

}
