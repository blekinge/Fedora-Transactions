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

package fedora.common.xml.namespace;

/**
 * The Fedora Management XML namespace.
 * 
 * <pre>
 * Namespace URI    : http://www.fedora.info/definitions/1/0/management/
 * Preferred Prefix : management
 * </pre>
 * 
 * @author Chris Wilper
 */
public class FedoraManagementNamespace
        extends XMLNamespace {

    //---
    // Singleton instantiation
    //---

    /** The only instance of this class. */
    private static final FedoraManagementNamespace ONLY_INSTANCE =
            new FedoraManagementNamespace();

    /**
     * Constructs the instance.
     */
    private FedoraManagementNamespace() {
        super("http://www.fedora.info/definitions/1/0/management/",
              "management");
    }

    /**
     * Gets the only instance of this class.
     * 
     * @return the instance.
     */
    public static FedoraManagementNamespace getInstance() {
        return ONLY_INSTANCE;
    }

}
