package App;

import AnalizadorSintactico.AnalizadorSintactico;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

/**
 * Menú principal y flujo del programa en consola.
 */
public class Inicio {

    private final Scanner ingreso;
    private AnalizadorSintactico analizador;
    private String rutaArchivo;

    public Inicio() {
        this.ingreso = new Scanner(System.in);
        this.analizador = null;
        this.rutaArchivo = null;
    }

    /**
     * Inicia el menú principal.
     */
    public void iniciar() {
        generarAnalizadores();
        boolean abierto = true;

        while (abierto) {
            imprimirMenuPrincipal();
            int opcion = obtenerOpcionValida();

            switch (opcion) {
                case 1 -> cargarArchivo();
                case 2 -> abierto = false;
                default -> System.out.println("Opcion no valida, intente de nuevo.");
            }
        }

        System.out.println("Saliendo del programa.");
    }

    /**
     * Muestra el submenú cuando ya existe un archivo cargado.
     */
    private void menuArchivo() {
        boolean abierto = true;

        while (abierto) {
            imprimirMenuArchivo();
            int opcion = obtenerOpcionValida();

            switch (opcion) {
                case 1 -> ejecutarAnalisis();
                case 2 -> mostrarResultado();
                case 3 -> abierto = false;
                default -> System.out.println("Opcion no valida, intente de nuevo.");
            }
        }
    }

    /**
     * Solicita la ruta del archivo y prepara el analizador.
     */
    private void cargarArchivo() {
        System.out.println("Ingrese la ruta del archivo .mlng a analizar:");
        String rutaIngresada = ingreso.nextLine().trim();

        try {
            this.analizador = new AnalizadorSintactico();
            this.rutaArchivo = rutaIngresada;
            System.out.println("Archivo registrado.\n");
            menuArchivo();
        } catch (Exception e) {
            System.out.println("No se pudo preparar el analizador: " + e.getMessage());
        }
    }

    /**
     * Ejecuta el análisis léxico y sintáctico.
     */
    private void ejecutarAnalisis() {
        if (analizador == null || rutaArchivo == null) {
            System.out.println("Primero debe cargar un archivo.");
            return;
        }

        System.out.println("Analizando...\n");
        analizador.analizarArchivo(rutaArchivo);
    }

    /**
     * Muestra el resultado del último análisis.
     */
    private void mostrarResultado() {
        if (analizador == null) {
            System.out.println("Primero debe cargar y analizar un archivo.");
            return;
        }

        analizador.mostrarResultadoFinal();
    }

    /**
     * Imprime el menú principal.
     */
    private void imprimirMenuPrincipal() {
        System.out.println("_____________ MiniLang - Analizador Sintactico _____________");
        System.out.println("[1] Cargar archivo (.mlng)");
        System.out.println("[2] Salir");
        System.out.print("Seleccione una opcion: ");
    }

    /**
     * Imprime el menú del archivo.
     */
    private void imprimirMenuArchivo() {
        System.out.println("\n_____________ Opciones del archivo _____________");
        System.out.println("[1] Analizar");
        System.out.println("[2] Mostrar resultado");
        System.out.println("[3] Volver");
        System.out.print("Seleccione una opcion: ");
    }

    /**
     * Obtiene una opción válida desde consola.
     *
     * @return opción numérica
     */
    private int obtenerOpcionValida() {
        while (true) {
            try {
                String entrada = ingreso.nextLine().trim();

                if (entrada.isEmpty()) {
                    System.out.print("Ingrese un numero valido: ");
                    continue;
                }

                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.print("Entrada invalida. Ingrese un numero valido: ");
            }
        }
    }

    /**
     * Genera los analizadores léxico y sintáctico usando JFlex y CUP.
     */
    private void generarAnalizadores() {
        try {
            String rootPath = System.getProperty("user.dir");
            String rutaLexer = rootPath + File.separator + "src" + File.separator + "AnalizadorLexico" + File.separator + "Lexer.flex";
            String rutaLexerCup = rootPath + File.separator + "src" + File.separator + "AnalizadorLexico" + File.separator + "LexerCup.flex";
            String rutaSintax = rootPath + File.separator + "src" + File.separator + "AnalizadorSintactico" + File.separator + "Sintax.cup";
            
            String[] rutaCup = {"-parser", "Sintax", rutaSintax};

            // Generar Lexer.java
            System.out.println("Generando Lexer.java...");
            File archivoLexer = new File(rutaLexer);
            jflex.Main.generate(archivoLexer);

            // Generar LexerCup.java
            System.out.println("Generando LexerCup.java...");
            File archivoLexerCup = new File(rutaLexerCup);
            jflex.Main.generate(archivoLexerCup);

            // Generar Sintax.java y sym.java
            System.out.println("Generando Sintax.java y sym.java...");
            java_cup.Main.main(rutaCup);

            // Mover sym.java a la carpeta correcta
            Path rutaSymOrigen = Paths.get(rootPath + File.separator + "sym.java");
            Path rutaSymDestino = Paths.get(rootPath + File.separator + "src" + File.separator + "AnalizadorSintactico" + File.separator + "sym.java");
            
            if (Files.exists(rutaSymOrigen)) {
                if (Files.exists(rutaSymDestino)) {
                    Files.delete(rutaSymDestino);
                }
                Files.move(rutaSymOrigen, rutaSymDestino);
                System.out.println("sym.java movido correctamente.");
            }

            // Mover Sintax.java a la carpeta correcta
            Path rutaSintaxOrigen = Paths.get(rootPath + File.separator + "Sintax.java");
            Path rutaSintaxDestino = Paths.get(rootPath + File.separator + "src" + File.separator + "AnalizadorSintactico" + File.separator + "Sintax.java");
            
            if (Files.exists(rutaSintaxOrigen)) {
                if (Files.exists(rutaSintaxDestino)) {
                    Files.delete(rutaSintaxDestino);
                }
                Files.move(rutaSintaxOrigen, rutaSintaxDestino);
                System.out.println("Sintax.java movido correctamente.");
            }

            System.out.println("¡Analizadores generados con éxito!");
        } catch (Exception e) {
            System.err.println("Error generando archivos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}