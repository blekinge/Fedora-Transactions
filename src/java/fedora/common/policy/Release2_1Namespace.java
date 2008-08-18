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

package fedora.common.policy;

import java.util.Vector;

import fedora.common.Constants;

public class Release2_1Namespace
        extends XacmlNamespace {

    private Release2_1Namespace(XacmlNamespace parent, String localName) {
        super(parent, localName);
    }

    public static Release2_1Namespace onlyInstance =
            new Release2_1Namespace(FedoraAsProjectNamespace.getInstance(),
                                    "2.1");
    static {
        onlyInstance.addNamespace(SubjectNamespace.getInstance());
        onlyInstance.addNamespace(ActionNamespace.getInstance());
        onlyInstance.addNamespace(ResourceNamespace.getInstance());
        onlyInstance.addNamespace(EnvironmentNamespace.getInstance());
    }

    public static final Release2_1Namespace getInstance() {
        return onlyInstance;
    }

    public static final void main(String[] args) {
        Release2_1Namespace instance = Release2_1Namespace.getInstance();
        Vector list = new Vector();
        instance.flatRep(list);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof XacmlName) {
                if (!((XacmlName) list.get(i)).toString()
                        .startsWith(Constants.ACTION.CONTEXT_ID.uri)) {
                    System.out.println(list.get(i));
                }

            }
        }
    }

}
