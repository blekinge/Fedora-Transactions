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
package fedora.utilities;

import javax.xml.transform.TransformerFactory;

import net.sf.saxon.FeatureKeys;


/**
 * 
 * @author Edwin Shin
 * @version $Id$
 */
public class XmlTransformUtility {
    
    /**
     * Convenience method to get a new instance of a TransformerFactory.
     * If the {@link #TransformerFactory} is an instance of 
     * net.sf.saxon.TransformerFactoryImpl, the attribute 
     * {@link #FeatureKeys.VERSION_WARNING} will be set to false in order to 
     * suppress the warning about using an XSLT1 stylesheet with an XSLT2
     * processor.
     * 
     * @return a new instance of TransformerFactory
     */
    public static TransformerFactory getTransformerFactory() {
        TransformerFactory factory = TransformerFactory.newInstance();
        if (factory.getClass().getName().equals("net.sf.saxon.TransformerFactoryImpl")) {
            factory.setAttribute(FeatureKeys.VERSION_WARNING, Boolean.FALSE);
        }
        return factory;
    }
}
