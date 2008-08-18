/*
 * The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.journal.readerwriter.multicast.rmi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {TestRmiTransport.class})
public class AllIntegrationTests {

    // Supports legacy test runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite =
                new junit.framework.TestSuite(AllIntegrationTests.class
                        .getName());

        suite.addTest(TestRmiTransport.suite());

        return suite;

    }

}
