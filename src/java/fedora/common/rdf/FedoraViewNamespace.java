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
 * The Fedora View RDF namespace.
 * 
 * <pre>
 * Namespace URI    : info:fedora/fedora-system:def/view#
 * Preferred Prefix : fedora-view
 * </pre>
 * 
 * @author Chris Wilper
 * @version $Id$
 */
public class FedoraViewNamespace
        extends RDFNamespace {

    private static final long serialVersionUID = 2L;

    // Properties

    /**
     * Deprecated as of Fedora 3.0. No replacement.
     */
    @Deprecated
    public final RDFName HAS_DATASTREAM;

    public final RDFName DISSEMINATES;

    public final RDFName DISSEMINATION_TYPE;

    public final RDFName IS_VOLATILE;

    public final RDFName LAST_MODIFIED_DATE;

    public final RDFName MIME_TYPE;
    
    public final RDFName VERSION;

    public FedoraViewNamespace() {

        uri = "info:fedora/fedora-system:def/view#";
        prefix = "fedora-view";

        // Properties
        HAS_DATASTREAM = new RDFName(this, "hasDatastream");
        DISSEMINATES = new RDFName(this, "disseminates");
        DISSEMINATION_TYPE = new RDFName(this, "disseminationType");
        IS_VOLATILE = new RDFName(this, "isVolatile");
        LAST_MODIFIED_DATE = new RDFName(this, "lastModifiedDate");
        MIME_TYPE = new RDFName(this, "mimeType");
        VERSION = new RDFName(this, "version");
    }

}
