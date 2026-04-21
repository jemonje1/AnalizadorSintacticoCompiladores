package AnalizadorSintactico;

import java.util.HashMap;
import java.util.Map;

public class Estado {
    private final int id;
    private final Map<String, Integer> shifts = new HashMap<>();
    private final Map<String, Integer> gotos = new HashMap<>();
    private final Map<String, Reglas> reduces = new HashMap<>();
    private final Map<String, Boolean> accepts = new HashMap<>();

    public Estado(int id) { this.id = id; }
    public int getId() { return id; }
    public Map<String, Integer> getShifts() { return shifts; }
    public Map<String, Integer> getGotos() { return gotos; }
    public Map<String, Reglas> getReduces() { return reduces; }
    public Map<String, Boolean> getAccepts() { return accepts; }
}