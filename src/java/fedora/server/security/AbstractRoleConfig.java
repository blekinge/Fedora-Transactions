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

package fedora.server.security;

/**
 * Abstract implementation of <code>BERoleConfig</code>. Concrete
 * implementations must override getRole().
 * 
 * @author Chris Wilper
 */
public abstract class AbstractRoleConfig
        implements BERoleConfig {

    private final BERoleConfig m_parent;

    private String[] m_ipList;

    private Boolean m_callbackBasicAuth;

    private Boolean m_callbackSSL;

    private Boolean m_callBasicAuth;

    private Boolean m_callSSL;

    private String m_callUsername;

    private String m_callPassword;

    protected AbstractRoleConfig(BERoleConfig parent) {
        m_parent = parent;
    }

    public abstract String getRole();

    public String[] getIPList() {
        return m_ipList;
    }

    public String[] getEffectiveIPList() {
        if (m_ipList != null) {
            return m_ipList;
        } else if (m_parent != null) {
            return m_parent.getEffectiveIPList();
        } else {
            return null;
        }
    }

    public void setIPList(String[] ips) {
        m_ipList = ips;
    }

    public Boolean getCallbackBasicAuth() {
        return m_callbackBasicAuth;
    }

    public Boolean getEffectiveCallbackBasicAuth() {
        if (m_callbackBasicAuth != null) {
            return m_callbackBasicAuth;
        } else if (m_parent != null) {
            return m_parent.getEffectiveCallbackBasicAuth();
        } else {
            return Boolean.FALSE;
        }
    }

    public void setCallbackBasicAuth(Boolean value) {
        m_callbackBasicAuth = value;
    }

    public Boolean getCallbackSSL() {
        return m_callbackSSL;
    }

    public Boolean getEffectiveCallbackSSL() {
        if (m_callbackSSL != null) {
            return m_callbackSSL;
        } else if (m_parent != null) {
            return m_parent.getEffectiveCallbackSSL();
        } else {
            return Boolean.FALSE;
        }
    }

    public void setCallbackSSL(Boolean value) {
        m_callbackSSL = value;
    }

    public Boolean getCallBasicAuth() {
        return m_callBasicAuth;
    }

    public Boolean getEffectiveCallBasicAuth() {
        if (m_callBasicAuth != null) {
            return m_callBasicAuth;
        } else if (m_parent != null) {
            return m_parent.getEffectiveCallBasicAuth();
        } else {
            return Boolean.FALSE;
        }
    }

    public void setCallBasicAuth(Boolean value) {
        m_callBasicAuth = value;
    }

    public Boolean getCallSSL() {
        return m_callSSL;
    }

    public Boolean getEffectiveCallSSL() {
        if (m_callSSL != null) {
            return m_callSSL;
        } else if (m_parent != null) {
            return m_parent.getEffectiveCallSSL();
        } else {
            return Boolean.FALSE;
        }
    }

    public void setCallSSL(Boolean value) {
        m_callSSL = value;
    }

    public String getCallUsername() {
        return m_callUsername;
    }

    public String getEffectiveCallUsername() {
        if (m_callUsername != null) {
            return m_callUsername;
        } else if (m_parent != null) {
            return m_parent.getEffectiveCallUsername();
        } else {
            return null;
        }
    }

    public void setCallUsername(String user) {
        m_callUsername = user;
    }

    public String getCallPassword() {
        return m_callPassword;
    }

    public String getEffectiveCallPassword() {
        if (m_callPassword != null) {
            return m_callPassword;
        } else if (m_parent != null) {
            return m_parent.getEffectiveCallPassword();
        } else {
            return null;
        }
    }

    public void setCallPassword(String pass) {
        m_callPassword = pass;
    }

}