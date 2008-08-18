
package fedora.test.integration;

import java.io.ByteArrayOutputStream;
import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.custommonkey.xmlunit.SimpleXpathEngine;

import fedora.client.FedoraClient;

import fedora.common.Constants;

import fedora.server.management.FedoraAPIM;

import fedora.test.DemoObjectTestSetup;
import fedora.test.FedoraServerTestCase;

import fedora.utilities.ExecUtility;

/**
 * @author Edwin Shin
 */
public class TestCommandLineUtilities
        extends FedoraServerTestCase
        implements Constants {

    static ByteArrayOutputStream sbOut = null;

    static ByteArrayOutputStream sbErr = null;

    static TestCommandLineUtilities curTest = null;

    public static Test suite() {
        TestSuite suite = new TestSuite("Command Line Utilities TestSuite");
        suite.addTestSuite(TestCommandLineUtilities.class);
        return new DemoObjectTestSetup(suite);
    }

    public void testFedoraPurgeAndIngest() {
        System.out.println("Purging object demo:5");
        System.out.println("FEDORA-HOME = " + FEDORA_HOME);
        purgeUsingScript("demo:5");
        if (sbErr.size() > 0) {
            System.err.println(sbErr.toString());
        }
        assertEquals(0, sbErr.size());
        System.out.println("Re-ingesting object demo:5");
        ingestFoxmlFile(new File(FEDORA_HOME
                + "/client/demo/foxml/local-server-demos/simple-image-demo/obj_demo_5.xml"));
        String out = sbOut.toString();
        String err = sbErr.toString();
        if (out.indexOf("Ingested PID: demo:5") == -1) {
            System.err.println("Saw message: " + out);
        }
        assertEquals(out.indexOf("Ingested PID: demo:5") != -1, true);
        System.out.println("Purge and ingest test succeeded");
    }

    public void testBatchBuildAndBatchIngestAndPurge() throws Exception {
        System.out.println("Building batch objects");
        batchBuild(new File(FEDORA_HOME
                           + "/client/demo/batch-demo/foxml-template.xml"),
                   new File(FEDORA_HOME
                           + "/client/demo/batch-demo/object-specifics"),
                   new File(FEDORA_HOME + "/client/demo/batch-demo/objects"),
                   new File(FEDORA_HOME + "/client/logs/build.log"));
        String out = sbOut.toString();
        String err = sbErr.toString();
        assertEquals(err,
                     true,
                     err
                             .indexOf("10 Fedora FOXML XML documents successfully created") != -1);
        System.out.println("Ingesting batch objects");
        batchIngest(new File(FEDORA_HOME + "/client/demo/batch-demo/objects"),
                    new File(FEDORA_HOME + "/server/logs/junit_ingest.log"));
        out = sbOut.toString();
        err = sbErr.toString();
        if (err.indexOf("10 objects successfully ingested into Fedora") == -1) {
            System.out
                    .println("Didn't find expected string in output:\n" + err);
            assertEquals(true, false);
        }
        assertEquals(err
                             .indexOf("10 objects successfully ingested into Fedora") != -1,
                     true);
        String batchObjs[] =
                {"demo:3010", "demo:3011", "demo:3012", "demo:3013",
                        "demo:3014", "demo:3015", "demo:3016", "demo:3017",
                        "demo:3018", "demo:3019"};
        System.out.println("Purging batch objects");
        purgeFast(batchObjs);
        System.out.println("Build and ingest test succeeded");
    }

    public void testBatchBuildIngestAndPurge() throws Exception {
        System.out.println("Building and Ingesting batch objects");
        batchBuildIngest(new File(FEDORA_HOME
                                 + "/client/demo/batch-demo/foxml-template.xml"),
                         new File(FEDORA_HOME
                                 + "/client/demo/batch-demo/object-specifics"),
                         new File(FEDORA_HOME
                                 + "/client/demo/batch-demo/objects"),
                         new File(FEDORA_HOME
                                 + "/server/logs/junit_buildingest.log"));
        String out = sbOut.toString();
        String err = sbErr.toString();
        assertEquals("Response did not contain expected string re: FOXML XML documents: <reponse>"
                             + err + "</response>",
                     err
                             .indexOf("10 Fedora FOXML XML documents successfully created") != -1,
                     true);
        assertEquals("Response did not contain expected string re: objects successfully ingested: <reponse>"
                             + err + "</reponse",
                     err
                             .indexOf("10 objects successfully ingested into Fedora") != -1,
                     true);
        String batchObjs[] =
                {"demo:3010", "demo:3011", "demo:3012", "demo:3013",
                        "demo:3014", "demo:3015", "demo:3016", "demo:3017",
                        "demo:3018", "demo:3019"};
        System.out.println("Purging batch objects");
        purgeFast(batchObjs);
        System.out.println("Build/ingest test succeeded");
    }

    public void testBatchModify() throws Exception {
        System.out.println("Running batch modify of objects");
        batchModify(new File(FEDORA_HOME
                            + "/client/demo/batch-demo/modify-batch-directives.xml"),
                    new File(FEDORA_HOME + "/server/logs/junit_modify.log"));
        String out = sbOut.toString();
        String err = sbErr.toString();
        if (out.indexOf("25 modify directives successfully processed.") == -1) {
            System.out.println(" out = " + out);
            System.out.println(" err = " + err);
        }
        
        if (out
                .indexOf("25 modify directives successfully processed.") == -1) {
            System.err.println(out);
        }
        assertEquals(false, out
                .indexOf("25 modify directives successfully processed.") == -1);
        assertEquals(false, out.indexOf("0 modify directives failed.") == -1);
        System.out.println("Purging batch modify object");
        purgeFast("demo:32");
        System.out.println("Batch modify test succeeded");
    }

    public void testExport() {
        System.out.println("Testing fedora-export");
        File outFile =
                new File(FEDORA_HOME + "/client/demo/batch-demo/demo_5.xml");
        String absPath = outFile.getAbsolutePath();
        if (outFile.exists()) {
            outFile.delete();
        }
        System.out.println("Exporting object demo:5");
        exportObj("demo:5", new File(FEDORA_HOME + "/client/demo/batch-demo"));
        String out = sbOut.toString();
        String err = sbErr.toString();
        assertEquals(out.indexOf("Exported demo:5") != -1, true);
        File outFile2 =
                new File(FEDORA_HOME + "/client/demo/batch-demo/demo_5.xml");
        String absPath2 = outFile2.getAbsolutePath();
        assertEquals(outFile2.exists(), true);
        System.out.println("Deleting exported file");
        if (outFile2.exists()) {
            outFile2.delete();
        }
        System.out.println("Export test succeeded");
    }

    public void testValidatePolicy() {
        System.out.println("Testing Validate Policies");
        File validDir =
                new File("src/test/junit/XACMLTestPolicies/valid-policies");
        traverseAndValidate(validDir, true);

        File invalidDir =
                new File("src/test/junit/XACMLTestPolicies/invalid-policies");
        traverseAndValidate(invalidDir, false);

        System.out.println("Validate Policies test succeeded");
    }

    private void traverseAndValidate(File testDir, boolean expectValid) {
        //      assertEquals(testDir.isDirectory(), true);
        File testFiles[] = testDir.listFiles(new java.io.FilenameFilter() {

            public boolean accept(File dir, String name) {
                if ((name.toLowerCase().startsWith("permit") || name
                        .toLowerCase().startsWith("deny"))
                        && name.endsWith(".xml")) {
                    return true;
                }
                return false;
            }
        });
        for (File element : testFiles) {
            System.out.println("Checking "
                    + (expectValid ? "valid" : "invalid") + " policy: "
                    + element.getName());
            execute(FEDORA_HOME + "/server/bin/validate-policy", element
                    .getAbsolutePath());
            String out = sbOut.toString();
            String err = sbErr.toString();

            if (expectValid) {
                assertTrue("Expected \"Validation successful\", but received \""
                                   + out + "\"",
                           out.indexOf("Validation successful") != -1);
            } else {
                assertTrue("Expected \"Validation failed\", but received \""
                        + out + "\"", out.indexOf("Validation failed") != -1);
            }
        }
    }

    private void ingestFoxmlDirectory(File dir) {
        execute(FEDORA_HOME + "/client/bin/fedora-ingest", "d",
                dir.getAbsolutePath(), FOXML1_1.uri, "DMO",
                getHost() + ":" + getPort(), getUsername(), 
                getPassword(), getProtocol(), "junit ingest");
    }

    private void ingestFoxmlFile(File f) {
        execute(FEDORA_HOME + "/client/bin/fedora-ingest", "f",
                f.getAbsolutePath(), FOXML1_1.uri, 
                getHost() + ":" + getPort(), getUsername(), 
                getPassword(), getProtocol(), "junit-ingest");
    }

    private static void purgeUsingScript(String pid) {
        //    File exe = new File("client/bin/fedora-purge");
        execute(FEDORA_HOME + "/client/bin/fedora-purge", 
        		getHost() + ":" + getPort(), getUsername(),
        		getPassword(), pid, getProtocol(), "junit-purge");
    }

    private static void purgeFast(String pid) throws Exception {
        getAPIM().purgeObject(pid, "because", false);
    }

    private static void purgeFast(String[] pids) throws Exception {
        FedoraAPIM apim = getAPIM();
        for (String element : pids) {
            apim.purgeObject(element, "because", false);
        }
    }

    private static FedoraAPIM getAPIM() throws Exception {
        String baseURL =
                getProtocol() + "://" + getHost() + ":" + getPort() + "/fedora";
        FedoraClient client =
                new FedoraClient(baseURL, getUsername(), getPassword());
        return client.getAPIM();
    }

    private void batchBuild(File objectTemplateFile,
                            File objectSpecificDir,
                            File objectDir,
                            File logFile) {
        execute(FEDORA_HOME + "/client/bin/fedora-batch-build",
                objectTemplateFile.getAbsolutePath(),
                objectSpecificDir.getAbsolutePath(),
                objectDir.getAbsolutePath(),
                logFile.getAbsolutePath(), "text");
    }

    private void batchIngest(File objectDir, File logFile) {
        execute(FEDORA_HOME + "/client/bin/fedora-batch-ingest", 
        		objectDir.getAbsolutePath(), logFile.getAbsolutePath(),
                "text", FOXML1_1.uri, getHost() + ":" + getPort(),
                getUsername(), getPassword(), getProtocol());
    }

    private void batchBuildIngest(File objectTemplateFile,
                                  File objectSpecificDir,
                                  File objectDir,
                                  File logFile) {
        execute(FEDORA_HOME + "/client/bin/fedora-batch-buildingest",
                objectTemplateFile.getAbsolutePath(),
                objectSpecificDir.getAbsolutePath(),
                objectDir.getAbsolutePath(),
                logFile.getAbsolutePath(), 
                "text",
                getHost() + ":" + getPort(),
                getUsername(),
                getPassword(),
                getProtocol());
    }

    private void batchModify(File batchDirectives, File logFile) {
        execute(FEDORA_HOME + "/client/bin/fedora-modify", 
        		getHost() + ":" + getPort(),
        		getUsername(), getPassword(), 
        		batchDirectives.getAbsolutePath(),
        		logFile.getAbsolutePath(), getProtocol());
    }

    private void exportObj(String pid, File dir) {
        execute(FEDORA_HOME + "/client/bin/fedora-export", 
        		getHost() + ":" + getPort(),
        		getUsername(), getPassword(), pid, FOXML1_1.uri,
        		"public", dir.getAbsolutePath(), getProtocol());
    }

    public static void execute(String... cmd) {
        String osName = System.getProperty("os.name");
        if (!osName.startsWith("Windows")) {
            // needed for the Fedora shell scripts
            cmd[0] = cmd[0] + ".sh";
        }
        if (sbOut != null && sbErr != null) {
            sbOut.reset();
            sbErr.reset();
            ExecUtility.execCommandLineUtility(cmd, sbOut, sbErr);
        } else {
            ExecUtility.execCommandLineUtility(cmd);
        }
    }

    public void setUp() throws Exception {
        sbOut = new ByteArrayOutputStream();
        sbErr = new ByteArrayOutputStream();
    }

    public void tearDown() {
        SimpleXpathEngine.clearNamespaces();
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestCommandLineUtilities.class);
    }

}
