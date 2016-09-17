
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author jdevia
 */
public class Automata {
    private Estado inicial;
    private Estado puntoEjecucion;
    private List<Estado> estados = new ArrayList<>();
    private List<Estado> estadosCompuestos = new ArrayList<>();

    public Automata(String archivoDef) throws AutomataException {
        try {
            this.cargarAutomata(archivoDef);
        }
        catch (IOException | ParserConfigurationException | SAXException exc) {
            throw new AutomataException("Error al procesar el archivo de definición. No se pudo generar el autómata.", (Throwable)exc);
        }catch (Exception exc) {
            throw exc;
        }
    }

    private void cargarAutomata(String archivoDef) throws ParserConfigurationException, SAXException, IOException, AutomataException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document documento = builder.parse(archivoDef);
        Element raiz = documento.getDocumentElement();
        NodeList nodosEstado = raiz.getElementsByTagName("estado");
        for (int i = 0; i < nodosEstado.getLength(); ++i) {
            Element estado = (Element)nodosEstado.item(i);
            Estado newEstado = new Estado();
            if (estado.getAttribute("etiqueta").isEmpty()) {
                throw new AutomataException("Debe etiquetar todos los estados para poder generar el aut\u00f3mata.");
            }
            newEstado.setEtiqueta(estado.getAttribute("etiqueta"));
            if (estado.getAttribute("inicial").equals("true")) {
                if (this.inicial == null) {
                    this.inicial = newEstado;
                } else {
                    throw new AutomataException("Solo puede existir un estado inicial para poder generar el aut\u00f3mata");
                }
            }
            if (estado.getAttribute("aceptador").equals("true")) {
                newEstado.setAceptador(true);
            }
            this.estados.add(newEstado);
        }
        if (this.inicial == null) {
            throw new AutomataException("Debe existir un estado inicial (y solo uno) para poder generar el aut\u00f3mata.");
        }
        if (this.estados.isEmpty()) {
            throw new AutomataException("Debe existir al menos un estado para poder generar el aut\u00f3mata.");
        }
        NodeList nodosTransiciones = raiz.getElementsByTagName("transicion");
        for (int i2 = 0; i2 < nodosTransiciones.getLength(); ++i2) {
            Element transicion = (Element)nodosTransiciones.item(i2);
            Transicion newTransicion = new Transicion();
            String etiquetaTransicion = transicion.getAttribute("etiqueta");
            if (etiquetaTransicion.isEmpty()) {
                throw new AutomataException("Todas las transiciones deben estar etiquetadas para poder generar el aut\u00f3mata.");
            }
            if (etiquetaTransicion.length() != 1 || !Character.isDigit(etiquetaTransicion.charAt(0)) && !Character.isLowerCase(etiquetaTransicion.charAt(0))) {
                throw new AutomataException("Solo se admiten d\u00edgitos (0 .. 9) \u00f3 letras min\u00fasculas (a .. z) como alfabeto del aut\u00f3mata.");
            }
            newTransicion.setEtiqueta(Character.valueOf(transicion.getAttribute("etiqueta").charAt(0)));
            String origen = transicion.getAttribute("origen");
            if (origen.isEmpty()) {
                throw new AutomataException("Origen de transici\u00f3n (" + etiquetaTransicion + ") sin especificar. No se puede generar el aut\u00f3mata");
            }
            Estado org = this.getEstado(origen);
            if (org == null) {
                throw new AutomataException("Origen de transici\u00f3n  ( " + etiquetaTransicion + " ) inexistente. No se puede generar el aut\u00f3mata");
            }
            newTransicion.setOrigen(org);
            org.getTransiciones().add(newTransicion);
            String destino = transicion.getAttribute("destino");
            if (destino.isEmpty()) {
                throw new AutomataException("Destino de transici\u00f3n (" + etiquetaTransicion + ") sin especificar. No se puede generar el aut\u00f3mata");
            }
            Estado dest = this.getEstado(destino);
            if (dest == null) {
                throw new AutomataException("Destino de transici\u00f3n  ( " + etiquetaTransicion + " ) inexistente. No se puede generar el aut\u00f3mata");
            }
            newTransicion.setDestino(dest);
        }
    }

    public Resultado ejecutar(String input) throws AutomataException {
        int paso = 0;
        Resultado resultado = new Resultado();
        this.puntoEjecucion = this.inicial;
        resultado.getLog().append("Estado Inicial: " + this.inicial.getEtiqueta() + "\n");
        for (int i = 0; i < input.length(); ++i) {
            char simbolo = input.charAt(i);
            if (!Character.isDigit(simbolo) && !Character.isLowerCase(simbolo)) {
                throw new AutomataException("La cadena de input contiene un caracter que no pertenece al alfabeto pre-definido.");
            }
            ++paso;
            List transiciones = this.puntoEjecucion.getTransiciones(simbolo);
            if (transiciones.isEmpty()) {
                resultado.setAceptada(false);
                resultado.getLog().append("" + paso + ") " + this.puntoEjecucion.getEtiqueta() + " ----(" + simbolo + ")----> ERR.\n");
                break;
            }
            if (transiciones.size() == 1) {
                puntoEjecucion = ((Transicion)transiciones.get(0)).getDestino();
                resultado.setAceptada(puntoEjecucion.isAceptador());
                resultado.getLog().append("" + paso + ") " + ((Transicion)transiciones.get(0)).getOrigen().getEtiqueta() + " ----(" + simbolo + ")----> " + this.puntoEjecucion.getEtiqueta() + "\n");
                continue;
            }
            puntoEjecucion = salvarIndeterminacion(transiciones);
            resultado.setAceptada(puntoEjecucion.isAceptador());
            resultado.getLog().append("" + paso + ") " + ((Transicion)transiciones.get(0)).getOrigen().getEtiqueta() + " ----(" + simbolo + ")----> " + this.puntoEjecucion.getEtiqueta() + "\n");
        }
        return resultado;
    }

    private Estado getEstado(String etiqueta) {
        Estado resultado = null;
        for (Estado estado : this.estados) {
            if (!estado.getEtiqueta().equals(etiqueta)) continue;
            return estado;
        }
        return resultado;
    }

    private Estado salvarIndeterminacion(List<Transicion> indeterminaciones) {
        Estado resultado = null;
        Estado orgTransiciones = indeterminaciones.get(0).getOrigen();
        boolean existe = false;
        for (Estado estado : this.estadosCompuestos) {
            int contador = 0;
            EstadoCompuesto auxEstado = (EstadoCompuesto)estado;
            for (Transicion auxTrans : indeterminaciones) {
                if (!auxEstado.getComposicion().contains((Object)auxTrans.getDestino())) continue;
                contador = 1;
            }
            if (contador == auxEstado.composicion.size()) {
                resultado = estado;
                existe = true;
                break;
            }
            existe = false;
        }
        if (!existe) {
            List<Transicion> nuevasTransiciones = new ArrayList<>();
            List<Estado> newComposicion = new ArrayList<>();
            StringBuilder etiquetaNewEst = new StringBuilder("<");
            boolean aceptador = false;
            Iterator iterator = indeterminaciones.iterator();
            while (iterator.hasNext()) {
                Transicion trans = (Transicion) iterator.next();
                if (trans.getDestino().isEstadoIrrecuperable()) continue;
                etiquetaNewEst.append(trans.getDestino().getEtiqueta());
                aceptador = aceptador || trans.getDestino().isAceptador();
                newComposicion.add(trans.getDestino());
                nuevasTransiciones.addAll(trans.getDestino().getTransiciones());
                if (iterator.hasNext()) {
                    etiquetaNewEst.append("-");
                    continue;
                }
                etiquetaNewEst.append(">");
            }
            if (newComposicion.size() > 1) {
                resultado = new EstadoCompuesto(new String(etiquetaNewEst), false, aceptador, newComposicion);
                for (Transicion trans : nuevasTransiciones) {
                    Transicion nueva = new Transicion(trans.getEtiqueta(), resultado, trans.getDestino());
                    resultado.getTransiciones().add(nueva);
                }
                this.estadosCompuestos.add(resultado);
                this.estados.add(resultado);
            } else {
                return (Estado)newComposicion.get(0);
            }
        }
        Transicion newTransicion = new Transicion();
        newTransicion.setEtiqueta(indeterminaciones.get(0).getEtiqueta());
        newTransicion.setOrigen(orgTransiciones);
        newTransicion.setDestino(resultado);
        orgTransiciones.getTransiciones().clear();
        orgTransiciones.getTransiciones().add(newTransicion);
        return resultado;
    }
}
