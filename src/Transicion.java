/**
 *
 * @author jdevia
 */
public class Transicion {
    private Character etiqueta;
    private Estado origen;
    private Estado destino;

    public Transicion() {
    }

    public Transicion(Character etiqueta, Estado origen, Estado destino) {
        this.etiqueta = etiqueta;
        this.origen = origen;
        this.destino = destino;
    }

    public Character getEtiqueta() {
        return this.etiqueta;
    }

    public void setEtiqueta(Character etiqueta) {
        this.etiqueta = etiqueta;
    }

    public Estado getOrigen() {
        return this.origen;
    }

    public void setOrigen(Estado origen) {
        this.origen = origen;
    }

    public Estado getDestino() {
        return this.destino;
    }

    public void setDestino(Estado destino) {
        this.destino = destino;
    }
}
