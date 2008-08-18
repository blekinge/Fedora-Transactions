#!/bin/sh

# Cannot proceed if FEDORA_HOME is not set
if [ -z "$FEDORA_HOME" ]; then
	echo "ERROR: The FEDORA_HOME environment variable is not defined."
	exit 1
fi

if [ -r "$FEDORA_HOME"/client/bin/set-env.sh ]; then
  	. "$FEDORA_HOME"/client/bin/set-env.sh
else
	echo "ERROR: $FEDORA_HOME/client/bin/set-env.sh was not found."
	exit 1
fi

echo "Starting Fedora Administrative Client..."
(exec "$JAVA" -Xms64m -Xmx96m \
              -cp $FEDORA_HOME/client:$FEDORA_HOME/client/fedora-client.jar \
              -Djava.endorsed.dirs=$FEDORA_HOME/client/lib \
              -Djavax.net.ssl.trustStore=$FEDORA_HOME/client/truststore \
              -Djavax.net.ssl.trustStorePassword=tomcat \
              -Dfedora.home=$FEDORA_HOME \
              -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl \
              -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl \
              fedora.client.Administrator $1 $2 $3 $4 $5)

restoreJavaHome
exit 0
