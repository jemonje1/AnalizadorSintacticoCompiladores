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

    /**
     * Inicializa la pila con el nivel base 0.
     * @param maxNiveles El límite máximo de profundidad permitido.
     */
    public PilaIdentacion(int maxNiveles) {
        this.niveles = new ArrayDeque<>();
        this.maxNiveles = maxNiveles;
        this.niveles.push(0); // El nivel base siempre es 0
    }

    /**
     * Retorna el nivel de espacios actual en el tope de la pila.
     */
    public int getNivelActual() {
        return niveles.peek();
    }

    /**
     * Indica si un nivel específico ya existe en la pila.
     */
    public boolean contieneNivel(int nivel) {
        return niveles.contains(nivel);
    }

    /**
     * Agrega un nuevo nivel de indentación a la pila.
     */
    public void apilarNivel(int nivel) {
        niveles.push(nivel);
    }

    /**
     * Elimina el nivel superior de la pila.
     */
    public int desapilarNivel() {
        if (niveles.size() <= 1) {
            return 0;
        }
        return niveles.pop();
    }

    /**
     * Valida si el nivel recibido está dentro del rango permitido (0 a maxNiveles).
     */
    public boolean nivelValido(int nivel) {
        return nivel >= 0 && nivel <= maxNiveles;
    }

    /**
     * Verifica si la pila ha regresado a su estado inicial.
     */
    public boolean estaEnBase() {
        return niveles.size() == 1 && niveles.peek() == 0;
    }

    /**
     * Valida si el programa terminó correctamente en nivel 0.
     * Corregido: sin etiquetas de referencia que causen errores de compilación.
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

    /**
     * Reinicia la pila al estado base 0.
     */
    public void reset() {
        niveles.clear();
        niveles.push(0);
    }
}