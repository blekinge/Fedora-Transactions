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
 * The WSDL HTTP XML Namespace.
 * 
 * <pre>
 * Namespace URI    : http://schemas.xmlsoap.org/wsdl/http/
 * Preferred Prefix : http
 * </pre>
 * 
 * @author Chris Wilper
 */
public class WSDLHTTPNamespace
        extends XMLNamespace {

    //---
    // Singleton instantiation
    //---

    /** The only instance of this class. */
    private static final WSDLHTTPNamespace ONLY_INSTANCE =
            new WSDLHTTPNamespace();

    /**
     * Constructs the instance.
     */
    protected WSDLHTTPNamespace() {
        super("http://schemas.xmlsoap.org/wsdl/http/", "http");
    }

    /**
     * Gets the only instance of this class.
     * 
     * @return the instance.
     */
    public static WSDLHTTPNamespace getInstance() {
        return ONLY_INSTANCE;
    }

}
