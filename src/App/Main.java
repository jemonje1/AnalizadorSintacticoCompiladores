package App;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 
Punto de entrada del programa.*/
public class Main {

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        Inicio inicio = new Inicio();
        inicio.iniciar();
    }
}