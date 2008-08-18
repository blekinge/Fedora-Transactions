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
 * The Fedora Binding Specification XML namespace.
 * 
 * <pre>
 * Namespace URI    : http://fedora.comm.nsdlib.org/service/bindspec 
 * Preferred Prefix : fbs
 * </pre>
 * 
 * @author Chris Wilper
 */
public class FedoraBindingSpecNamespace
        extends XMLNamespace {

    //---
    // Elements
    //---

    /** The <code>DSInput</code> element. */
    public final QName DS_INPUT;

    /** The <code>DSInputInstruction</code> element. */
    public final QName DS_INPUT_INSTRUCTION;

    /** The <code>DSInputLabel</code> element. */
    public final QName DS_INPUT_LABEL;

    /** The <code>DSInputSpec</code> element. */
    public final QName DS_INPUT_SPEC;

    /** The <code>DSMIME</code> element. */
    public final QName DS_MIME;

    //---
    // Attributes
    //---

    /** The <code>bDefPID</code> attribute. */
    public final QName BDEF_PID;

    /** The <code>DSMax</code> attribute. */
    public final QName DS_MAX;

    /** The <code>DSMin</code> attribute. */
    public final QName DS_MIN;

    /** The <code>DSOrdinality</code> attribute. */
    public final QName DS_ORDINALITY;

    /** The <code>label</code> attribute. */
    public final QName LABEL;

    /** The <code>wsdlMsgPartName</code> attribute. */
    public final QName WSDL_MSG_PART_NAME;

    //---
    // Singleton instantiation
    //---

    /** The only instance of this class. */
    private static final FedoraBindingSpecNamespace ONLY_INSTANCE =
            new FedoraBindingSpecNamespace();

    /**
     * Constructs the instance.
     */
    private FedoraBindingSpecNamespace() {
        super("http://fedora.comm.nsdlib.org/service/bindspec", "fbs");

        // elements
        DS_INPUT = new QName(this, "DSInput");
        DS_INPUT_INSTRUCTION = new QName(this, "DSInputInstruction");
        DS_INPUT_LABEL = new QName(this, "DSInputLabel");
        DS_INPUT_SPEC = new QName(this, "DSInputSpec");
        DS_MIME = new QName(this, "DSMIME");

        // attributes
        BDEF_PID = new QName(this, "bDefPID");
        DS_MAX = new QName(this, "DSMax");
        DS_MIN = new QName(this, "DSMin");
        DS_ORDINALITY = new QName(this, "DSOrdinality");
        LABEL = new QName(this, "label");
        WSDL_MSG_PART_NAME = new QName(this, "wsdlMsgPartName");
    }

    /**
     * Gets the only instance of this class.
     * 
     * @return the instance.
     */
    public static FedoraBindingSpecNamespace getInstance() {
        return ONLY_INSTANCE;
    }

}
