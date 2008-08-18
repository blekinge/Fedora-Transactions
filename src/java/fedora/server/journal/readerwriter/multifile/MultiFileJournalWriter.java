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

package fedora.server.journal.readerwriter.multifile;

import java.io.File;

import java.util.Date;
import java.util.Map;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import fedora.server.journal.JournalException;
import fedora.server.journal.JournalWriter;
import fedora.server.journal.ServerInterface;
import fedora.server.journal.entry.CreatorJournalEntry;
import fedora.server.journal.helpers.JournalHelper;
import fedora.server.journal.helpers.ParameterHelper;

/**
 * An implementation of JournalWriter that writes a series of Journal files to a
 * specified directory. New files are begun when the current file becomes too
 * large or too old.
 * 
 * @author Jim Blake
 */
public class MultiFileJournalWriter
        extends JournalWriter
        implements MultiFileJournalConstants {

    /** the directory that will hold the journal files. */
    private final File journalDirectory;

    /** journal file names will start with this string. */
    private final String filenamePrefix;

    /** number of bytes before we start a new file - 0 means no limit */
    private final long sizeLimit;

    /** number of milliseconds before we start a new file - 0 means no limit */
    private final long ageLimit;

    /** the current journal file - start with a dummy that is already closed. */
    private JournalOutputFile currentJournal = JournalOutputFile.DUMMY_FILE;

    private boolean open = true;

    /**
     * Parse the parameters to find out how we are operating.
     */
    public MultiFileJournalWriter(Map<String, String> parameters,
                                  String role,
                                  ServerInterface server)
            throws JournalException {
        super(parameters, role, server);
        journalDirectory =
                ParameterHelper
                        .parseParametersForWritableDirectory(parameters,
                                                             PARAMETER_JOURNAL_DIRECTORY);
        filenamePrefix =
                ParameterHelper.parseParametersForFilenamePrefix(parameters);
        sizeLimit = ParameterHelper.parseParametersForSizeLimit(parameters);
        ageLimit = ParameterHelper.parseParametersForAgeLimit(parameters);

        checkForPotentialFilenameConflict();
    }

    /**
     * Look at the list of files in the current directory, and make sure that
     * any new files we create won't conflict with them.
     */
    private void checkForPotentialFilenameConflict() throws JournalException {
        File[] journalFiles =
                MultiFileJournalHelper
                        .getSortedArrayOfJournalFiles(journalDirectory,
                                                      filenamePrefix);
        if (journalFiles.length == 0) {
            return;
        }

        String newestFilename = journalFiles[journalFiles.length - 1].getName();
        String potentialFilename =
                JournalHelper.createTimestampedFilename(filenamePrefix,
                                                        new Date());
        if (newestFilename.compareTo(potentialFilename) > 0) {
            throw new JournalException("The name of one or more existing files in the journal "
                    + "directory (e.g. '"
                    + newestFilename
                    + "') may conflict with new Journal "
                    + "files. Has the system clock changed?");
        }
    }

    /**
     * Before writing an entry, check to see whether we need to close the
     * current file and/or open a new one.
     */
    @Override
    public void prepareToWriteJournalEntry() throws JournalException {
        if (open) {
            currentJournal.closeIfAppropriate();

            if (!currentJournal.isOpen()) {
                currentJournal =
                        new JournalOutputFile(this,
                                              filenamePrefix,
                                              journalDirectory,
                                              sizeLimit,
                                              ageLimit);
            }
        }
    }

    /**
     * We've prepared for the entry, so just write it, but remember to
     * synchronize on the file, so we don't get an asynchronous close while
     * we're writing. After writing the entry, flush the file.
     */
    @Override
    public void writeJournalEntry(CreatorJournalEntry journalEntry)
            throws JournalException {
        if (open) {
            try {
                synchronized (JournalWriter.SYNCHRONIZER) {
                    XMLEventWriter xmlWriter = currentJournal.getXmlWriter();
                    super.writeJournalEntry(journalEntry, xmlWriter);
                    xmlWriter.flush();
                    currentJournal.closeIfAppropriate();
                }
            } catch (XMLStreamException e) {
                throw new JournalException(e);
            }
        }
    }

    /**
     * Close the current journal file.
     */
    @Override
    public void shutdown() throws JournalException {
        if (open) {
            currentJournal.close();
            open = false;
        }
    }

    /**
     * A convenience method so the JournalOutputFile can request its own header.
     */
    void getDocumentHeader(XMLEventWriter xmlWriter) throws JournalException {
        super.writeDocumentHeader(xmlWriter);
    }

    /**
     * A convenience method so the JournalOutputFile can request its own
     * trailer.
     */
    void getDocumentTrailer(XMLEventWriter xmlWriter) throws JournalException {
        super.writeDocumentTrailer(xmlWriter);
    }

    /**
     * Create an informative message for debugging purposes.
     */
    @Override
    public String toString() {
        return super.toString() + ", journalDirectory='" + journalDirectory
                + "', filenamePrefix='" + filenamePrefix + "', sizeLimit="
                + sizeLimit + "(bytes), ageLimit=" + ageLimit + "(msec)";
    }

}
