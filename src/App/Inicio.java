package App;

import AnalizadorLexico.AnalizadorLexico;
import java.util.Scanner;

/**
 * Menú principal y flujo del programa en consola.
 */
public class Inicio {

    // region ATRIBUTOS
    private final Scanner scanner;
    private AnalizadorLexico analizador;
    private String rutaArchivo;
    // endregion

    // region CONSTRUCTOR
    public Inicio() {
        this.scanner = new Scanner(System.in);
        this.analizador = null;
        this.rutaArchivo = null;
        boolean i = false;
    }
    // endregion

    // region METODOS - NAVEGACION
    /**
     * Inicia el menú principal.
     */
    public void iniciar() {
        boolean salir = false;
        while (!salir) {
            imprimirMenuPrincipal();
            int opcion = obtenerOpcionValida();

            switch (opcion) {
                case 1 -> cargarArchivo();
                case 2 -> salir = true;
                default -> System.out.println("Opcion no valida, intente de nuevo.");
            }
        }
        System.out.println("Saliendo del programa.");
    }

    /**
     * Muestra el submenú cuando ya hay un archivo cargado.
     */
    private void menuArchivo() {
        boolean volver = false;
        while (!volver) {
            imprimirMenuArchivo();
            int opcion = obtenerOpcionValida();

            switch (opcion) {
                case 1 -> ejecutarAnalisis();
                case 2 -> mostrarTokensEnMemoria();
                case 3 -> volver = true;
                default -> System.out.println("Opcion no valida, intente de nuevo.");
            }
        }
    }
    // endregion

    // region METODOS - ACCIONES
    /**
     * Solicita al usuario la ruta del archivo .mlng y prepara el analizador.
     */
    private void cargarArchivo() {
        System.out.println("Ingrese la ruta del archivo .mlng a analizar:");
        String ruta = scanner.nextLine().trim();

        try {
            this.analizador = new AnalizadorLexico();
            this.rutaArchivo = ruta;
            System.out.println("Archivo registrado.\n");
            menuArchivo();
        } catch (Exception e) {
            System.out.println("No se pudo preparar el analizador: " + e.getMessage());
        }
    }

    /**
     * Ejecuta el análisis léxico y muestra errores o éxito.
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
     * Muestra los tokens que quedaron en memoria (último análisis ejecutado).
     */
    private void mostrarTokensEnMemoria() {
        if (analizador == null) {
            System.out.println("Primero debe cargar y analizar un archivo.");
            return;
        }
        analizador.mostrarTokensEnConsola();
    }
    // endregion

    // region METODOS - IMPRESION
    /**
     * Imprime el menú principal.
     */
    private void imprimirMenuPrincipal() {
        System.out.println("_____________ MiniLang - Analizador Lexico _____________");
        System.out.println("[1] Cargar archivo (.mlng)");
        System.out.println("[2] Salir");
        System.out.print("Seleccione una opcion: ");
    }

    /**
     * Imprime el menú del archivo.
     */
    private void imprimirMenuArchivo() {
        System.out.println("\n_____________ Opciones del archivo _____________");
        System.out.println("[1] Analizar (genera .out y reporte)");
        System.out.println("[2] Mostrar tokens en consola");
        System.out.println("[3] Volver");
        System.out.print("Seleccione una opcion: ");
    }
    // endregion

    // region METODOS - UTILIDADES
    /**
     * Obtiene una opción numérica válida desde consola.
     * @return entero con la opción elegida.
     */
    private int obtenerOpcionValida() {
        while (true) {
            try {
                String entrada = scanner.nextLine().trim();
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
    // endregion
}