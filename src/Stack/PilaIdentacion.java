package Stack;

import java.util.ArrayDeque;
import java.util.Deque;

public class PilaIdentacion {

    private final Deque<Integer> niveles;
    private final int maxNiveles;

    public PilaIdentacion(int maxNiveles) {
        this.niveles = new ArrayDeque<>();
        this.maxNiveles = maxNiveles;
        this.niveles.push(0);
    }

    public int getNivelActual() {
        return niveles.peek();
    }

    public boolean contieneNivel(int nivel) {
        return niveles.contains(nivel);
    }

    public void apilarNivel(int nivel) {
        niveles.push(nivel);
    }

    public int desapilarNivel() {
        if (niveles.size() <= 1) {
            return 0;
        }
        return niveles.pop();
    }

    public boolean nivelValido(int nivel) {
        return (niveles.size() - 1) < maxNiveles;
    }

    public boolean estaEnBase() {
        return niveles.size() == 1 && niveles.peek() == 0;
    }

    public int getProfundidad() {
        return niveles.size() - 1;
    }

    public void reset() {
        niveles.clear();
        niveles.push(0);
    }
}