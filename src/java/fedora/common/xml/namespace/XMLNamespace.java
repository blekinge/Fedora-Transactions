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
 * An XML namespace.
 * 
 * @author Chris Wilper
 * @version $Id$
 */
public class XMLNamespace {
    
    /**
     * Namespace URI to use to represent that there is no Namespace. 
     * Defined by the Namespace specification to be "".
     */
    public static final String NULL_NS_URI = "";
    
    /** The URI of this namespace. */
    public final String uri;

    /**
     * The preferred prefix for this namespace when used in instance documents.
     */
    public final String prefix;

    /**
     * Constructs an instance.
     * 
     * @param uri
     *        the URI of the namespace.
     * @param prefix
     *        the preferred prefix.
     * @throws IllegalArgumentException
     *         if either parameter is null.
     */
    public XMLNamespace(String uri, String prefix) {
        if (uri == null) {
            throw new IllegalArgumentException("uri cannot be null");
        }
        if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be null");
        }
        this.uri = uri;
        this.prefix = prefix;
    }

    //---
    // Object overrides
    //---

    /**
     * Returns the URI of the namespace. {@inheritDoc}
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
        if (o instanceof XMLNamespace
                && o.getClass().getName().equals(this.getClass().getName())) {
            XMLNamespace n = (XMLNamespace) o;
            return uri.equals(n.uri);
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
