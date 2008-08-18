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

package fedora.server.validation;

/**
 * Constants for validating Fedora objects and their components. These constants
 * are also expressed in the schematron rules. They are only repeated here so
 * that per-field validation can occur outside the context of schematron
 * validation.
 * 
 * @author Chris Wilper
 */
public interface ValidationConstants {

    /** Characters a datastream ID can never have. */
    public static final char[] DATASTREAM_ID_BADCHARS = new char[] {'+', ':'};

    /** Maximum characters a datastream ID can have. */
    public static final int DATASTREAM_ID_MAXLEN = 64;

    /** Maximum characters a datastream label can have. */
    public static final int DATASTREAM_LABEL_MAXLEN = 255;

    /** Maximum characters a disseminator ID can have. */
    public static final int DISSEMINATOR_ID_MAXLEN = 64;

    /** Characters a disseminator ID can never have. */
    public static final char[] DISSEMINATOR_ID_BADCHARS = new char[] {'+', ':'};

    /** Maximum characters a disseminator label can have. */
    public static final int DISSEMINATOR_LABEL_MAXLEN = 255;

    /** Maximum characters an object label can have. */
    public static final int OBJECT_LABEL_MAXLEN = 255;

}
