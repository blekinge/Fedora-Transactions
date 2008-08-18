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

import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.regex.Pattern;

/**
 * @author Robert Haschart
 */
public class XMLPrintWriter
        extends PrintWriter {

    private int curIndentLevel = 0;

    private int spacesForIndent;

    private int spacesForHangingIndent;

    public static String sep = "";

    private static String spaces =
            "                                                        ";

    private static Pattern XMLDec;

    private static Pattern open;

    private static Pattern open2;

    private static Pattern close;

    private static Pattern close2;

    private static Pattern close3;

    private static Pattern openNClose1;

    static {
        sep = System.getProperty("line.separator");
        XMLDec = Pattern.compile("<\\?xml[^>?]*\\?>");
        open = Pattern.compile("<([^/][^ >]*) ?[^>]*?>");
        open2 = Pattern.compile("<[^ >]*?[^>]*");
        close = Pattern.compile("</[^<>]*?>");
        close2 = Pattern.compile("[^<>]*?/>");
        close3 = Pattern.compile("[^<>]*?>");
        openNClose1 = Pattern.compile("<([^ >]*)[^>]*?/>");
    }

    public XMLPrintWriter(FileWriter out) {
        super(out);
        spacesForIndent = 4;
        spacesForHangingIndent = 8;
    }

    public XMLPrintWriter(FileWriter out,
                          int spacesForIndent,
                          int spacesForHangingIndent) {
        super(out);
        this.spacesForIndent = spacesForIndent;
        this.spacesForHangingIndent = spacesForHangingIndent;

    }

    public void printlnWithIndent(String outputLine) {
        String lines[] = outputLine.split(sep);
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();
            //            System.out.println("{"+lines[i]+"}");
            if (lines[i].length() == 0) {
                continue;
            }
            int preAdjust = preAdjustIndentLevel(lines[i]);
            int postAdjust = postAdjustIndentLevel(lines[i]);
            if (curIndentLevel + preAdjust >= 0) {
                curIndentLevel += preAdjust;
            }
            //            super.println(spaces.substring(0, curIndentLevel)+
            //                    "["+preAdjust+"]"+lines[i]+
            //                    "["+postAdjust+"]");
            super.println(spaces.substring(0, curIndentLevel) + lines[i]);
            if (curIndentLevel + postAdjust >= 0) {
                curIndentLevel += postAdjust;
            }
        }
    }

    private int preAdjustIndentLevel(String line) {
        if (XMLDec.matcher(line).find()) {
            return 0;
        }
        if (openNClose1.matcher(line).find()) {
            return 0;
        }
        if (open.matcher(line).find() && close.matcher(line).find()) {
            return 0;
        }
        if (close.matcher(line).find()) {
            return -spacesForIndent;
        }
        if (close2.matcher(line).find()) {
            return 0;
        }
        if (close3.matcher(line).matches()) {
            return 0;
        }
        if (open.matcher(line).find()) {
            return 0;
        }
        if (open2.matcher(line).find()) {
            return 0;
        }
        return 0;
    }

    private int postAdjustIndentLevel(String line) {
        if (XMLDec.matcher(line).find()) {
            return 0;
        }
        if (openNClose1.matcher(line).find()) {
            return 0;
        }
        if (open.matcher(line).find() && close.matcher(line).find()) {
            return 0;
        }
        if (close.matcher(line).find()) {
            return 0;
        }
        if (close2.matcher(line).find()) {
            return -spacesForHangingIndent;
        }
        if (close3.matcher(line).matches()) {
            return spacesForIndent - spacesForHangingIndent;
        }
        if (open.matcher(line).find()) {
            return spacesForIndent;
        }
        if (open2.matcher(line).find()) {
            return spacesForHangingIndent;
        }
        return 0;
    }

}
