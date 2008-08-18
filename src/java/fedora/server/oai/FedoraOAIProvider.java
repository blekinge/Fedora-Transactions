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

package fedora.server.oai;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fedora.common.Constants;

import fedora.oai.BadResumptionTokenException;
import fedora.oai.CannotDisseminateFormatException;
import fedora.oai.DateGranularitySupport;
import fedora.oai.DeletedRecordSupport;
import fedora.oai.Header;
import fedora.oai.IDDoesNotExistException;
import fedora.oai.NoMetadataFormatsException;
import fedora.oai.NoRecordsMatchException;
import fedora.oai.NoSetHierarchyException;
import fedora.oai.OAIProvider;
import fedora.oai.Record;
import fedora.oai.RepositoryException;
import fedora.oai.SimpleHeader;
import fedora.oai.SimpleMetadataFormat;
import fedora.oai.SimpleRecord;
import fedora.oai.SimpleResumptionToken;
import fedora.oai.SimpleSetInfo;

import fedora.server.errors.ServerException;
import fedora.server.errors.UnknownSessionTokenException;
import fedora.server.search.Condition;
import fedora.server.search.FieldSearch;
import fedora.server.search.FieldSearchQuery;
import fedora.server.search.FieldSearchResult;
import fedora.server.search.ObjectFields;
import fedora.server.utilities.DCFields;
import fedora.server.utilities.StreamUtility;

/**
 * Simple FieldSearch-based OAI provider.
 * 
 * @author Chris Wilper
 */
public class FedoraOAIProvider
        implements Constants, OAIProvider {

    private final String m_repositoryName;

    private final String m_repositoryDomainName;

    private final String m_localname;

    private final String m_relpath;

    private final Set m_adminEmails;

    private final Set<String> m_descriptions;

    private final List<SimpleSetInfo> m_setInfos;

    private final long m_maxSets;

    private final long m_maxRecords;

    private final long m_maxHeaders;

    private final FieldSearch m_fieldSearch;

    private final Set<SimpleMetadataFormat> m_formats;

    private static Set s_emptySet = new HashSet();

    private static String[] s_headerFields =
            new String[] {"pid", "dcmDate"};

    private static String[] s_headerAndDCFields =
            new String[] {"pid", "dcmDate", "title", "creator",
                    "subject", "description", "publisher", "contributor",
                    "date", "type", "format", "identifier", "source",
                    "language", "relation", "coverage", "rights"};

    public FedoraOAIProvider(String repositoryName,
                             String repositoryDomainName,
                             String localname,
                             String relpath,
                             Set adminEmails,
                             Set friendBaseURLs,
                             String namespaceID,
                             long maxSets,
                             long maxRecords,
                             long maxHeaders,
                             FieldSearch fieldSearch) {
        m_repositoryName = repositoryName;
        m_repositoryDomainName = repositoryDomainName;
        m_localname = localname;
        m_relpath = relpath;
        m_adminEmails = adminEmails;
        m_maxSets = maxSets;
        m_maxRecords = maxRecords;
        m_maxHeaders = maxHeaders;
        m_fieldSearch = fieldSearch;
        m_descriptions = new HashSet<String>();
        StringBuffer buf = new StringBuffer();
        buf.append("      <oai-identifier xmlns=\"" + OAI_IDENTIFIER.uri
                + "\"\n");
        buf.append("          xmlns:xsi=\"" + XSI.uri + "\"\n");
        buf.append("          xsi:schemaLocation=\"" + OAI_IDENTIFIER.uri
                + "\n");
        buf.append("          " + OAI_IDENTIFIER2_0.xsdLocation + "\">\n");
        buf.append("        <scheme>oai</scheme>\n");
        buf.append("        <repositoryIdentifier>" + m_repositoryDomainName
                + "</repositoryIdentifier>\n");
        buf.append("        <delimiter>:</delimiter>\n");
        buf.append("        <sampleIdentifier>oai:" + m_repositoryDomainName
                + ":" + namespaceID + ":7654</sampleIdentifier>\n");
        buf.append("      </oai-identifier>");
        m_descriptions.add(buf.toString());
        if (friendBaseURLs != null && friendBaseURLs.size() > 0) {
            buf = new StringBuffer();
            buf.append("      <friends xmlns=\"" + OAI_FRIENDS.uri + "\"\n");
            buf.append("          xmlns:xsi=\"" + XSI.uri + "\"\n");
            buf.append("          xsi:schemaLocation=\"" + OAI_FRIENDS.uri
                    + "\n");
            buf.append("          " + OAI_FRIENDS2_0.xsdLocation + "\">\n");
            Iterator iter = friendBaseURLs.iterator();
            while (iter.hasNext()) {
                buf.append("        <baseURL>" + (String) iter.next()
                        + "</baseURL>\n");
            }
            buf.append("      </friends>");
            m_descriptions.add(buf.toString());
        }
        m_formats = new HashSet<SimpleMetadataFormat>();
        m_formats.add(new SimpleMetadataFormat("oai_dc",
                                               OAI_DC2_0.xsdLocation,
                                               OAI_DC.uri));
        m_setInfos = new ArrayList<SimpleSetInfo>();
    }

    public String getRepositoryName() {
        return m_repositoryName;
    }

    public String getBaseURL(String protocol, String port) {
        return protocol + "://" + m_localname + ":" + port + m_relpath;
    }

    public String getProtocolVersion() {
        return "2.0";
    }

    public Date getEarliestDatestamp() {
        return new Date();
    }

    public DeletedRecordSupport getDeletedRecordSupport() {
        return DeletedRecordSupport.NO;
    }

    public DateGranularitySupport getDateGranularitySupport() {
        return DateGranularitySupport.SECONDS;
    }

    public Set getAdminEmails() {
        return m_adminEmails;
    }

    public Set getSupportedCompressionEncodings() {
        return s_emptySet;
    }

    public Set getDescriptions() {
        return m_descriptions;
    }

    public Record getRecord(String identifier, String metadataPrefix)
            throws CannotDisseminateFormatException, IDDoesNotExistException,
            RepositoryException {
        if (!metadataPrefix.equals("oai_dc")) {
            throw new CannotDisseminateFormatException("Repository does not provide that format in OAI-PMH responses.");
        }
        String pid = getPID(identifier);
        List l = null;
        try {
            //FIXME: use maxResults from... config instead of hardcoding 100?
            l =
                    m_fieldSearch
                            .findObjects(s_headerAndDCFields,
                                         100,
                                         new FieldSearchQuery(Condition
                                                 .getConditions("pid='"
                                                         + pid
                                                         + "' dcmDate>'2000-01-01'")))
                            .objectFieldsList();
        } catch (ServerException se) {
            throw new RepositoryException(se.getClass().getName() + ": "
                    + se.getMessage());
        }
        if (l.size() > 0) {
            ObjectFields f = (ObjectFields) l.get(0);
            return new SimpleRecord(getHeader(f), getDCXML(f), s_emptySet);
        } else {
            // see if it exists
            try {
                l =
                        m_fieldSearch
                                .findObjects(new String[] {"pid"},
                                             1,
                                             new FieldSearchQuery(Condition
                                                     .getConditions("pid='"
                                                             + pid + "'")))
                                .objectFieldsList();
            } catch (ServerException se) {
                throw new RepositoryException(se.getClass().getName() + ": "
                        + se.getMessage());
            }
            if (l.size() == 0) {
                throw new IDDoesNotExistException("The provided id does not match any item in the repository.");
            } else {
                throw new CannotDisseminateFormatException("The item doesn't even have dc_oai metadata.");
            }
        }
    }

    public List getRecords(Date from,
                           Date until,
                           String metadataPrefix,
                           String set) throws CannotDisseminateFormatException,
            NoRecordsMatchException, NoSetHierarchyException,
            RepositoryException {
        if (!metadataPrefix.equals("oai_dc")) {
            throw new CannotDisseminateFormatException("Repository does not provide that format in OAI-PMH responses.");
        }
        List l = null;
        FieldSearchResult fsr;
        try {
            fsr =
                    m_fieldSearch
                            .findObjects(s_headerAndDCFields,
                                         (int) getMaxRecords(),
                                         new FieldSearchQuery(Condition
                                                 .getConditions("dcmDate>'2000-01-01'"
                                                         + getDatePart(from,
                                                                       until))));
            l = fsr.objectFieldsList();
        } catch (ServerException se) {
            throw new RepositoryException(se.getClass().getName() + ": "
                    + se.getMessage());
        }
        if (l.size() == 0) {
            throw new NoRecordsMatchException("No records match the given criteria.");
        }
        ArrayList<Object> ret = new ArrayList<Object>();
        for (int i = 0; i < l.size(); i++) {
            ObjectFields f = (ObjectFields) l.get(i);
            ret.add(new SimpleRecord(getHeader(f), getDCXML(f), s_emptySet));
        }
        if (fsr.getToken() != null) {
            // add resumptionToken stuff
            ret.add(new SimpleResumptionToken(fsr.getToken(), fsr
                    .getExpirationDate(), fsr.getCompleteListSize(), fsr
                    .getCursor()));
        }
        return ret;
    }

    private Header getHeader(ObjectFields f) {
        String identifier = "oai:" + m_repositoryDomainName + ":" + f.getPid();
        Date datestamp = f.getDCMDate();
        HashSet<String> setSpecs = new HashSet<String>();
        return new SimpleHeader(identifier, datestamp, setSpecs, true);
    }

    private String getDCXML(DCFields dc) {
        StringBuffer out = new StringBuffer();
        out.append("        <oai_dc:dc\n");
        out.append("            xmlns:oai_dc=\"" + OAI_DC.uri + "\"\n");
        out.append("            xmlns:dc=\"" + DC.uri + "\"\n");
        out.append("            xmlns:xsi=\"" + XSI.uri + "\"\n");
        out.append("            xsi:schemaLocation=\"" + OAI_DC.uri + "\n");
        out.append("            " + OAI_DC2_0.xsdLocation + "\">\n");
        for (int i = 0; i < dc.titles().size(); i++) {
            out.append("          <dc:title>");
            out.append(StreamUtility.enc((String) dc.titles().get(i)));
            out.append("          </dc:title>\n");
        }
        for (int i = 0; i < dc.creators().size(); i++) {
            out.append("          <dc:creator>");
            out.append(StreamUtility.enc((String) dc.creators().get(i)));
            out.append("          </dc:creator>\n");
        }
        for (int i = 0; i < dc.subjects().size(); i++) {
            out.append("          <dc:subject>");
            out.append(StreamUtility.enc((String) dc.subjects().get(i)));
            out.append("          </dc:subject>\n");
        }
        for (int i = 0; i < dc.descriptions().size(); i++) {
            out.append("          <dc:description>");
            out.append(StreamUtility.enc((String) dc.descriptions().get(i)));
            out.append("          </dc:description>\n");
        }
        for (int i = 0; i < dc.publishers().size(); i++) {
            out.append("          <dc:publisher>");
            out.append(StreamUtility.enc((String) dc.publishers().get(i)));
            out.append("          </dc:publisher>\n");
        }
        for (int i = 0; i < dc.contributors().size(); i++) {
            out.append("          <dc:contributor>");
            out.append(StreamUtility.enc((String) dc.contributors().get(i)));
            out.append("          </dc:contributor>\n");
        }
        for (int i = 0; i < dc.dates().size(); i++) {
            String dateString = StreamUtility.enc((String) dc.dates().get(i));
            out.append("          <dc:date>");
            out.append(dateString);
            out.append("          </dc:date>\n");
        }
        for (int i = 0; i < dc.types().size(); i++) {
            out.append("          <dc:type>");
            out.append(StreamUtility.enc((String) dc.types().get(i)));
            out.append("          </dc:type>\n");
        }
        for (int i = 0; i < dc.formats().size(); i++) {
            out.append("          <dc:format>");
            out.append(StreamUtility.enc((String) dc.formats().get(i)));
            out.append("          </dc:format>\n");
        }
        for (int i = 0; i < dc.identifiers().size(); i++) {
            out.append("          <dc:identifier>");
            out.append(StreamUtility.enc((String) dc.identifiers().get(i)));
            out.append("          </dc:identifier>\n");
        }
        for (int i = 0; i < dc.sources().size(); i++) {
            out.append("          <dc:source>");
            out.append(StreamUtility.enc((String) dc.sources().get(i)));
            out.append("          </dc:source>\n");
        }
        for (int i = 0; i < dc.languages().size(); i++) {
            out.append("          <dc:language>");
            out.append(StreamUtility.enc((String) dc.languages().get(i)));
            out.append("          </dc:language>\n");
        }
        for (int i = 0; i < dc.relations().size(); i++) {
            out.append("          <dc:relation>");
            out.append(StreamUtility.enc((String) dc.relations().get(i)));
            out.append("          </dc:relation>\n");
        }
        for (int i = 0; i < dc.coverages().size(); i++) {
            out.append("          <dc:coverage>");
            out.append(StreamUtility.enc((String) dc.coverages().get(i)));
            out.append("          </dc:coverage>\n");
        }
        for (int i = 0; i < dc.rights().size(); i++) {
            out.append("          <dc:rights>");
            out.append(StreamUtility.enc((String) dc.rights().get(i)));
            out.append("          </dc:rights>\n");
        }
        out.append("        </oai_dc:dc>");
        return out.toString();
    }

    public List getRecords(String resumptionToken)
            throws CannotDisseminateFormatException, NoRecordsMatchException,
            NoSetHierarchyException, BadResumptionTokenException,
            RepositoryException {
        // this is the exact same as the other getRecords, except for the FieldSearch call,
        // and the fact that we re-throw UnknownSessionTokenException
        // as a BadResumptionTokenException
        List l = null;
        FieldSearchResult fsr;
        try {
            fsr = m_fieldSearch.resumeFindObjects(resumptionToken);
            l = fsr.objectFieldsList();
        } catch (UnknownSessionTokenException uste) {
            throw new BadResumptionTokenException("Not a known resumptionToken.");
        } catch (ServerException se) {
            throw new RepositoryException(se.getClass().getName() + ": "
                    + se.getMessage());
        }
        if (l.size() == 0) {
            throw new NoRecordsMatchException("No records match the given criteria.");
        }
        ArrayList<Object> ret = new ArrayList<Object>();
        for (int i = 0; i < l.size(); i++) {
            ObjectFields f = (ObjectFields) l.get(i);
            ret.add(new SimpleRecord(getHeader(f), getDCXML(f), s_emptySet));
        }
        if (fsr.getToken() != null) {
            ret.add(new SimpleResumptionToken(fsr.getToken(), fsr
                    .getExpirationDate(), fsr.getCompleteListSize(), fsr
                    .getCursor()));
        }
        return ret;
    }

    private String getDatePart(Date from, Date until) {
        if (from == null && until == null) {
            return "";
        }
        StringBuffer out = new StringBuffer();
        // Note OAI only support ISO8601 dates to the seconds
        // and Fedora stores dates down to the millisecond level.
        // This should not matter since OAI requests specify
        // date ranges (from-until), and as long as the requests
        // are always in the same units, subsequent requests can pick 
        // up at the last end-point in time (in seconds) without
        // concern for millisecond granularity.
        SimpleDateFormat formatter =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        if (from != null) {
            out.append(" dcmDate>='");
            out.append(formatter.format(from));
            out.append("'");
        }
        if (until != null) {
            out.append(" dcmDate<='");
            out.append(formatter.format(until));
            out.append("'");
        }
        return out.toString();
    }

    public List getHeaders(Date from,
                           Date until,
                           String metadataPrefix,
                           String set) throws CannotDisseminateFormatException,
            NoRecordsMatchException, NoSetHierarchyException,
            RepositoryException {
        if (!metadataPrefix.equals("oai_dc")) {
            throw new CannotDisseminateFormatException("Repository does not provide that format in OAI-PMH responses.");
        }
        List l = null;
        FieldSearchResult fsr;
        try {
            fsr =
                    m_fieldSearch
                            .findObjects(s_headerFields,
                                         (int) getMaxHeaders(),
                                         new FieldSearchQuery(Condition
                                                 .getConditions("dcmDate>'2000-01-01'"
                                                         + getDatePart(from,
                                                                       until))));
            l = fsr.objectFieldsList();
        } catch (ServerException se) {
            throw new RepositoryException(se.getClass().getName() + ": "
                    + se.getMessage());
        }
        if (l.size() == 0) {
            throw new NoRecordsMatchException("No records match the given criteria.");
        }
        ArrayList<Object> ret = new ArrayList<Object>();
        for (int i = 0; i < l.size(); i++) {
            ObjectFields f = (ObjectFields) l.get(i);
            String identifier =
                    "oai:" + m_repositoryDomainName + ":" + f.getPid();
            Date datestamp = f.getDCMDate();
            HashSet<String> setSpecs = new HashSet<String>();

            ret.add(new SimpleHeader(identifier, datestamp, setSpecs, true));
        }
        if (fsr.getToken() != null) {
            ret.add(new SimpleResumptionToken(fsr.getToken(), fsr
                    .getExpirationDate(), fsr.getCompleteListSize(), fsr
                    .getCursor()));
        }
        return ret;
    }

    public List getHeaders(String resumptionToken)
            throws CannotDisseminateFormatException, NoRecordsMatchException,
            NoSetHierarchyException, BadResumptionTokenException,
            RepositoryException {
        // this is the exact same as the other getHeaders, except for the FieldSearch call,
        // and the fact that we re-throw UnknownSessionTokenException
        // as a BadResumptionTokenException
        List l = null;
        FieldSearchResult fsr;
        try {
            fsr = m_fieldSearch.resumeFindObjects(resumptionToken);
            l = fsr.objectFieldsList();
        } catch (UnknownSessionTokenException uste) {
            throw new BadResumptionTokenException("Not a known resumptionToken.");
        } catch (ServerException se) {
            throw new RepositoryException(se.getClass().getName() + ": "
                    + se.getMessage());
        }
        if (l.size() == 0) {
            throw new NoRecordsMatchException("No records match the given criteria.");
        }
        ArrayList<Object> ret = new ArrayList<Object>();
        for (int i = 0; i < l.size(); i++) {
            ObjectFields f = (ObjectFields) l.get(i);
            String identifier =
                    "oai:" + m_repositoryDomainName + ":" + f.getPid();
            Date datestamp = f.getDCMDate();
            HashSet<String> setSpecs = new HashSet<String>();
            ret.add(new SimpleHeader(identifier, datestamp, setSpecs, true));
        }
        if (fsr.getToken() != null) {
            ret.add(new SimpleResumptionToken(fsr.getToken(), fsr
                    .getExpirationDate(), fsr.getCompleteListSize(), fsr
                    .getCursor()));
        }
        return ret;
    }

    public List getSets() throws NoSetHierarchyException, RepositoryException {
        return m_setInfos;
    }

    public List getSets(String resumptionToken)
            throws BadResumptionTokenException, NoSetHierarchyException,
            RepositoryException {
        // no resumptionTokens are currently used on getSets since it's always so small
        throw new BadResumptionTokenException("Not a known resumptionToken.");
    }

    private String getPID(String id) throws IDDoesNotExistException {
        if (!id.startsWith("oai:" + m_repositoryDomainName + ":")) {
            throw new IDDoesNotExistException("For this repository, all identifiers in OAI requests should begin with oai:"
                    + m_repositoryDomainName + ":");
        }
        if (id.indexOf("'") != -1) {
            throw new IDDoesNotExistException("For this repository, no identifiers contain the apostrophe character.");
        }
        return id.substring(4 + m_repositoryDomainName.length() + 1);
    }

    public Set getMetadataFormats(String id) throws NoMetadataFormatsException,
            IDDoesNotExistException, RepositoryException {
        if (id == null) {
            return m_formats;
        }
        String pid = getPID(id);
        List l = null;
        try {
            l =
                    m_fieldSearch
                            .findObjects(new String[] {"pid"},
                                         1,
                                         new FieldSearchQuery(Condition
                                                 .getConditions("pid='"
                                                         + pid
                                                         + "' dcmDate>'2000-01-01'")))
                            .objectFieldsList();
        } catch (ServerException se) {
            throw new RepositoryException(se.getClass().getName() + ": "
                    + se.getMessage());
        }
        if (l.size() > 0) {
            return m_formats;
        }
        try {
            l =
                    m_fieldSearch.findObjects(new String[] {"pid"},
                                              1,
                                              new FieldSearchQuery(Condition
                                                      .getConditions("pid='"
                                                              + pid + "'")))
                            .objectFieldsList();
        } catch (ServerException se) {
            throw new RepositoryException(se.getClass().getName() + ": "
                    + se.getMessage());
        }
        if (l.size() > 0) {
            throw new NoMetadataFormatsException("The item doesn't even have dc_oai metadata.");
        } else {
            throw new IDDoesNotExistException("The provided id does not match any item in the repository.");
        }
    }

    public long getMaxSets() throws RepositoryException {
        return m_maxSets;
    }

    public long getMaxRecords() throws RepositoryException {
        return m_maxRecords;
    }

    public long getMaxHeaders() throws RepositoryException {
        return m_maxHeaders;
    }

}