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

package fedora.server.utilities.status;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

public class ServerStatusMessage {

    public static final ServerStatusMessage NEW_SERVER_MESSAGE =
            new ServerStatusMessage(ServerState.NEW_SERVER, null, null);

    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ssa z";

    private final ServerState _state;

    private Date _date;

    private String _detail;

    public ServerStatusMessage(ServerState state, Date time, String detail) {
        _state = state;
        _date = time;
        if (_date == null) {
            _date = new Date();
        }
        if (detail != null) {
            _detail = detail.trim();
        }
    }

    public ServerState getState() {
        return _state;
    }

    public Date getDate() {
        return _date;
    }

    public String getDetail() {
        return _detail;
    }

    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append("STATE  : " + _state.getName() + "\n");
        out.append("AS OF  : " + dateToString(_date) + "\n");
        if (_detail != null) {
            out.append("DETAIL : " + _detail + "\n");
        }
        return out.toString();
    }

    public static String dateToString(Date d) {
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
        return formatter.format(d);
    }

    public static Date stringToDate(String s) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
        return formatter.parse(s);
    }
}
