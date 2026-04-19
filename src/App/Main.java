/*
    @author: Sophia Corea
    @author: Javier Monje
*/

package App;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Punto de entrada del programa.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        // Permite caracteres del abecedario español
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        Inicio inicio = new Inicio();
        inicio.iniciar();
    }
}