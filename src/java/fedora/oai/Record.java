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

package fedora.oai;

import java.util.Set;

/**
 * Metadata expressed in a single format with a header and optional "about"
 * data which is descriptive of the metadata (such as rights or provenance 
 * statements).
 * 
 * @author Chris Wilper
 * @see <a
 *      href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Record">
 *      http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Record</a>
 */
public interface Record {

    /**
     * Get the header portion of the record.
     */
    public abstract Header getHeader();

    /**
     * Get the metadata portion of the record. This must be an xml chunk in
     * which the W3C schema is identified by the root element's
     * xsi:schemaLocation attribute. If getHeader().isAvailable() is false, this
     * may be null.
     */
    public abstract String getMetadata();

    /**
     * Get the 'about' portions of the record. There will be zero or more items
     * in the resulting Set. These are descriptors of the metadata. These must
     * be xml chunks in which the W3C schema is identified by the root element's
     * xsi:schemaLocation attribute. If getHeader().isAvailable() is false, this
     * may be null.
     */
    public abstract Set getAbouts();

}
