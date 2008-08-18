package fedora.server.transaction.exceptions;

import fedora.server.errors.ServerException;

/**
 * Created by IntelliJ IDEA. User: abr Date: Aug 8, 2008 Time: 2:02:57 PM To
 * change this template use File | Settings | File Templates.
 */
public class InvalidTokenException extends Exception {

    /**
     * Constructs a new exception with <code>null</code> as its detail message. The
     * cause is not initialized, and may subsequently be initialized by a call to
     * {@link #initCause}.
     */
    public InvalidTokenException() {
    }

    /**
     * Constructs a new exception with the specified detail message.  The cause is
     * not initialized, and may subsequently be initialized by a call to {@link
     * #initCause}.
     *
     * @param message the detail message. The detail message is saved for later
     *                retrieval by the {@link #getMessage()} method.
     */
    public InvalidTokenException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * <p>Note that the detail message associated with <code>cause</code> is
     * <i>not</i> automatically incorporated in this exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link
     *                #getCause()} method).  (A <tt>null</tt> value is permitted,
     *                and indicates that the cause is nonexistent or unknown.)
     * @since 1.4
     */
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message of
     * <tt>(cause==null ? null : cause.toString())</tt> (which typically contains
     * the class and detail message of <tt>cause</tt>). This constructor is useful
     * for exceptions that are little more than wrappers for other throwables (for
     * example, {@link java.security.PrivilegedActionException}).
     *
     * @param cause the cause (which is saved for later retrieval by the {@link
     *              #getCause()} method).  (A <tt>null</tt> value is permitted, and
     *              indicates that the cause is nonexistent or unknown.)
     * @since 1.4
     */
    public InvalidTokenException(Throwable cause) {
        super(cause);
    }
}
