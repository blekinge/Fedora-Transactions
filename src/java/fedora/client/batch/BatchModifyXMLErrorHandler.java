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

package fedora.client.batch;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Ross Wayland
 */
public class BatchModifyXMLErrorHandler
        implements ErrorHandler {

    public BatchModifyXMLErrorHandler() {
    }

    public void warning(SAXParseException e) throws SAXException {
        System.err.print("BatchModifyXMLErrorHandler detected SAX WARNING: ");
        printPubID(e);
        printMsg(e);
    }

    public void error(SAXParseException e) throws SAXException {
        System.err
                .print("BatchModifyXMLErrorHandler detected SAX ERROR.  Re-throwing SAXException.");
        throw new SAXException(formatParseExceptionMsg(e));
    }

    public void fatalError(SAXParseException e) throws SAXException {
        System.err
                .print("BatchModifyXMLErrorHandler detected SAX FATAL ERROR.  Re-throwing SAXException.");
        throw new SAXException(formatParseExceptionMsg(e));
    }

    private void printPubID(SAXParseException e) {
        if (e.getPublicId() != null) {
            System.err.print(e.getPublicId() + " ");
        }
        if (e.getLineNumber() != -1) {
            System.err.print("line: " + e.getLineNumber() + " ");
        }
    }

    private void printMsg(SAXParseException e) {
        System.err.println(e.getClass().getName()
                + " - "
                + (e.getMessage() == null ? "(no detail provided)" : e
                        .getMessage()));
    }

    private String formatParseExceptionMsg(SAXParseException spe) {
        String systemId = spe.getSystemId();
        if (systemId == null) {
            systemId = "null";
        }
        String info =
                "URI=" + systemId + " Line=" + spe.getLineNumber() + ": "
                        + spe.getMessage();
        return info;
    }
}
