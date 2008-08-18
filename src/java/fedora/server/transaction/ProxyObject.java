package fedora.server.transaction;

import fedora.server.storage.types.DigitalObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA. User: abr Date: Aug 7, 2008 Time: 3:44:43 PM To
 * change this template use File | Settings | File Templates.
 */
public class ProxyObject {


    private DigitalObject object;

    private String pid;

    private Transaction token;


    public ProxyObject(DigitalObject object, String pid, Transaction token) {
        this.object = object;
        this.pid = pid;
        this.token = token;
        token.registerProxy(this);
    }

    public Transaction getToken() {
        return token;
    }

    public DigitalObject getObject() {
        return object;
    }

    public String getPid() {
        return pid;
    }
}
