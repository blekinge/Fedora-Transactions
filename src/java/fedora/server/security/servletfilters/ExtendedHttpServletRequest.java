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

package fedora.server.security.servletfilters;

import java.security.Principal;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import fedora.server.errors.authorization.AuthzOperationalException;

/**
 * @author Bill Niebel
 */
public interface ExtendedHttpServletRequest
        extends HttpServletRequest {

    public static final String SUCCEEDED = "succeeded";

    public static final String FAILED = "failed";

    public static final ImmutableHashSet IMMUTABLE_NULL_SET =
            new ImmutableHashSet();

    public void audit();

    public void lockWrapper() throws Exception;

    public void setSponsoredUser() throws Exception;

    public void lockSponsoredUser() throws Exception;

    public void setAuthenticated(Principal userPrincipal, String authority)
            throws Exception;

    public boolean isUserSponsored();

    public boolean isAuthenticated();

    public Set getAttributeValues(String key) throws AuthzOperationalException;

    public boolean hasAttributeValues(String key)
            throws AuthzOperationalException;

    public boolean isAttributeDefined(String key)
            throws AuthzOperationalException;

    public void addAttributes(String authority, Map attributes)
            throws Exception;

    public String getUser() throws Exception;

    public String getPassword() throws Exception;

    public Map getAllAttributes() throws Exception;

    public String getAuthority();

    public String getFromHeader();

}
