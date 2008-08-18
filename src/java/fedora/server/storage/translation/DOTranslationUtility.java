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

package fedora.server.storage.translation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.log4j.Logger;

import fedora.common.Constants;
import fedora.common.Models;
import fedora.common.rdf.RDFName;
import fedora.common.xml.namespace.QName;

import fedora.server.Server;
import fedora.server.config.ServerConfiguration;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.StreamWriteException;
import fedora.server.storage.types.AuditRecord;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.Disseminator;
import fedora.server.utilities.DateUtility;
import fedora.server.utilities.StreamUtility;

import static fedora.common.Models.CONTENT_MODEL_3_0;
import static fedora.common.Models.FEDORA_OBJECT_3_0;
import static fedora.common.Models.SERVICE_DEFINITION_3_0;
import static fedora.common.Models.SERVICE_DEPLOYMENT_3_0;

/**
 * Utility methods for usage by digital object serializers and deserializers.
 * This class provides methods for detecting various forms of relative
 * repository URLs, which are URLs that point to the hostname and port of the
 * local repository. Methods will detect these kinds of URLS in datastream
 * location fields and in special cases of inline XML. Methods are available to
 * convert these URLS back and forth from relative URL syntax, to Fedora's
 * internal local URL syntax, and to absolute URL sytnax. This utility class
 * defines different "translation contexts" and the format of these relative
 * URLs will be set appropriately to the context. Currently defined translation
 * contexts are: 0=Deserialize XML into java object appropriate for in-memory
 * usage 1=Serialize java object to XML appropriate for "public" export
 * (absolute URLs) 2=Serialize java object to XML appropriate for move/migrate
 * to another repository 3=Serialize java object to XML appropriate for internal
 * storage</b>
 * </p>
 * The public "normalize*" methods in this class should be called to make the
 * right decisions about what conversions should occur for what contexts. Other
 * utility methods set default values for datastreams and disseminators.
 * 
 * @author Sandy Payette
 */
@SuppressWarnings("deprecation")
public abstract class DOTranslationUtility
        implements Constants {

    /** Logger for this class. */
    private static final Logger LOG =
            Logger.getLogger(DOTranslationUtility.class.getName());

    /**
     * DESERIALIZE_INSTANCE: Deserialize XML into a java object appropriate for
     * in-memory usage. This will make the value of relative repository URLs
     * appropriate for instantiations of the digital object in memory. For
     * External (E) and Redirected (R) datastreams, any URLs that are relative
     * to the local repository are converted to absolute URLs using the
     * currently configured hostname:port of the repository. To do this, the
     * dsLocation is searched for instances the Fedora local URL string
     * ("http://local.fedora.server") which is the way Fedora internally keeps
     * track of instances of relative repository URLs. For Managed Content (M)
     * datastreams, the internal identifiers are instantiated as is. Also,
     * certain reserved inline XML datastreams (WSDL and SERVICE_PROFILE) are
     * searched for relative repository URLs and they are made absolute.
     */
    public static final int DESERIALIZE_INSTANCE = 0;

    /**
     * SERIALIZE_EXPORT_PUBLIC: Serialize digital object to XML appropriate for
     * "public" external use. This is context is appropriate when the exporting
     * repository will continue to exist and will continue to support callback
     * URLs for datastream content and disseminations. This gives a "public"
     * export of an object in which all relative repository URLs AND internal
     * identifiers are converted to absolute callback URLs. For External (E) and
     * Redirected (R) datastreams, any URLs that are relative to the local
     * repository are converted to absolute URLs using the currently configured
     * hostname:port of the repository. For Managed Content (M) datastreams, the
     * internal identifiers in dsLocation are converted to default dissemination
     * URLs so they can serve as callbacks to the repository to obtain the
     * internally managed content. Also, selected inline XML datastreams (i.e.,
     * WSDL and SERVICE_PROFILE) are searched for relative repository URLs and
     * they are made absolute.
     */
    public static final int SERIALIZE_EXPORT_PUBLIC = 1;

    /**
     * SERIALIZE_EXPORT_MIGRATE: Serialize digital object to XML in a manner
     * appropriate for migrating or moving objects from one repository to
     * another. This context is appropriate when the local repository will NOT
     * be available after objects have been migrated to a new repository. For
     * External (E) and Redirected (R)datastreams, any URLs that are relative to
     * the local repository will be expressed with the Fedora local URL syntax
     * (which consists of the string "local.fedora.server" standing in place of
     * the actual "hostname:port"). This enables a new repository to ingest the
     * serialization and maintain the relative nature of the URLs (they will
     * become relative to the *new* repository. Also, for Managed Content (M)
     * datastreams, the internal identifiers in dsLocation are converted to
     * default dissemination URLs. This enables the new repository to callback
     * to the old repository to obtain the content bytestream to be stored in
     * the new repository. Also, within selected inline XML datastreams (i.e.,
     * WSDL and SERVICE_PROFILE) any URLs that are relative to the local
     * repository will also be expressed with the Fedora local URL syntax.
     */
    public static final int SERIALIZE_EXPORT_MIGRATE = 2;

    /**
     * SERIALIZE_STORAGE_INTERNAL: Serialize java object to XML appropriate for
     * persistent storage in the repository, ensuring that any URLs that are
     * relative to the local repository are stored with the Fedora local URL
     * syntax. The Fedora local URL syntax consists of the string
     * "local.fedora.server" standing in place of the actual "hostname:port" on
     * the URL). Managed Content (M) datastreams are stored with internal
     * identifiers in dsLocation. Also, within selected inline XML datastreams
     * (i.e., WSDL and SERVICE_PROFILE) any URLs that are relative to the local
     * repository will also be stored with the Fedora local URL syntax. Note
     * that a view of the storage serialization can be obtained via the
     * getObjectXML method of API-M.
     */
    public static final int SERIALIZE_STORAGE_INTERNAL = 3;

    /**
     * SERIALIZE_EXPORT_ARCHIVE: Serialize digital object to XML in a manner
     * appropriate for creating a stand alone archive of objects from a
     * repository that will NOT be available after objects have been exported.
     * For External (E) and Redirected (R)datastreams, any URLs that are
     * relative to the local repository will be expressed with the Fedora local
     * URL syntax (which consists of the string "local.fedora.server" standing
     * in place of the actual "hostname:port"). This enables a new repository to
     * ingest the serialization and maintain the relative nature of the URLs
     * (they will become relative to the *new* repository. Also, for Managed
     * Content (M) datastreams, the internal identifiers in dsLocation are
     * converted to default dissemination URLs, and the contents of the URL's
     * are included inline via base-64 encoding. This enables the new repository
     * recreate the content bytestream to be stored in the new repository, when
     * the original repository is no longer available. Also, within selected
     * inline XML datastreams (i.e., WSDL and SERVICE_PROFILE) any URLs that are
     * relative to the local repository will also be expressed with the Fedora
     * local URL syntax.
     */
    public static final int SERIALIZE_EXPORT_ARCHIVE = 4;
   
    /**
     * Deserialize or Serialize as is.  This context doesn't attempt to do 
     * any conversion of URLs.
     */
    public static final int AS_IS = 5;

    // Fedora URL LOCALIZATION Pattern:
    // Pattern that is used as the internal replacement syntax for URLs that
    // refer back to the local repository.  This pattern virtualized the
    // repository server address, so that if the host:port of the repository is
    // changed, objects that have URLs that refer to the local repository won't break.
    public static Pattern s_fedoraLocalPattern =
            Pattern.compile("http://local.fedora.server/");

    // PATTERN FOR DEPRECATED METHOD (getItem of the Default Disseminator), for example:
    public static Pattern s_getItemPattern =
            Pattern.compile("/fedora-system:3/getItem\\?itemID=");

    // ABSOLUTE REPOSITORY URL Patterns:
    // Patterns of how the protocol and repository server address may be encoded
    // in a URL that points back to the local repository.
    private static Pattern s_servernamePort; // "http://hostname:port/"

    private static Pattern s_servername; // "http://hostname/"

    private static Pattern s_localhostPort; // "http://localhost:port/"

    private static Pattern s_localhost; // "http://localhost/"

    private static Pattern s_servernamePortSSL; // "https://hostname:redirectport/"

    private static Pattern s_servernameSSL; // "https://hostname/"

    private static Pattern s_localhostPortSSL; // "https://localhost:redirectport/"

    private static Pattern s_localhostSSL; // "https://localhost/"

    // CALLBACK DISSEMINATION URL Pattern (for M datastreams in export files):
    // Pattern of how protocol, repository server address, and path is encoded
    // for a callback dissemination URL to the local repository.
    // This is used for encoding datastream location URLs for Managed Content
    // datastreams inside an export file.  Internal Fedora identifiers for
    // the Managed Content datastreams are replaced with public callback URLS.
    private static String s_localDissemUrlStart; // "http://hostname:port/fedora/get/"

    // The actual host and port of the Fedora repository server
    private static String s_hostInfo = null;

    private static boolean m_serverOnPort80 = false;

    private static boolean m_serverOnRedirectPort443 = false;
    
    private static XMLInputFactory m_xmlInputFactory = XMLInputFactory.newInstance();

    // initialize static class with stuff that's used by all DO Serializerers
    static {
        // get host port from system properties (for testing without server instance)
        String fedoraServerHost = System.getProperty("fedoraServerHost");
        String fedoraServerPort = System.getProperty("fedoraServerPort");
        String fedoraServerPortSSL = System.getProperty("fedoraRedirectPort");
        if (fedoraServerPort != null) {
            if (fedoraServerPort.equals("80")) {
                m_serverOnPort80 = true;
            }
        }
        if (fedoraServerPortSSL != null) {
            if (fedoraServerPortSSL.equals("443")) {
                m_serverOnRedirectPort443 = true;
            }
        }

        // otherwise, get host port from the server instance if they are null			
        if (fedoraServerHost == null || fedoraServerPort == null) {
            // if fedoraServerHost or fedoraServerPort system properties
            // are not defined, read them from server configuration
            ServerConfiguration config = Server.getConfig();
            fedoraServerHost = config.getParameter("fedoraServerHost")
                    .getValue();
            fedoraServerPort = config.getParameter("fedoraServerPort")
                    .getValue();
            fedoraServerPortSSL = config.getParameter("fedoraRedirectPort")
                    .getValue();
            if (fedoraServerPort.equals("80")) {
                m_serverOnPort80 = true;
            }
            if (fedoraServerPortSSL.equals("443")) {
                m_serverOnRedirectPort443 = true;
            }
        }
        // set the currently configured host:port of the repository
        s_hostInfo = "http://" + fedoraServerHost;
        if (!fedoraServerPort.equals("80") && !fedoraServerPort.equals("443")) {
            s_hostInfo = s_hostInfo + ":" + fedoraServerPort;
        }
        s_hostInfo = s_hostInfo + "/";

        // compile the pattern for public dissemination URLs at local server
        s_localDissemUrlStart = s_hostInfo + "fedora/get/";

        // compile other patterns using the configured host and port
        s_servernamePort =
                Pattern.compile("http://" + fedoraServerHost + ":"
                        + fedoraServerPort + "/");
        s_servername = Pattern.compile("http://" + fedoraServerHost + "/");
        s_localhostPort =
                Pattern.compile("http://localhost:" + fedoraServerPort + "/");
        s_localhost = Pattern.compile("http://localhost/");

        s_servernamePortSSL =
                Pattern.compile("https://" + fedoraServerHost + ":"
                        + fedoraServerPortSSL + "/");
        s_servernameSSL = Pattern.compile("https://" + fedoraServerHost + "/");
        s_localhostPortSSL =
                Pattern.compile("https://localhost:" + fedoraServerPortSSL
                        + "/");
        s_localhostSSL = Pattern.compile("https://localhost/");
    }

    /**
     * Make URLs that are relative to the local Fedora repository ABSOLUTE URLs.
     * First, see if any URLs are expressed in relative URL syntax (beginning
     * with "fedora/get" or "fedora/search") and convert these to the special
     * Fedora local URL syntax ("http://local.fedora.server/..."). Then look for
     * all URLs that contain the special Fedora local URL syntax and replace
     * instances of this string with the actual host:port configured for the
     * repository. This ensures that all forms of relative repository URLs are
     * converted to proper absolute URLs that reference the hostname:port of the
     * local Fedora repository. Examples:
     * "http://local.fedora.server/fedora/get/demo:1/DS1" is converted to
     * "http://myrepo.com:8080/fedora/get/demo:1/DS1" "fedora/get/demo:1/DS1" is
     * converted to "http://myrepo.com:8080/fedora/get/demo:1/DS1"
     * "http://local.fedora.server/fedora/get/demo:1/sdef:1/getFoo?in="http://local.fedora.server/fedora/get/demo:2/DC"
     * is converted to
     * "http://myrepo.com:8080/fedora/get/demo:1/sdef:1/getFoo?in="http://myrepo.com:8080/fedora/get/demo:2/DC"
     * 
     * @param xmlContent
     * @return String with all relative repository URLs and Fedora local URLs
     *         converted to absolute URL syntax.
     */
    private static String makeAbsoluteURLs(String input) {
        String output = input;

        // Make absolute URLs out of all instances of the Fedora local URL syntax ...
        output = s_fedoraLocalPattern.matcher(output).replaceAll(s_hostInfo);
        LOG.debug("makeAbsoluteURLs: input=" + input + ", output=" + output);
        return output;
    }

    /**
     * Detect all forms of URLs that point to the local Fedora repository and
     * make sure they are encoded in the special Fedora local URL syntax
     * (http://local.fedora.server/..."). First, look for relative URLs that
     * begin with "fedora/get" or "fedora/search" replaces instances of these
     * string patterns with the special Fedora relative URL syntax. Then, look
     * for absolute URLs that have a host:port equal to the host:port currently
     * configured for the Fedora repository and replace host:port with the
     * special string. The special Fedora relative URL string provides a
     * consistent unique string be easily searched for and either converted back
     * to an absolute URL or a relative URL to the repository. Examples:
     * "http://myrepo.com:8080/fedora/get/demo:1/DS1" is converted to
     * "http://local.fedora.server/fedora/get/demo:1/DS1"
     * "https://myrepo.com:8443/fedora/get/demo:1/sdef:1/getFoo?in="http://myrepo.com:8080/fedora/get/demo:2/DC"
     * is converted to
     * "http://local.fedora.server/fedora/get/demo:1/sdef:1/getFoo?in="http://local.fedora.server/fedora/get/demo:2/DC"
     * "http://myrepo.com:8080/saxon..." (internal service in sDep WSDL) is
     * converted to "http://local.fedora.server/saxon..."
     * 
     * @param input
     * @return String with all forms of relative repository URLs converted to
     *         the Fedora local URL syntax.
     */
    private static String makeFedoraLocalURLs(String input) {
        String output = input;

        // Detect any absolute URLs that refer to the local repository 
        // and convert them to the Fedora LOCALIZATION URL syntax
        // (i.e., "http://local.fedora.server/...")\

        // convert URLs that begin with http along with host and port
        // explicitly configured for the repository
        output =
                s_servernamePort.matcher(output)
                        .replaceAll(s_fedoraLocalPattern.pattern());
        output =
                s_localhostPort.matcher(output).replaceAll(s_fedoraLocalPattern
                        .pattern());

        // convert URLs that begin with https along with the host and port
        // explicitly configured for the repository
        output =
                s_servernamePortSSL.matcher(output)
                        .replaceAll(s_fedoraLocalPattern.pattern());
        output =
                s_localhostPortSSL.matcher(output)
                        .replaceAll(s_fedoraLocalPattern.pattern());

        if (m_serverOnPort80) {
            // if the server is running on port 80, convert
            // URLs that begin with "http://localhost/"
            output =
                    s_servername.matcher(output)
                            .replaceAll(s_fedoraLocalPattern.pattern());
            output =
                    s_localhost.matcher(output).replaceAll(s_fedoraLocalPattern
                            .pattern());
        }
        if (m_serverOnRedirectPort443) {
            // if the server is running on port 443, convert
            // URLs that begin with "https://localhost/"
            output =
                    s_servernameSSL.matcher(output)
                            .replaceAll(s_fedoraLocalPattern.pattern());
            output =
                    s_localhostSSL.matcher(output)
                            .replaceAll(s_fedoraLocalPattern.pattern());
        }
        LOG.debug("makeFedoraLocalURLs: input=" + input + ", output=" + output);
        return output;
    }

    /**
     * Utility method to detect instances of of dsLocation URLs that use a
     * deprecated default disseminator method
     * (/fedora/get/{PID}/fedora-system:3/getItem?itemID={DSID} and replace it
     * with the new API-A-LITE syntax for getting a datastream
     * (/fedora/get/{PID}/{DSID}
     * 
     * @param input
     * @return
     */
    private static String convertGetItemURLs(String input) {
        String output = input;

        // Detect the old default disseminator syntax for getting datastreams
        // (i.e., getItem), and replace with new API-A-LITE syntax.

        output = s_getItemPattern.matcher(input).replaceAll("/");
        LOG.debug("convertGetItemURLs: input=" + input + ", output=" + output);
        return output;
    }

    /*
     * Utility method to normalize the value of datastream location depending on
     * the translation context. This is mainly to deal with External (E) and
     * Redirected (R) datastream locations that are self-referential to the
     * local repository (i.e., relative repository URLs) and with Managed
     * Content (M) datastreams whose location is an internal identifier. @param
     * PID The PID of the object that contains the datastream @param ds The
     * datastream whose location is to be processed @param transContext Integer
     * value indicating the serialization or deserialization context. Valid
     * values are defined as constants in
     * fedora.server.storage.translation.DOTranslationUtility:
     * 0=DOTranslationUtility.DESERIALIZE_INSTANCE
     * 1=DOTranslationUtility.SERIALIZE_EXPORT_PUBLIC
     * 2=DOTranslationUtility.SERIALIZE_EXPORT_MIGRATE
     * 3=DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL
     * 2=DOTranslationUtility.SERIALIZE_EXPORT_ARCHIVE @return
     */
    public static Datastream normalizeDSLocationURLs(String PID,
                                                     Datastream origDS,
                                                     int transContext) {
        Datastream ds = origDS.copy();
        if (transContext == AS_IS) {
            return ds;
        }
        if (transContext == DOTranslationUtility.DESERIALIZE_INSTANCE) {
            if (ds.DSControlGrp.equals("E") || ds.DSControlGrp.equals("R")) {
                // MAKE ABSOLUTE REPO URLs
                ds.DSLocation = makeAbsoluteURLs(ds.DSLocation);
            }
        } else if (transContext == DOTranslationUtility.SERIALIZE_EXPORT_PUBLIC) {
            if (ds.DSControlGrp.equals("E") || ds.DSControlGrp.equals("R")) {
                // MAKE ABSOLUTE REPO URLs
                ds.DSLocation = makeAbsoluteURLs(ds.DSLocation);
            } else if (ds.DSControlGrp.equals("M")) {
                //if (!ds.DSLocation.startsWith("http://localhost:8080/fedora-demo")) {

                // MAKE DISSEMINATION URLs
                if (ds.DSCreateDT == null) {
                    ds.DSLocation =
                            s_localDissemUrlStart + PID + "/" + ds.DatastreamID;
                } else {
                    ds.DSLocation =
                            s_localDissemUrlStart
                                    + PID
                                    + "/"
                                    + ds.DatastreamID
                                    + "/"
                                    + DateUtility
                                            .convertDateToString(ds.DSCreateDT);
                }
                //}
            }
        } else if (transContext == DOTranslationUtility.SERIALIZE_EXPORT_MIGRATE) {
            if (ds.DSControlGrp.equals("E") || ds.DSControlGrp.equals("R")) {
                // MAKE FEDORA LOCAL REPO URLs
                ds.DSLocation = makeFedoraLocalURLs(ds.DSLocation);
            } else if (ds.DSControlGrp.equals("M")) {
                // MAKE DISSEMINATION URLs
                if (ds.DSCreateDT == null) {
                    ds.DSLocation =
                            s_localDissemUrlStart + PID + "/" + ds.DatastreamID;
                } else {
                    ds.DSLocation =
                            s_localDissemUrlStart
                                    + PID
                                    + "/"
                                    + ds.DatastreamID
                                    + "/"
                                    + DateUtility
                                            .convertDateToString(ds.DSCreateDT);
                }
            }
        } else if (transContext == DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL) {
            //String relativeLoc=ds.DSLocation;
            if (ds.DSControlGrp.equals("E") || ds.DSControlGrp.equals("R")) {
                // MAKE FEDORA LOCAL REPO URLs
                ds.DSLocation = makeFedoraLocalURLs(ds.DSLocation);
            } else if (ds.DSControlGrp.equals("M")) {
                // MAKE INTERNAL IDENTIFIERS (PID+DSID+DSVersionID)
                ds.DSLocation =
                        PID + "+" + ds.DatastreamID + "+" + ds.DSVersionID;
            }
        } else if (transContext == DOTranslationUtility.SERIALIZE_EXPORT_ARCHIVE) {
            if (ds.DSControlGrp.equals("E") || ds.DSControlGrp.equals("R")) {
                // MAKE FEDORA LOCAL REPO URLs
                ds.DSLocation = makeFedoraLocalURLs(ds.DSLocation);
            } else if (ds.DSControlGrp.equals("M")) {
                // MAKE DISSEMINATION URLs
                if (ds.DSCreateDT == null) {
                    ds.DSLocation =
                            s_localDissemUrlStart + PID + "/" + ds.DatastreamID;
                } else {
                    ds.DSLocation =
                            s_localDissemUrlStart
                                    + PID
                                    + "/"
                                    + ds.DatastreamID
                                    + "/"
                                    + DateUtility
                                            .convertDateToString(ds.DSCreateDT);
                }
            }
        }

        // In any event, look for the deprecated getItem method of the default disseminator
        // (i.e., "/fedora-system:3/getItem?itemID=") and convert to new API-A-LITE syntax.
        if (ds.DSControlGrp.equals("E") || ds.DSControlGrp.equals("R")) {
            ds.DSLocation = convertGetItemURLs(ds.DSLocation);
        }

        return ds;
    }

    /**
     * Utility method to normalize a chunk of inline XML depending on the
     * translation context. This is mainly to deal with certain inline XML
     * datastreams found in Service Deployment objects that may contain a
     * service URL that references the host:port of the local Fedora server.
     * This method will usually only ever be called to check WSDL and
     * SERVICE_PROFILE inline XML datastream, but is of general utility for
     * dealing with any datastreams that may contain URLs that reference the
     * local Fedora server. However, it this method should be used sparingly,
     * and only on inline XML datastreams where the impact of the conversions is
     * well understood.
     * 
     * @param xml
     *        a chunk of XML that's contents of an inline XML datastream
     * @param transContext
     *        Integer value indicating the serialization or deserialization
     *        context. Valid values are defined as constants in
     *        fedora.server.storage.translation.DOTranslationUtility:
     *        0=DOTranslationUtility.DESERIALIZE_INSTANCE
     *        1=DOTranslationUtility.SERIALIZE_EXPORT_PUBLIC
     *        2=DOTranslationUtility.SERIALIZE_EXPORT_MIGRATE
     *        3=DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL
     *        4=DOTranslationUtility.SERIALIZE_EXPORT_ARCHIVE
     * @return the inline XML contents with appropriate conversions.
     */
    public static String normalizeInlineXML(String xml, int transContext) {
        if (transContext == AS_IS) {
            return xml;
        }
        if (transContext == DOTranslationUtility.DESERIALIZE_INSTANCE) {
            // MAKE ABSOLUTE REPO URLs
            return makeAbsoluteURLs(xml);
        } else if (transContext == DOTranslationUtility.SERIALIZE_EXPORT_PUBLIC) {
            // MAKE ABSOLUTE REPO URLs
            return makeAbsoluteURLs(xml);
        } else if (transContext == DOTranslationUtility.SERIALIZE_EXPORT_MIGRATE) {
            // MAKE FEDORA LOCAL REPO URLs
            return makeFedoraLocalURLs(xml);
        } else if (transContext == DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL) {
            // MAKE FEDORA LOCAL REPO URLs
            return makeFedoraLocalURLs(xml);
        } else if (transContext == DOTranslationUtility.SERIALIZE_EXPORT_ARCHIVE) {
            // MAKE FEDORA LOCAL REPO URLs
            return makeFedoraLocalURLs(xml);
        }
        return xml;
    }

    /**
     * Check for null values in attributes and set them to empty string so
     * 'null' does not appear in XML attribute values. This helps in XML
     * validation of required attributes. If 'null' is the attribute value then
     * validation would incorrectly consider in a valid non-empty value. Also,
     * we set some other default values here.
     * 
     * @param ds
     *        The Datastream object to work on.
     * @return The Datastream value with default set.
     * @throws ObjectIntegrityException
     */

    public static Datastream setDatastreamDefaults(Datastream ds)
            throws ObjectIntegrityException {

        if ((ds.DSMIME == null || ds.DSMIME.equals(""))
                && ds.DSControlGrp.equalsIgnoreCase("X")) {
            ds.DSMIME = "text/xml";
        }

        if (ds.DSState == null || ds.DSState.equals("")) {
            ds.DSState = "A";
        }

        // For METS backward compatibility
        if (ds.DSInfoType == null || ds.DSInfoType.equals("")
                || ds.DSInfoType.equalsIgnoreCase("OTHER")) {
            ds.DSInfoType = "UNSPECIFIED";
        }

        // LOOK! For METS backward compatibility:
        // If we have a METS MDClass value, and DSFormatURI isn't already
        // assigned, preserve MDClass and MDType in a DSFormatURI.
        // Note that the system is taking over the DSFormatURI in this case.
        // Therefore, if a client subsequently modifies the DSFormatURI
        // this METS legacy informatin will be lost, in which case the inline
        // datastream will default to amdSec/techMD in a subsequent METS export.
        if (ds.DSControlGrp.equalsIgnoreCase("X")) {
            if (((DatastreamXMLMetadata) ds).DSMDClass != 0
                    && ds.DSFormatURI == null) {
                String mdClassName = "";
                String mdType = ds.DSInfoType;
                String otherType = "";
                if (((DatastreamXMLMetadata) ds).DSMDClass == 1) {
                    mdClassName = "techMD";
                } else if (((DatastreamXMLMetadata) ds).DSMDClass == 2) {
                    mdClassName = "sourceMD";
                } else if (((DatastreamXMLMetadata) ds).DSMDClass == 3) {
                    mdClassName = "rightsMD";
                } else if (((DatastreamXMLMetadata) ds).DSMDClass == 4) {
                    mdClassName = "digiprovMD";
                } else if (((DatastreamXMLMetadata) ds).DSMDClass == 5) {
                    mdClassName = "descMD";
                }
                if (!mdType.equals("MARC") && !mdType.equals("EAD")
                        && !mdType.equals("DC") && !mdType.equals("NISOIMG")
                        && !mdType.equals("LC-AV") && !mdType.equals("VRA")
                        && !mdType.equals("TEIHDR") && !mdType.equals("DDI")
                        && !mdType.equals("FGDC")) {
                    mdType = "OTHER";
                    otherType = ds.DSInfoType;
                }
                ds.DSFormatURI =
                        "info:fedora/fedora-system:format/xml.mets."
                        + mdClassName + "." + mdType + "." + otherType;
            }
        }
        return ds;
    }

    /**
     * Appends XML to a StringBuffer. Essentially, just appends all text content
     * of the inputStream, trimming any leading and trailing whitespace. It does
     * his in a streaming fashion, with resource consumption entirely comprised
     * of fixed internal buffers.
     * 
     * @param in
     *        InputStreaming containing serialized XML.
     * @param buf
     *        StringBuffer to write XML content to.
     * @param encoding
     *        Character set encoding.
     */

    /*
     * XXX: May want to change StringBuffer to StringBuilder or even Appendable
     * at some point, but that involves a very minor refactoring of the existing
     * serializers.
     */
    public static void appendXMLStream(InputStream in,
                                       StringBuffer buf,
                                       String encoding)
            throws ObjectIntegrityException, UnsupportedEncodingException,
            StreamIOException {
        if (in == null) {
            throw new ObjectIntegrityException("Object's inline xml "
                    + "stream cannot be null.");
        }
        try {
            InputStreamReader chars =
                    new InputStreamReader(in, Charset.forName(encoding));

            /* Content buffer */
            char[] charBuf = new char[4096];

            /* Beginning/ending whitespace buffer */
            char[] wsBuf = new char[4096];

            int len;
            int start;
            int end;
            int wsLen = 0;
            boolean atBeginning = true;
            while ((len = chars.read(charBuf)) != -1) {
                start = 0;
                end = len - 1;

                /* Strip out any leading whitespace */
                if (atBeginning) {
                    while (start < len) {
                        if (charBuf[start] > 0x20) break;
                        start++;
                    }
                    if (start < len) atBeginning = false;
                }

                /*
                 * Hold aside any whitespace at the end of the current chunk. If
                 * we make it to the next chunk, then append our whitespace to
                 * the buffer. Using this methodology, we may "trim" at most
                 * {buffer length} characters from the end.
                 */

                if (wsLen > 0) {
                    /* Commit previous ending whitespace */
                    buf.append(wsBuf, 0, wsLen);
                    wsLen = 0;
                }

                while (end > start) {
                    /* Buffer current ending whitespace */
                    if (charBuf[end] > 0x20) break;
                    wsBuf[wsLen] = charBuf[end];
                    wsLen++;
                    end--;
                }

                if (start < len) {
                    buf.append(charBuf, start, end + 1 - start);
                }
            }
        } catch (UnsupportedEncodingException uee) {
            throw uee;
        } catch (IOException ioe) {
            throw new StreamIOException("Error reading from inline xml datastream.");
        } finally {
            try {
                in.close();
            } catch (IOException closeProb) {
                throw new StreamIOException("Error closing read stream.");
            }
        }
    }

    /*
     * Certain serviceDeployment datastreams require special processing to
     * fix/complete URLs and do variable substitution (such as replacing
     * 'local.fedora.server' with fedora's baseURL)
     */
    public static void normalizeDatastreams(DigitalObject obj,
                                     int transContext,
                                     String characterEncoding)
            throws UnsupportedEncodingException {
        if (transContext == AS_IS) {
            return;
        }
        if (obj.hasRelationship(MODEL.HAS_MODEL, Models.SERVICE_DEPLOYMENT_3_0)) {
            Iterator<String> datastreams = obj.datastreamIdIterator();
            while (datastreams.hasNext()) {
                String dsid = datastreams.next();

                if (dsid.equals("WSDL") || dsid.equals("SERVICE-PROFILE")) {
                    for (Datastream d : obj.datastreams(dsid)) {
                        if (!(d instanceof DatastreamXMLMetadata)) {
                            LOG
                                    .warn(obj.getPid()
                                            + " : Refusing to normalize URLs in datastream "
                                            + dsid
                                            + " because it is not inline XML");
                            continue;
                        }

                        DatastreamXMLMetadata xd = (DatastreamXMLMetadata) d;
                        LOG.debug(obj.getPid() + " : normalising URLs in "
                                + dsid);
                        xd.xmlContent =
                                DOTranslationUtility
                                        .normalizeInlineXML(new String(xd.xmlContent,
                                                                       "UTF-8"),
                                                            transContext)
                                        .getBytes(characterEncoding);
                    }
                }
            }
        }
    }

    @Deprecated
    public static Disseminator setDisseminatorDefaults(Disseminator diss)
            throws ObjectIntegrityException {

        // Until future when we implement selective versioning,
        // set default to true.
        diss.dissVersionable = true;

        if (diss.dissState == null || diss.dissState.equals("")) {
            diss.dissState = "A";
        }
        return diss;
    }

    protected static String oneString(String[] idList) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < idList.length; i++) {
            if (i > 0) {
                out.append(' ');
            }
            out.append((String) idList[i]);
        }
        return out.toString();
    }

    protected static void writeToStream(StringBuffer buf,
                                        OutputStream out,
                                        String encoding,
                                        boolean closeWhenFinished)
            throws StreamIOException, UnsupportedEncodingException {
        try {
            out.write(buf.toString().getBytes(encoding));
            out.flush();
        } catch (IOException e) {
            throw new StreamWriteException("Error serializing object", e);
        } finally {
            if (closeWhenFinished) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOG.warn("Unable to close serializer stream", e);
                }
            }
        }
    }

    protected static String getStateAttribute(DigitalObject obj) {
        try {
            char s = obj.getState().toUpperCase().charAt(0);
            if (s == 'D') {
                return MODEL.DELETED.localName;
            } else if (s == 'I') {
                return MODEL.INACTIVE.localName;
            } else {
                return MODEL.ACTIVE.localName;
            }
        } catch (Throwable th) {
            return null;
        }
    }

    public static RDFName getTypeAttribute(DigitalObject obj)
            throws ObjectIntegrityException {
        if (obj.hasRelationship(MODEL.HAS_MODEL, SERVICE_DEFINITION_3_0)) {
            return MODEL.BDEF_OBJECT;
        }
        if (obj.hasRelationship(MODEL.HAS_MODEL, SERVICE_DEPLOYMENT_3_0)) {
            return MODEL.BMECH_OBJECT;
        }
        if (obj.hasRelationship(MODEL.HAS_MODEL, CONTENT_MODEL_3_0)) {

            // FOXML 1.0 doesn't support this type; down-convert
            return MODEL.DATA_OBJECT;
        }
        if (obj.hasRelationship(MODEL.HAS_MODEL, FEDORA_OBJECT_3_0)) {
            return MODEL.DATA_OBJECT;
        }
        return null;
    }

    /**
     * The audit record is created by the system, so programmatic validation
     * here is o.k. Normally, validation takes place via XML Schema and
     * Schematron.
     * 
     * @param audit
     * @throws ObjectIntegrityException
     */
    protected static void validateAudit(AuditRecord audit)
            throws ObjectIntegrityException {
        if (audit.id == null || audit.id.equals("")) {
            throw new ObjectIntegrityException("Audit record must have id.");
        }
        if (audit.date == null || audit.date.equals("")) {
            throw new ObjectIntegrityException("Audit record must have date.");
        }
        if (audit.processType == null || audit.processType.equals("")) {
            throw new ObjectIntegrityException("Audit record must have processType.");
        }
        if (audit.action == null || audit.action.equals("")) {
            throw new ObjectIntegrityException("Audit record must have action.");
        }
        if (audit.componentID == null) {
            audit.componentID = ""; // for backwards compatibility, no error on null
            // throw new ObjectIntegrityException("Audit record must have componentID.");
        }
        if (audit.responsibility == null || audit.responsibility.equals("")) {
            throw new ObjectIntegrityException("Audit record must have responsibility.");
        }
    }

    protected static String getAuditTrail(DigitalObject obj)
            throws ObjectIntegrityException {
        StringBuffer buf = new StringBuffer();
        final String indent0 = "            ";
        final String indent1 = indent0 + "    ";
        final String indent2 = indent1 + "    ";
        appendOpenElement(buf, indent0, AUDIT.AUDIT_TRAIL, true);
        for (AuditRecord audit : obj.getAuditRecords()) {
            DOTranslationUtility.validateAudit(audit);
            appendOpenElement(buf, indent1, AUDIT.RECORD, AUDIT.ID, audit.id);
            appendFullElement(buf,
                              indent2,
                              AUDIT.PROCESS,
                              AUDIT.TYPE,
                              audit.processType);
            appendFullElement(buf, indent2, AUDIT.ACTION, audit.action);
            appendFullElement(buf,
                              indent2,
                              AUDIT.COMPONENT_ID,
                              audit.componentID);
            appendFullElement(buf,
                              indent2,
                              AUDIT.RESPONSIBILITY,
                              audit.responsibility);
            appendFullElement(buf, indent2, AUDIT.DATE, DateUtility
                    .convertDateToString(audit.date));
            appendFullElement(buf,
                              indent2,
                              AUDIT.JUSTIFICATION,
                              audit.justification);
            appendCloseElement(buf, indent1, AUDIT.RECORD);
        }
        appendCloseElement(buf, indent0, AUDIT.AUDIT_TRAIL);
        return buf.toString();
    }

    protected static List<AuditRecord> getAuditRecords(XMLEventReader reader)
            throws XMLStreamException {
        List<AuditRecord> records = new ArrayList<AuditRecord>();
        String inElement = null;

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement element = (StartElement) event;
                inElement = element.getName().getLocalPart();
                if (inElement.equals(AUDIT.RECORD.localName)) {
                    AuditRecord record = new AuditRecord();
                    java.util.Iterator<?> it = element.getAttributes();
                    while (it.hasNext()) {
                        Attribute attr = (Attribute) it.next();
                        if (attr.getName().getLocalPart()
                                .equals(AUDIT.ID.localName)) {
                            record.id = attr.getValue();
                        }
                    }
                    records.add(record);
                } else if (inElement.equals(AUDIT.PROCESS.localName)) {
                    java.util.Iterator<?> it = element.getAttributes();
                    while (it.hasNext()) {
                        Attribute attr = (Attribute) it.next();
                        if (attr.getName().getLocalPart()
                                .equals(AUDIT.TYPE.localName)) {
                            records.get(records.size() - 1).processType =
                                    attr.getValue();
                        }
                    }
                }
            }
            if (event.isEndElement()) {
                inElement = "";
            }
            if (event.isCharacters()) {
                Characters characters = (Characters) event;
                if (!records.isEmpty()) {
                    AuditRecord record = records.get(records.size() - 1);
                    if (inElement.equals(AUDIT.ACTION.localName)) {
                        record.action = characters.getData();
                    } else if (inElement.equals(AUDIT.COMPONENT_ID.localName)) {
                        record.componentID = characters.getData();
                    } else if (inElement.equals(AUDIT.DATE.localName)) {
                        record.date =
                                DateUtility.convertStringToDate(characters
                                        .getData());
                    } else if (inElement.equals(AUDIT.JUSTIFICATION.localName)) {
                        record.justification = characters.getData();
                    } else if (inElement.equals(AUDIT.RESPONSIBILITY.localName)) {
                        record.responsibility = characters.getData();
                    }
                }
            }
        }
        return records;
    }

    /**
     * Parse an audit:auditTrail and return a list of AuditRecords.
     * 
     * @since 3.0
     * @param auditTrail
     * @return
     * @throws XMLStreamException
     */
    protected static List<AuditRecord> getAuditRecords(InputStream auditTrail)
            throws XMLStreamException {
        XMLEventReader eventReader;
        synchronized (m_xmlInputFactory) {
            eventReader = m_xmlInputFactory.createXMLEventReader(auditTrail);
        }
        List<AuditRecord> records = getAuditRecords(eventReader);
        eventReader.close();
        return records;
    }

    protected static List<AuditRecord> getAuditRecords(Reader auditTrail)
            throws XMLStreamException {
        XMLEventReader eventReader;
        synchronized (m_xmlInputFactory) {
            eventReader = m_xmlInputFactory.createXMLEventReader(auditTrail);
        }
        List<AuditRecord> records = getAuditRecords(eventReader);
        eventReader.close();
        return records;
    }

    private static void appendOpenElement(StringBuffer buf,
                                          String indent,
                                          QName element,
                                          boolean declareNamespace) {
        buf.append(indent + "<" + element.qName);
        if (declareNamespace) {
            buf.append(" xmlns:" + element.namespace.prefix);
            buf.append("=\"" + element.namespace.uri + "\"");
        }
        buf.append(">\n");
    }

    private static void appendOpenElement(StringBuffer buf,
                                          String indent,
                                          QName element,
                                          QName attribute,
                                          String attributeContent) {
        buf.append(indent + "<" + element.qName + " ");
        buf.append(attribute.localName + "=\"");
        buf.append(StreamUtility.enc(attributeContent) + "\">\n");
    }

    private static void appendCloseElement(StringBuffer buf,
                                           String indent,
                                           QName element) {
        buf.append(indent + "</" + element.qName + ">\n");
    }

    private static void appendFullElement(StringBuffer buf,
                                          String indent,
                                          QName element,
                                          QName attribute,
                                          String attributeContent) {
        buf.append(indent + "<" + element.qName + " ");
        buf.append(attribute.localName + "=\"");
        buf.append(StreamUtility.enc(attributeContent) + "\"/>\n");
    }

    private static void appendFullElement(StringBuffer buf,
                                          String indent,
                                          QName element,
                                          String elementContent) {
        buf.append(indent + "<" + element.qName + ">");
        buf.append(StreamUtility.enc(elementContent));
        buf.append("</" + element.qName + ">\n");
    }
}
