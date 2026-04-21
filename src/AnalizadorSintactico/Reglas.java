package AnalizadorSintactico;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Reglas {
    private final int id;
    private final String cabeza;
    private final List<String> cuerpo;
    private final Set<String> lookaheads;

    public Reglas(int id, String cabeza, List<String> cuerpo, Set<String> lookaheads) {
        this.id = id;
        this.cabeza = cabeza;
        this.cuerpo = cuerpo;
        this.lookaheads = lookaheads == null ? Collections.emptySet() : lookaheads;
    }

    public int getId() {
        return id;
    }

    public String getCabeza() {
        return cabeza;
    }

    public List<String> getCuerpo() {
        return cuerpo;
    }

    public Set<String> getLookaheads() {
        return lookaheads;
    }

    public boolean aceptaLookahead(String lookahead) {
        return lookaheads.isEmpty() || lookaheads.contains(lookahead);
    }

    @Override
    public String toString() {
        return id + ") " + cabeza + " -> " + (cuerpo.isEmpty() ? "ε" : String.join(" ", cuerpo));
    }
}