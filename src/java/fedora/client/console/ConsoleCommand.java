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

package fedora.client.console;

import java.lang.reflect.Method;

/**
 * @author Chris Wilper
 */
public class ConsoleCommand {

    Method m_method;

    String m_methodDescription;

    String[] m_paramNames;

    String[] m_paramDescriptions;

    String m_returnDescription;

    public ConsoleCommand(Method method,
                          String methodDescription,
                          String[] paramNames,
                          String[] paramDescriptions,
                          String returnDescription) {
        m_method = method;
        m_methodDescription = methodDescription;
        m_paramNames = paramNames;
        if (paramNames == null) {
            m_paramNames = new String[method.getParameterTypes().length];
            for (int i = 0; i < m_paramNames.length; i++) {
                m_paramNames[i] = "param" + (i + 1);
            }
        }
        m_paramDescriptions = paramDescriptions;
        m_returnDescription = returnDescription;
    }

    public String getName() {
        return m_method.getName();
    }

    public Class[] getParameterTypes() {
        return m_method.getParameterTypes();
    }

    public String[] getParameterNames() {
        return m_paramNames;
    }

    public String[] getParameterDescriptions() {
        return m_paramDescriptions;
    }

    public String getMethodDescription() {
        return m_methodDescription;
    }

    public Class getReturnType() {
        return m_method.getReturnType();
    }

    public Object invoke(Object instance, Object[] paramValues)
            throws Throwable {
        return m_method.invoke(instance, paramValues);
    }

    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer();
        ret.append(getUnqualifiedName(m_method.getReturnType()));
        ret.append(" ");
        ret.append(m_method.getName());
        ret.append("(");
        Class[] types = m_method.getParameterTypes();
        for (int i = 0; i < types.length; i++) {
            if (i > 0) {
                ret.append(", ");
            }
            ret.append(getUnqualifiedName(types[i]));
            ret.append(' ');
            ret.append(m_paramNames[i]);
        }
        ret.append(")");
        return ret.toString();
    }

    public String getUnqualifiedName(Class cl) {
        if (cl == null) {
            return "void";
        }
        if (cl.getPackage() == null) {
            return bracketsForArrays(cl.getName());
        }
        String pName = cl.getPackage().getName();
        if (pName != null && pName.length() > 0) {
            return cl.getName().substring(pName.length() + 1);
        }
        return bracketsForArrays(cl.getName());
    }

    private String bracketsForArrays(String in) {
        if (in.equals("[B")) {
            return "byte[]";
        }
        if (in.startsWith("[L")) {
            try {
                return getUnqualifiedName(Class.forName(in.substring(2, in
                        .length() - 1)))
                        + "[]";
            } catch (ClassNotFoundException cnfe) {
                System.out.println("class not found: "
                        + in.substring(2, in.length() - 1));
            }
        }
        return in;
    }

}
