
import java.util.List;

/**
 *
 * @author jdevia
 */
public class EstadoCompuesto extends Estado{
    List<Estado> composicion;

    public EstadoCompuesto() {
    }

    public EstadoCompuesto(String etiqueta, boolean inicial, boolean aceptador, List<Estado> composicion) {
        super(etiqueta, inicial, aceptador);
        this.composicion = composicion;
    }

    public List<Estado> getComposicion() {
        return this.composicion;
    }

    public void setComposicion(List<Estado> composicion) {
        this.composicion = composicion;
    }
    
}
