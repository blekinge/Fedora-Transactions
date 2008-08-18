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
package fedora.server.storage.types;

import fedora.common.Constants;

/**
 * DigitalObject utility methods.
 *
 * @author Chris Wilper
 */
public abstract class DigitalObjectUtil
        implements Constants {
   
    /**
     * Upgrades a legacy (pre-Fedora-3.0) object by setting the correct MIME
     * type and Format URI for all "reserved" datastreams.
     * 
     * @param obj the object to update.
     */
    @SuppressWarnings("deprecation")
    public static void updateLegacyDatastreams(DigitalObject obj) {
        final String xml = "text/xml";
        final String rdf = "application/rdf+xml";
        updateLegacyDatastream(obj, "DC", xml, OAI_DC2_0.uri);
        updateLegacyDatastream(obj, "RELS-EXT", rdf, RELS_EXT1_0.uri);
        updateLegacyDatastream(obj, "RELS-INT", rdf, RELS_INT1_0.uri);
        updateLegacyDatastream(obj, "POLICY", xml, XACML_POLICY1_0.uri);
        String fType = obj.getExtProperty(RDF.TYPE.uri);
        if (MODEL.BDEF_OBJECT.looselyMatches(fType, false)) {
            updateLegacyDatastream(obj,
                                   "METHODMAP",
                                   xml,
                                   SDEF_METHOD_MAP1_0.uri);
        } else if (MODEL.BMECH_OBJECT.looselyMatches(fType, false)) {
            updateLegacyDatastream(obj,
                                   "METHODMAP",
                                   xml,
                                   SDEP_METHOD_MAP1_0.uri);
            updateLegacyDatastream(obj,
                                   "DSINPUTSPEC",
                                   xml,
                                   DS_INPUT_SPEC1_0.uri);
            updateLegacyDatastream(obj,
                                   "WSDL",
                                   xml,
                                   WSDL.uri);
        }
    }
    
    private static void updateLegacyDatastream(DigitalObject obj,
                                               String dsId,
                                               String mimeType,
                                               String formatURI) {
        for (Datastream ds: obj.datastreams(dsId)) {
            ds.DSMIME = mimeType;
            ds.DSFormatURI = formatURI;
        }
    }
}
