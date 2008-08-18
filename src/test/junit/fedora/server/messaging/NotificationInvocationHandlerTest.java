/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.messaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jms.TextMessage;
import javax.naming.Context;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fedora.server.MockContext;
import fedora.server.management.Management;
import fedora.server.management.MockManagementDelegate;
import fedora.server.messaging.Messaging.MessageType;
import fedora.server.proxy.ProxyFactory;

/**
 * Test sending of messages by NotificationInvocationHandler.
 * 
 * @author Edwin Shin
 * @version $Id$
 */
public class NotificationInvocationHandlerTest {

    private JMSManager jmsMgr;

    @Before
    public void setUp() throws Exception {
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, 
                          "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, 
                          "vm://localhost?broker.useShutdownHook=false&broker.persistent=false");
        props.setProperty(JMSManager.CONNECTION_FACTORY_NAME, "ConnectionFactory");
        props.setProperty("topic.fedora.apim.update", "fedora.apim.update");

        jmsMgr = new JMSManager(props);
    }

    @After
    public void tearDown() throws Exception {
        if (jmsMgr != null) {
            jmsMgr.close();
        }
    }

    @Test
    public void testAPIMMessages() throws Exception {

        new Thread() {

            public void run() {
                try {
                    Map<String, List<String>> mdMap =
                            new HashMap<String, List<String>>();
                    List<String> destinations = new ArrayList<String>();
                    destinations.add("fedora.apim.update");
                    mdMap.put(MessageType.apimUpdate.toString(), destinations);
                    Messaging msg = new MessagingImpl("http://localhost:8080/fedora", 
                                                      mdMap, 
                                                      jmsMgr);
                    Object[] invocationHandlers =
                            {new NotificationInvocationHandler(msg)};
                    Management mgmt = new MockManagementDelegate();
                    Management proxy =
                            (Management) ProxyFactory
                                    .getProxy(mgmt, invocationHandlers);
                    proxy.purgeObject(new MockContext(),
                                      "demo:test",
                                      null,
                                      false);
                } catch (Exception e) {
                    fail(e.getMessage());
                }
            }
        }.start();

        TextMessage message = (TextMessage) jmsMgr.listen("fedora.apim.update");
        APIMMessage apim = new AtomAPIMMessage(message.getText());
        assertEquals("demo:test", apim.getPID());
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(NotificationInvocationHandlerTest.class);
    }
}
