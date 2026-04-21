import AnalizadorLexico.AnalizadorLexico;
import AnalizadorLexico.Token;
import AnalizadorSintactico.AnalizadorSintactico;
import Archivo.ArchivoMiniLang;
import Stack.PilaIdentacion;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Inicio {

    private final Scanner scanner;
    private final ArchivoMiniLang gestorArchivo;

    public Inicio() {
        this.scanner = new Scanner(System.in);
        this.gestorArchivo = new ArchivoMiniLang();
    }

    public void iniciar() {
        boolean salir = false;

        while (!salir) {
            System.out.println("\n----- Menu -----");
            System.out.println("1. Cargar Archivo");
            System.out.println("2. Salir");
            System.out.println("----------------");
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    menuArchivo();
                    break;
                case "2":
                    salir = true;
                    System.out.println("Programa finalizado.");
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }

        scanner.close();
    }

    private void menuArchivo() {
        System.out.print("\nIngrese la ruta del archivo (.mlng): ");
        String rutaEntrada = scanner.nextLine().trim();

        String contenido;
        try {
            contenido = gestorArchivo.leerContenido(rutaEntrada);
            System.out.println("Archivo .mlng leido correctamente.");
        } catch (Exception e) {
            System.out.println("No se pudo leer el archivo: " + e.getMessage());
            return;
        }

        boolean volver = false;
        while (!volver) {
            System.out.println("\n------ Menu de archivo ------");
            System.out.println("1. Analizar (Generar .out)");
            System.out.println("2. Mostrar Tokens y analisis sintactico");
            System.out.println("3. Volver");
            System.out.println("-----------------------------");
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    ejecutarAnalisis(rutaEntrada, contenido, true, false);
                    break;
                case "2":
                    ejecutarAnalisis(rutaEntrada, contenido, false, true);
                    break;
                case "3":
                    volver = true;
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    private void ejecutarAnalisis(String rutaEntrada, String contenido, boolean generarOut, boolean mostrarConsola) {
        try {
            AnalizadorLexico lexer = new AnalizadorLexico(contenido);
            List<Token> tokens = lexer.analizar();

            PilaIdentacion pila = lexer.getPila();
            List<String> erroresIndentacion = new ArrayList<>();

            if (!pila.estaEnBase()) {
                erroresIndentacion.add("line " + lexer.getLineaActual() + ", col 1: ERROR El programa finaliza en nivel "
                        + pila.getNivelActual() + " en lugar de nivel 0. No se cerraron todas las indentaciones.");
            }

            AnalizadorSintactico sintactico = new AnalizadorSintactico();
            boolean sintaxisCorrecta = sintactico.analizar(tokens);

            List<String> erroresLex = lexer.getErrores();
            List<String> erroresSin = sintactico.getParser().getErrores();

            boolean compilacionExitosa = erroresLex.isEmpty()
                    && erroresIndentacion.isEmpty()
                    && erroresSin.isEmpty()
                    && sintaxisCorrecta;

            if (mostrarConsola) {
                System.out.println("\n--- TOKENS ---");
                for (Token token : tokens) {
                    System.out.println(token);
                }

                System.out.println("\n--- RESUMEN DE ERRORES ---");
                int total = erroresLex.size() + erroresIndentacion.size() + erroresSin.size();
                System.out.println("Total de errores: " + total);

                for (String err : erroresLex) {
                    System.out.println("[LEXICO] " + err);
                }
                for (String err : erroresIndentacion) {
                    System.out.println("[INDENTACION] " + err);
                }
                for (String err : erroresSin) {
                    System.out.println("[SINTACTICO] " + err);
                }

                if (total == 0) {
                    System.out.println("Sin errores.");
                }

                System.out.println("\nResultado: " + (compilacionExitosa ? "CADENA ACEPTADA" : "CADENA RECHAZADA"));
            }

            if (generarOut) {
                generarReporte(rutaEntrada, tokens, erroresLex, erroresIndentacion, erroresSin, compilacionExitosa);
            }

        } catch (Exception e) {
            System.out.println("ERROR CRITICO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generarReporte(String ruta, List<Token> tokens, List<String> erroresLex,
                                List<String> erroresInd, List<String> erroresSin, boolean exitosa) {
        StringBuilder reporte = new StringBuilder();

        reporte.append("REPORTE DE COMPILACION MINILANG\n");
        reporte.append("Archivo: ").append(ruta).append("\n\n");

        reporte.append("--- LISTADO DE TOKENS ---\n");
        for (Token t : tokens) {
            reporte.append(t.toString()).append("\n");
        }
        reporte.append("\n");

        reporte.append("--- RESUMEN DE ERRORES ---\n");
        List<String> todosLosErrores = new ArrayList<>();
        todosLosErrores.addAll(erroresLex);
        todosLosErrores.addAll(erroresInd);
        todosLosErrores.addAll(erroresSin);

        if (todosLosErrores.isEmpty()) {
            reporte.append("No se detectaron errores durante la compilacion.\n");
        } else {
            reporte.append("Total de errores: ").append(todosLosErrores.size()).append("\n\n");
            for (String err : erroresLex) {
                reporte.append("[LEXICO] ").append(err).append("\n");
            }
            for (String err : erroresInd) {
                reporte.append("[INDENTACION] ").append(err).append("\n");
            }
            for (String err : erroresSin) {
                reporte.append("[SINTACTICO] ").append(err).append("\n");
            }
        }

        reporte.append("\n--- RESULTADO FINAL ---\n");
        reporte.append(exitosa
                ? "COMPILACION EXITOSA - CADENA ACEPTADA\n"
                : "COMPILACION FALLIDA - CADENA RECHAZADA\n");

        try {
            Path rutaSalida = gestorArchivo.obtenerRutaSalida(ruta);
            Files.writeString(rutaSalida, reporte.toString());
            System.out.println("Reporte generado en: " + rutaSalida.toAbsolutePath());
        } catch (Exception e) {
            System.out.println("Error al escribir el archivo .out: " + e.getMessage());
        }
    }
}