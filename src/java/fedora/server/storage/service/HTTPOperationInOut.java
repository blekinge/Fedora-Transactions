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

package fedora.server.storage.service;

/**
 * A data structure for holding input and output specification for WSDL 
 * HTTP operation binding.
 * 
 * @author Sandy Payette
 */
public class HTTPOperationInOut {

    public static final String MIME_BINDING_TYPE = "MIME";

    public static final String URL_REPLACE_BINDING_TYPE = "URL_REPLACE";

    /**
     * ioBindingType: At this time, Fedora's WSDLParser can deal with: 1)
     * mime:content (ioBindingType = MIME_BINDING_TYPE) 2) mime:mimeXml
     * (ioBindingType = MIME_BINDING_TYPE) 3) http:urlReplacement (ioBindingType =
     * URL_REPLACE_BINDING_TYPE) Not supported by Fedora's WSDLParser at this
     * time are: 1) mime:multipartRelated 2) http:urlEncoded
     */
    public String ioBindingType = null;

    /**
     * ioMIMEContent: Applies only when ioBindingType is MIME_BINDING_TYPE.
     * Defines the MIME type(s) of the content that is used as input or output
     * to an operation. Multiple MIME types in the array indicate alternative
     * formats. May may carry the name of the Message part that it pertains to,
     * although this can typically be inferred. NOTE: When ioBindingType =
     * URL_REPLACE_BINDING_TYPE this will be set to an array of zero length.
     */
    public MIMEContent[] ioMIMEContent;

}
