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
 * The Fedora Method Map XML namespace.
 * 
 * <pre>
 * Namespace URI    : http://fedora.comm.nsdlib.org/service/methodmap
 * Preferred Prefix : fmm
 * </pre>
 * 
 * @author Chris Wilper
 */
public class FedoraMethodMapNamespace
        extends XMLNamespace {

    //---
    // Elements
    //---

    /** The <code>DatastreamInputParm</code> element. */
    public final QName DATASTREAM_INPUT_PARM;

    /** The <code>DefaultInputParm</code> element. */
    public final QName DEFAULT_INPUT_PARM;

    /** The <code>Method</code> element. */
    public final QName METHOD;

    /** The <code>MethodMap</code> element. */
    public final QName METHOD_MAP;

    /** The <code>MethodReturnType</code> element. */
    public final QName METHOD_RETURN_TYPE;

    /** The <code>UserInputParm</code> element. */
    public final QName USER_INPUT_PARM;

    /** The <code>ValidParm</code> element. */
    public final QName VALID_PARM;

    /** The <code>ValidParmValues</code> element. */
    public final QName VALID_PARM_VALUES;

    //---
    // Attributes
    //---

    /** The <code>bDefPID</code> attribute. */
    public final QName BDEF_PID;

    /** The <code>defaultValue</code> attribute. */
    public final QName DEFAULT_VALUE;

    /** The <code>label</code> attribute. */
    public final QName LABEL;

    /** The <code>name</code> attribute. */
    public final QName NAME;

    /** The <code>operationLabel</code> attribute. */
    public final QName OPERATION_LABEL;

    /** The <code>operationName</code> attribute. */
    public final QName OPERATION_NAME;

    /** The <code>parmName</code> attribute. */
    public final QName PARM_NAME;

    /** The <code>passBy</code> attribute. */
    public final QName PASS_BY;

    /** The <code>required</code> attribute. */
    public final QName REQUIRED;

    /** The <code>value</code> attribute. */
    public final QName VALUE;

    /** The <code>wsdlMsgName</code> attribute. */
    public final QName WSDL_MSG_NAME;

    /** The <code>wsdlMsgOutput</code> attribute. */
    public final QName WSDL_MSG_OUTPUT;

    /** The <code>wsdlMsgTOMIME</code> attribute. */
    public final QName WSDL_MSG_TO_MIME;

    //---
    // Singleton instantiation
    //---

    /** The only instance of this class. */
    private static final FedoraMethodMapNamespace ONLY_INSTANCE =
            new FedoraMethodMapNamespace();

    /**
     * Constructs the instance.
     */
    private FedoraMethodMapNamespace() {
        super("http://fedora.comm.nsdlib.org/service/methodmap", "fmm");

        // elements
        DATASTREAM_INPUT_PARM = new QName(this, "DatastreamInputParm");
        DEFAULT_INPUT_PARM = new QName(this, "DefaultInputParm");
        METHOD = new QName(this, "Method");
        METHOD_MAP = new QName(this, "MethodMap");
        METHOD_RETURN_TYPE = new QName(this, "MethodReturnType");
        USER_INPUT_PARM = new QName(this, "UserInputParm");
        VALID_PARM = new QName(this, "ValidParm");
        VALID_PARM_VALUES = new QName(this, "ValidParmValues");

        // attributes
        BDEF_PID = new QName(this, "bDefPID");
        DEFAULT_VALUE = new QName(this, "defaultValue");
        LABEL = new QName(this, "label");
        NAME = new QName(this, "name");
        OPERATION_LABEL = new QName(this, "operationLabel");
        OPERATION_NAME = new QName(this, "operationName");
        PARM_NAME = new QName(this, "parmName");
        PASS_BY = new QName(this, "passBy");
        REQUIRED = new QName(this, "required");
        VALUE = new QName(this, "value");
        WSDL_MSG_NAME = new QName(this, "wsdlMsgName");
        WSDL_MSG_OUTPUT = new QName(this, "wsdlMsgOutput");
        WSDL_MSG_TO_MIME = new QName(this, "wsdlMsgTOMIME");
    }

    /**
     * Gets the only instance of this class.
     * 
     * @return the instance.
     */
    public static FedoraMethodMapNamespace getInstance() {
        return ONLY_INSTANCE;
    }

}
