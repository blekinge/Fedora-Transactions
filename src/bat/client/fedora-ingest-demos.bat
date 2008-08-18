@echo off

goto checkEnv
:envOk

set OLD_JAVA_HOME=%JAVA_HOME%
set JAVA_HOME=%THIS_JAVA_HOME%

:runMinimized

echo Ingesting Demonstration Objects...

set DO_FORMAT=info:fedora/fedora-system:FOXML-1.1

"%JAVA_HOME%\bin\java" -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\fedora-client.jar -Djavax.net.ssl.trustStore="%FEDORA_HOME%\client\truststore" -Djavax.net.ssl.trustStorePassword=tomcat -Dfedora.home=%FEDORA_HOME% -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.utility.ingest.Ingest d %FEDORA_HOME%\client\demo\foxml\local-server-demos %DO_FORMAT% %1:%2 %3 %4 %5
"%JAVA_HOME%\bin\java" -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\fedora-client.jar -Djavax.net.ssl.trustStore="%FEDORA_HOME%\client\truststore" -Djavax.net.ssl.trustStorePassword=tomcat -Dfedora.home=%FEDORA_HOME% -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.utility.ingest.Ingest d %FEDORA_HOME%\client\demo\foxml\open-server-demos %DO_FORMAT% %1:%2 %3 %4 %5

echo Finished.

set JAVA_HOME=%OLD_JAVA_HOME%

goto end

:checkEnv
if "%FEDORA_HOME%" == "" goto noFedoraHome
if not exist "%FEDORA_HOME%\client\fedora-client.jar" goto clientNotFound
if "%FEDORA_JAVA_HOME%" == "" goto tryJavaHome
set THIS_JAVA_HOME=%FEDORA_JAVA_HOME%

:checkJava
if not exist "%THIS_JAVA_HOME%\bin\java.exe" goto noJavaBin
if not exist "%THIS_JAVA_HOME%\bin\orbd.exe" goto badJavaVersion
goto envOk

:tryJavaHome
if "%JAVA_HOME%" == "" goto noJavaHome
set THIS_JAVA_HOME=%JAVA_HOME%
goto checkJava

:noFedoraHome
echo ERROR: Environment variable, FEDORA_HOME must be set.
goto end

:clientNotFound
echo ERROR: FEDORA_HOME does not appear correctly set.
echo Client cannot be found at %FEDORA_HOME%\client\fedora-client.jar
goto end

:noJavaHome
echo ERROR: FEDORA_JAVA_HOME was not defined, nor was (the fallback) JAVA_HOME.
goto end

:noJavaBin
echo ERROR: java.exe was not found in %THIS_JAVA_HOME%
echo Make sure FEDORA_JAVA_HOME or JAVA_HOME is set correctly.
goto end

:badJavaVersion
echo ERROR: java was found in %THIS_JAVA_HOME%, but it was not version 1.4
echo Make sure FEDORA_JAVA_HOME or JAVA_HOME points to a 1.4JRE/JDK base.
goto end

:end
