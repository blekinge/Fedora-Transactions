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

import fedora.client.utility.validate.ValidationResult.Level;

/**
 * A note that can be attached to a {@link ValidationResult}. Also, a
 * collection of static methods for creating such notes.
 * 
 * @author Jim Blake
 */
public class ValidationResultNotation {

    public static ValidationResultNotation objectNotFound(String pid) {
        return new ValidationResultNotation(Level.ERROR,
                                            "ObjectNotFound",
                                            "Object not found for pid '" + pid
                                                    + "'");
    }

    public static ValidationResultNotation noContentModel() {
        return new ValidationResultNotation(Level.INFO,
                                            "NoContentModel",
                                            "No content model.");
    }

    public static ValidationResultNotation unrecognizedContentModelUri(String uri) {
        return new ValidationResultNotation(Level.INFO,
                                            "UnrecognizedContentModelUri",
                                            "Content model URI is not recognized "
                                                    + "as an object PID: '"
                                                    + uri + "'");
    }

    public static ValidationResultNotation contentModelNotFound(String pid) {
        return new ValidationResultNotation(Level.INFO,
                                            "ContentModelNotFound",
                                            "Content model was not found, PID='"
                                                    + pid + "'");
    }

    public static ValidationResultNotation errorFetchingContentModel(String pid,
                                                                     ObjectSourceException e) {
        return new ValidationResultNotation(Level.ERROR,
                                            "ErrorFetchingContentModel",
                                            ("Attempt to fetch Content model '"
                                                    + pid
                                                    + "' produced this error '"
                                                    + e + "'"));
    }

    public static ValidationResultNotation contentModelNotValid(InvalidContentModelException e) {
        return new ValidationResultNotation(Level.ERROR,
                                            "ContentModelNotValid",
                                            "Attempt to build Content Model '"
                                                    + e.getContentModelPid()
                                                    + "' produced this error '"
                                                    + e + "'");
    }

    public static ValidationResultNotation noMatchingDatastreamId(String contentModelPid,
                                                                  String dsId) {
        return new ValidationResultNotation(Level.ERROR,
                                            "NoMatchingDatastreamId",
                                            "Object has no datastream '"
                                                    + dsId
                                                    + "', required by content model '"
                                                    + contentModelPid + "'");
    }

    public static ValidationResultNotation datastreamDoesNotMatchForms(String contentModelPid,
                                                                       String dsId) {
        return new ValidationResultNotation(Level.ERROR,
                                            "DatastreamDoesNotMatchForms",
                                            "Datastream '"
                                                    + dsId
                                                    + "' doesn't match any form "
                                                    + "in the corresponding type "
                                                    + "model of content model '"
                                                    + contentModelPid + "'.");
    }

    /**
     * The severity of the notation. Generally {@link Level#INFO} is not a
     * problem, {@link Level#ERROR} means that the object is invalid, and
     * {@link Level#WARN} is subject to interpretation.
     */
    private final Level level;

    /**
     * The "type" of the notation. Even though this is not an enumeration, it
     * should be chosen from a small number of values. This encourages the
     * ability to mask unwanted errors by category.
     */
    private final String category;

    /**
     * The text of the notation.
     */
    private final String message;

    /**
     * By restricting instance creation to the various factory methods, we know
     * that there are a limited number of categories.
     */
    private ValidationResultNotation(Level level,
                                     String category,
                                     String message) {
        if (level == null) {
            throw new IllegalArgumentException("level may not be null.");
        }
        this.level = level;

        this.message = message == null ? "" : message;
        this.category = category == null ? "" : category;
    }

    public Level getLevel() {
        return level;
    }

    public String getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
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
        ValidationResultNotation that = (ValidationResultNotation) obj;

        return equivalent(level, that.level)
                && equivalent(category, that.category)
                && equivalent(message, that.message);
    }

    private boolean equivalent(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    @Override
    public int hashCode() {
        return hashIt(level) ^ hashIt(category) ^ hashIt(message);
    }

    private int hashIt(Object o) {
        return o == null ? 0 : o.hashCode();
    }

    @Override
    public String toString() {
        return level + " [" + category + "] " + message;
    }

}
