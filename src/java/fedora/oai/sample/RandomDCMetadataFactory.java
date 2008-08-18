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

package fedora.oai.sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import fedora.common.Constants;

/**
 * @author Chris Wilper
 */
public class RandomDCMetadataFactory
        implements Constants {

    private static String[] s_dcElements =
            new String[] {"title", "creator", "subject", "description",
                    "publisher", "contributor", "date", "type", "format",
                    "identifier", "source", "language", "relation", "coverage",
                    "rights"};

    private final ArrayList m_wordList = new ArrayList();

    public RandomDCMetadataFactory(File dictionaryFile)
            throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(dictionaryFile));
        String nextLine = "";
        while (nextLine != null) {
            nextLine = in.readLine();
            if (nextLine != null) {
                String[] words = nextLine.split(" ");
                for (String w : words) {
                    if (allLetters(w)) {
                        m_wordList.add(w);
                    }
                }
            }
        }
        in.close();
    }

    public String get(int repeatMax, int wordMax) {
        return get(repeatMax, wordMax, m_wordList);
    }

    public static String get(int repeatMax, int wordMax, List wordList) {
        StringBuffer out = new StringBuffer();
        out.append("<oai_dc:dc\n" + "    xmlns:oai_dc=\"" + OAI_DC.uri + "\"\n"
                + "    xmlns:dc=\"" + DC.uri + "\"\n" + "    xmlns:xsi=\""
                + XSI.uri + "\">\n");
        for (String dcElement : s_dcElements) {
            int num = 1 + getRandom(repeatMax);
            for (int i = 0; i < num; i++) {
                out.append("<dc:" + dcElement + ">"
                        + getRandomWords(wordMax, wordList) + "</dc:"
                        + dcElement + ">\n");
            }
        }
        out.append("</oai_dc:dc>");
        return out.toString();
    }

    private static String getRandomWords(int wordMax, List wordList) {
        int count = 1 + getRandom(wordMax);
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                out.append(" ");
            }
            out.append((String) wordList.get(getRandom(wordList.size())));
        }
        return out.toString();
    }

    public static int getRandom(int belowThis) {
        return (int) (Math.random() * belowThis);
    }

    private static boolean allLetters(String w) {
        if (w.length() == 0) {
            return false;
        }
        String l = w.toLowerCase();
        for (int i = 0; i < l.length(); i++) {
            char c = l.charAt(i);
            if (c < 'a' || c > 'z') {
                return false;
            }
        }
        return true;
    }

}
