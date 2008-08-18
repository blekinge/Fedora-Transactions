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
package fedora.server.messaging;

import fedora.server.errors.MessagingException;

/**
 * The Messaging subsystem interface.
 *
 * @author Edwin Shin
 * @version $Id$
 */
public interface Messaging {

    enum MessageType {
        apimUpdate, apimAccess;
    }
    
    /**
     * Send the <code>FedoraMessage</code> to the specified destination.
     * 
     * @param destName The destination of the message.
     * @param message The message to send.
     * @throws MessagingException
     */
	public void send(String destName, FedoraMessage message) throws MessagingException;
	
	/**
	 * Send a message representing the <code>FedoraMethod</code>.
	 * The message representation and destination(s) are determined by the 
	 * implementing class.
	 *  
	 * @param method The method to send.
	 * @throws MessagingException
	 */
	public void send(FedoraMethod method) throws MessagingException;
	
	/**
	 * Shutdown and/or close any resources and/or connections.
	 * 
	 * @throws MessagingException
	 */
	public void close() throws MessagingException;
}
