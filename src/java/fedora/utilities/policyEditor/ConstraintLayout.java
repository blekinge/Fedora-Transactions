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

package fedora.utilities.policyEditor;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

import java.util.Hashtable;

/**
 * A base class for layouts which simplifies the business of building new
 * layouts with constraints.
 */
public class ConstraintLayout
        implements LayoutManager2 {

    protected final static int PREFERRED = 0;

    protected final static int MINIMUM = 1;

    protected final static int MAXIMUM = 2;

    protected int hMargin = 0;

    protected int vMargin = 0;

    private Hashtable constraints;

    protected boolean includeInvisible = false;

    public void addLayoutComponent(String constraint, Component c) {
        setConstraint(c, constraint);
    }

    public void addLayoutComponent(Component c, Object constraint) {
        setConstraint(c, constraint);
    }

    public void removeLayoutComponent(Component c) {
        if (constraints != null) {
            constraints.remove(c);
        }
    }

    public void setConstraint(Component c, Object constraint) {
        if (constraint != null) {
            if (constraints == null) {
                constraints = new Hashtable();
            }
            constraints.put(c, constraint);
        } else if (constraints != null) {
            constraints.remove(c);
        }
    }

    public Object getConstraint(Component c) {
        if (constraints != null) {
            return constraints.get(c);
        }
        return null;
    }

    public void setIncludeInvisible(boolean includeInvisible) {
        this.includeInvisible = includeInvisible;
    }

    public boolean getIncludeInvisible() {
        return includeInvisible;
    }

    protected boolean includeComponent(Component c) {
        return includeInvisible || c.isVisible();
    }

    public Dimension minimumLayoutSize(Container target) {
        return calcLayoutSize(target, MINIMUM);
    }

    public Dimension maximumLayoutSize(Container target) {
        return calcLayoutSize(target, MAXIMUM);
    }

    public Dimension preferredLayoutSize(Container target) {
        return calcLayoutSize(target, PREFERRED);
    }

    public Dimension calcLayoutSize(Container target, int type) {
        Dimension dim = new Dimension(0, 0);
        measureLayout(target, dim, type);
        Insets insets = target.getInsets();
        dim.width += insets.left + insets.right + 2 * hMargin;
        dim.height += insets.top + insets.bottom + 2 * vMargin;
        return dim;
    }

    public void invalidateLayout(Container target) {
    }

    public float getLayoutAlignmentX(Container parent) {
        return 0.5f;
    }

    public float getLayoutAlignmentY(Container parent) {
        return 0.5f;
    }

    public void layoutContainer(Container target) {
        measureLayout(target, null, PREFERRED);
    }

    public void measureLayout(Container target, Dimension dimension, int type) {
    }

    protected Dimension getComponentSize(Component c, int type) {
        if (type == MINIMUM) {
            return c.getMinimumSize();
        }
        if (type == MAXIMUM) {
            return c.getMaximumSize();
        }
        return c.getPreferredSize();
    }
}
