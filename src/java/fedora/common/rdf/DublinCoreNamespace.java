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

package fedora.common.rdf;

/**
 * The Dublin Core RDF namespace.
 * 
 * <pre>
 * Namespace URI    : http://purl.org/dc/elements/1.1/
 * Preferred Prefix : dc
 * </pre>
 * 
 * @author Chris Wilper
 */
public class DublinCoreNamespace
        extends RDFNamespace {

    private static final long serialVersionUID = 1L;

    public final RDFName TITLE;

    public final RDFName CREATOR;

    public final RDFName SUBJECT;

    public final RDFName DESCRIPTION;

    public final RDFName PUBLISHER;

    public final RDFName CONTRIBUTOR;

    public final RDFName DATE;

    public final RDFName TYPE;

    public final RDFName FORMAT;

    public final RDFName IDENTIFIER;

    public final RDFName SOURCE;

    public final RDFName LANGUAGE;

    public final RDFName RELATION;

    public final RDFName COVERAGE;

    public final RDFName RIGHTS;

    public DublinCoreNamespace() {

        uri = "http://purl.org/dc/elements/1.1/";
        prefix = "dc";

        TITLE = new RDFName(this, "title");
        CREATOR = new RDFName(this, "creator");
        SUBJECT = new RDFName(this, "subject");
        DESCRIPTION = new RDFName(this, "description");
        PUBLISHER = new RDFName(this, "publisher");
        CONTRIBUTOR = new RDFName(this, "contributor");
        DATE = new RDFName(this, "date");
        TYPE = new RDFName(this, "type");
        FORMAT = new RDFName(this, "format");
        IDENTIFIER = new RDFName(this, "identifier");
        SOURCE = new RDFName(this, "source");
        LANGUAGE = new RDFName(this, "language");
        RELATION = new RDFName(this, "relation");
        COVERAGE = new RDFName(this, "coverage");
        RIGHTS = new RDFName(this, "rights");

    }

}
