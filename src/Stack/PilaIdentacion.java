package Stack;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Pila de niveles de indentación para INDENT/DEDENT.
 */
public class PilaIdentacion {

    // region ATRIBUTOS
    private final Deque<Integer> niveles;
    private final int maxNiveles;
    // endregion

    // region CONSTRUCTOR
    public PilaIdentacion(int maxNiveles) {
        this.niveles = new ArrayDeque<>();
        this.maxNiveles = maxNiveles;
        this.niveles.push(0); // nivel base
    }
    // endregion

    // region METODOS - PILA
    /**
     * Obtiene el nivel actual (tope de la pila).
     *
     * @return nivel actual
     */
    public int nivelActual() {
        return niveles.peek();
    }

    /**
     * Retorna cuántos niveles (excluyendo el 0 base) hay actualmente.
     *
     * @return cantidad de indentaciones activas
     */
    public int cantidadIndentaciones() {
        return Math.max(0, niveles.size() - 1);
    }

    /**
     * Apila un nuevo nivel de indentación.
     *
     * @param nuevoNivel nivel a apilar
     */
    public void push(int nuevoNivel) {
        niveles.push(nuevoNivel);
    }

    /**
     * Desapila un nivel (sin bajar del 0 base).
     *
     * @return nivel que fue removido
     */
    public int pop() {
        if (niveles.size() <= 1) {
            return 0;
        }
        return niveles.pop();
    }

    /**
     * Indica si el valor existe dentro de la pila.
     *
     * @param nivel nivel a buscar
     * @return true si existe
     */
    public boolean contieneNivel(int nivel) {
        return niveles.contains(nivel);
    }

    /**
     * Devuelve el máximo de niveles permitido (recomendación: 5).
     *
     * @return max niveles
     */
    public int getMaxNiveles() {
        return maxNiveles;
    }
    // endregion
}