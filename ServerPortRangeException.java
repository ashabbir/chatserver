
/**
 * Class ServerPortRange Exception, is throws by CLient as well as server if
 * args have wrong server port this exception class is present to ensure caller
 * connects with the correct range its decoppled so range depends on the caller
 * to specify.
 *
 * @author ashabbir
 */
class ServerPortRangeException extends Exception {

    public ServerPortRangeException() {
        this("serverPort range not allowed.");
    }

    public ServerPortRangeException(String message) {
        super(message);
    }
}

