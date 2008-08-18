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

/**
 * <p>
 * These classes are used to validate objects against their content models, 
 * and to validate the content models for internal consistency.
 * </p>
 * <p>
 * This package contains the basic Validator framework, with core classes and interfaces.
 * </p>
 * <p>
 * <code>fedora.client.utility.validate.process</code> contains a client-side {@link ValidatorProcess} 
 * that parses the command line arguments, queries Fedora for the requested objects, validates them. 
 * It also has an implementation of {@link ValidatorResult} that uses Log4J to present and 
 * control the output. 
 * </p>
 * <p>
 * <code>fedora.client.utility.validate.remote</code> contains an implementation of {@link ObjectSource} 
 * that can be used to access a remote instance of Fedora, with helper classes. 
 * </p>
 * <p>
 * </p>
 */

package fedora.client.utility.validate;

