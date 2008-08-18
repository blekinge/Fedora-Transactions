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

package fedora.client.utility.validate.process;

import java.io.IOException;

import java.util.Iterator;

import javax.xml.rpc.ServiceException;

import fedora.client.utility.validate.ObjectSourceException;
import fedora.client.utility.validate.ObjectValidator;
import fedora.client.utility.validate.ValidationResults;
import fedora.client.utility.validate.process.ValidatorProcessParameters.IteratorType;
import fedora.client.utility.validate.remote.RemoteObjectSource;
import fedora.client.utility.validate.remote.ServiceInfo;

/**
 * A command-line utility that validates objects in a remote repository,
 * selected by criteria. See the javadoc for {@link ValidatorProcessParameters}
 * for the usage details.
 * 
 * @author Jim Blake
 */
public class ValidatorProcess {

    /**
     * Open the connection to the Fedora server.
     */
    private static RemoteObjectSource openObjectSource(ServiceInfo serviceInfo) {
        try {
            return new RemoteObjectSource(serviceInfo);
        } catch (ServiceException e) {
            throw new IllegalStateException("Failed to initialize "
                    + "the ValidatorProcess: ", e);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize "
                    + "the ValidatorProcess: ", e);
        }
    }

    /**
     * The list of PIDs may come from a file, or from a query against the object
     * source.
     */
    private static Iterator<String> getPidIterator(ValidatorProcessParameters parms,
                                                   RemoteObjectSource objectSource)
            throws ObjectSourceException {
        if (parms.getIteratorType() == IteratorType.FS_QUERY) {
            return objectSource.findObjectPids(parms.getQuery());
        } else {
            return new PidfileIterator(parms.getPidfile());
        }
    }

    public static void main(String[] args) throws ObjectSourceException {
        try {
            // Parse the parameters.
            ValidatorProcessParameters parms =
                    new ValidatorProcessParameters(args);

            // Create the tools we will need.
            RemoteObjectSource objectSource =
                    openObjectSource(parms.getServiceInfo());
            ValidationResults results =
                    new Log4jValidationResults(parms.getLogConfigProperties());

            // Get the list of PIDs.
            Iterator<String> pids = getPidIterator(parms, objectSource);

            // Go through the list, validating.
            ObjectValidator validator = new ObjectValidator(objectSource);
            while (pids.hasNext()) {
                results.record(validator.validate(pids.next()));
            }

            // Display the results.
            results.closeResults();
        } catch (ValidatorProcessUsageException e) {
            System.err.println(e.getMessage());
        }
    }

}
