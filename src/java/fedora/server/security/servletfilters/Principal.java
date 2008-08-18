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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * import java.security.Principal; import java.util.Map; import java.util.Set;
 * import java.util.HashSet; import java.util.Hashtable;
 */

/**
 * @author Bill Niebel
 */
public class Principal
        implements java.security.Principal {

    private final Log log = LogFactory.getLog(Principal.class);

    private final String name;

    public Principal(String name) {
        //this.authority = null;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        //need to re-implement this    	
        return "Principal[" + getName() + "]";
    }

    @Override
    public int hashCode() {
        //need to implement this    	
        return 1;
    }

    @Override
    public boolean equals(Object another) {
        //need to implement this    	
        return false;
    }

    public String[] getRoles() {
        return new String[0];
    }

}
