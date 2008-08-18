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

package fedora.server.security.servletfilters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Bill Niebel
 */
public class Base {

    protected static Log log = LogFactory.getLog(Base.class);

    protected static final String[] StringArrayPrototype = new String[0];

    private static final String INTO = " . . .";

    private static final String OUTOF = ". . . ";

    protected String getClassName() {
        String classname = this.getClass().getName();
        String[] parts = classname.split("\\.");
        if (parts.length > 0) {
            classname = parts[parts.length - 1];
        }
        return classname;
    }

    public final String enter(String method) {
        return getClassName() + "." + method + " " + INTO;
    }

    public final String exit(String method) {
        return getClassName() + "." + OUTOF + " " + method;
    }

    public final String enterExit(String method) {
        return getClassName() + "." + OUTOF + method + INTO;
    }

    public final String passFail(String method, String test, String result) {
        return getClassName() + "." + method + ": " + result + " " + test
                + " test";
    }

    public final String pass(String method, String test) {
        return passFail(method, test, "passed");
    }

    public final String fail(String method, String test) {
        return passFail(method, test, "failed");
    }

    public final String format(String method, String msg) {
        return getClassName() + "." + method + ": " + msg;
    }

    public final String format(String method, String msg, String name) {
        return format(method, msg, name, null);
    }

    public final String format(String method,
                               String msg,
                               String name,
                               String value) {
        return getClassName() + "." + method + ": "
                + (msg == null ? "" : msg + " ") + name + "=="
                + (value == null ? "" : value);
    }

    public final void showThrowable(Throwable th, Log log, String msg) {
        if (log.isErrorEnabled()) {
            if (msg != null) {
                log.error(msg);
            }
            log.error(th);
            log.error(th.getMessage());
            if (th.getCause() != null) {
                log.error(th.getCause().getMessage());
            }
            th.printStackTrace();
        }
    }

    public static final boolean booleanValue(String string) throws Exception {
        if (Boolean.TRUE.toString().equals(string)
                || Boolean.FALSE.toString().equals(string)) {
            return (new Boolean(string)).booleanValue();
        } else {
            throw new Exception("does not represent a boolean");
        }
    }

    protected boolean initErrors = false;

    protected void initThisSubclass(String key, String value) {
        log.debug("AFB.iTS");
        String method = "initThisSubclass() ";
        if (log.isDebugEnabled()) {
            log.debug(enter(method));
        }
        initErrors = true;
        if (log.isErrorEnabled()) {
            log.error(format(method, "unknown parameter", key, value));
        }
        if (log.isDebugEnabled()) {
            log.debug(exit(method));
        }
    }

}
