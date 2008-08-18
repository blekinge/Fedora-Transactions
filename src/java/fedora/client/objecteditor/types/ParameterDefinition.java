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

import java.util.List;

/**
 * Defines a single parameter for a method.
 */
public class ParameterDefinition {

    private final String m_name;

    private final String m_label;

    private final boolean m_isRequired;

    private final String m_defaultValue;

    private final List m_validValues;

    /**
     * Initialize a parameter definition with all values. The label,
     * defaultValue, and validValues may each be null or empty.
     */
    public ParameterDefinition(String name,
                               String label,
                               boolean isRequired,
                               String defaultValue,
                               List validValues) {
        m_name = name;
        m_label = label;
        m_isRequired = isRequired;
        m_defaultValue = defaultValue;
        m_validValues = validValues;
    }

    public String getName() {
        return m_name;
    }

    public String getLabel() {
        return m_label;
    }

    public boolean isRequired() {
        return m_isRequired;
    }

    public String getDefaultValue() {
        return m_defaultValue;
    }

    public List validValues() {
        return m_validValues;
    }
}
