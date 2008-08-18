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

package fedora.client.utility.validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fedora.client.utility.validate.types.ObjectInfo;

/**
 * <p>
 * The accumulated result of validating on a digital object. This result
 * contains a reference to the object and may also contain (0 or more)
 * "notations" that describe the results of the validation.
 * </p>
 * <p>
 * Each notation has a {@link Level} associated with it, and if any of these
 * levels are {@link Level#ERROR ERROR}, the validation is considered to have
 * failed.
 * </p>
 * 
 * @author Jim Blake
 */
public class ValidationResult {

    public enum Level {
        /** Information of interest - not a problem. */
        INFO,
        /** May be a problem - subject to interpretation. */
        WARN,
        /** Validation failed - object is not valid. */
        ERROR
    };

    private final ObjectInfo object;

    private final List<ValidationResultNotation> notes =
            new ArrayList<ValidationResultNotation>();

    public ValidationResult(ObjectInfo object) {
        if (object == null) {
            throw new IllegalArgumentException("object may not be null.");
        }
        this.object = object;
    }

    public void addNote(ValidationResultNotation note) {
        notes.add(note);
    }

    public ObjectInfo getObject() {
        return object;
    }

    public Collection<ValidationResultNotation> getNotes() {
        return new ArrayList<ValidationResultNotation>(notes);
    }

    /**
     * What's the highest severity level of any of the notations on this result?
     */
    public Level getSeverityLevel() {
        Level severity = Level.INFO;
        for (ValidationResultNotation note : notes) {
            Level noteLevel = note.getLevel();
            if (noteLevel.compareTo(severity) > 0) {
                severity = noteLevel;
            }
        }
        return severity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }
        ValidationResult that = (ValidationResult) obj;
        return object.equals(that.object) && notes.equals(that.notes);
    }

    @Override
    public int hashCode() {
        return object.hashCode() ^ notes.hashCode();
    }

    @Override
    public String toString() {
        return "pid='" + object.getPid() + "', " + " Notes=" + notes;
    }
}
