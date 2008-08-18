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

/*
 * The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.journal.readerwriter.multicast;

import java.io.File;
import java.io.StringWriter;

import java.util.Map;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import javanet.staxutils.IndentingXMLEventWriter;

import fedora.server.journal.JournalException;
import fedora.server.journal.entry.CreatorJournalEntry;
import fedora.server.journal.entry.JournalEntry;

/**
 * <p>
 * <b>Title:</b> JournalEntrySizeEstimator.java
 * </p>
 * <p>
 * <b>Description:</b> The easiest, simplest, most maintainable way to estimate
 * the size of a formatted JournalEntry is to just have the JournalWriter format
 * it. We save some work and avoid memory overflow issues by removing any data
 * files from the entry before formatting: we know how big the files will be
 * when encoded.
 * </p>
 * 
 * @author jblake
 * @version $Id: JournalEntrySizeEstimator.java,v 1.3 2007/06/01 17:21:31 jblake
 *          Exp $
 */
public class JournalEntrySizeEstimator {

    private final MulticastJournalWriter journalWriter;

    public JournalEntrySizeEstimator(MulticastJournalWriter journalWriter) {
        this.journalWriter = journalWriter;
    }

    /**
     * Create a modified entry, minus any file arguments, and ask the
     * JournalWriter to format it so we can check the size. The size of the
     * file(s) can be determined separately.
     * 
     * @throws JournalException
     */
    public long estimateSize(JournalEntry journalEntry) throws JournalException {
        try {
            long totalFileSizes = 0;

            // Initialize the copy of the JournalEntry
            CreatorJournalEntry copyEntry =
                    new CreatorJournalEntry(journalEntry.getMethodName(),
                                            journalEntry.getContext());

            // Add arguments to the copy of the JournalEntry, except for File
            // arguments. Calculate any file sizes here.
            Map<String, Object> argumentsMap = journalEntry.getArgumentsMap();
            for (String name : argumentsMap.keySet()) {
                Object value = argumentsMap.get(name);
                if (value instanceof File) {
                    totalFileSizes += estimateFileSize((File) value);
                    copyEntry.addArgument(name, (File) null);
                } else {
                    copyEntry.addArgument(name, value);
                }
            }

            // Create an XMLEventWriter on a StringBuffer, and ask the
            // JournalWriter to write the copy of the JournalEntry to it.
            StringWriter stringWriter = new StringWriter();
            XMLEventWriter xmlEventWriter = createXmlEventWriter(stringWriter);
            journalWriter.writeJournalEntry(copyEntry, xmlEventWriter);

            // Add the size of the formatted string to the size of the encoded
            // files.
            return totalFileSizes + stringWriter.getBuffer().length();
        } catch (FactoryConfigurationError e) {
            throw new JournalException("can't estimate the size of a JournalEntry",
                                       e);
        } catch (XMLStreamException e) {
            throw new JournalException("can't estimate the size of a JournalEntry",
                                       e);
        }
    }

    /**
     * When a file is Base64-encoded, it takes 4 bytes to encode 3 bytes of
     * content, plus we throw in a newline after encoding 57 characters or so.
     */
    private long estimateFileSize(File file) {
        if (file == null) {
            return 0;
        } else {
            return file.length() * 4 / 3 + file.length() / 57;
        }
    }

    /**
     * Wrap an XMLEventWriter around that StringWriter.
     */
    private XMLEventWriter createXmlEventWriter(StringWriter stringWriter)
            throws FactoryConfigurationError, XMLStreamException {
        return new IndentingXMLEventWriter(XMLOutputFactory.newInstance()
                .createXMLEventWriter(stringWriter));
    }
}
