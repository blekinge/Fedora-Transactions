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

package fedora.server.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import java.util.Date;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import fedora.common.Constants;

/**
 * Provides a client for testing the Fedora Access SOAP service.
 * 
 * @author Ross Wayland
 */
public class TestClientAPIA {

    /**
     * <p>
     * Tests the Fedora Access SOAP service by making calls to each of the
     * supported services.
     * </p>
     * 
     * @param args
     *        An array of command line arguments.
     */
    public static void main(String[] args) {
        String PID = "uva-lib:1225";
        String qName1 = Constants.API.uri;
        String endpoint = "http://localhost:8080/fedora/services/access";
        Date asOfDate = null;

        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new java.net.URL(endpoint));
            // Test GetDissemination
            PID = "1007.lib.dl.test/text_ead/viu00003";
            String sDefPID = "web_ead";
            String method = "get_admin";
            call
                    .setOperationName(new javax.xml.namespace.QName(qName1,
                                                                    "GetDissemination"));
            fedora.server.types.gen.MIMETypedStream dissemination =
                    (fedora.server.types.gen.MIMETypedStream) call
                            .invoke(new Object[] {PID, sDefPID, method,
                                    asOfDate});
            if (dissemination != null) {
                String mime = dissemination.getMIMEType();
                System.out.println("\n\n****DISSEMINATION RESULTS*****\n"
                        + "Dissemination MIME: " + mime);
                BufferedReader br =
                        new BufferedReader(new InputStreamReader(new ByteArrayInputStream(dissemination
                                .getStream())));

                String line = null;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            }

            //Test View Objecct
            call
                    .setOperationName(new javax.xml.namespace.QName(qName1,
                                                                    "GetObjectMethods"));
            fedora.server.types.gen.ObjectMethodsDef[] objectView = null;
            QName qn = new QName(Constants.TYPES.uri, "ObjectMethodsDef");
            call
                    .registerTypeMapping(fedora.server.types.gen.ObjectMethodsDef.class,
                                         qn,
                                         new org.apache.axis.encoding.ser.BeanSerializerFactory(fedora.server.types.gen.ObjectMethodsDef.class,
                                                                                                qn),
                                         new org.apache.axis.encoding.ser.BeanDeserializerFactory(fedora.server.types.gen.ObjectMethodsDef.class,
                                                                                                  qn));
            objectView =
                    (fedora.server.types.gen.ObjectMethodsDef[]) call
                            .invoke(new Object[] {PID, asOfDate});
            for (int i = 0; i < objectView.length; i++) {
                fedora.server.types.gen.ObjectMethodsDef ov =
                        new fedora.server.types.gen.ObjectMethodsDef();
                ov = objectView[i];
                System.out.println("objDef[" + i + "] " + "\n" + ov.getPID()
                        + "\n" + ov.getServiceDefinitionPID() + "\n"
                        + ov.getMethodName() + "\n" + ov.getAsOfDate());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getStackTrace());
        }
    }
}