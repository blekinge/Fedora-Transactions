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

package fedora.server.search;

import fedora.server.errors.InvalidOperatorException;

/**
 * The {@link Operator}s that can be used in a {@link FieldSearchQuery}.
 * 
 * @author Jim Blake
 */
public enum Operator {
    EQUALS("=", "eq"), CONTAINS("~", "has"), GREATER_THAN(">", "gt"),
    GREATER_OR_EQUAL(">=", "ge"), LESS_THAN("<", "lt"), LESS_OR_EQUAL("<=",
            "le");

    private final String symbol;

    private final String abbreviation;

    private Operator(String symbol, String abbreviation) {
        this.symbol = symbol;
        this.abbreviation = abbreviation;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public static Operator fromAbbreviation(String abbreviation)
            throws InvalidOperatorException {
        for (Operator operator : Operator.values()) {
            if (operator.abbreviation.equals(abbreviation)) {
                return operator;
            }
        }
        throw new InvalidOperatorException("Operator, '" + abbreviation
                + "' does not match one of eq, has, gt, ge, lt, or le.");

    }

    @Override
    public String toString() {
        return name() + "[" + symbol + ", " + abbreviation + "]";
    }

}
