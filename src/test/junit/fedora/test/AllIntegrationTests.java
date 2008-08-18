
package fedora.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {fedora.server.AllIntegrationTests.class})
public class AllIntegrationTests {

    // Supports legacy tests runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite =
                new junit.framework.TestSuite(AllIntegrationTests.class
                        .getName());

        suite.addTest(fedora.server.AllIntegrationTests.suite());

        return suite;
    }
}
