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

package fedora.server.security;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.ParsingException;
import com.sun.xacml.Policy;
import com.sun.xacml.PolicySet;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.combine.PolicyCombiningAlgorithm;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.ctx.Status;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderResult;

import org.apache.log4j.Logger;

import org.w3c.dom.Element;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import fedora.common.Constants;

import fedora.server.ReadOnlyContext;
import fedora.server.Server;
import fedora.server.errors.GeneralException;
import fedora.server.errors.ObjectNotInLowlevelStorageException;
import fedora.server.storage.DOManager;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.Datastream;

/**
 * @author Bill Niebel
 * @see http://sourceforge.net/mailarchive/message.php?msg_id=6068981
 *      to understand why this class is needed (why configuring the xacml pdp 
 *      with all of the multiplexed policy finders just won't work)
 */
public class PolicyFinderModule
        extends com.sun.xacml.finder.PolicyFinderModule {

    /** Logger for this class. */
    private static final Logger LOG =
            Logger.getLogger(PolicyFinderModule.class.getName());

    private String combiningAlgorithm = null;

    private List repositoryPolicies = null;

    private File schemaFile = null;

    private final DOManager doManager;

    public PolicyFinderModule(String combiningAlgorithm,
                              String repositoryPolicyDirectoryPath,
                              String repositoryBackendPolicyDirectoryPath,
                              String repositoryPolicyGuiToolDirectoryPath,
                              DOManager doManager,
                              boolean validateRepositoryPolicies,
                              boolean validateObjectPoliciesFromDatastream,
                              String policySchemaPath)
            throws GeneralException {
        this.combiningAlgorithm = combiningAlgorithm;

        List filelist = new ArrayList();
        log("before building regular file list");
        buildRepositoryPolicyFileList(new File(repositoryPolicyDirectoryPath),
                                      filelist);
        log("after building regular file list");
        log("before building (backend generated) file list "
                + repositoryBackendPolicyDirectoryPath);
        buildRepositoryPolicyFileList(new File(repositoryBackendPolicyDirectoryPath),
                                      filelist);
        log("after building (backend generated) file list");
        /*
         * add back > 2.1b vvvvv log("before building (pgt generated) file list " +
         * repositoryPolicyGuiToolDirectoryPath);
         * buildRepositoryPolicyFileList(new
         * File(repositoryPolicyGuiToolDirectoryPath), filelist); log("after
         * building (pgt generated) file list"); add back > 2.1b ^^^^^
         */
        log("before getting repo policies");

        //String schemaName = System.getProperty(POLICY_SCHEMA_PROPERTY);
        log("XXpolicySchemaPath=" + policySchemaPath);
        if (policySchemaPath == null) {
            this.validateRepositoryPolicies = false;
            this.validateObjectPoliciesFromDatastream = false;
        } else {
            this.validateRepositoryPolicies = validateRepositoryPolicies;
            this.validateObjectPoliciesFromDatastream =
                    validateObjectPoliciesFromDatastream;
            //if (this.validateRepositoryPolicies || this.validateObjectPoliciesFromFile || this.validateObjectPoliciesFromDatastream) {			
            if (this.validateRepositoryPolicies
                    || this.validateObjectPoliciesFromDatastream) {
                schemaFile = new File(policySchemaPath);
                if (!schemaFile.canRead()) {
                    this.validateRepositoryPolicies = false;
                    //this.validateObjectPoliciesFromFile = false;
                    this.validateObjectPoliciesFromDatastream = false;
                }
            }
        }

        repositoryPolicies = loadRepositoryPolicies(filelist);

        this.doManager = doManager;

    }

    public static final String POLICY_SCHEMA_PROPERTY =
            "com.sun.xacml.PolicySchema";

    public static final String JAXP_SCHEMA_LANGUAGE =
            "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    public static final String W3C_XML_SCHEMA = Constants.XML_XSD.uri;

    public static final String JAXP_SCHEMA_SOURCE =
            "http://java.sun.com/xml/jaxp/properties/schemaSource";

    private final DocumentBuilder getDocumentBuilder(ErrorHandler handler,
                                                     boolean validate)
            throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);

        DocumentBuilder builder = null;

        // as of 1.2, we always are namespace aware
        factory.setNamespaceAware(true);

        log("ZZschemaFile=" + schemaFile);
        log("validate=" + validate);
        if (schemaFile == null) {
            factory.setValidating(false);
            builder = factory.newDocumentBuilder();
        } else {
            factory.setValidating(validate);
            log("VALIDATION ON? = " + validate);
            factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            log("schemaFile=" + schemaFile.isFile() + schemaFile.canRead());
            factory.setAttribute(JAXP_SCHEMA_SOURCE, schemaFile);
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(handler);
        }
        return builder;
    }

    private static final AbstractPolicy getAbstractPolicyFromDOM(Element rootElement,
                                                                 String errorLabel)
            throws GeneralException {
        AbstractPolicy abstractPolicy = null;
        String name = rootElement.getTagName();
        try {
            if (name.equals("Policy")) {
                abstractPolicy = Policy.getInstance(rootElement);
            } else if (name.equals("PolicySet")) {
                abstractPolicy = PolicySet.getInstance(rootElement);
            } else {
                String msg =
                        "bad root node for repo-wide policy in " + errorLabel;
                LOG.info(msg);
                throw new GeneralException(msg);
            }
        } catch (ParsingException e) {
            String msg = "couldn't parse repo-wide policy in " + errorLabel;
            LOG.error(msg, e);
            throw new GeneralException(msg);
        }
        return abstractPolicy;
    }

    private int loadErrors = 0;

    public final int getLoadErrors() {
        return loadErrors;
    }

    private final int logNgo(int errors, String msg, String detail) {
        log(msg);
        if (detail != null) {
            log("\t" + detail);
        }
        return errors + 1;
    }

    private final Vector loadRepositoryPolicies(List filelist)
            throws GeneralException {
        Vector repositoryPolicies = new Vector();
        Iterator it = filelist.iterator();
        int methodErrors = 0;
        while (it.hasNext()) {
            String filepath = (String) it.next();
            log("filepath=" + filepath);

            File file = new File(filepath);
            if (!file.exists()) {
                methodErrors =
                        logNgo(methodErrors,
                               "error loading repository-wide policy at "
                                       + filepath,
                               "file not found");
            }
            Element rootElement = null;
            if (methodErrors == 0) {
                try {
                    log("GETTING A REPOSITORY POLICY = " + filepath);
                    log("schemaFile = " + schemaFile);
                    DocumentBuilder builder =
                            getDocumentBuilder(new SchemaErrorHandler(),
                                               validateRepositoryPolicies);
                    rootElement = builder.parse(file).getDocumentElement();
                } catch (ParserConfigurationException e) {
                    log("parser failure at " + filepath);
                    methodErrors =
                            logNgo(methodErrors,
                                   "error loading repository-wide policy at "
                                           + filepath,
                                   e.getMessage());
                } catch (SAXException e) {
                    log("policy breaks schema at " + filepath);
                    methodErrors =
                            logNgo(methodErrors,
                                   "error loading repository-wide policy at "
                                           + filepath,
                                   e.getMessage());
                } catch (IOException e) {
                    log("policy can't be read at " + filepath);
                    methodErrors =
                            logNgo(methodErrors,
                                   "error loading repository-wide policy at "
                                           + filepath,
                                   e.getMessage());
                }
            }
            log("methodErrors=" + methodErrors);
            if (methodErrors == 0) {
                AbstractPolicy abstractPolicy;
                try {
                    log("before getting abstract policy from dom, at "
                            + filepath);
                    abstractPolicy =
                            getAbstractPolicyFromDOM(rootElement, filepath);
                    log("after getting abstract policy from dom");
                    repositoryPolicies.add(abstractPolicy);
                } catch (GeneralException e) {
                    methodErrors =
                            logNgo(methodErrors,
                                   "error loading repository-wide policy at "
                                           + filepath,
                                   e.getMessage());
                    e.printStackTrace();
                } catch (Throwable other) {
                    log("other exception is" + other.getMessage() + " " + other);
                    other.printStackTrace();
                }
            }
        }
        loadErrors += methodErrors;
        log("loadErrors=" + loadErrors);
        if (loadErrors != 0) {
            repositoryPolicies.clear();
            throw new GeneralException("problems loading repo-wide policies");
        }
        return repositoryPolicies;
    }

    private final AbstractPolicy getObjectPolicyFromObject(String pid)
            throws Throwable {
        AbstractPolicy objectPolicyFromObject = null;
        DOReader reader = null;
        try {
            reader =
                    doManager.getReader(Server.USE_DEFINITIVE_STORE,
                                        ReadOnlyContext.EMPTY,
                                        pid);
        } catch (ObjectNotInLowlevelStorageException ee) {
            // nonexistent object is not an error (action is to create the object)			
        } catch (Throwable e) {
            LOG.error("error reading policy from xml for object " + pid, e);
            throw e;
        }
        if (reader != null) {
            Datastream policyDatastream = reader.GetDatastream("POLICY", null);
            if (policyDatastream != null) {
                try {
                    InputStream instream = policyDatastream.getContentStream();
                    log("GETTING A OBJECT POLICY FROM DATASTREAM");
                    DocumentBuilder builder =
                            getDocumentBuilder(null,
                                               validateObjectPoliciesFromDatastream);
                    Element rootElement =
                            builder.parse(instream).getDocumentElement();
                    objectPolicyFromObject =
                            getAbstractPolicyFromDOM(rootElement,
                                                     "object xml for " + pid);
                } catch (ParserConfigurationException e) {
                    log("parser failure at " + pid);
                    throw e;
                } catch (SAXException e) {
                    log("policy breaks schema at " + pid);
                    throw e;
                } catch (IOException e) {
                    log("policy can't be read at " + pid);
                    throw e;
                } catch (Throwable e) {
                    LOG
                            .error("error reading policy from xml for object "
                                    + pid, e);
                    throw e;
                }
            }
        }
        return objectPolicyFromObject;
    }

    private static final void buildRepositoryPolicyFileList(File directory,
                                                            List filelist) {
        String[] files = directory.list();
        for (String element : files) {
            File file =
                    new File(directory.getPath() + File.separator + element);
            if (file.isDirectory()) {
                buildRepositoryPolicyFileList(file, filelist);
            } else {
                String temp = file.getAbsolutePath();
                if (temp.endsWith(".xml")) {
                    filelist.add(temp);
                }
            }
        }
    }

    private boolean validateRepositoryPolicies = false;

    private boolean validateObjectPoliciesFromDatastream = false;

    /**
     * pass along an init() call to the various multiplexed PolicyFinderModules
     */
    @Override
    public void init(PolicyFinder finder) {
    }

    /**
     * the set of multiplexed PolicyFinderModules can support the request if
     * -any- of the various PolicyFinderModules individually can
     */
    @Override
    public boolean isRequestSupported() {
        return true;
    }

    private static final List ERROR_CODE_LIST = new ArrayList(1);
    static {
        ERROR_CODE_LIST.add(Status.STATUS_PROCESSING_ERROR);
    }

    /* copy of code in AttributeFinderModule; consider refactoring */
    protected final Object getAttributeFromEvaluationResult(EvaluationResult attribute /*
     * URI
     * type,
     * URI
     * id,
     * URI
     * category,
     * EvaluationCtx
     * context
     */) {
        if (attribute.indeterminate()) {
            return null;
        }

        if (attribute.getStatus() != null
                && !Status.STATUS_OK.equals(attribute.getStatus())) {
            return null;
        } // (resourceAttribute.getStatus() == null) == everything is ok

        AttributeValue attributeValue = attribute.getAttributeValue();
        if (!(attributeValue instanceof BagAttribute)) {
            return null;
        }

        BagAttribute bag = (BagAttribute) attributeValue;
        if (1 != bag.size()) {
            return null;
        }

        Iterator it = bag.iterator();
        Object element = it.next();

        if (element == null) {
            return null;
        }

        if (it.hasNext()) {
            log(element.toString());
            while (it.hasNext()) {
                log(it.next().toString());
            }
            return null;
        }

        return element;
    }

    private final String getPid(EvaluationCtx context) {
        URI resourceIdType = null;
        URI resourceIdId = null;
        try {
            resourceIdType = new URI(StringAttribute.identifier);
            resourceIdId = new URI(Constants.OBJECT.PID.uri);
        } catch (URISyntaxException e) {
            LOG.error("Bad URI syntax", e);
        }
        EvaluationResult attribute =
                context
                        .getResourceAttribute(resourceIdType,
                                              resourceIdId,
                                              null);
        Object element = getAttributeFromEvaluationResult(attribute);
        if (element == null) {
            log("PolicyFinderModule:getPid" + " exit on "
                    + "can't get contextId on request callback");
            return null;
        }

        if (!(element instanceof StringAttribute)) {
            log("PolicyFinderModule:getPid" + " exit on "
                    + "couldn't get contextId from xacml request "
                    + "non-string returned");
            return null;
        }

        String pid = ((StringAttribute) element).getValue();

        if (pid == null) {
            log("PolicyFinderModule:getPid" + " exit on " + "null contextId");
            return null;
        }

        return pid;
    }

    /*
     * return a deny-biased policy set which includes all repository-wide and
     * any object-specific policies
     */
    @Override
    public PolicyFinderResult findPolicy(EvaluationCtx context) {

        long findStartTime = System.currentTimeMillis();

        PolicyFinderResult policyFinderResult = null;
        try {
            List policies = new Vector(repositoryPolicies);
            String pid = getPid(context);
            if (pid != null && !"".equals(pid)) {
                AbstractPolicy objectPolicyFromObject =
                        getObjectPolicyFromObject(pid);
                if (objectPolicyFromObject != null) {
                    policies.add(objectPolicyFromObject);
                }
            }
            PolicyCombiningAlgorithm policyCombiningAlgorithm =
                    (PolicyCombiningAlgorithm) Class
                            .forName(combiningAlgorithm).newInstance();
            //new OrderedDenyOverridesPolicyAlg();
            PolicySet policySet =
                    new PolicySet(new URI(""),
                                  policyCombiningAlgorithm,
                                  "",
                                  null /*
                                   * no general target beyond those of
                                   * multiplexed individual policies
                                   */,
                                  policies);
            policyFinderResult = new PolicyFinderResult(policySet);
        } catch (Throwable e) {
            e.printStackTrace();
            policyFinderResult =
                    new PolicyFinderResult(new Status(ERROR_CODE_LIST, e
                            .getMessage()));
        } finally {
            long dur = System.currentTimeMillis() - findStartTime;
            LOG.debug("Finding the policy for this evaluation context took "
                    + dur + "ms.");
        }
        return policyFinderResult;
    }

    private static final boolean log = false;

    private final void log(String msg) {
        if (log) {
            LOG.debug(msg);
        }
    }

}
