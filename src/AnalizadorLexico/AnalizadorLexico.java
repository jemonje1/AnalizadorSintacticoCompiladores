package AnalizadorLexico;

import Archivo.ArchivoMiniLang;
import Stack.PilaIdentacion;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Analizador léxico para MiniLang.
 * Reconoce tokens, NEWLINE , INDENT/DEDENT y reporta errores sin detener ejecución.
 */
public class AnalizadorLexico {

    // region ATRIBUTOS
    private final ArchivoMiniLang archivo;
    private final Token token;
    private final PilaIdentacion pilaIndent;

    // Control de "inicio de línea" para indentación
    private boolean despuesDeNewline;
    // endregion

    // region CONSTRUCTOR
    public AnalizadorLexico() {
        this.archivo = new ArchivoMiniLang();
        this.token = new Token();
        this.pilaIndent = new PilaIdentacion(5);
        this.despuesDeNewline = true;
    }
    // endregion

    // region METODOS - INTERFAZ
    /**
     * Analiza el archivo de entrada y genera:
     * - archivo .out con tokens
     * - salida por consola con errores o éxito
     *
     * @param rutaArchivo ruta del archivo .mlng
     */
    public void analizarArchivo(String rutaArchivo) {
        token.limpiar();
        resetIndentacion();
        try {
            List<String> lineas = archivo.leerLineas(rutaArchivo);
            analizarLineas(lineas);
            // Al terminar, cerrar indentación pendiente
            cerrarIndentacionAlFinal(lineas.size() == 0 ? 1 : lineas.size());
            // Emitir EOF
            token.agregarToken(Token.TipoToken.EOF, "EOF", Math.max(1, lineas.size()), 1, 1);
            // Escribir .out
            escribirSalida(rutaArchivo);
            // Mostrar errores o éxito
            if (token.hayErrores()) {
                System.out.println("\nAnalisis finalizado con ERRORES LEXICOS");
                token.mostrarErrores();
            } else {
                System.out.println("\nAnalisis finalizado EXITOSAMENTE");
                System.out.println("Tokens generados (en orden): " + token.getTokensEnOrden().size());
            }
        } catch (Exception e) {
            System.out.println("Error durante el analisis: " + e.getMessage());
        }
    }

    /**
     * Muestra tokens en consola (orden + resumen).
     */
    public void mostrarTokensEnConsola() {
        token.mostrarTokensEnOrden();
        token.mostrarTokensResumen();
    }
    // endregion

    // region METODOS - ANALISIS PRINCIPAL
    /**
     * Analiza todas las líneas del archivo y genera tokens.
     *
     * @param lineas las líneas del archivo a analizar
     */
    private void analizarLineas(List<String> lineas) {
        int lineaActual = 0;

        for (String linea : lineas) {
            lineaActual++;
            if (linea == null) linea = "";

            // Manejo de comentarios de línea: # ... (se ignoran)
            // Si hay código antes del #, se conserva.
            String sinComentario = recortarComentario(linea);

            // Determinar si la línea tiene contenido (tokens) luego de quitar comentarios y espacios
            boolean lineaConContenido = !sinComentario.trim().isEmpty();

            // Indentación: solo se aplica si la línea tiene contenido
            if (despuesDeNewline) {
                if (lineaConContenido) {
                    procesarIndentacion(lineaActual, sinComentario);
                }
                despuesDeNewline = false;
            }

            if (lineaConContenido) {
                tokenizarLinea(lineaActual, sinComentario);
                // NEWLINE (al final de línea con tokens)
                int colNL = Math.max(1, linea.length() + 1);
                token.agregarToken(Token.TipoToken.NEWLINE, "\\n", lineaActual, colNL, colNL);
                despuesDeNewline = true;
            } else {
                // Línea vacía o solo comentario: no emite NEWLINE
                // Se mantiene despuesDeNewline=true para que la próxima línea calcule indentación.
                despuesDeNewline = true;
            }
        }
    }

    /**
     * Recorta el comentario de línea (inicia con #).
     *
     * @param linea línea original
     * @return línea sin comentario
     */
    private String recortarComentario(String linea) {
        int idx = linea.indexOf('#');
        if (idx < 0) return linea;
        return linea.substring(0, idx);
    }
    // endregion

    // region METODOS - INDENTACION
    /**
     * Procesa la indentación al inicio de una línea.
     * - Si sube: emite INDENT
     * - Si baja: emite DEDENT(s)
     * - Si baja a nivel que no existe: error y sincroniza al nivel más cercano inferior
     *
     * @param lineaNum número de línea
     * @param linea contenido (sin comentarios)
     */
    private void procesarIndentacion(int lineaNum, String linea) {
        // Contar espacios/tabs iniciales
        int pos = 0;
        int columnasIndent = 0;
        boolean tieneTab = false;
        boolean tieneEspacio = false;

        while (pos < linea.length()) {
            char c = linea.charAt(pos);
            if (c == '\t') {
                tieneTab = true;
                columnasIndent += 4; // normalización: 1 tab = 4 columnas
                pos++;
            } else if (c == ' ') {
                tieneEspacio = true;
                columnasIndent += 1;
                pos++;
            } else if (c == '\r') {
                pos++;
            } else {
                break;
            }
        }

        // Regla simple: si mezcla tabs y espacios al inicio, se permite pero se reporta advertencia como error léxico
        // (esto es una decisión de diseño; documentar en README)
        if (tieneTab && tieneEspacio) {
            token.reportarError(lineaNum, 1, "Indentacion mezcla tabs y espacios (normalizado a columnas)");
        }

        int actual = pilaIndent.nivelActual();

        // Límite de indentaciones
        // Se considera como niveles activos (sin contar el 0 base)
        if (columnasIndent > actual && pilaIndent.cantidadIndentaciones() + 1 > pilaIndent.getMaxNiveles()) {
            token.reportarError(lineaNum, 1, "Indentacion excede el maximo de " + pilaIndent.getMaxNiveles() + " niveles");
            // sincronizar: no apilar más allá, quedarse en el nivel actual
            return;
        }

        if (columnasIndent > actual) {
            pilaIndent.push(columnasIndent);
            token.agregarToken(Token.TipoToken.INDENT, "INDENT", lineaNum, 1, 1);
            return;
        }

        if (columnasIndent == actual) {
            return;
        }

        // Disminuye
        if (pilaIndent.contieneNivel(columnasIndent)) {
            while (pilaIndent.nivelActual() > columnasIndent) {
                pilaIndent.pop();
                token.agregarToken(Token.TipoToken.DEDENT, "DEDENT", lineaNum, 1, 1);
            }
            return;
        }

        // No existe el nivel
        token.reportarError(lineaNum, 1, "Indentacion invalida. No existe el nivel " + columnasIndent + " en la pila");

        // Sincronizar al nivel más cercano inferior
        while (pilaIndent.nivelActual() > columnasIndent && pilaIndent.nivelActual() != 0) {
            pilaIndent.pop();
            token.agregarToken(Token.TipoToken.DEDENT, "DEDENT", lineaNum, 1, 1);
        }
    }

    /**
     * Cierra toda indentación pendiente al finalizar el archivo.
     *
     * @param lineaFinal línea final usada para ubicar los DEDENT
     */
    private void cerrarIndentacionAlFinal(int lineaFinal) {
        while (pilaIndent.cantidadIndentaciones() > 0) {
            pilaIndent.pop();
            token.agregarToken(Token.TipoToken.DEDENT, "DEDENT", lineaFinal, 1, 1);
        }
    }

    /**
     * Resetea la pila de indentación.
     */
    private void resetIndentacion() {
        // No se recrea la pila para mantener simple; se desapila hasta dejar base
        while (pilaIndent.cantidadIndentaciones() > 0) {
            pilaIndent.pop();
        }
        despuesDeNewline = true;
    }
    // endregion

    // region METODOS - TOKENIZACION
    /**
     * Tokeniza una línea completa (sin comentarios).
     *
     * @param lineaNum número de línea
     * @param linea contenido
     */
    private void tokenizarLinea(int lineaNum, String linea) {
        int i = 0;
        int length = linea.length();

        // Saltar indentación inicial (ya fue procesada)
        while (i < length && (linea.charAt(i) == ' ' || linea.charAt(i) == '\t' || linea.charAt(i) == '\r')) {
            i++;
        }

        while (i < length) {
            char c = linea.charAt(i);

            // Ignorar espacios internos
            if (c == ' ' || c == '\r' || c == '\t') {
                i++;
                continue;
            }

            int colInicio = i + 1;

            // Strings
            if (c == '"') {
                int inicioQuote = i;
                i++; // consumir "
                StringBuilder sb = new StringBuilder();
                boolean cerrado = false;

                while (i < length) {
                    char cc = linea.charAt(i);
                    if (cc == '"') {
                        cerrado = true;
                        i++;
                        break;
                    }
                    sb.append(cc);
                    i++;
                }

                if (!cerrado) {
                    token.reportarError(lineaNum, colInicio, "Cadena sin cerrar");
                    // Recuperación: consumir hasta final de línea
                    token.agregarToken(Token.TipoToken.STRING_LIT, sb.toString(), lineaNum, colInicio, length);
                    return;
                }

                int colFin = i; // ya avanzó 1 después de cerrar
                token.agregarToken(Token.TipoToken.STRING_LIT, sb.toString(), lineaNum, colInicio, colFin);
                continue;
            }

            // Operadores de dos caracteres
            if (i + 1 < length) {
                String dos = "" + c + linea.charAt(i + 1);
                if (dos.equals("<=") ) {
                    token.agregarToken(Token.TipoToken.LE, dos, lineaNum, colInicio, colInicio + 1);
                    i += 2;
                    continue;
                }
                if (dos.equals(">=") ) {
                    token.agregarToken(Token.TipoToken.GE, dos, lineaNum, colInicio, colInicio + 1);
                    i += 2;
                    continue;
                }
                if (dos.equals("==") ) {
                    token.agregarToken(Token.TipoToken.EQ, dos, lineaNum, colInicio, colInicio + 1);
                    i += 2;
                    continue;
                }
                if (dos.equals("!=") ) {
                    token.agregarToken(Token.TipoToken.NE, dos, lineaNum, colInicio, colInicio + 1);
                    i += 2;
                    continue;
                }
                if (dos.equals("++") ) {
                    token.agregarToken(Token.TipoToken.INC, dos, lineaNum, colInicio, colInicio + 1);
                    i += 2;
                    continue;
                }
                if (dos.equals("--") ) {
                    token.agregarToken(Token.TipoToken.DEC, dos, lineaNum, colInicio, colInicio + 1);
                    i += 2;
                    continue;
                }
            }

            // Números (int/float/percent)
            if (Character.isDigit(c)) {
                int start = i;
                while (i < length && Character.isDigit(linea.charAt(i))) {
                    i++;
                }
                boolean esFloat = false;

                if (i < length && linea.charAt(i) == '.') {
                    int punto = i;
                    i++;
                    int digitsAfter = 0;
                    while (i < length && Character.isDigit(linea.charAt(i))) {
                        i++;
                        digitsAfter++;
                    }
                    if (digitsAfter == 0) {
                        // número mal formado: 12.
                        token.reportarError(lineaNum, punto + 1, "Numero flotante mal formado");
                        // Recuperación: tratar como INTNUM hasta antes del punto y continuar
                        String entero = linea.substring(start, punto);
                        token.agregarToken(Token.TipoToken.INTNUM, entero, lineaNum, start + 1, punto);
                        continue;
                    }
                    esFloat = true;
                }

                // Percent (solo permitido para enteros o floats? en tu referencia: [0-9]+%)
                if (i < length && linea.charAt(i) == '%') {
                    String per = linea.substring(start, i + 1);
                    token.agregarToken(Token.TipoToken.PERNUM, per, lineaNum, start + 1, i + 1);
                    i++;
                    continue;
                }

                String num = linea.substring(start, i);
                if (esFloat) {
                    token.agregarToken(Token.TipoToken.FLOATNUM, num, lineaNum, start + 1, i);
                } else {
                    token.agregarToken(Token.TipoToken.INTNUM, num, lineaNum, start + 1, i);
                }
                continue;
            }

            // Identificadores / keywords
            if (Character.isLetter(c)) {
                int start = i;
                i++;
                while (i < length && (Character.isLetterOrDigit(linea.charAt(i)))) {
                    i++;
                }
                String lex = linea.substring(start, i);

                // ID max 31
                if (lex.length() > 31) {
                    token.reportarError(lineaNum, colInicio, "Identificador excede 31 caracteres, se trunco");
                    lex = lex.substring(0, 31);
                }

                Token.TipoToken tipo = keywordOrId(lex);
                token.agregarToken(tipo, lex, lineaNum, start + 1, start + lex.length());
                continue;
            }

            // Símbolos / operadores de 1 char
            switch (c) {
                case '{' -> token.agregarToken(Token.TipoToken.LBRACE, "{", lineaNum, colInicio, colInicio);
                case '}' -> token.agregarToken(Token.TipoToken.RBRACE, "}", lineaNum, colInicio, colInicio);
                case '(' -> token.agregarToken(Token.TipoToken.LPAREN, "(", lineaNum, colInicio, colInicio);
                case ')' -> token.agregarToken(Token.TipoToken.RPAREN, ")", lineaNum, colInicio, colInicio);
                case ';' -> token.agregarToken(Token.TipoToken.SEMI, ";", lineaNum, colInicio, colInicio);
                case ',' -> token.agregarToken(Token.TipoToken.COMMA, ",", lineaNum, colInicio, colInicio);

                case '+' -> token.agregarToken(Token.TipoToken.PLUS, "+", lineaNum, colInicio, colInicio);
                case '-' -> token.agregarToken(Token.TipoToken.MINUS, "-", lineaNum, colInicio, colInicio);
                case '*' -> token.agregarToken(Token.TipoToken.STAR, "*", lineaNum, colInicio, colInicio);
                case '/' -> token.agregarToken(Token.TipoToken.SLASH, "/", lineaNum, colInicio, colInicio);

                case '=' -> token.agregarToken(Token.TipoToken.ASSIGN, "=", lineaNum, colInicio, colInicio);
                case '<' -> token.agregarToken(Token.TipoToken.LT, "<", lineaNum, colInicio, colInicio);
                case '>' -> token.agregarToken(Token.TipoToken.GT, ">", lineaNum, colInicio, colInicio);
                case '!' -> token.agregarToken(Token.TipoToken.NOT, "!", lineaNum, colInicio, colInicio);

                default -> {
                    token.reportarError(lineaNum, colInicio, "Caracter inesperado '" + c + "'");
                    token.agregarToken(Token.TipoToken.UNKNOWN, String.valueOf(c), lineaNum, colInicio, colInicio);
                }
            }

            i++;
        }
    }

    /**
     * Determina si un lexema es keyword o ID.
     *
     * @param lex lexema
     * @return tipo
     */
    private Token.TipoToken keywordOrId(String lex) {
        switch (lex) {
            case "int" -> { return Token.TipoToken.KW_INT; }
            case "float" -> { return Token.TipoToken.KW_FLOAT; }
            case "string" -> { return Token.TipoToken.KW_STRING; }
            case "bool" -> { return Token.TipoToken.KW_BOOL; }
            case "if" -> { return Token.TipoToken.KW_IF; }
            case "else" -> { return Token.TipoToken.KW_ELSE; }
            case "for" -> { return Token.TipoToken.KW_FOR; }
            case "while" -> { return Token.TipoToken.KW_WHILE; }
            case "true" -> { return Token.TipoToken.KW_TRUE; }
            case "false" -> { return Token.TipoToken.KW_FALSE; }
            case "Read" -> { return Token.TipoToken.KW_READ; }
            case "Write" -> { return Token.TipoToken.KW_WRITE; }
            default -> { return Token.TipoToken.ID; }
        }
    }
    // endregion

    // region METODOS - SALIDA
    /**
     * Escribe el archivo .out con los tokens en orden.
     *
     * @param rutaArchivo ruta del archivo de entrada
     */
    private void escribirSalida(String rutaArchivo) {
        Path salida = archivo.obtenerRutaSalida(rutaArchivo);

        try (BufferedWriter bw = Files.newBufferedWriter(salida, StandardCharsets.UTF_8)) {
            for (Token.TokenInfo t : token.getTokensEnOrden()) {
                bw.write(t.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("No se pudo escribir el archivo de salida: " + e.getMessage());
        }

        System.out.println("Archivo generado: " + salida.toAbsolutePath());
    }
    // endregion
}