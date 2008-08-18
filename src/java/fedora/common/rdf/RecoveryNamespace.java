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
 * The Fedora Recovery RDF namespace.
 * 
 * <pre>
 * Namespace URI    : info:fedora/fedora-system:def/recovery#
 * Preferred Prefix : recovery
 * </pre>
 * 
 * <p>
 * These are context attributes used for recovery. They represent potentially
 * auto-generated ids.
 * </p>
 * <p>
 * When Fedora is in journaling mode, these values are logged in the journal so
 * that they can be reused in recovery mode. This helps to ensure that the
 * recovery process populates the repository in the same way it was originally
 * populated.
 * </p>
 */
public class RecoveryNamespace
        extends RDFNamespace {

    private static final long serialVersionUID = 1L;

    /** The ID of the datastream, whether given or generated at add time. */
    public final RDFName DATASTREAM_ID;

    /** The ID of the disseminator, whether given or generated at add time. */
    public final RDFName DISSEMINATOR_ID;

    /** The PID of the object, whether given or generated at ingest time. */
    public final RDFName PID;

    /** The list of generated PIDs. */
    public final RDFName PID_LIST;

    /** The temporary ID that was assigned to the stream at upload time. */
    public final RDFName UPLOAD_ID;

    public RecoveryNamespace() {

        uri = "info:fedora/fedora-system:def/recovery#";
        prefix = "recovery";

        // Properties
        DATASTREAM_ID = new RDFName(this, "dsID");
        DISSEMINATOR_ID = new RDFName(this, "dissID");
        PID = new RDFName(this, "pid");
        PID_LIST = new RDFName(this, "pidList");
        UPLOAD_ID = new RDFName(this, "uploadID");
    }

}
