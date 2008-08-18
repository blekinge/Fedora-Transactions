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

package fedora.server.access.defaultdisseminator;

import java.lang.reflect.Method;

import fedora.server.errors.MethodNotFoundException;
import fedora.server.errors.ServerException;
import fedora.server.storage.types.Property;

/**
 * Invokes a method on an internal service.
 * 
 * <p>This is done using Java reflection where the service is the target object 
 * of a dynamic method request.
 * 
 * @author Sandy Payette
 */
public class ServiceMethodDispatcher {

    /**
     * Invoke a method on an internal service. This is done using Java
     * reflection where the service is the target object of a dynamic method
     * request.
     * 
     * @param service_object
     *        the target object of the service request
     * @param methodName
     *        the method to invoke on the target object
     * @param userParms
     *        parameters to the method to invoke on target object
     * @return
     * @throws ServerException
     */
    public Object invokeMethod(Object service_object,
                               String methodName,
                               Property[] userParms) throws ServerException {
        Method method = null;
        if (userParms == null) {
            userParms = new Property[0];
        }
        Object[] parmValues = new Object[userParms.length];
        Class[] parmClassTypes = new Class[userParms.length];
        for (int i = 0; i < userParms.length; i++) {
            // Get parm value.  Always treat the parm value as a string.
            parmValues[i] = new String(userParms[i].value);
            parmClassTypes[i] = parmValues[i].getClass();
        }
        // Invoke method: using Java Reflection
        try {
            method =
                    service_object.getClass().getMethod(methodName,
                                                        parmClassTypes);
            return method.invoke(service_object, parmValues);
        } catch (Exception e) {
            throw new MethodNotFoundException("Error executing method: "
                    + methodName, e);
        }
    }
}
