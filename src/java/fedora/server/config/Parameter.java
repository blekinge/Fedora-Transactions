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

package fedora.server.config;

import java.io.File;

import java.util.Map;

import fedora.common.Constants;

/**
 *
 */
public class Parameter
        implements Constants {

    private final String m_name;

    private String m_value;

    private boolean m_isFilePath;

    private String m_comment;

    private final Map<String, String> m_profileValues;

    public Parameter(String name,
                     String value,
                     boolean isFilePath,
                     String comment,
                     Map<String, String> profileValues) {
        m_name = name;
        m_value = value;
        m_isFilePath = isFilePath;
        m_comment = comment;
        m_profileValues = profileValues;
    }

    public String getName() {
        return m_name;
    }

    /**
     * Gets the value of the parameter. Same as getValue(false).
     * 
     * @return The value of the parameter
     */
    public String getValue() {
        return getValue(false);
    }

    /**
     * Gets the value of the parameter. Prepends the location of FEDORA_HOME if
     * asAbsolutePath is true and the parameter location does not already
     * specify an absolute pathname.
     * 
     * @param asAbsolutePath
     *        Whether to return the parameter value as an absolute file path
     *        relative to FEDORA_HOME.
     * @return The value, null if undefined.
     */
    public String getValue(boolean asAbsolutePath) {
        String path = m_value;
        if (asAbsolutePath) {
            if (path != null && m_isFilePath) {
                File f = new File(path);
                if (!f.isAbsolute()) {
                    path = FEDORA_HOME + File.separator + path;
                }
            }
        }
        return path;
    }

    public void setValue(String newValue) {
        m_value = newValue;
    }

    public Map<String, String> getProfileValues() {
        return m_profileValues;
    }

    public void setIsFilePath(boolean newValue) {
        m_isFilePath = newValue;
    }

    public boolean getIsFilePath() {
        return m_isFilePath;
    }

    public String getComment() {
        return m_comment;
    }

    public void setComment(String comment) {
        m_comment = comment;
    }

    @Override
    public String toString() {
        return m_name;
    }

}
