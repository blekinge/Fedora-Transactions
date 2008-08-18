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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Logger;

/**
 * <p>
 * <b>Title:</b> TransportOutputFile.java
 * </p>
 * <p>
 * <b>Description:</b> A "renaming" journal file, for use by Transports.
 * </p>
 * <p>
 * When this file is created, a prefix is put on the filename to show that
 * output is in progress. When the file is closed, it is renamed to remove the
 * prefix.
 * </p>
 * 
 * @author jblake
 * @version $Id$
 */
public class TransportOutputFile {

    private static final Logger LOG =
            Logger.getLogger(TransportOutputFile.class);

    private enum State {
        READY, OPEN, CLOSED
    };

    private final File file;

    private final File tempFile;

    private State state = State.READY;

    private FileWriter fileWriter;

    /**
     * Sture the filename, and the "in process" filename.
     * 
     * @throws IOException
     *         if either file already exists.
     */
    public TransportOutputFile(File directory, String name)
            throws IOException {
        LOG.debug("creating TransportOutputFile: '" + directory + "', '" + name
                + "'");

        file = new File(directory, name);
        if (file.exists()) {
            throw new IOException("File " + file + " already exists.");
        }

        tempFile = new File(directory, ("_" + name));
        if (tempFile.exists()) {
            throw new IOException("File " + tempFile + " already exists.");
        }
    }

    /**
     * Create the file with its "in progress" filename.
     * 
     * @return a Writer on the new file.
     */
    public Writer open() throws IOException {
        switch (state) {
            case OPEN:
                throw new IllegalStateException("File " + tempFile
                        + " is already open.");
            case CLOSED:
                throw new IllegalStateException("File " + tempFile
                        + " has been closed already.");
            default: // READY
                state = State.OPEN;
                tempFile.createNewFile();
                fileWriter = new FileWriter(tempFile);
                return fileWriter;
        }
    }

    /**
     * Close the writer and rename the file.
     * 
     * @throws IOException
     */
    public void close() throws IOException {
        switch (state) {
            case READY:
                throw new IllegalStateException("File " + tempFile
                        + " hasn't been opened yet.");
            case CLOSED:
                throw new IllegalStateException("File " + tempFile
                        + " has been closed already.");
            default: // OPEN
                fileWriter.close();
                tempFile.renameTo(file);
                state = State.CLOSED;
        }
    }

    /**
     * Did somebody ask who we are?
     */
    public String getName() {
        return file.getName();
    }
}
