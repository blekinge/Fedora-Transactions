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
 * The OAI DC XML namespace.
 * 
 * <pre>
 * Namespace URI    : http://www.openarchives.org/OAI/2.0/oai_dc/
 * Preferred Prefix : oai_dc
 * </pre>
 * 
 * @author Chris Wilper
 */
public class OAIDCNamespace
        extends XMLNamespace {

    //---
    // Elements
    //---

    /** The <code>dc</code> element. */
    public final QName DC;

    //---
    // Singleton instantiation
    //---

    /** The only instance of this class. */
    private static final OAIDCNamespace ONLY_INSTANCE = new OAIDCNamespace();

    /**
     * Constructs the instance.
     */
    private OAIDCNamespace() {
        super("http://www.openarchives.org/OAI/2.0/oai_dc/", "oai_dc");

        // elements
        DC = new QName(this, "dc");
    }

    /**
     * Gets the only instance of this class.
     * 
     * @return the instance.
     */
    public static OAIDCNamespace getInstance() {
        return ONLY_INSTANCE;
    }

}
