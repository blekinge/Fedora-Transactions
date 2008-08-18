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
 * A namespace-qualified name in XML.
 * 
 * @author Chris Wilper
 * @version $Id$
 */
public class QName extends javax.xml.namespace.QName {

    private static final long serialVersionUID = 6368425528110304020L;

    /** The namespace to which this name belongs. */
    public final XMLNamespace namespace;

    /** The local part of the qualified name. */
    public final String localName;

    /**
     * A string of the form: <code>prefix:localName</code>, acceptable for
     * use in an instance document. The prefix used will be the preferred prefix
     * of the namespace.
     */
    public final String qName;

    /**
     * Constructs an instance.
     * 
     * @param namespace
     *        the namespace to which this name belongs.
     * @param localName
     *        the local part of the qualified name.
     * @throws IllegalArgumentException
     *         if either parameter is null.
     */
    public QName(XMLNamespace namespace, String localName) {
        super(namespace.uri, localName, namespace.prefix);
        
        if (namespace == null) {
            throw new IllegalArgumentException("namespace cannot be null");
        }
        if (localName == null) {
            throw new IllegalArgumentException("localName cannot be null");
        }
        this.namespace = namespace;
        this.localName = localName;
        qName = namespace.prefix + ":" + localName;
    }
}
