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

package fedora.server.access.defaultdisseminator;

import fedora.server.errors.ServerException;
import fedora.server.storage.types.MIMETypedStream;

/**
 * Defines the methods of the default service definition that is associated
 * with every Fedora Object.
 * 
 * @author Sandy Payette
 */
public interface DefaultDisseminator {

    /**
     * Returns an HTML rendering of the object profile which contains key
     * metadata from the object, plus URLs for the object's Dissemination Index
     * and Item Index. The data is returned as HTML in a presentation-oriented
     * format. This is accomplished by doing an XSLT transform on the XML that
     * is obtained from getObjectProfile in API-A.
     * 
     * @return a MIMETypedStream that is an HTML rendering of the object
     *         profile.
     * @throws ServerException
     */
    public MIMETypedStream viewObjectProfile() throws ServerException;

    /**
     * Returns an HTML rendering of the Dissemination Index for the object. The
     * Dissemination Index is a list of method definitions that represent all
     * disseminations possible on the object. The Dissemination Index is
     * returned as HTML in a presentation-oriented format. This is accomplished
     * by doing an XSLT transform on the XML that is obtained from listMethods
     * in API-A.
     * 
     * @return a MIMETypedStream that is an HTML rendering of the Dissemination
     *         Index for the object.
     * @throws ServerException
     */
    public MIMETypedStream viewMethodIndex() throws ServerException;

    /**
     * Returns an HTML rendering of the Item Index for the object. The Item
     * Index is a list of all datastreams in the object. The datastream items
     * can be data or metadata. The Item Index is returned as HTML in a
     * presentation-oriented format. This is accomplished by doing an XSLT
     * transform on the XML that is obtained from listDatastreams in API-A.
     * 
     * @return a MIMETypedStream that is an HTML rendering of the Item Index for
     *         the object.
     * @throws ServerException
     */
    public MIMETypedStream viewItemIndex() throws ServerException;

    /**
     * Returns the Dublin Core record for the object, if one exists. The record
     * is returned as HTML in a presentation-oriented format.
     * 
     * @return a MIMETypedStream that is an HTML rendering of the Dublin Core
     *         record for the object.
     * @throws ServerException
     */
    public MIMETypedStream viewDublinCore() throws ServerException;

}
