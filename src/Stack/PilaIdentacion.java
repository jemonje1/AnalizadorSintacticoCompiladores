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
        this.niveles.push(0);
    }

    /**
     * Retorna el nivel actual de la pila.
     *
     * @return nivel actual
     */
    public int getNivelActual() {
        return niveles.peek();
    }

    /**
     * Indica si un nivel ya existe en la pila.
     *
     * @param nivel nivel a buscar
     * @return true si existe
     */
    public boolean contieneNivel(int nivel) {
        return niveles.contains(nivel);
    }

    /**
     * Agrega un nuevo nivel a la pila.
     *
     * @param nivel nivel a apilar
     */
    public void apilarNivel(int nivel) {
        niveles.push(nivel);
    }

    /**
     * Elimina el nivel superior de la pila, sin quitar el 0 base.
     *
     * @return nivel desapilado
     */
    public int desapilarNivel() {
        if (niveles.size() <= 1) {
            return 0;
        }
        return niveles.pop();
    }

    /**
     * Valida si el nivel recibido está dentro del rango permitido.
     *
     * @param nivel nivel a validar
     * @return true si es válido
     */
    public boolean nivelValido(int nivel) {
        return nivel >= 0 && nivel <= maxNiveles;
    }

    /**
     * Indica si solo queda el nivel base.
     *
     * @return true si está en base
     */
    public boolean estaEnBase() {
        return niveles.size() == 1 && niveles.peek() == 0;
    }

    /**
     * Reinicia la pila al estado base.
     */
    public void reset() {
        niveles.clear();
        niveles.push(0);
    }

    /**
     * Retorna el máximo de niveles permitidos.
     *
     * @return máximo de niveles
     */
    public int getMaxNiveles() {
        return maxNiveles;
    }

    /**
     * Valida si el programa terminó en nivel 0.
     *
     * @param lineaUltima última línea procesada
     * @return true si terminó correctamente
     */
    public boolean validarCierreFinal(int lineaUltima) {
        if (!estaEnBase()) {
            System.err.println("Error de identacion: El programa finalizo en nivel " + getNivelActual()
                    + ". No se cerraron todas las identaciones correctamente al llegar al final (Linea "
                    + lineaUltima + ").");
            return false;
        }
        return true;
    }
}