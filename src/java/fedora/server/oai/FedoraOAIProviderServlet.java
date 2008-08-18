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

import java.io.File;

import fedora.common.Constants;

import fedora.oai.OAIProvider;
import fedora.oai.OAIProviderServlet;
import fedora.oai.OAIResponder;
import fedora.oai.RepositoryException;

import fedora.server.Server;

/**
 * FedoraOAIProviderServlet.
 * 
 * @author Chris Wilper
 */
public class FedoraOAIProviderServlet
        extends OAIProviderServlet {

    private static final long serialVersionUID = 1L;

    OAIResponder m_responder;

    @Override
    public OAIResponder getResponder() throws RepositoryException {
        if (m_responder == null) {
            try {
                Server server =
                        Server.getInstance(new File(Constants.FEDORA_HOME),
                                           false);
                OAIProvider provider =
                        (OAIProvider) server
                                .getModule("fedora.oai.OAIProvider");
                m_responder = new OAIResponder(provider);
            } catch (Exception e) {
                throw new RepositoryException(e.getClass().getName() + ": "
                        + e.getMessage());
            }
        }
        return m_responder;
    }

    public static void main(String[] args) throws Exception {
        new FedoraOAIProviderServlet().test(args);
    }

}
