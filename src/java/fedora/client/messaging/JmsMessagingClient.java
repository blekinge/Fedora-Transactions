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
package fedora.client.messaging;

import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;

import org.apache.log4j.Logger;

import fedora.server.errors.MessagingException;
import fedora.server.messaging.JMSManager;
import fedora.server.messaging.JMSManager.DestinationType;

/**
 * A messaging client which listens for messages via JMS.
 *
 * @author Bill Branan
 */
public class JmsMessagingClient implements MessagingClient, MessageListener {

    private String m_clientId;
    private MessagingListener m_listener;
    private Properties m_connectionProperties;
    private String m_messageSelector;
    private boolean m_durable;

    private JMSManager m_jmsManager;

    private Logger LOG = Logger.getLogger(JmsMessagingClient.class.getName());

    /**
     * Creates a messaging client
     * @see JmsMessagingClient#JmsMessagingClient(String, MessagingListener, Properties, String, boolean)
     */
    public JmsMessagingClient(String clientId,
                              MessagingListener listener,
                              Properties connectionProperties)
            throws MessagingException {
        this(clientId, listener, connectionProperties, "", false);
    }

    /**
     * Creates a messaging client
     * @see JmsMessagingClient#JmsMessagingClient(String, MessagingListener, Properties, String, boolean)
     */
    public JmsMessagingClient(String clientId,
                              MessagingListener listener,
                              Properties connectionProperties,
                              boolean durable)
            throws MessagingException {
        this(clientId, listener, connectionProperties, "", durable);
    }

    /**
     * Creates a messaging client
     *
     * <h4>Client ID</h4>
     * <p>
     * The clientId provides applications with the opportunity to create
     * multiple messaging clients and track them based on this identifier.
     * The clientId is used within the MessagingClient when creating a
     * connection for durable subscriptions.
     * </p>
     *
     * <h4>Message Listener</h4>
     * <p>
     * A listener, the onMessage() method of which will be called when a
     * message arrives from the messaging provider. See the documentation
     * for javax.jms.MessageListener for more information.
     * </p>
     *
     * <h4>Connection Properties</h4>
     *
     * <p>All of the following properties must be included:</p>
     * <table border="1">
     *   <th>Property</th>
     *   <th>Description</th>
     *   <th>Example Value</th>
     *   <tr>
     *     <td>java.naming.factory.initial (javax.naming.Context.INITIAL_CONTEXT_FACTORY)</td>
     *     <td>The JNDI initial context factory which will allow lookup of the other attributes</td>
     *     <td>org.apache.activemq.jndi.ActiveMQInitialContextFactory</td>
     *   </tr>
     *   <tr>
     *     <td>java.naming.provider.url (javax.naming.Context.PROVIDER_URL)</td>
     *     <td>The JNDI provider URL
     *     <td>tcp://localhost:61616</td>
     *   </tr>
     *   <tr>
     *     <td>connection.factory.name (fedora.server.messaging.JMSManager.CONNECTION_FACTORY)</td>
     *     <td>The JNDI name of the connection factory needed to create a JMS Connection</td>
     *     <td>ConnectionFactory</td>
     *   </tr>
     * </table>
     * <p>One or more of the following properties must be specified:</p>
     * <table border="1">
     *   <th>Property Name</th>
     *   <th>Description</th>
     *   <th>Example Value</th>
     *   <tr>
     *     <td>topic.X (where X = name of subscription topic, example - topic.fedoraManagement)</td>
     *     <td>A topic over which notification messages will be provided</td>
     *     <td>fedora.apim.*</td>
     *   </tr>
     *   <tr>
     *     <td>queue.X (where X = name of subscription queue, example - queue.fedoraManagement)</td>
     *     <td>A queue through which notification messages will be provided</td>
     *     <td>fedora.apim.update</td>
     *   </tr>
     * </table>
     *
     * <h4>Durable</h4>
     * <p>
     * Specifies whether the topics included in the connection properties should
     * have durable consumers. If set to true, all topics listeners will be
     * constructed as durable subscribers. If set to false, all topic listeners
     * will be constructed as non-durable subscribers. This does not affect
     * queue listeners.
     * </p>
     * <p>
     * If there is a need for multiple topics, some of which are durable and some
     * of which are not, then two MessagingClients should be created.
     * One client would include topics needing durable subscribers and the other
     * client would include topics not needing durable subscribers.
     * A single MessageListener can be registered as the listener for both clients.
     * </p>
     *
     * <h4>Message Selector</h4>
     * <p>
     * A JMS message selector allows a client to specify, by header field references
     * and property references, the messages it is interested in. Only messages
     * whose header and property values match the selector are delivered.
     * See the javadoc for javax.jms.Message for more information about message selectors.
     * </p>
     *
     * @param clientId identification value for this messaging client
     * @param listener the listener which will be called when messages arrive
     * @param connectionProperties set of properties necessary to connect to JMS provider
     * @param messageSelector a selection which determines the messages to deliver
     * @param durable determines if the underlying JMS subscribers are durable
     * @throws MessagingException if listener is null or required properties are not set
     */
    public JmsMessagingClient(String clientId,
                              MessagingListener listener,
                              Properties connectionProperties,
                              String messageSelector,
                              boolean durable)
            throws MessagingException {

        // Check for a null listener
        if (listener == null) {
            throw new MessagingException("MessageListener may not be null");
        }

        // Check for null properties
        if (connectionProperties == null) {
            throw new MessagingException("Connection properties may not be null");
        }

        // Check for required property values
        String initialContextFactory =
            connectionProperties.getProperty(Context.INITIAL_CONTEXT_FACTORY);
        String providerUrl =
            connectionProperties.getProperty(Context.PROVIDER_URL);
        String connectionFactoryName =
            connectionProperties.getProperty(JMSManager.CONNECTION_FACTORY_NAME);

        if (initialContextFactory == null
            || providerUrl == null
            || connectionFactoryName == null) {
            throw new MessagingException("Propery values for "
                    + "'java.naming.factory.initial', "
                    + "'java.naming.provider.url', and"
                    + "'connection.factory.name' must be provided "
                    + "in order to initialize a messaging client");
        }

        // Check for valid client ID if durable subscriptions are required
        if (durable) {
            if (clientId == null || clientId.equals("")) {
                throw new MessagingException("ClientId must be "
                        + "specified for durable subscriptions");
            }
        }

        m_clientId = clientId;
        m_listener = listener;
        m_connectionProperties = connectionProperties;
        m_messageSelector = messageSelector;
        m_durable = durable;
    }

    /**
     * Starts the MessagingClient. This method must be called
     * in order to receive messages.
     */
    public void start() throws MessagingException {
        try {
            m_jmsManager = new JMSManager(m_connectionProperties, m_clientId);

            // Create Destinations based on properties
            Enumeration<?> propertyNames = m_connectionProperties.keys();
            while (propertyNames.hasMoreElements()) {
                String propertyName = (String) propertyNames.nextElement();
                if (propertyName.startsWith("topic.")) {
                    String destinationName =
                            m_connectionProperties.getProperty(propertyName);
                    m_jmsManager.createDestination(destinationName,
                                                   DestinationType.Topic);
                } else if (propertyName.startsWith("queue.")) {
                    String destinationName =
                            m_connectionProperties.getProperty(propertyName);
                    m_jmsManager.createDestination(destinationName,
                                                   DestinationType.Queue);
                }
            }

            // Get destination list
            List<Destination> destinations = m_jmsManager.getDestinations();

            // If there are no Destinations, throw an exception
            if (destinations.size() == 0) {
                throw new MessagingException("No destinations available for "
                        + "subscription, make sure that there is at least one topic "
                        + "or queue specified in the connection properties.");
            }

            // Subscribe
            for (Destination destination : destinations) {
                if (m_durable && (destination instanceof Topic)) {
                    m_jmsManager.listenDurable((Topic) destination,
                                               m_messageSelector,
                                               this,
                                               null);
                } else {
                    m_jmsManager.listen(destination, m_messageSelector, this);
                }
            }
        } catch (MessagingException me) {
            LOG.error("MessagingException encountered attempting to start "
                    + "Messaging Client: " + m_clientId
                    + ". Exception message: " + me.getMessage(), me);
            throw me;
        }
    }

    /**
     * Stops the MessagingClient, shuts down connections. If the unsubscribe
     * parameter is set to true, all durable subscriptions will be removed.
     *
     * @param unsubscribe
     */
    public void stop(boolean unsubscribe) throws MessagingException {
        try {
            if (unsubscribe) {
                m_jmsManager.unsubscribeAllDurable();
            }
            m_jmsManager.close();
        } catch (MessagingException me) {
            LOG.error("Messaging Exception encountered attempting to stop "
                    + "Messaging Client: " + m_clientId
                    + ". Exception message: " + me.getMessage(), me);
            throw me;
        }
    }

    /**
     * Receives messages and passes them to the MessagingListener
     * along with the client id.
     *
     * {@inheritDoc}
     */
    public void onMessage(Message message) {
        m_listener.onMessage(m_clientId, message);
    }

}