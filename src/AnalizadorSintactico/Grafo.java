package AnalizadorSintactico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Grafo {
    private final List<Reglas> reglas = new ArrayList<>();

    public void agregarRegla(Reglas regla) {
        reglas.add(regla);
    }

    public List<Reglas> getReglas() {
        return Collections.unmodifiableList(reglas);
    }
}