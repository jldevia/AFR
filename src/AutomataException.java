/**
 *
 * @author jdevia
 */
public class AutomataException extends Exception{
    private static final long serialVersionUID = 1;

    public AutomataException(String mensaje) {
        super(mensaje);
    }

    public AutomataException(String mensaje, Throwable exc) {
        super(mensaje, exc);
    }
}
