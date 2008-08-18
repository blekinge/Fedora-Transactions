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

package fedora.server.config.webxml;

import java.util.ArrayList;
import java.util.List;

public class ServletMapping {

    private String servletName;

    /**
     * Only one url-pattern per servlet-mapping is supported pre-Servlet 2.5.
     */
    private final List<String> urlPatterns;

    public ServletMapping() {
        urlPatterns = new ArrayList<String>();
    }

    public String getServletName() {
        return servletName;
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    public List<String> getUrlPatterns() {
        return urlPatterns;
    }

    /**
     * Only one url-pattern per servlet-mapping is supported pre-Servlet 2.5.
     * 
     * @param urlPattern
     *        the url-pattern to add to this ServletMapping.
     */
    public void addUrlPattern(String urlPattern) {
        urlPatterns.add(urlPattern);
    }
}
