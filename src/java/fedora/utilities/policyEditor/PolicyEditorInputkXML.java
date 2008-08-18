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

/**
 * @author Robert Haschart
 */
package fedora.utilities.policyEditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashMap;
import java.util.Map;

import org.kxml2.io.KXmlParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class PolicyEditorInputkXML {

    static boolean verbose = true;

    static XmlPullParser xr = null;

    static FedoraNode fedoraNode = null;

    static FedoraNode fedoraRoot = null;

    static Object semaphore = null;

    static {
        init();
    }

    private PolicyEditorInputkXML() {
    }

    public static void init() {
        xr = new KXmlParser();
        semaphore = new Object();
        fedoraNode = null;
        fedoraRoot = null;
    }

    static FedoraNode readInputStream(InputStream stream, String name) {
        verbose = false;
        System.out.println("Opening metadata url " + name);
        try {
            synchronized (semaphore) {
                xr.setInput(new InputStreamReader(stream));
                parseMetadata(xr);
                return fedoraRoot;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open URL: " + name);
        } catch (IOException ioe) {
            System.out.println("Problems reading file: " + name);
        } catch (XmlPullParserException ioe) {
            System.out.println("Error parsing file: " + name);
        }
        fedoraRoot = null;
        return null;
    }

    static FedoraNode readURLbyName(String urlName) {
        try {
            //		System.out.println("metadataURL is: " + urlName);
            URL url = new URL(urlName);
            return readInputStream(url.openStream(), url.toString());
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException ioe) {
            System.out.println("Problems Opening URL: " + urlName);
            return null;
        }

    }

    static FedoraNode readResourcebyName(String resourceName) {
        System.out.println("resource URL is: " + resourceName);
        URL url = ClassLoader.getSystemClassLoader().getResource(resourceName);
        if (url == null) {
            System.out.println("Resource not found: " + resourceName);
            return null;
        }
        try {
            fedoraNode = null;
            fedoraRoot = null;
            FedoraNode.model = null;
            return readInputStream(url.openStream(), url.toString());
        } catch (IOException ioe) {
            System.out.println("Problems Opening Resource: " + resourceName);
            return null;
        }

    }

    static void readFile(File file) {
        try {
            readInputStream(new FileInputStream(file), file.toString());
        } catch (FileNotFoundException e) {
            System.out.println("Problems Opening File: " + file.toString());
            // return(null);
        }
    }

    /*
     * static boolean readDataBuffer(StringBuffer buffer, FedoraNode list) {
     * verbose = false; StringReader xmlBuffer = new StringReader(new
     * String(buffer)); try { xr.setInput(xmlBuffer); fedoraNode = list;
     * parseMetadata(xr); return(true); } catch (IOException ioe) {
     * System.out.println("Problems reading from InputStream
     * :"+ioe.getMessage()); } catch (XmlPullParserException ioe) {
     * System.out.println("Problems parsing XML ");
     * System.out.println(ioe.getMessage()); } fedoraNode = null; return(false); }
     */
    protected static void parseMetadata(XmlPullParser xr)
            throws XmlPullParserException, IOException {
        int eventType = xr.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
            } else if (eventType == XmlPullParser.END_DOCUMENT) {
            } else if (eventType == XmlPullParser.START_TAG) {
                if (xr.getName().equals("resource")) {
                    startResourceElement(xr);
                }
                if (xr.getName().equals("template")) {
                    startGroupTemplateElement(xr);
                }
                if (xr.getName().equals("access")) {
                    startAccessElement(xr);
                }
                if (xr.getName().equals("ruledef")) {
                    startRuledefElement(xr);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (xr.getName().equals("resource")) {
                    endResourceElement(xr);
                }
                if (xr.getName().equals("template")) {
                    endGroupTemplateElement(xr);
                }
                if (xr.getName().equals("access")) {
                    endAccessElement(xr);
                }
                if (xr.getName().equals("ruledef")) {
                    endRuledefElement(xr);
                }
            } else if (eventType == XmlPullParser.TEXT) {
                characters(xr);
            }
            eventType = xr.next();
        }
    }

    protected static void startResourceElement(XmlPullParser xr) {
        String name = xr.getAttributeValue(null, "name");
        if (name == null) {
            name = xr.getName();
        }
        String shortname = xr.getAttributeValue(null, "shortname");
        String value = xr.getAttributeValue(null, "value");
        String value2 = xr.getAttributeValue(null, "value1");
        String action = htmlunescape(xr.getAttributeValue(null, "action"));
        String resource = htmlunescape(xr.getAttributeValue(null, "resource"));
        if (verbose) {
            System.out.println("start element: " + name);
        }
        Object parent = null;
        if (fedoraNode != null) {
            parent = fedoraNode;
        }
        fedoraNode = new FedoraNode(parent, name, shortname, action, resource);
        if (value != null) {
            if (value.equals(FedoraNode.seeParentShort) || value.equals("0")
                    || value.length() == 0) {
                fedoraNode.setValue(0, FedoraNode.seeParent);
            } else if (value.equals(FedoraNode.seeChildrenShort)
                    || value.equals("1")) {
                fedoraNode.setValue(0, FedoraNode.seeChildren);
            } else {
                fedoraNode.setValue(0, GroupRuleInfo
                        .findEntryByShortName(true, value));
            }
        }
        if (value2 != null) {
            if (value2.equals(FedoraNode.seeParentShort) || value2.equals("0")
                    || value2.length() == 0) {
                fedoraNode.setValue(1, FedoraNode.seeParent);
            } else if (value2.equals(FedoraNode.seeChildrenShort)
                    || value2.equals("1")) {
                fedoraNode.setValue(1, FedoraNode.seeChildren);
            } else {
                fedoraNode.setValue(1, GroupRuleInfo
                        .findEntryByShortName(false, value2));
            }
        }
        if (fedoraRoot == null) {
            fedoraRoot = fedoraNode;
        }
        if (parent != null) {
            ((FedoraNode) parent).children.add(fedoraNode);
        }
    }

    protected static void endResourceElement(XmlPullParser xr) {
        fedoraNode = (FedoraNode) fedoraNode.parent;
    }

    protected static void startGroupTemplateElement(XmlPullParser xr) {
        String name = xr.getAttributeValue(null, "name");
        String description = xr.getAttributeValue(null, "description");
        String rule = xr.getAttributeValue(null, "rule");
        String subject = xr.getAttributeValue(null, "subject");
        String condition = xr.getAttributeValue(null, "condition");
        String parms = xr.getAttributeValue(null, "parms");
        if (rule.equals("permit")) {
            GroupRuleInfo newRule =
                    new GroupRuleInfo(name,
                                      description,
                                      subject,
                                      condition,
                                      parms,
                                      true);
            GroupRuleInfo.permitTemplates.addElement(newRule);
        } else if (rule.equals("deny")) {
            GroupRuleInfo newRule =
                    new GroupRuleInfo(name,
                                      description,
                                      subject,
                                      condition,
                                      parms,
                                      false);
            GroupRuleInfo.denyTemplates.addElement(newRule);
        }

    }

    protected static void endGroupTemplateElement(XmlPullParser xr) {
    }

    protected static void startAccessElement(XmlPullParser xr) {
        String shortname = xr.getAttributeValue(null, "shortname");
        String value = xr.getAttributeValue(null, "value");
        String value2 = xr.getAttributeValue(null, "value1");
        fedoraNode =
                Utility.findNodeByShortName(PolicyEditor.mainWin.getRootNode(),
                                            shortname);
        if (fedoraNode == null) {
            return;
        }
        if (value != null) {
            if (value.equals(FedoraNode.seeParentShort) || value.equals("0")
                    || value.length() == 0) {
                fedoraNode.setValue(0, FedoraNode.seeParent);
            } else if (value.equals(FedoraNode.seeChildrenShort)
                    || value.equals("1")) {
                fedoraNode.setValue(0, FedoraNode.seeChildren);
            } else {
                fedoraNode.setValue(0, GroupRuleInfo
                        .findEntryByShortName(true, value));
            }
        }
        if (value2 != null) {
            if (value2.equals(FedoraNode.seeParentShort) || value2.equals("0")
                    || value2.length() == 0) {
                fedoraNode.setValue(1, FedoraNode.seeParent);
            } else if (value2.equals(FedoraNode.seeChildrenShort)
                    || value2.equals("1")) {
                fedoraNode.setValue(1, FedoraNode.seeChildren);
            } else {
                fedoraNode.setValue(1, GroupRuleInfo
                        .findEntryByShortName(false, value2));
            }
        }
    }

    protected static void endAccessElement(XmlPullParser xr) {
    }

    protected static void startRuledefElement(XmlPullParser xr) {
        String buildparm = xr.getAttributeValue(null, "buildparm");
        String buildparms[] = buildparm.split(",");
        String parms = xr.getAttributeValue(null, "parms");
        boolean accept = buildparms[0].equals("permit");
        if (buildparms[1].equals("Template")) {
            int templateNum = Integer.parseInt(buildparms[2]);
            GroupRuleInfo.buildFromTemplate(accept, templateNum, parms);
        } else if (buildparms[1].equals("Combo")) {
            int andOrOr =
                    buildparms[2].equals("and") ? GroupRuleInfo.AND
                            : GroupRuleInfo.OR;
            int rules[] = new int[buildparms.length - 3];
            for (int i = 0; i < buildparms.length - 3; i++) {
                rules[i] = Integer.parseInt(buildparms[i + 3]);
            }
            GroupRuleInfo.buildFromRules(accept, rules, andOrOr);
        }
    }

    protected static void endRuledefElement(XmlPullParser xr) {
    }

    protected static void characters(XmlPullParser xr) {
    }

    // see http://hotwired.lycos.com/webmonkey/reference/special_characters/
    static Object[][] entities = {
    // {"#39", new Integer(39)},       // ' - apostrophe
            {"#10", new Integer(10)}, // \n - carriage return
            {"quot", new Integer(34)}, // " - double-quote
            {"amp", new Integer(38)}, // & - ampersand
            //   {"lt", new Integer(60)},        // < - less-than
            //   {"gt", new Integer(62)},        // > - greater-than
            {"nbsp", new Integer(160)}, // non-breaking space
            {"copy", new Integer(169)}, // � - copyright
            {"reg", new Integer(174)}, // � - registered trademark
            {"Agrave", new Integer(192)}, // � - uppercase A, grave accent
            {"Aacute", new Integer(193)}, // � - uppercase A, acute accent
            {"Acirc", new Integer(194)}, // � - uppercase A, circumflex accent
            {"Atilde", new Integer(195)}, // � - uppercase A, tilde
            {"Auml", new Integer(196)}, // � - uppercase A, umlaut
            {"Aring", new Integer(197)}, // � - uppercase A, ring
            {"AElig", new Integer(198)}, // � - uppercase AE
            {"Ccedil", new Integer(199)}, // � - uppercase C, cedilla
            {"Egrave", new Integer(200)}, // � - uppercase E, grave accent
            {"Eacute", new Integer(201)}, // � - uppercase E, acute accent
            {"Ecirc", new Integer(202)}, // � - uppercase E, circumflex accent
            {"Euml", new Integer(203)}, // � - uppercase E, umlaut
            {"Igrave", new Integer(204)}, // � - uppercase I, grave accent
            {"Iacute", new Integer(205)}, // � - uppercase I, acute accent
            {"Icirc", new Integer(206)}, // � - uppercase I, circumflex accent
            {"Iuml", new Integer(207)}, // � - uppercase I, umlaut
            {"ETH", new Integer(208)}, // � - uppercase Eth, Icelandic
            {"Ntilde", new Integer(209)}, // � - uppercase N, tilde
            {"Ograve", new Integer(210)}, // � - uppercase O, grave accent
            {"Oacute", new Integer(211)}, // � - uppercase O, acute accent
            {"Ocirc", new Integer(212)}, // � - uppercase O, circumflex accent
            {"Otilde", new Integer(213)}, // � - uppercase O, tilde
            {"Ouml", new Integer(214)}, // � - uppercase O, umlaut
            {"Oslash", new Integer(216)}, // � - uppercase O, slash
            {"Ugrave", new Integer(217)}, // � - uppercase U, grave accent
            {"Uacute", new Integer(218)}, // � - uppercase U, acute accent
            {"Ucirc", new Integer(219)}, // � - uppercase U, circumflex accent
            {"Uuml", new Integer(220)}, // � - uppercase U, umlaut
            {"Yacute", new Integer(221)}, // � - uppercase Y, acute accent
            {"THORN", new Integer(222)}, // � - uppercase THORN, Icelandic
            {"szlig", new Integer(223)}, // � - lowercase sharps, German
            {"agrave", new Integer(224)}, // � - lowercase a, grave accent
            {"aacute", new Integer(225)}, // � - lowercase a, acute accent
            {"acirc", new Integer(226)}, // � - lowercase a, circumflex accent
            {"atilde", new Integer(227)}, // � - lowercase a, tilde
            {"auml", new Integer(228)}, // � - lowercase a, umlaut
            {"aring", new Integer(229)}, // � - lowercase a, ring
            {"aelig", new Integer(230)}, // � - lowercase ae
            {"ccedil", new Integer(231)}, // � - lowercase c, cedilla
            {"egrave", new Integer(232)}, // � - lowercase e, grave accent
            {"eacute", new Integer(233)}, // � - lowercase e, acute accent
            {"ecirc", new Integer(234)}, // � - lowercase e, circumflex accent
            {"euml", new Integer(235)}, // � - lowercase e, umlaut
            {"igrave", new Integer(236)}, // � - lowercase i, grave accent
            {"iacute", new Integer(237)}, // � - lowercase i, acute accent
            {"icirc", new Integer(238)}, // � - lowercase i, circumflex accent
            {"iuml", new Integer(239)}, // � - lowercase i, umlaut
            {"igrave", new Integer(236)}, // � - lowercase i, grave accent
            {"iacute", new Integer(237)}, // � - lowercase i, acute accent
            {"icirc", new Integer(238)}, // � - lowercase i, circumflex accent
            {"iuml", new Integer(239)}, // � - lowercase i, umlaut
            {"eth", new Integer(240)}, // � - lowercase eth, Icelandic
            {"ntilde", new Integer(241)}, // � - lowercase n, tilde
            {"ograve", new Integer(242)}, // � - lowercase o, grave accent
            {"oacute", new Integer(243)}, // � - lowercase o, acute accent
            {"ocirc", new Integer(244)}, // � - lowercase o, circumflex accent
            {"otilde", new Integer(245)}, // � - lowercase o, tilde
            {"ouml", new Integer(246)}, // � - lowercase o, umlaut
            {"oslash", new Integer(248)}, // � - lowercase o, slash
            {"ugrave", new Integer(249)}, // � - lowercase u, grave accent
            {"uacute", new Integer(250)}, // � - lowercase u, acute accent
            {"ucirc", new Integer(251)}, // � - lowercase u, circumflex accent
            {"uuml", new Integer(252)}, // � - lowercase u, umlaut
            {"yacute", new Integer(253)}, // � - lowercase y, acute accent
            {"thorn", new Integer(254)}, // � - lowercase thorn, Icelandic
            {"yuml", new Integer(255)}, // � - lowercase y, umlaut
            {"euro", new Integer(8364)}, // Euro symbol
    };

    static Map e2i = new HashMap();

    static Map i2e = new HashMap();
    static {
        for (int i = 0; i < entities.length; ++i) {
            e2i.put(entities[i][0], entities[i][1]);
            i2e.put(entities[i][1], entities[i][0]);
        }
    }

    /**
     * Turns funky characters into HTML entity equivalents
     * <p>
     * e.g. <tt>"bread" & "butter"</tt> =>
     * <tt>&amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;</tt>.
     * Update: supports nearly all HTML entities, including funky accents. See
     * the source code for more detail.
     * 
     * @see #htmlunescape(String)
     */
    public static String htmlescape(String s1) {
        if (s1 == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer();
        int i;
        for (i = 0; i < s1.length(); ++i) {
            char ch = s1.charAt(i);
            String entity = (String) i2e.get(new Integer((int) ch));
            if (entity == null) {
                if ((int) ch > 128) {
                    buf.append("&#" + (int) ch + ";");
                } else {
                    buf.append(ch);
                }
            } else {
                buf.append("&" + entity + ";");
            }
        }
        return buf.toString();
    }

    /**
     * Given a string containing entity escapes, returns a string containing the
     * actual Unicode characters corresponding to the escapes. Note: nasty bug
     * fixed by Helge Tesgaard (and, in parallel, by Alex, but Helge deserves
     * major props for emailing me the fix). 15-Feb-2002 Another bug fixed by
     * Sean Brown <sean@boohai.com>
     * 
     * @see #htmlescape(String)
     */
    public static String htmlunescape(String s1) {
        if (s1 == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer();
        int i;
        for (i = 0; i < s1.length(); ++i) {
            char ch = s1.charAt(i);
            if (ch == '&') {
                int semi = s1.indexOf(';', i + 1);
                if (semi == -1) {
                    buf.append(ch);
                    continue;
                }
                String entity = s1.substring(i + 1, semi);
                Integer iso;
                if (entity.charAt(0) == '#') {
                    iso = new Integer(entity.substring(1));
                } else {
                    iso = (Integer) e2i.get(entity);
                }
                if (iso == null) {
                    buf.append("&" + entity + ";");
                } else {
                    buf.append((char) iso.intValue());
                }
                i = semi;
            } else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }

}
