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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.HashMap;

import fedora.common.Constants;

import fedora.server.storage.translation.DOTranslationUtility;
import fedora.server.storage.translation.DOTranslatorImpl;
import fedora.server.storage.translation.METSFedoraExt1_1DODeserializer;
import fedora.server.storage.translation.METSFedoraExt1_1DOSerializer;
import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DigitalObject;
import fedora.server.validation.DOValidatorImpl;

/**
 * Tests the METS deserializer and serializer by opening a METS file (supplied
 * at command-line), deserializing it, re-serializing it, and sending it to
 * STDOUT.
 * 
 * @author Chris Wilper
 */
public class METSInOutTest
        implements Constants {

    public static void main(String args[]) {
        FileInputStream in = null;
        // set system properties for testing purposes      	
        System.setProperty("fedoraServerHost", "localhost");
        System.setProperty("fedoraServerPort", "80");

        try {
            if (args.length < 1) {
                throw new IOException("At least one parameter needed.");
            }
            in = new FileInputStream(new File(args[0]));
        } catch (IOException ioe) {
            System.out.println("Error: " + ioe.getMessage());
            System.out
                    .println("Give the path to an existing METS file, and optionally, the level of validation to perform on the re-serialized version.");
            System.exit(0);
        }
        try {
            METSFedoraExt1_1DODeserializer deser =
                    new METSFedoraExt1_1DODeserializer();
            METSFedoraExt1_1DOSerializer ser =
                    new METSFedoraExt1_1DOSerializer();
            HashMap desers = new HashMap();
            HashMap sers = new HashMap();
            desers.put(METS_EXT1_1.uri, deser);
            sers.put(METS_EXT1_1.uri, ser);
            DOTranslatorImpl trans = new DOTranslatorImpl(sers, desers);
            DigitalObject obj = new BasicDigitalObject();
            System.out.println("Deserializing...");
            trans.deserialize(in,
                              obj,
                              METS_EXT1_1.uri,
                              "UTF-8",
                              DOTranslationUtility.DESERIALIZE_INSTANCE);
            System.out.println("Done.");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.out.println("Re-serializing for STORAGE...");
            trans.serialize(obj,
                            out,
                            METS_EXT1_1.uri,
                            "UTF-8",
                            DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL);
            System.out.println("Done.");
            if (args.length > 1) {
                ByteArrayInputStream newIn =
                        new ByteArrayInputStream(out.toByteArray());
                HashMap xmlSchemaMap = new HashMap();
                // LOOK!  These path values should work if test is run from
                // the FEDORA HOME directory.  Adjust accordingly for test environment.
                xmlSchemaMap.put(METS_EXT1_1.uri,
                                 "dist/server/xsd/mets-fedora-ext1-1.xsd");
                HashMap ruleSchemaMap = new HashMap();
                ruleSchemaMap.put(METS_EXT1_1.uri,
                                  "dist/server/schematron/metsExtRules1-0.xml");
                DOValidatorImpl v =
                        new DOValidatorImpl(null,
                                            xmlSchemaMap,
                                            "dist/server/schematron/preprocessor.xslt",
                                            ruleSchemaMap);
                if (args[1].equals("1")) {
                    v.validate(newIn,
                               METS_EXT1_1.uri,
                               DOValidatorImpl.VALIDATE_XML_SCHEMA,
                               "ingest");
                    System.out.println("XML Schema validation: PASSED!");
                } else {
                    if (args[1].equals("2")) {
                        v.validate(newIn,
                                   METS_EXT1_1.uri,
                                   DOValidatorImpl.VALIDATE_SCHEMATRON,
                                   "ingest");
                        System.out.println("Schematron validation: PASSED!");
                    } else {
                        System.out.println("Unrecognized validation level, '"
                                + args[1] + "'");
                    }
                }
            } else {
                System.out.println("Here it is:");
                System.out.println(out.toString("UTF-8"));
            }

        } catch (Exception e) {
            System.out.println("Error: (" + e.getClass().getName() + "):"
                    + e.getMessage());
            e.printStackTrace();
        }
    }
}