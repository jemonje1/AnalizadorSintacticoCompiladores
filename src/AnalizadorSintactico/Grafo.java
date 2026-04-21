package AnalizadorSintactico;

import java.util.HashMap;
import java.util.Map;

public class Grafo {
    private final Map<Integer, Estado> estados = new HashMap<>();

    public void agregarEstado(Estado estado) {
        estados.put(estado.getId(), estado);
    }

    public void agregarShift(int desde, String simbolo, int hacia) {
        estados.get(desde).getShifts().put(simbolo, hacia);
    }

    public void agregarGoto(int desde, String simbolo, int hacia) {
        estados.get(desde).getGotos().put(simbolo, hacia);
    }

    public void agregarReduce(int desde, String lookahead, Reglas regla) {
        estados.get(desde).getReduces().put(lookahead, regla);
    }

    public void agregarAceptar(int desde, String lookahead) {
        estados.get(desde).getAccepts().put(lookahead, true);
    }

    public Estado obtenerEstado(int id) {
        return estados.get(id);
    }
}