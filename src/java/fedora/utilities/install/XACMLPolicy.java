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

package fedora.utilities.install;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.util.Iterator;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import fedora.common.Constants;

import fedora.utilities.XMLDocument;

public class XACMLPolicy
        extends XMLDocument {

    //private InstallOptions options;

    public XACMLPolicy(File policyFile, InstallOptions installOptions)
            throws FileNotFoundException, DocumentException {
        this(new FileInputStream(policyFile), installOptions);
    }

    public XACMLPolicy(InputStream policyFile, InstallOptions installOptions)
            throws FileNotFoundException, DocumentException {
        super(policyFile);
        //options = installOptions;
    }

    /**
     * If the Apply element returned by the following XPath
     * /Policy/Condition[@FunctionId='urn:oasis:names:tc:xacml:1.0:function:not']
     * /Apply'][@FunctionId='urn:oasis:names:tc:xacml:1.0:function:or']
     * /Apply'][@FunctionId='urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of']
     * /Apply'][@FunctionId='urn:oasis:names:tc:xacml:1.0:function:string-bag']
     * does not already have a child AttributeValue element that matches the
     * host, add a new AttributeValue element that does. This is used by the
     * Installer in the event the user chooses a host other than the default
     * localhost for the Fedora server.
     * 
     * @param host
     */
    public void addServerHost(String host) {
        String applyXPath =
                "/Policy/*[local-name()='Rule'][@Effect='Deny']/"
                        + "*[local-name()='Condition'][@FunctionId='urn:oasis:names:tc:xacml:1.0:function:not']/"
                        + "*[local-name()='Apply'][@FunctionId='urn:oasis:names:tc:xacml:1.0:function:or']/"
                        + "*[local-name()='Apply'][@FunctionId='urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of']/"
                        + "*[local-name()='Apply'][@FunctionId='urn:oasis:names:tc:xacml:1.0:function:string-bag']";
        String avXPath =
                "*[local-name()='AttributeValue'][@DataType='"
                        + Constants.RDF_XSD.STRING.uri + "']";
        Element apply = (Element) getDocument().selectSingleNode(applyXPath);
        Iterator it = apply.selectNodes(avXPath).iterator();
        boolean hasHost = false;
        Element attributeValue;
        while (it.hasNext()) {
            attributeValue = (Element) it.next();
            if (attributeValue.getText().equals(host)) {
                hasHost = true;
            }
        }

        if (!hasHost) {
            attributeValue = apply.addElement("AttributeValue");
            attributeValue.addAttribute("DataType",
                                        Constants.RDF_XSD.STRING.uri);
            attributeValue.setText(host);
        }
    }
}