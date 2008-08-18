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

import fedora.common.xml.namespace.FOXMLNamespace;

/**
 * The Atom APIM 1.0 XML format.
 * 
 * <pre>
 * Format URI        : info:fedora/fedora-system:ATOM-APIM-1.0
 * Primary Namespace : http://www.w3.org/2005/Atom
 * XSD Schema URL    : http://www.kbcafe.com/rss/atom.xsd.xml
 * </pre>
 * 
 * @author Edwin Shin
 * @since 3.0
 * @version $Id$
 */
public class AtomApiM1_0Format
        extends XMLFormat {

    /** The only instance of this class. */
    private static final AtomApiM1_0Format ONLY_INSTANCE = new AtomApiM1_0Format();

    /**
     * Constructs the instance.
     */
    private AtomApiM1_0Format() {
        super("info:fedora/fedora-system:ATOM-APIM-1.0",
              FOXMLNamespace.getInstance(),
              "http://www.kbcafe.com/rss/atom.xsd.xml");
    }

    /**
     * Gets the only instance of this class.
     * 
     * @return the instance.
     */
    public static AtomApiM1_0Format getInstance() {
        return ONLY_INSTANCE;
    }

}
