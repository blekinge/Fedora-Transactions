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

package fedora.common.xml.format;

import fedora.common.xml.namespace.FedoraAccessNamespace;

/**
 * The Fedora Object History 1.0 XML format.
 * 
 * <pre>
 * Format URI        : info:fedora/fedora-system:FedoraObjectHistory-1.0
 * Primary Namespace : http://www.fedora.info/definitions/1/0/access/
 * XSD Schema URL    : http://www.fedora.info/definitions/1/0/fedoraObjectHistory.xsd
 * </pre>
 * 
 * @author Chris Wilper
 */
public class FedoraObjectHistory1_0Format
        extends XMLFormat {

    /** The only instance of this class. */
    private static final FedoraObjectHistory1_0Format ONLY_INSTANCE =
            new FedoraObjectHistory1_0Format();

    /**
     * Constructs the instance.
     */
    private FedoraObjectHistory1_0Format() {
        super("info:fedora/fedora-system:FedoraObjectHistory-1.0",
              FedoraAccessNamespace.getInstance(),
              "http://www.fedora.info/definitions/1/0/objectHistory.xsd");
    }

    /**
     * Gets the only instance of this class.
     * 
     * @return the instance.
     */
    public static FedoraObjectHistory1_0Format getInstance() {
        return ONLY_INSTANCE;
    }

}
