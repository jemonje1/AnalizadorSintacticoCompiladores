package AnalizadorSintactico;

import java.util.List;

public class Reglas {
    private final int id;
    private final String cabeza;
    private final List<String> cuerpo;

    public Reglas(int id, String cabeza, List<String> cuerpo) {
        this.id = id;
        this.cabeza = cabeza;
        this.cuerpo = cuerpo;
    }

    public int getId() { return id; }
    public String getCabeza() { return cabeza; }
    public List<String> getCuerpo() { return cuerpo; }

    @Override
    public String toString() {
        return id + ") " + cabeza + " -> " + (cuerpo.isEmpty() ? "ε" : String.join(" ", cuerpo));
    }
}