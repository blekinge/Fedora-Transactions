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

package fedora.common.xml.format;

import fedora.common.xml.namespace.XMLNamespace;

/**
 * An XML format.
 * 
 * @author Chris Wilper
 */
public class XMLFormat {

    /** The URI of this format. */
    public final String uri;

    /** The primary XML namespace of this format. */
    public final XMLNamespace namespace;

    /** The primary public location of the XSD schema for this format. */
    public final String xsdLocation;

    /**
     * Constructs an instance.
     * 
     * @param uri
     *        the URI of the format.
     * @param xmlNamespace
     *        the primary XML namespace.
     * @param xsdSchemaLocation
     *        the public location of the XSD schema.
     * @throws IllegalArgumentException
     *         if any parameter is null.
     */
    public XMLFormat(String uri, XMLNamespace namespace, String xsdLocation) {
        if (uri == null) {
            throw new IllegalArgumentException("uri cannot be null");
        }
        if (namespace == null) {
            throw new IllegalArgumentException("namespace cannot be null");
        }
        if (xsdLocation == null) {
            throw new IllegalArgumentException("xsdLocation cannot be null");
        }
        this.uri = uri;
        this.namespace = namespace;
        this.xsdLocation = xsdLocation;
    }

    //---
    // Object overrides
    //---

    /**
     * Returns the URI of the format. {@inheritDoc}
     */
    @Override
    public String toString() {
        return uri;
    }

    /**
     * Returns true iff the given object is an instance of this class and has
     * the same URI. {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof XMLFormat) {
            XMLFormat f = (XMLFormat) o;
            return uri.equals(f.uri);
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return uri.hashCode();
    }

}
