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

package fedora.client.objecteditor.types;

import java.io.IOException;
import java.io.InputStream;

import java.util.List;

/**
 * Defines a method that an object exposes via Fedora. A service definition
 * object specifies this.
 */
public class MethodDefinition {

    private final String m_name;

    private final String m_label;

    private final List m_parameterDefinitions;

    /**
     * Initialize a MethodDefinition object with all values.
     */
    public MethodDefinition(String name, String label, List parameterDefinitions) {
        m_name = name;
        m_label = label;
        m_parameterDefinitions = parameterDefinitions;
    }

    /**
     * Parse a stream of XML and return the list of method definitions defined
     * therein. The parsing is very relaxed. The xml may, but needn't be
     * namespace-qualified, and will only be validated according to the rules
     * implied below in parentheses.
     * 
     * <pre>
     * &lt;Method operationName="METHOD_NAME" 
     *                 label="METHOD_LABEL"(0-1)&gt;
     *     &lt;UserInputParm parmName="PARAMETER_NAME" 
     *                       label="PARAMETER_LABEL"(0-1)
     *                    required="IS_REQUIRED"
     *                defaultValue="DEFAULT_VALUE"&gt;
     *         &lt;ValidParmValues>(0-1)
     *             &lt;ValidParm value="VALID_VALUE_1"/&gt;(0-)
     *         &lt;/ValidParmValues&gt;
     *     &lt;/UserInputParm&gt;(0-) 
     * &lt;/Method&gt;
     * </pre>
     */
    public static List parse(InputStream xml) throws IOException {
        return new MethodDefinitionsDeserializer(xml).getResult();
    }

    /**
     * Get the name of the method.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Get a short description of the method. This may be null.
     */
    public String getLabel() {
        return m_label;
    }

    /**
     * Get the method's list of <code>ParameterDefinition</code>s. If the
     * method takes no parameters, this will be an empty list.
     */
    public List parameterDefinitions() {
        return m_parameterDefinitions;
    }

}