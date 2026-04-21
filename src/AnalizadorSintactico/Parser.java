package AnalizadorSintactico;

import AnalizadorLexico.Token;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Parser {
    private final Grafo grafo;
    private final List<String> erroresSintacticos;

    public Parser(Grafo grafo) {
        this.grafo = grafo;
        this.erroresSintacticos = new ArrayList<>();
    }

    public boolean parsear(List<Token> tokens) {
        erroresSintacticos.clear();
        Deque<Integer> pilaEstados = new ArrayDeque<>();
        pilaEstados.push(0);
        int indice = 0;

        while (indice < tokens.size()) {
            int estadoActual = pilaEstados.peek();
            Token tokenActual = tokens.get(indice);
            String lookahead = tokenActual.getTipo().toString();
            Estado estado = grafo.obtenerEstado(estadoActual);

            // 1. Verificar Aceptación
            if (estado.getAccepts().containsKey(lookahead)) {
                return erroresSintacticos.isEmpty();
            }

            // 2. Acción SHIFT
            if (estado.getShifts().containsKey(lookahead)) {
                pilaEstados.push(estado.getShifts().get(lookahead));
                indice++;
            } 
            // 3. Acción REDUCE
            else if (estado.getReduces().containsKey(lookahead)) {
                Reglas regla = estado.getReduces().get(lookahead);
                for (int i = 0; i < regla.getCuerpo().size(); i++) {
                    pilaEstados.pop();
                }
                int tope = pilaEstados.peek();
                Integer proximoEstado = grafo.obtenerEstado(tope).getGotos().get(regla.getCabeza());
                
                if (proximoEstado != null) {
                    pilaEstados.push(proximoEstado);
                } else {
                    registrarError(tokenActual, "Fallo en GOTO para " + regla.getCabeza());
                    indice++; // Intento de recuperación
                }
            } 
            // 4. Manejo de Errores (Modo Pánico) [cite: 92]
            else {
                registrarError(tokenActual, "No se esperaba el token '" + tokenActual.getLexema() + "'");
                if (tokens.get(indice).getTipo() == Token.TipoToken.EOF) break;
                indice++; 
            }
        }
        return erroresSintacticos.isEmpty();
    }

    private void registrarError(Token t, String mensaje) {
        erroresSintacticos.add(String.format("line %d, col %d: ERROR Sintáctico. %s", t.getLinea(), t.getColumna(), mensaje));
    }

    public List<String> getErrores() { return erroresSintacticos; }
}