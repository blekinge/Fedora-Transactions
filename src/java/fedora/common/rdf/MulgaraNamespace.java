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

import org.mulgara.query.rdf.Mulgara;

/**
 * The Mulgara RDF namespace.
 * 
 * <pre>
 * Namespace URI    : http://mulgara.org/mulgara#
 * Preferred Prefix : mulgara
 * </pre>
 * 
 * @see org.mulgara.query.rdf.Mulgara
 * @see org.mulgara.query.SpecialPredicates
 * @see org.mulgara.resolver.xsd.XSDResolverFactory
 * @author Edwin Shin
 */
public class MulgaraNamespace
        extends RDFNamespace {

    private static final long serialVersionUID = 1L;

    // Properties
    public final RDFName AFTER;

    public final RDFName BEFORE;

    public final RDFName GT;

    public final RDFName LT;

    public final RDFName IS;

    public final RDFName NOT_OCCURS;

    public final RDFName OCCURS;

    public final RDFName OCCURS_LESS_THAN;

    public final RDFName OCCURS_MORE_THAN;

    public MulgaraNamespace() {

        uri = Mulgara.NAMESPACE; // http://mulgara.org/mulgara#
        prefix = "mulgara";

        // Properties
        AFTER = new RDFName(this, "after");
        BEFORE = new RDFName(this, "before");
        GT = new RDFName(this, "gt");
        LT = new RDFName(this, "lt");

        IS = new RDFName(this, "is");
        NOT_OCCURS = new RDFName(this, "notOccurs");
        OCCURS = new RDFName(this, "occurs");
        OCCURS_LESS_THAN = new RDFName(this, "occursLessThan");
        OCCURS_MORE_THAN = new RDFName(this, "occursMoreThan");
    }

}
