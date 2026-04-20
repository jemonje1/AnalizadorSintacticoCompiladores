package Stack;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Pila de niveles de indentación para MiniLang.
 * Controla niveles del 0 al 5 y valida cierres correctos.
 */
public class PilaIdentacion {

    private final Deque<Integer> niveles;
    private final int maxNiveles;

    public PilaIdentacion(int maxNiveles) {
        this.niveles = new ArrayDeque<>();
        this.maxNiveles = maxNiveles;
        this.niveles.push(0); // Nivel base #0# siempre presente
    }

    /**
     * Procesa el nivel de indentación de una línea.
     * @param espacios Cantidad de espacios iniciales detectados.
     * @param linea Fila actual para reportar error.
     * @param columna Columna actual (usualmente 1).
     * @return El tipo de cambio (1: INDENT, -1: DEDENT, 0: MISMO NIVEL, -2: ERROR)
     */
    public int procesarNivel(int espacios, int linea, int columna) {
        // En MiniLang definimos nivel = espacios / 4 (asumiendo tab = 4)
        // Si usas espacios simples, el nivel es simplemente la profundidad detectada
        int nivelDeseado = espacios; 

        if (nivelDeseado > maxNiveles) {
            System.err.println("Error de sobreidentacion en Linea " + linea + ", Columna " + columna + 
                ": Nivel " + nivelDeseado + " excede el limite de " + maxNiveles);
            return -2; // Código de error por exceso
        }

        int nivelActual = niveles.peek();

        if (nivelDeseado > nivelActual) {
            // Es un INDENT (#1#, #2#, etc.)
            niveles.push(nivelDeseado);
            return 1; 
        } else if (nivelDeseado < nivelActual) {
            // Es un DEDENT o regreso a nivel anterior
            if (!niveles.contains(nivelDeseado)) {
                System.err.println("Error de identacion en Linea " + linea + ", Columna " + columna + 
                    ": El nivel " + nivelDeseado + " no corresponde a una apertura previa.");
                return -2;
            }
            
            // Desapilamos hasta llegar al nivel deseado
            while (niveles.peek() > nivelDeseado) {
                niveles.pop();
            }
            return -1;
        }

        return 0; // Se mantiene en el mismo nivel
    }

    /**
     * Valida si el programa cerró correctamente en nivel #0# al llegar al final.
     * @param lineaUltima Para indicar dónde falló el cierre.
     * @return true si es correcto, false si quedaron niveles abiertos.
     */
    public boolean validarCierreFinal(int lineaUltima) {
        if (niveles.peek() != 0) {
            System.err.println("Error de identacion: El programa finalizo en nivel #" + niveles.peek() + 
                "#. No se cerraron todas las identaciones correctamente al llegar al final (Linea " + lineaUltima + ").");
            return false;
        }
        return true;
    }

    public int getNivelActual() {
        return niveles.peek();
    }

    public void reset() {
        niveles.clear();
        niveles.push(0);
    }
}