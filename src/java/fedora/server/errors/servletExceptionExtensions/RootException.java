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

package fedora.server.errors.servletExceptionExtensions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import fedora.server.errors.authorization.AuthzDeniedException;
import fedora.server.errors.authorization.AuthzException;
import fedora.server.errors.authorization.AuthzOperationalException;
import fedora.server.errors.authorization.AuthzPermittedException;

/**
 * Root of HTTP servlet exceptions.
 * 
 * @author Bill Niebel
 */
public abstract class RootException
        extends ServletException {

    private final HttpServletRequest request;

    private final String action;

    private final String detail;

    private final String[] details;

    public final String getAction() {
        return action;
    }

    public final String getDetail() {
        return detail;
    }

    public final String[] getDetails() {
        return details;
    }

    public final HttpServletRequest getRequest() {
        return request;
    }

    public RootException(HttpServletRequest request,
                         String action,
                         String detail,
                         String[] details) {
        super();
        this.action = action;
        this.detail = detail;
        this.details = details;
        this.request = request;
    }

    public RootException(String message,
                         HttpServletRequest request,
                         String action,
                         String detail,
                         String[] details) {
        super(message);
        this.request = request;
        this.action = action;
        this.detail = detail;
        this.details = details;
    }

    public RootException(String message,
                         Throwable cause,
                         HttpServletRequest request,
                         String action,
                         String detail,
                         String[] details) {
        super(message, cause);
        this.request = request;
        this.action = action;
        this.detail = detail;
        this.details = details;
    }

    public static final ServletException getServletException(AuthzException ae,
                                                             HttpServletRequest request,
                                                             String action,
                                                             String[] details) {
        if (ae instanceof AuthzOperationalException) {
            return new InternalError500Exception(request,
                                                 action,
                                                 "Internal Error during authorization",
                                                 details);
        } else if (ae instanceof AuthzDeniedException) {
            return new Forbidden403Exception(request,
                                             action,
                                             AuthzDeniedException.BRIEF_DESC,
                                             details);
        } else if (ae instanceof AuthzPermittedException) {
            return new Continue100Exception(request,
                                            action,
                                            AuthzPermittedException.BRIEF_DESC,
                                            details);
        } else {
            //AuthzException has only the above three extensions, so code shouldn't reach here
            return new InternalError500Exception(request,
                                                 action,
                                                 "bug revealed in throwServletException(ae,...)",
                                                 new String[0]);
        }
    }

}
