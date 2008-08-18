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

package fedora.utilities;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A Java Logging <code>Filter</code> that redirects <code>LogRecord</code>s
 * to a Log4J <code>Logger</code>. This class is useful if your project wants
 * consistent logging configuration based on Log4J, but must work with a library
 * that uses the Java Logging API. By using this filter's convenience method,
 * <code>apply</code>, messages that would otherwise go to Java Logging
 * handler(s) are instead sent to the configured Log4J appender(s). Example use:
 * 
 * <pre>
 *     // Causes all messages generated by the org.example.Example 
 *     // Java Logger to be sent to an equivalently-named Log4J appender.
 *     Log4JRedirectFilter.apply("org.example.Example");
 * </pre>
 * 
 * The log level mapping used is as follows:
 * 
 * <pre>
 *   Log4J           Java Logging
 *   ------------    ------------
 *   OFF             OFF
 *   ERROR, FATAL    SEVERE
 *   WARN            WARNING
 *   INFO            INFO, CONFIG
 *   DEBUG           FINER, FINE
 *   TRACE           FINEST
 *   ALL             ALL
 * </pre>
 * 
 * @author Chris Wilper
 */
public class Log4JRedirectFilter
        implements Filter {

    private static final String FQCN = Log4JRedirectFilter.class.getName();

    private final Logger _log4jLogger;

    public Log4JRedirectFilter(Logger log4jLogger) {
        _log4jLogger = log4jLogger;
    }

    /**
     * Convenience method for applying a <code>Log4JRedirectFilter</code> to a
     * Java Logger, by name. Log records destined for the given Java logger will
     * be redirected to the Log4J logger of the same name.
     */
    public static void apply(String javaLoggerName) {

        java.util.logging.Logger javaLogger =
                java.util.logging.Logger.getLogger(javaLoggerName);

        Logger log4jLogger = Logger.getLogger(javaLoggerName);

        // set the level of the java logger according to the level
        // of the equivalent log4j logger.  By doing this, we are overriding
        // the level currently in play for the Java Logger with that configured
        // for the equivalently-named Log4J logger.
        javaLogger.setLevel(getJavaLogLevel(log4jLogger.getLevel()));

        // set the filter to one that redirects messages to a Log4J logger
        // of the same name
        javaLogger.setFilter(new Log4JRedirectFilter(log4jLogger));

    }

    /**
     * Always returns false, but before doing so, sends the given record to the
     * Log4J Logger.
     */
    public boolean isLoggable(LogRecord record) {
        _log4jLogger.callAppenders(getLoggingEvent(record));
        return false;
    }

    /**
     * Translates the given Java Logging <code>LogRecord</code> to a
     * roughly-equivalent Log4J <code>LoggingEvent</code>.
     */
    private LoggingEvent getLoggingEvent(LogRecord record) {
        Level log4jLevel = getLog4jLevel(record.getLevel());
        return new CustomLoggingEvent(FQCN, _log4jLogger, log4jLevel, record);
    }

    /**
     * Gets the Java Logging Level analogous to the given Log4J Level.
     */
    private static java.util.logging.Level getJavaLogLevel(Level log4jLevel) {
        if (log4jLevel == null) {
            return java.util.logging.Level.SEVERE;
        } else if (log4jLevel == Level.OFF) {
            return java.util.logging.Level.OFF;
        } else if (log4jLevel == Level.ERROR || log4jLevel == Level.FATAL) {
            return java.util.logging.Level.SEVERE;
        } else if (log4jLevel == Level.WARN) {
            return java.util.logging.Level.WARNING;
        } else if (log4jLevel == Level.INFO) {
            return java.util.logging.Level.CONFIG;
        } else if (log4jLevel == Level.DEBUG) {
            return java.util.logging.Level.FINER;
        } else if (log4jLevel == Level.TRACE) {
            return java.util.logging.Level.FINEST;
        } else if (log4jLevel == Level.ALL) {
            return java.util.logging.Level.ALL;
        } else {
            throw new IllegalArgumentException("Unrecognized Log4J Level: "
                    + log4jLevel.getClass().getName());
        }
    }

    /**
     * Gets the Log4J Level analogous to the given Java Logging level.
     */
    private static Level getLog4jLevel(java.util.logging.Level javaLevel) {
        if (javaLevel == null) {
            return Level.ERROR;
        } else if (javaLevel == java.util.logging.Level.OFF) {
            return Level.OFF;
        } else if (javaLevel == java.util.logging.Level.SEVERE) {
            return Level.ERROR;
        } else if (javaLevel == java.util.logging.Level.WARNING) {
            return Level.WARN;
        } else if (javaLevel == java.util.logging.Level.INFO) {
            return Level.INFO;
        } else if (javaLevel == java.util.logging.Level.CONFIG) {
            return Level.INFO;
        } else if (javaLevel == java.util.logging.Level.FINE) {
            return Level.DEBUG;
        } else if (javaLevel == java.util.logging.Level.FINER) {
            return Level.DEBUG;
        } else if (javaLevel == java.util.logging.Level.FINEST) {
            return Level.TRACE;
        } else if (javaLevel == java.util.logging.Level.ALL) {
            return Level.ALL;
        } else {
            throw new IllegalArgumentException("Unrecognized Java Logging "
                    + "Level: " + javaLevel.getClass().getName());
        }
    }

    /**
     * This subclass is necessary because we need to force the source classname
     * and source methodname into the Log4J LoggingEvent.
     */
    class CustomLoggingEvent
            extends LoggingEvent {

        private static final long serialVersionUID = 1L;

        private final LogRecord _record;

        private LocationInfo _locationInfo;

        public CustomLoggingEvent(String callerFQCN,
                                  Logger logger,
                                  Level level,
                                  LogRecord record) {
            super(callerFQCN, logger, level, record.getMessage(), record
                    .getThrown());
            _record = record;
        }

        @Override
        public synchronized LocationInfo getLocationInformation() {
            if (_locationInfo == null) {
                Throwable stackInfo = new Throwable();
                stackInfo.fillInStackTrace();
                _locationInfo =
                        new CustomLocationInfo(stackInfo, _record
                                .getSourceClassName(), _record
                                .getSourceMethodName());
            }
            return _locationInfo;
        }

    }

    /**
     * Supports forcing the classname and method name for our
     * <code>CustomLoggingEvent</code> class.
     */
    class CustomLocationInfo
            extends LocationInfo {

        private static final long serialVersionUID = 1L;

        private final String _sourceClassName;

        private final String _sourceMethodName;

        public CustomLocationInfo(Throwable stackInfo,
                                  String sourceClassName,
                                  String sourceMethodName) {
            super(stackInfo, sourceClassName);
            _sourceClassName = sourceClassName;
            _sourceMethodName = sourceMethodName;
        }

        @Override
        public String getClassName() {
            return _sourceClassName;
        }

        @Override
        public String getMethodName() {
            return _sourceMethodName;
        }

        @Override
        public String getFileName() {
            // Java Logging doesn't provide this, so we don't know
            return NA;
        }

        @Override
        public String getLineNumber() {
            // Java Logging doesn't provide this, so we don't know
            return NA;
        }

    }

}