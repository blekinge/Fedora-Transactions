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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import fedora.server.validation.DOValidatorSchematron;
import fedora.server.validation.DOValidatorSchematronResult;

/**
 * @author Sandy Payette
 */
public class ValidateSchematronTest
        extends TestCase {

    protected String inFile = null;

    protected String inSchematronPPFile = null;

    protected String inSchematronRulesFile = null;

    protected String tempdir = null;

    protected DOValidatorSchematronResult result = null;

    @Override
    protected void setUp() {
        tempdir = "TestValidation";
        inSchematronPPFile = "src/schematron/preprocessor.xslt";

        // FOXML
        inFile = "TestIngestFiles/foxml-reference-ingest.xml";			
        inSchematronRulesFile = "src/xml/schematron/foxmlRules1-0.xml";

        FileInputStream in = null;
        try {
            in = new FileInputStream(new File(inFile));
        } catch (IOException ioe) {
            System.out.println("Error on XML file inputstream: "
                    + ioe.getMessage());
            ioe.printStackTrace();
        }

        try {
            DOValidatorSchematron dovs =
                    new DOValidatorSchematron(inSchematronRulesFile,
                                              inSchematronPPFile,
                                              "ingest");
            dovs.validate(in);
        } catch (Exception e) {
            System.out.println("Error: (" + e.getClass().getName() + "):"
                    + e.getMessage());
            e.printStackTrace();
        }
    }

    public void testFoo() {
        //assertNotNull("Failure: foo is null.", foo.getA());	
    }
}
