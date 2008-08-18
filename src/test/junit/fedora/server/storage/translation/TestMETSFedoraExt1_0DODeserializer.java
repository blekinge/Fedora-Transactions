
package fedora.server.storage.translation;

import org.junit.Test;

import fedora.server.storage.types.DigitalObject;

/**
 * Unit tests for METSFedoraExt1_0DODeserializer.
 * 
 * @author Chris Wilper
 */
public class TestMETSFedoraExt1_0DODeserializer
        extends TestMETSFedoraExtDODeserializer {

    public TestMETSFedoraExt1_0DODeserializer() {
        // superclass sets protected fields 
        // m_deserializer and m_serializer as given below
        super(new METSFedoraExt1_0DODeserializer(),
              new METSFedoraExt1_0DOSerializer());
    }

    //---
    // Tests
    //---

    @Test
    /* FIXME: no longer disseminators, so shold probably remove test */
    //public void testTwoDisseminators() {
    //    doTestTwoDisseminators();
    //}
    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(TestMETSFedoraExt1_0DODeserializer.class);
    }
}
