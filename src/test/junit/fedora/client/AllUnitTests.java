
package fedora.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {fedora.client.utility.AllUnitTests.class})
public class AllUnitTests {

    // Supports legacy tests runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite =
                new junit.framework.TestSuite(AllUnitTests.class.getName());

        suite.addTest(fedora.client.utility.AllUnitTests.suite());

        return suite;
    }
}
