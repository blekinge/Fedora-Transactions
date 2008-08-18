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

/**
 * A datastream binding rule.
 * 
 * @author Sandy Payette
 */
public class DeploymentDSBindRule {

    public String bindingKeyName;

    public int minNumBindings;

    public int maxNumBindings;

    public boolean ordinality;

    public String bindingLabel;

    public String bindingInstruction;

    public String[] bindingMIMETypes;

    public DeploymentDSBindRule() {
    }

    private static final String ANY_MIME_TYPE = "*/*";

    /**
     * In human readable string, describe which mime types are allowed.
     */
    public String describeAllowedMimeTypes() {
        StringBuffer out = new StringBuffer();
        if (bindingMIMETypes == null || bindingMIMETypes.length == 0) {
            return ANY_MIME_TYPE;
        }
        for (int i = 0; i < bindingMIMETypes.length; i++) {
            String allowed = bindingMIMETypes[i];
            if (allowed == null) {
                return ANY_MIME_TYPE;
            }
            if (allowed.equals("*/*")) {
                return ANY_MIME_TYPE;
            }
            if (allowed.equals("*")) {
                return ANY_MIME_TYPE;
            }
            if (i > 0) {
                if (i < bindingMIMETypes.length - 1) {
                    out.append(", ");
                } else {
                    if (i > 1) {
                        out.append(",");
                    }
                    out.append(" or ");
                }
            }
            out.append(allowed);
        }
        return out.toString();
    }

}