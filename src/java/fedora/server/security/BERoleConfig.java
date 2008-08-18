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
 * Configuration for a given backend service role.
 * 
 * @author Chris Wilper
 */
public interface BERoleConfig {

    /**
     * Get the name of the role this configuration applies to.
     */
    public String getRole();

    /**
     * Get the list of IP addresses that are allowed to make back-end callbacks
     * to Fedora using this role. For SDep/MethodRoleConfig, null means the
     * effective value is inherited. For DefaultRoleConfig, null means no
     * restriction.
     */
    public String[] getIPList();

    public String[] getEffectiveIPList();

    /**
     * Set the list of IP addresses that are allowed to make back-end callbacks
     * to Fedora using this role. For SDep/MethodRoleConfig, null means the
     * effective value is inherited. For DefaultRoleConfig, null means no
     * restriction.
     */
    public void setIPList(String[] ips);

    /**
     * Get whether backend callbacks for this role require basic auth. For
     * SDep/MethodRoleConfig, null means the effective value is inherited. For
     * DefaultRoleConfig, null means the effective value is false.
     */
    public Boolean getCallbackBasicAuth();

    public Boolean getEffectiveCallbackBasicAuth();

    /**
     * Set whether backend callbacks for this role require basic auth. For
     * SDep/MethodRoleConfig, null means the effective value is inherited. For
     * DefaultRoleConfig, null means the effective value is false.
     */
    public void setCallbackBasicAuth(Boolean value);

    /**
     * Get whether backend callbacks for this role require SSL. For
     * SDep/MethodRoleConfig, null means the effective value is inherited. For
     * DefaultRoleConfig, null means the effective value is false.
     */
    public Boolean getCallbackSSL();

    public Boolean getEffectiveCallbackSSL();

    /**
     * Set whether backend callbacks for this role require SSL. For
     * SDep/MethodRoleConfig, null means the effective value is inherited. For
     * DefaultRoleConfig, null means the effective value is false.
     */
    public void setCallbackSSL(Boolean value);

    /**
     * Get whether backend calls for this role will use basic auth. For
     * SDep/MethodRoleConfig, null means the effective value is inherited. For
     * DefaultRoleConfig, null means the effective value is false.
     */
    public Boolean getCallBasicAuth();

    public Boolean getEffectiveCallBasicAuth();

    /**
     * Set whether backend calls for this role will use basic auth. For
     * SDep/MethodRoleConfig, null means the effective value is inherited. For
     * DefaultRoleConfig, null means the effective value is false.
     */
    public void setCallBasicAuth(Boolean value);

    /**
     * Get whether backend calls for this role will SSL. For
     * SDep/MethodRoleConfig, null means the effective value is inherited. For
     * DefaultRoleConfig, null means the effective value is false.
     */
    public Boolean getCallSSL();

    public Boolean getEffectiveCallSSL();

    /**
     * Set whether backend calls for this role will SSL. For
     * SDep/MethodRoleConfig, null means the effective value is inherited. For
     * DefaultRoleConfig, null means the effective value is false.
     */
    public void setCallSSL(Boolean value);

    /**
     * Get the basicauth username for backend calls for this role. For
     * SDep/MethodRoleConfig, null means the effective value is inherited. For
     * DefaultRoleConfig, null means unspecified.
     */
    public String getCallUsername();

    public String getEffectiveCallUsername();

    /**
     * Set the basicauth username for backend calls for this role. For
     * SDep/MethodRoleConfig, null means the effective value is inherited. For
     * DefaultRoleConfig, null means unspecified.
     */
    public void setCallUsername(String user);

    /**
     * Get the basicauth password for backend calls for this role. For
     * SDep/MethodRoleConfig, null means the effective value is inherited. For
     * DefaultRoleConfig, null means unspecified.
     */
    public String getCallPassword();

    public String getEffectiveCallPassword();

    /**
     * Set the basicauth password for backend calls for this role. For
     * SDep/MethodRoleConfig, null means the effective value is inherited. For
     * DefaultRoleConfig, null means unspecified.
     */
    public void setCallPassword(String pass);

}