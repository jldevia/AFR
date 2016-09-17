
/**
 *
 * @author jdevia
 */
public class Resultado {
    private boolean aceptada = false;
    private StringBuilder log = new StringBuilder();

    public boolean isAceptada() {
        return this.aceptada;
    }

    public void setAceptada(boolean aceptada) {
        this.aceptada = aceptada;
    }

    public StringBuilder getLog() {
        return this.log;
    }

    public void setLog(StringBuilder log) {
        this.log = log;
    }
}
