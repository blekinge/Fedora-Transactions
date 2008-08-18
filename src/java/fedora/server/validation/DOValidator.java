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

package fedora.server.validation;

import java.io.File;
import java.io.InputStream;

import fedora.server.errors.ServerException;

/**
 * Validates a digital object.
 * 
 * @author Sandy Payette
 */
public interface DOValidator {

    /**
     * Validates a digital object.
     * 
     * @param in
     *        The digital object provided as a bytestream.
     * @param validationLevel
     *        The level of validation to perform on the digital object. This is
     *        an integer from 0-2 with the following meanings: 0 = VALIDATE_ALL
     *        (do all validation levels) 1 = VALIDATE_XML_SCHEMA (perform only
     *        XML Schema validation) 2 = VALIDATE_SCHEMATRON (perform only
     *        Schematron Rules validation)
     * @param phase
     *        The stage in the work flow for which the validation should be
     *        contextualized. "ingest" = the object is in the submission format
     *        for the ingest stage phase "store" = the object is in the
     *        authoritative format for the final storage phase
     * @throws ServerException
     *         If validation fails for any reason.
     */
    public void validate(InputStream in,
                         String format,
                         int validationLevel,
                         String phase) throws ServerException;

    /**
     * Validates a digital object.
     * 
     * @param in
     *        The digital object provided as a file.
     * @param validationLevel
     *        The level of validation to perform on the digital object. This is
     *        an integer from 0-2 with the following meanings: 0 = VALIDATE_ALL
     *        (do all validation levels) 1 = VALIDATE_XML_SCHEMA (perform only
     *        XML Schema validation) 2 = VALIDATE_SCHEMATRON (perform only
     *        Schematron Rules validation)
     * @param phase
     *        The stage in the work flow for which the validation should be
     *        contextualized. "ingest" = the object is in the submission format
     *        for the ingest stage phase "store" = the object is in the
     *        authoritative format for the final storage phase
     * @throws ServerException
     *         If validation fails for any reason.
     */
    public void validate(File in,
                         String format,
                         int validationLevel,
                         String phase) throws ServerException;

}
