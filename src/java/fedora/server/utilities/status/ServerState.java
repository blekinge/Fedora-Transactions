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

public class ServerState {

    public static final ServerState NEW_SERVER = new ServerState("New Server");

    public static final ServerState NOT_STARTING =
            new ServerState("Not Starting");

    public static final ServerState STARTING = new ServerState("Starting");

    public static final ServerState STARTED = new ServerState("Started");

    public static final ServerState STARTUP_FAILED =
            new ServerState("Startup Failed");

    public static final ServerState STOPPING = new ServerState("Stopping");

    public static final ServerState STOPPED = new ServerState("Stopped");

    public static final ServerState STOPPED_WITH_ERR =
            new ServerState("Stopped with error");

    public static final ServerState[] STATES =
            new ServerState[] {NEW_SERVER, NOT_STARTING, STARTING, STARTED,
                    STARTUP_FAILED, STOPPING, STOPPED, STOPPED_WITH_ERR};

    private final String _name;

    private ServerState(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    @Override
    public String toString() {
        return _name;
    }

    public static ServerState fromString(String name) throws Exception {

        for (ServerState element : STATES) {
            if (element.getName().equals(name)) {
                return element;
            }
        }
        throw new Exception("Unrecognized Server State: " + name);
    }

}
