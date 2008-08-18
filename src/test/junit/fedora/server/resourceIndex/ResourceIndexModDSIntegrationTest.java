
package fedora.server.resourceIndex;

import org.junit.Test;

import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DigitalObject;

/**
 * Tests modifying objects in the RI, with respect to their datastreams. Note:
 * All tests run at RI level 1 unless otherwise noted.
 * 
 * @author Chris Wilper
 */
public class ResourceIndexModDSIntegrationTest
        extends ResourceIndexIntegrationTest {

    /**
     * Add a datastream to an existing object.
     */
    @Test
    public void testModObjOnceAddDS() throws Exception {
        DigitalObject original = getTestObject("test:1", "test1");

        DigitalObject modified = deepCopy(original);
        addEDatastream(modified, "DS1");

        doModifyTest(1, original, modified);
    }

    /**
     * Delete a datastream from an existing object.
     */
    @Test
    public void testModObjOnceDelDS() throws Exception {
        DigitalObject original = getTestObject("test:1", "test1");
        addEDatastream(original, "DS1");

        DigitalObject modified = deepCopy(original);
        
        for (Datastream d : modified.datastreams("DS1")) {
            modified.removeDatastreamVersion(d);
        }

        doModifyTest(1, original, modified);
    }

    /**
     * Add a datastream and delete another from an existing object.
     */
    @Test
    public void testModObjOnceAddOneDSDelAnother() throws Exception {
        DigitalObject original = getTestObject("test:1", "test1");
        addEDatastream(original, "DS1");

        DigitalObject modified = deepCopy(original);
        addEDatastream(modified, "DS2");
        
        for (Datastream d : modified.datastreams("DS1")) {
            modified.removeDatastreamVersion(d);
        }

        doModifyTest(1, original, modified);
    }

    /**
     * Add a Dublin Core field to the DC datastream of an existing object.
     */
    @Test
    public void testModObjOnceAddOneDCField() throws Exception {
        DigitalObject original = getTestObject("test:1", "test1");
        addXDatastream(original, "DC", getDC("<dc:title>test</dc:title>"));

        DigitalObject modified = deepCopy(original);
        addXDatastream(modified, "DC", getDC("<dc:title>test</dc:title>\n"
                + "<dc:identifier>id</dc:identifier>"));

        doModifyTest(1, original, modified);
    }

    /**
     * Delete a Dublin Core field from the DC datastream of an existing object.
     */
    @Test
    public void testModObjOnceDelOneDCField() throws Exception {
        DigitalObject original = getTestObject("test:1", "test1");
        addXDatastream(original, "DC", getDC("<dc:title>test</dc:title>\n"
                + "<dc:identifier>id</dc:identifier>"));

        DigitalObject modified = deepCopy(original);
        addXDatastream(modified, "DC", getDC("<dc:title>test</dc:title>"));

        doModifyTest(1, original, modified);
    }

    /**
     * Add a Dublin Core field and delete another from the DC datastream of an
     * existing object.
     */
    @Test
    public void testModObjOnceAddOneDCFieldDelAnother() throws Exception {
        DigitalObject original = getTestObject("test:1", "test1");
        addXDatastream(original, "DC", getDC("<dc:title>test</dc:title>"));

        DigitalObject modified = deepCopy(original);
        addXDatastream(modified,
                       "DC",
                       getDC("<dc:identifier>id</dc:identifier>"));

        doModifyTest(1, original, modified);
    }

    /**
     * Add a relation to the RELS-EXT datastream of an existing object.
     */
    @Test
    public void testModObjOnceAddOneRELSEXTField() throws Exception {
        String rel1 = "<foo:bar rdf:resource=\"http://example.org/baz\"/>";
        String rel2 = "<foo:bar rdf:resource=\"http://example.org/quux\"/>";

        DigitalObject original = getTestObject("test:1", "test1");
        addXDatastream(original, "RELS-EXT", getRELSEXT(rel1));

        DigitalObject modified = deepCopy(original);
        addXDatastream(modified, "RELS-EXT", getRELSEXT(rel1 + "\n" + rel2));

        doModifyTest(1, original, modified);
    }

    /**
     * Delete a relation from the RELS-EXT datastream of an existing object.
     */
    @Test
    public void testModObjOnceDelOneRELSEXTField() throws Exception {
        String rel1 = "<foo:bar rdf:resource=\"http://example.org/baz\"/>";
        String rel2 = "<foo:bar rdf:resource=\"http://example.org/quux\"/>";

        DigitalObject original = getTestObject("test:1", "test1");
        addXDatastream(original, "RELS-EXT", getRELSEXT(rel1 + "\n" + rel2));

        DigitalObject modified = deepCopy(original);
        addXDatastream(modified, "RELS-EXT", getRELSEXT(rel1));

        doModifyTest(1, original, modified);
    }

    /**
     * Add a relation and delete another from the RELS-EXT datastream of an
     * existing object.
     */
    @Test
    public void testModObjOnceAddOneRELSEXTFieldDelAnother() throws Exception {
        String rel1 = "<foo:bar rdf:resource=\"http://example.org/baz\"/>";
        String rel2 = "<foo:bar rdf:resource=\"http://example.org/quux\"/>";

        DigitalObject original = getTestObject("test:1", "test1");
        addXDatastream(original, "RELS-EXT", getRELSEXT(rel1));

        DigitalObject modified = deepCopy(original);
        addXDatastream(modified, "RELS-EXT", getRELSEXT(rel2));

        doModifyTest(1, original, modified);
    }

    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ResourceIndexModDSIntegrationTest.class);
    }

}
