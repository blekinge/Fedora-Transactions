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

package fedora.server;

import java.util.Date;
import java.util.Iterator;

/**
 * A holder of context name-value pairs.
 * 
 * @author Chris Wilper
 */
public interface Context {

    public MultiValueMap getEnvironmentAttributes();

    public Iterator environmentAttributes();

    public int nEnvironmentValues(String name);

    public String getEnvironmentValue(String name);

    public String[] getEnvironmentValues(String name);

    public Iterator subjectAttributes();

    public int nSubjectValues(String name);

    public String getSubjectValue(String name);

    public String[] getSubjectValues(String name);

    public Iterator actionAttributes();

    public int nActionValues(String name);

    public String getActionValue(String name);

    public String[] getActionValues(String name);

    public Iterator resourceAttributes();

    public int nResourceValues(String name);

    public String getResourceValue(String name);

    public String[] getResourceValues(String name);

    public void setActionAttributes(MultiValueMap actionAttributes);

    public void setResourceAttributes(MultiValueMap resourceAttributes);

    public String getPassword();

    public String toString();

    public Date now();

    public boolean getNoOp();

    public static final String FEDORA_AUX_SUBJECT_ATTRIBUTES =
            "FEDORA_AUX_SUBJECT_ATTRIBUTES";

    //public boolean useCachedObject();

}
