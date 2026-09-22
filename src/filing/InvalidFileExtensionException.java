
package filing;

/**
 * Thrown when an imported file has the wrong extension. 
 * i.e. (.txt) instead of (.tctrl)
 * @author tshim
 */
public class InvalidFileExtensionException extends Exception {

    /**
     * Creates a new instance of <code>InvalidFileExtensionException</code> without
     * detail message.
     */
    public InvalidFileExtensionException() {
    }

    /**
     * Constructs an instance of <code>InvalidFileExtensionException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidFileExtensionException(String msg) {
        super(msg);
    }
}
