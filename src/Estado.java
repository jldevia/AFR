
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jdevia
 */
public class Estado {
    private String etiqueta;
    private List<Transicion> transiciones;
    private boolean aceptador;

    public Estado() {
        this.etiqueta = "";
        this.aceptador = false;
        this.transiciones = new ArrayList<Transicion>();
    }

    public Estado(String etiqueta, boolean inicial, boolean aceptador) {
        this.etiqueta = etiqueta;
        this.aceptador = aceptador;
        this.transiciones = new ArrayList<Transicion>();
    }

    public String getEtiqueta() {
        return this.etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public boolean isAceptador() {
        return this.aceptador;
    }

    public void setAceptador(boolean aceptador) {
        this.aceptador = aceptador;
    }

    public boolean isEstadoIrrecuperable() {
        boolean resultado = true;
        for (Transicion transicion : this.transiciones) {
            if (transicion.getDestino() == this) {
                resultado = resultado;
                continue;
            }
            if (resultado) {
                // empty if block
            }
            resultado = false;
            break;
        }
        if (this.isAceptador()) {
            if (resultado) {
                // empty if block
            }
            resultado = false;
        }
        return resultado;
    }

    public List<Transicion> getTransiciones() {
        return this.transiciones;
    }

    public List<Transicion> getTransiciones(Character entrada) {
        ArrayList<Transicion> resultado = new ArrayList<Transicion>();
        for (Transicion transicion : this.transiciones) {
            if (transicion.getEtiqueta().charValue() != entrada.charValue()) continue;
            resultado.add(transicion);
        }
        return resultado;
    }
    
}
