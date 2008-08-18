
package fedora.server.resourceIndex;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {ResourceIndexDatePrecisionIntegrationTest.class,
        ResourceIndexAddDelDSIntegrationTest.class,
        ResourceIndexAddDelMiscIntegrationTest.class,
        ResourceIndexModDSIntegrationTest.class,
        ResourceIndexModMiscIntegrationTest.class})
public class AllIntegrationTests {

    // Supports legacy test runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite =
                new junit.framework.TestSuite(AllIntegrationTests.class
                        .getName());

        suite.addTest(ResourceIndexDatePrecisionIntegrationTest.suite());

        suite.addTest(ResourceIndexAddDelDSIntegrationTest.suite());
        suite.addTest(ResourceIndexAddDelMiscIntegrationTest.suite());

        suite.addTest(ResourceIndexModDSIntegrationTest.suite());
        suite.addTest(ResourceIndexModMiscIntegrationTest.suite());

        return suite;

    }

}
