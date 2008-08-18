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

package fedora.server.storage.lowlevel;

import java.io.File;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import fedora.server.errors.LowlevelStorageException;

/**
 * @author Bill Niebel
 */
class TimestampPathAlgorithm
        extends PathAlgorithm {

    private final String storeBase;

    private static final String[] PADDING = {"", "0", "00", "000"};

    private static final String SEP = File.separator;

    public TimestampPathAlgorithm(Map configuration) {
        super(configuration);
        storeBase = (String) configuration.get("storeBase");
    }

    @Override
    public final String get(String pid) throws LowlevelStorageException {
        return format(encode(pid));
    }

    public String format(String pid) throws LowlevelStorageException {
        GregorianCalendar calendar = new GregorianCalendar();
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        String month = leftPadded(1 + calendar.get(Calendar.MONTH), 2);
        String dayOfMonth = leftPadded(calendar.get(Calendar.DAY_OF_MONTH), 2);
        String hourOfDay = leftPadded(calendar.get(Calendar.HOUR_OF_DAY), 2);
        String minute = leftPadded(calendar.get(Calendar.MINUTE), 2);
        //String second = leftPadded(calendar.get(Calendar.SECOND),2);
        return storeBase + SEP + year + SEP + month + dayOfMonth + SEP
                + hourOfDay + SEP + minute /* + sep + second */+ SEP + pid;
    }

    private final String leftPadded(int i, int n)
            throws LowlevelStorageException {
        if (n > 3 || n < 0 || i < 0 || i > 999) {
            throw new LowlevelStorageException(true, getClass().getName()
                    + ": faulty date padding");
        }
        int m = i > 99 ? 3 : i > 9 ? 2 : 1;
        int p = n - m;
        return PADDING[p] + Integer.toString(i);
    }
}
