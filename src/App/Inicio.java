package App;

import AnalizadorSintactico.AnalizadorSintactico;
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
}