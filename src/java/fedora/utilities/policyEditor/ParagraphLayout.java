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

/*
 * Copyright (C) Jerry Huxtable 1998-2001. All rights reserved.
 */

package fedora.utilities.policyEditor;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

public class ParagraphLayout
        extends ConstraintLayout {

    public final static int TYPE_MASK = 0x03;

    public final static int STRETCH_H_MASK = 0x04;

    public final static int STRETCH_V_MASK = 0x08;

    public final static int NEW_PARAGRAPH_VALUE = 1;

    public final static int NEW_PARAGRAPH_TOP_VALUE = 2;

    public final static int NEW_LINE_VALUE = 3;

    public final static Integer NEW_PARAGRAPH = new Integer(0x01);

    public final static Integer NEW_PARAGRAPH_TOP = new Integer(0x02);

    public final static Integer NEW_LINE = new Integer(0x03);

    public final static Integer STRETCH_H = new Integer(0x04);

    public final static Integer STRETCH_V = new Integer(0x08);

    public final static Integer STRETCH_HV = new Integer(0x0c);

    public final static Integer NEW_LINE_STRETCH_H = new Integer(0x07);

    public final static Integer NEW_LINE_STRETCH_V = new Integer(0x0b);

    public final static Integer NEW_LINE_STRETCH_HV = new Integer(0x0f);

    protected int hGapMajor, vGapMajor;

    protected int hGapMinor, vGapMinor;

    protected int rows;

    protected int colWidth1;

    protected int colWidth2;

    public ParagraphLayout() {
        this(10, 10, 12, 11, 4, 4);
    }

    public ParagraphLayout(int hMargin,
                           int vMargin,
                           int hGapMajor,
                           int vGapMajor,
                           int hGapMinor,
                           int vGapMinor) {
        this.hMargin = hMargin;
        this.vMargin = vMargin;
        this.hGapMajor = hGapMajor;
        this.vGapMajor = vGapMajor;
        this.hGapMinor = hGapMinor;
        this.vGapMinor = vGapMinor;
    }

    @Override
    public void measureLayout(Container target, Dimension dimension, int type) {
        int count = target.getComponentCount();
        if (count > 0) {
            Insets insets = target.getInsets();
            Dimension size = target.getSize();
            int y = 0;
            int rowHeight = 0;
            int colWidth = 0;
            boolean lastWasParagraph = false;

            Dimension[] sizes = new Dimension[count];

            // First pass: work out the column widths and row heights
            for (int i = 0; i < count; i++) {
                Component c = target.getComponent(i);
                if (includeComponent(c)) {
                    Dimension d = getComponentSize(c, type);
                    int w = d.width;
                    int h = d.height;
                    sizes[i] = d;
                    Integer n = (Integer) getConstraint(c);

                    if (i == 0 || n == NEW_PARAGRAPH || n == NEW_PARAGRAPH_TOP) {
                        if (i != 0) {
                            y += rowHeight + vGapMajor;
                        }
                        colWidth1 = Math.max(colWidth1, w);
                        colWidth = 0;
                        rowHeight = 0;
                        lastWasParagraph = true;
                    } else if (n == NEW_LINE || lastWasParagraph) {
                        if (!lastWasParagraph && i != 0) {
                            y += rowHeight + vGapMinor;
                        }
                        colWidth = w;
                        colWidth2 = Math.max(colWidth2, colWidth);
                        if (!lastWasParagraph) {
                            rowHeight = 0;
                        }
                        lastWasParagraph = false;
                    } else {
                        colWidth += w + hGapMinor;
                        colWidth2 = Math.max(colWidth2, colWidth);
                        lastWasParagraph = false;
                    }
                    rowHeight = Math.max(h, rowHeight);
                }
            }

            // Second pass: actually lay out the components
            if (dimension != null) {
                dimension.width = colWidth1 + hGapMajor + colWidth2;
                dimension.height = y + rowHeight;
            } else {
                int spareHeight =
                        size.height - (y + rowHeight) - insets.top
                                - insets.bottom - 2 * vMargin;
                y = 0;
                lastWasParagraph = false;
                int start = 0;
                int rowWidth = 0;
                Integer paragraphType = NEW_PARAGRAPH;
                boolean stretchV = false;

                boolean firstLine = true;
                for (int i = 0; i < count; i++) {
                    Component c = target.getComponent(i);
                    if (includeComponent(c)) {
                        Dimension d = sizes[i];
                        int h = d.height;
                        Integer n = (Integer) getConstraint(c);
                        int nv = n != null ? n.intValue() : 0;

                        if (i == 0 || n == NEW_PARAGRAPH
                                || n == NEW_PARAGRAPH_TOP) {
                            if (i != 0) {
                                layoutRow(target,
                                          sizes,
                                          start,
                                          i - 1,
                                          y,
                                          rowWidth,
                                          rowHeight,
                                          firstLine,
                                          type,
                                          paragraphType);
                            }
                            stretchV = false;
                            paragraphType = n;
                            start = i;
                            firstLine = true;
                            if (i != 0) {
                                y += rowHeight + vGapMajor;
                            }
                            rowHeight = 0;
                            rowWidth = colWidth1 + hGapMajor - hGapMinor;
                            lastWasParagraph = true;
                        } else if (n == NEW_LINE || lastWasParagraph) {
                            if (!lastWasParagraph) {
                                layoutRow(target,
                                          sizes,
                                          start,
                                          count - 1,
                                          y,
                                          rowWidth,
                                          rowHeight,
                                          firstLine,
                                          type,
                                          paragraphType);
                                stretchV = false;
                                start = i;
                                firstLine = false;
                                y += rowHeight + vGapMinor;
                                rowHeight = 0;
                            }
                            rowWidth += sizes[i].width + hGapMinor;
                            lastWasParagraph = false;
                        } else {
                            rowWidth += sizes[i].width + hGapMinor;
                            lastWasParagraph = false;
                        }
                        if ((nv & STRETCH_V_MASK) != 0 && !stretchV) {
                            stretchV = true;
                            h += spareHeight;
                        }
                        rowHeight = Math.max(h, rowHeight);
                    }
                }
                layoutRow(target,
                          sizes,
                          start,
                          count - 1,
                          y,
                          rowWidth,
                          rowHeight,
                          firstLine,
                          type,
                          paragraphType);
            }
        }

    }

    protected void layoutRow(Container target,
                             Dimension[] sizes,
                             int start,
                             int end,
                             int y,
                             int rowWidth,
                             int rowHeight,
                             boolean paragraph,
                             int type,
                             Integer paragraphType) {
        int x = 0;
        Insets insets = target.getInsets();
        Dimension size = target.getSize();
        int spareWidth =
                size.width - rowWidth - insets.left - insets.right - 2
                        * hMargin;

        for (int i = start; i <= end; i++) {
            Component c = target.getComponent(i);
            if (includeComponent(c)) {
                Integer n = (Integer) getConstraint(c);
                int nv = n != null ? n.intValue() : 0;
                Dimension d = sizes[i];
                int w = d.width;
                int h = d.height;

                if ((nv & STRETCH_H_MASK) != 0) {
                    w += spareWidth;
                    Dimension max = getComponentSize(c, MAXIMUM);
                    Dimension min = getComponentSize(c, MINIMUM);
                    w = Math.max(min.width, Math.min(max.width, w));
                }
                if ((nv & STRETCH_V_MASK) != 0) {
                    h = rowHeight;
                    Dimension max = getComponentSize(c, MAXIMUM);
                    Dimension min = getComponentSize(c, MINIMUM);
                    h = Math.max(min.height, Math.min(max.height, h));
                }

                if (i == start) {
                    if (paragraph) {
                        x = colWidth1 - w;
                    } else {
                        x = colWidth1 + hGapMajor;
                    }
                } else if (paragraph && i == start + 1) {
                    x = colWidth1 + hGapMajor;
                }
                int yOffset =
                        paragraphType == NEW_PARAGRAPH_TOP ? 0
                                : (rowHeight - h) / 2;
                c.setBounds(insets.left + hMargin + x, insets.top + vMargin + y
                        + yOffset, w, h);
                x += w + hGapMinor;
            }
        }
    }

}
