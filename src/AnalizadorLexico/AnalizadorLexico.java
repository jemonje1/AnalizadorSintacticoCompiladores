package AnalizadorLexico;

import Stack.PilaIdentacion;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalizadorLexico {
    private final String contenido;
    private int pos = 0;
    private int lineaActual = 1;
    private int columnaActual = 1;
    private final List<String> errores;
    private final PilaIdentacion pila;
    private boolean inicioDeLinea = true;

    public AnalizadorLexico(String contenido) {
        this.contenido = contenido == null ? "" : contenido;
        this.errores = new ArrayList<>();
        this.pila = new PilaIdentacion(5);
    }

    public List<Token> analizar() {
        List<Token> tokens = new ArrayList<>();

        while (pos < contenido.length()) {
            if (inicioDeLinea) {
                procesarIndentacion(tokens);
                inicioDeLinea = false;
                if (pos >= contenido.length()) {
                    break;
                }
            }

            char actual = contenido.charAt(pos);

            if (actual == ' ' || actual == '\t') {
                avanzar();
                continue;
            }

            if (actual == '\n' || actual == '\r') {
                manejarSaltoDeLinea(tokens);
                continue;
            }

            Token token = buscarSiguienteToken();
            if (token != null) {
                tokens.add(token);
            } else {
                errores.add(String.format("line %d, col %d: ERROR Caracter inesperado '%c'",
                        lineaActual, columnaActual, actual));
                tokens.add(new Token(Token.TipoToken.DESCONOCIDO, String.valueOf(actual), lineaActual, columnaActual));
                avanzar();
            }
        }

        while (!pila.estaEnBase()) {
            pila.desapilarNivel();
            tokens.add(new Token(Token.TipoToken.DEDENT, "DEDENT", lineaActual, columnaActual));
        }

        tokens.add(new Token(Token.TipoToken.EOF, "$", lineaActual, columnaActual));
        return tokens;
    }

    private void procesarIndentacion(List<Token> tokens) {
        int inicioPos = pos;
        int inicioCol = columnaActual;

        int conteoEspacios = 0;
        while (pos < contenido.length()) {
            char c = contenido.charAt(pos);
            if (c == ' ') {
                conteoEspacios++;
                pos++;
                columnaActual++;
            } else if (c == '\t') {
                conteoEspacios += 4;
                pos++;
                columnaActual++;
            } else {
                break;
            }
        }

        if (pos >= contenido.length()) {
            return;
        }

        char siguiente = contenido.charAt(pos);

        // Línea vacía o con solo espacios: no altera indentación
        if (siguiente == '\n' || siguiente == '\r') {
            return;
        }

        int nivelPrevio = pila.getNivelActual();

        if (conteoEspacios > nivelPrevio) {
            if (!pila.nivelValido(conteoEspacios)) {
                errores.add(String.format("line %d, col 1: ERROR Sobrepaso de limite de indentacion (maximo 5 niveles)",
                        lineaActual));
                return;
            }

            pila.apilarNivel(conteoEspacios);
            tokens.add(new Token(Token.TipoToken.INDENT, "INDENT", lineaActual, 1));
        } else if (conteoEspacios < nivelPrevio) {
            while (conteoEspacios < pila.getNivelActual()) {
                pila.desapilarNivel();
                tokens.add(new Token(Token.TipoToken.DEDENT, "DEDENT", lineaActual, 1));
            }

            if (conteoEspacios != pila.getNivelActual()) {
                errores.add(String.format("line %d, col 1: ERROR Indentacion invalida. No coincide con ningun nivel abierto",
                        lineaActual));
            }
        }
    }

    private void manejarSaltoDeLinea(List<Token> tokens) {
        char actual = contenido.charAt(pos);

        if (actual == '\r' && pos + 1 < contenido.length() && contenido.charAt(pos + 1) == '\n') {
            tokens.add(new Token(Token.TipoToken.NEWLINE, "\\n", lineaActual, columnaActual));
            pos += 2;
            lineaActual++;
            columnaActual = 1;
            inicioDeLinea = true;
            return;
        }

        tokens.add(new Token(Token.TipoToken.NEWLINE, "\\n", lineaActual, columnaActual));
        pos++;
        lineaActual++;
        columnaActual = 1;
        inicioDeLinea = true;
    }

    private Token buscarSiguienteToken() {
        String resto = contenido.substring(pos);

        // Comentarios tipo //...
        if (resto.startsWith("//")) {
            int col = columnaActual;
            int fin = 0;
            while (fin < resto.length() && resto.charAt(fin) != '\n' && resto.charAt(fin) != '\r') {
                fin++;
            }
            String lexema = resto.substring(0, fin);
            avanzarMulti(fin);
            return new Token(Token.TipoToken.COMMENT, lexema, lineaActual, col);
        }

        // Palabras reservadas
        String[] reservadas = {
                "int", "float", "string", "bool", "if", "else", "for", "while",
                "true", "false", "read", "write"
        };

        Token.TipoToken[] tiposReservados = {
                Token.TipoToken.INT, Token.TipoToken.FLOAT, Token.TipoToken.STRING, Token.TipoToken.BOOL,
                Token.TipoToken.IF, Token.TipoToken.ELSE, Token.TipoToken.FOR, Token.TipoToken.WHILE,
                Token.TipoToken.TRUE, Token.TipoToken.FALSE, Token.TipoToken.READ, Token.TipoToken.WRITE
        };

        for (int i = 0; i < reservadas.length; i++) {
            if (resto.startsWith(reservadas[i])) {
                if (resto.length() == reservadas[i].length()
                        || !Character.isLetterOrDigit(resto.charAt(reservadas[i].length()))) {
                    int col = columnaActual;
                    avanzarMulti(reservadas[i].length());
                    return new Token(tiposReservados[i], reservadas[i], lineaActual, col);
                }
            }
        }

        // Operadores multicarácter
        if (resto.startsWith("==")) return crearToken(Token.TipoToken.ESIGUAL, "==", 2);
        if (resto.startsWith("!=")) return crearToken(Token.TipoToken.NOIGUAL, "!=", 2);
        if (resto.startsWith(">=")) return crearToken(Token.TipoToken.MAIGUAL, ">=", 2);
        if (resto.startsWith("<=")) return crearToken(Token.TipoToken.MEIGUAL, "<=", 2);
        if (resto.startsWith("++")) return crearToken(Token.TipoToken.INC, "++", 2);
        if (resto.startsWith("--")) return crearToken(Token.TipoToken.DEC, "--", 2);

        // PERNUM
        Matcher mPer = Pattern.compile("^[0-9]+%").matcher(resto);
        if (mPer.find()) {
            return crearToken(Token.TipoToken.PERNUM, mPer.group(), mPer.group().length());
        }

        // FLOATNUM
        Matcher mFloat = Pattern.compile("^[0-9]+\\.[0-9]+").matcher(resto);
        if (mFloat.find()) {
            return crearToken(Token.TipoToken.FLOATNUM, mFloat.group(), mFloat.group().length());
        }

        // INTNUM
        Matcher mInt = Pattern.compile("^[0-9]+").matcher(resto);
        if (mInt.find()) {
            return crearToken(Token.TipoToken.INTNUM, mInt.group(), mInt.group().length());
        }

        // ID
        Matcher mId = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*").matcher(resto);
        if (mId.find()) {
            String lexema = mId.group();
            if (lexema.length() > 31) {
                errores.add(String.format("line %d, col %d: ERROR Identificador mayor a 31 caracteres (se truncara)",
                        lineaActual, columnaActual));
                lexema = lexema.substring(0, 31);
            }
            int longitudOriginal = mId.group().length();
            int col = columnaActual;
            avanzarMulti(longitudOriginal);
            return new Token(Token.TipoToken.ID, lexema, lineaActual, col);
        }

        // STRINGWORD
        if (resto.startsWith("\"")) {
            Matcher mStr = Pattern.compile("^\"([^\"\\\\]|\\\\.)*\"").matcher(resto);
            if (mStr.find()) {
                return crearToken(Token.TipoToken.STRINGWORD, mStr.group(), mStr.group().length());
            } else {
                errores.add(String.format("line %d, col %d: ERROR Cadena sin cerrar", lineaActual, columnaActual));
                avanzar();
                return null;
            }
        }

        // Símbolos simples
        char c = resto.charAt(0);
        switch (c) {
            case '{': return crearToken(Token.TipoToken.LLAVEIZQ, "{", 1);
            case '}': return crearToken(Token.TipoToken.LLAVEDER, "}", 1);
            case '(': return crearToken(Token.TipoToken.PARENIZQ, "(", 1);
            case ')': return crearToken(Token.TipoToken.PARENDER, ")", 1);
            case ';': return crearToken(Token.TipoToken.PYC, ";", 1);
            case ',': return crearToken(Token.TipoToken.COMA, ",", 1);
            case '+': return crearToken(Token.TipoToken.SUM, "+", 1);
            case '-': return crearToken(Token.TipoToken.REST, "-", 1);
            case '*': return crearToken(Token.TipoToken.MULT, "*", 1);
            case '/': return crearToken(Token.TipoToken.DIV, "/", 1);
            case '=': return crearToken(Token.TipoToken.IGUAL, "=", 1);
            case '<': return crearToken(Token.TipoToken.MENOR, "<", 1);
            case '>': return crearToken(Token.TipoToken.MAYOR, ">", 1);
            case '!': return crearToken(Token.TipoToken.NOT, "!", 1);
            default: return null;
        }
    }

    private Token crearToken(Token.TipoToken tipo, String lexema, int longitud) {
        int col = columnaActual;
        avanzarMulti(longitud);
        return new Token(tipo, lexema, lineaActual, col);
    }

    private void avanzar() {
        pos++;
        columnaActual++;
    }

    private void avanzarMulti(int n) {
        pos += n;
        columnaActual += n;
    }

    public List<String> getErrores() {
        return errores;
    }

    public PilaIdentacion getPila() {
        return pila;
    }

    public int getLineaActual() {
        return lineaActual;
    }
}