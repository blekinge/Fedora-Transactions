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

package fedora.server.journal.readerwriter.singlefile;

import java.io.FileWriter;
import java.io.IOException;

import java.util.Map;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import javanet.staxutils.IndentingXMLEventWriter;

import fedora.server.journal.JournalException;
import fedora.server.journal.JournalWriter;
import fedora.server.journal.ServerInterface;
import fedora.server.journal.entry.CreatorJournalEntry;

/**
 * A rudimentary implementation of JournalWriter that just writes all entries to
 * a single Journal file. Useful only for System tests.
 * 
 * @author Jim Blake
 */
public class SingleFileJournalWriter
        extends JournalWriter
        implements SingleFileJournalConstants {

    private final FileWriter out;

    private final XMLEventWriter writer;

    private boolean fileHasHeader = false;

    /**
     * Get the name of the journal file from the server parameters, create the
     * file, wrap it in an XMLEventWriter, and initialize it with a document
     * header.
     */
    public SingleFileJournalWriter(Map<String, String> parameters,
                                   String role,
                                   ServerInterface server)
            throws JournalException {
        super(parameters, role, server);

        if (!parameters.containsKey(PARAMETER_JOURNAL_FILENAME)) {
            throw new JournalException("Parameter '"
                    + PARAMETER_JOURNAL_FILENAME + "' not set.");
        }

        try {
            out = new FileWriter(parameters.get(PARAMETER_JOURNAL_FILENAME));

            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            writer =
                    new IndentingXMLEventWriter(factory
                            .createXMLEventWriter(out));
        } catch (IOException e) {
            throw new JournalException(e);
        } catch (XMLStreamException e) {
            throw new JournalException(e);
        }
    }

    /**
     * Make sure that the file has been initialized before writing any journal
     * entries.
     */
    @Override
    public void prepareToWriteJournalEntry() throws JournalException {
        if (!fileHasHeader) {
            super.writeDocumentHeader(writer);
            fileHasHeader = true;
        }
    }

    /**
     * Every journal entry just gets added to the file.
     */
    @Override
    public void writeJournalEntry(CreatorJournalEntry journalEntry)
            throws JournalException {
        try {
            super.writeJournalEntry(journalEntry, writer);
            writer.flush();
        } catch (XMLStreamException e) {
            throw new JournalException(e);
        }
    }

    /**
     * Add the document trailer and close the journal file.
     */
    @Override
    public void shutdown() throws JournalException {
        try {
            if (fileHasHeader) {
                super.writeDocumentTrailer(writer);
            }
            writer.close();
            out.close();
        } catch (XMLStreamException e) {
            throw new JournalException(e);
        } catch (IOException e) {
            throw new JournalException(e);
        }
    }

}
