

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

/**
 * Orquestador del compilador: Maneja la entrada de usuario, 
 * los llamados a los analizadores y la generación del archivo .out.
 * 
 * Flujo: .mlng -> Análisis Léxico -> Validación Pila Indentación -> Análisis Sintáctico -> .out
 */
public class Inicio {

    private final Scanner scanner;
    private final ArchivoMiniLang gestorArchivo;

    public Inicio() {
        this.scanner = new Scanner(System.in);
        this.gestorArchivo = new ArchivoMiniLang();
    }

    /**
     * Lógica principal de ejecución.
     * Flujo: Entrada .mlng -> Análisis Léxico -> Pila de Indentación -> Análisis Sintáctico -> Salida .out
     */
    public void iniciar() {
        System.out.println("      ANALIZADOR SINTACTICO     \n");
        System.out.print("Ingrese la ruta del archivo (.mlng): ");
        
        String rutaEntrada = scanner.nextLine();

        try {
            // 1. Validar y leer el archivo .mlng
            String contenido = gestorArchivo.leerContenido(rutaEntrada);
            System.out.println(" Archivo .mlng leido correctamente");

            // 2. Análisis Léxico (genera tokens e INDENT/DEDENT con pila de identación)
            System.out.println("• Iniciando Analisis Lexico...");
            AnalizadorLexico lexer = new AnalizadorLexico(contenido);
            List<Token> tokens = lexer.analizar();
            System.out.println(" Analisis Lexico completado - " + tokens.size() + " tokens generados");
            
            // Obtener la pila de identación del lexer para validación
            PilaIdentacion pila = lexer.getPila();
            
            // 3. Validación de Pila de Indentación
            System.out.println("• Validando Pila de Indentacion...");
            List<String> erroresIndentacion = new ArrayList<>();
            if (!pila.estaEnBase()) {
                erroresIndentacion.add("Error de identacion: El programa finaliza en nivel " + 
                    pila.getNivelActual() + " en lugar de nivel 0. No se cerraron todas las indentaciones.");
            }
            
            if (erroresIndentacion.isEmpty()) {
                System.out.println("Pila de Indentacion válida");
            } else {
                System.out.println("Errores de Indentacion detectados");
            }

            // 4. Análisis Sintáctico
            System.out.println("Iniciando Analisis Sintactico...");
            AnalizadorSintactico sintactico = new AnalizadorSintactico();
            boolean sintaxisCorrecta = sintactico.analizar(tokens);
            System.out.println("Analisis Sintactico completado");

            // 5. Generar reporte final
            boolean compilacionExitosa = lexer.getErrores().isEmpty() && 
                                        erroresIndentacion.isEmpty() && 
                                        sintactico.getParser().getErrores().isEmpty();
            
            generarReporte(rutaEntrada, tokens, lexer.getErrores(), erroresIndentacion, 
                          sintactico.getParser().getErrores(), compilacionExitosa);

        } catch (Exception e) {
            System.err.println("ERROR CRITICO: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    /**
     * Crea el contenido del reporte y lo escribe en el archivo .out.
     * Incluye tokens, errores léxicos, de indentación y sintácticos.
     */
    private void generarReporte(String ruta, List<Token> tokens, List<String> erroresLex, 
                               List<String> erroresInd, List<String> erroresSin, boolean exitosa) {
        StringBuilder reporte = new StringBuilder();
        
        // Encabezado
        reporte.append("REPORTE DE COMPILACION MINILANG\n");
        reporte.append("Archivo: ").append(ruta).append("\n");
        reporte.append("\n\n");
        
        // Sección 1: Tokens
        reporte.append("--- LISTADO DE TOKENS ---\n");
        for (Token t : tokens) {
            reporte.append(t.toString()).append("\n");
        }
        reporte.append("\n");
        
        // Sección 2: Errores (combinados)
        reporte.append("--- RESUMEN DE ERRORES ---\n");
        List<String> todosLosErrores = new ArrayList<>();
        todosLosErrores.addAll(erroresLex);
        todosLosErrores.addAll(erroresInd);
        todosLosErrores.addAll(erroresSin);
        
        if (todosLosErrores.isEmpty()) {
            reporte.append("No se detectaron errores durante la compilación.\n");
        } else {
            reporte.append(String.format("Total de errores: %d\n\n", todosLosErrores.size()));
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
        
        // Sección 3: Resultado Final
        reporte.append("\n--- RESULTADO FINAL ---\n");
        reporte.append(exitosa ? "COMPILACION EXITOSA - CADENA ACEPTADA\n" 
                               : "COMPILACION FALLIDA - CADENA RECHAZADA\n");

        // Escribir archivo .out
        try {
            Path rutaSalida = gestorArchivo.obtenerRutaSalida(ruta);
            Files.writeString(rutaSalida, reporte.toString());
            System.out.println("\nPROCESO FINALIZADO");
            System.out.println("Reporte generado en: " + rutaSalida.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error al escribir el archivo .out: " + e.getMessage());
        }
    }
}