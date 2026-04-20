package AnalizadorLexico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Representa el almacenamiento de tokens y errores léxicos.
 *
 * Nota: Se mantiene un HashMap para seguir el patrón de referencia (lexema -> tipo),
 * y una lista ordenada para poder generar el .out en el orden de aparición.
 */
public class Token {

    // region ENUM - TIPOS DE TOKEN
    /**
     * Tipos de token reconocidos por MiniLang.
     */
    public enum TipoToken {
        // Palabras reservadas
        INT,
        FLOAT,
        STRING,
        BOOL,
        IF,
        ELSE,
        FOR,
        WHILE,
        TRUE,
        FALSE,
        READ,
        WRITE,

        // Identificadores y literales
        ID,
        INTNUM,
        FLOATNUM,
        PERNUM,
        STRINGWORD,

        // Operadores y símbolos
        LLAVEIZQ,      // {
        LLAVEDER,      // }
        PARENIZQ,      // (
        PARENDER,      // )
        PYC,        // ;
        COMA,       // ,

        SUM,        // +
        REST,       // -
        MULT,        // *
        DIV,       // /
        IGUAL,      // =

        MENOR,          // <
        MAYOR,          // >
        MEIGUAL,          // <=
        MAIGUAL,          // >=
        ESIGUAL,          // ==
        NOIGUAL,          // !=

        INC,         // ++
        DEC,         // --
        NOT,         // !

        // Espacios en blanco
        NEWLINE,
        INDENT,
        DEDENT,

        //Fin de archivo
        EOF,

        // Desconocido
        DESCONOCIDO
    }
    // endregion

    // region CLASE - TOKENINFO
    /**
     * Estructura para un token con posición.
     */
    public static class TokenInfo {
        public final TipoToken tipo;
        public final String lexema;
        public final int linea;
        public final int colInicio;
        public final int colFin;

        public TokenInfo(TipoToken tipo, String lexema, int linea, int colInicio, int colFin) {
            this.tipo = tipo;
            this.lexema = lexema;
            this.linea = linea;
            this.colInicio = colInicio;
            this.colFin = colFin;
        }

        @Override
        public String toString() {
            String val = (lexema == null) ? "" : lexema;
            return "line " + linea + ", col " + colInicio + "-" + colFin + ": " + tipo + (val.isEmpty() ? "" : " " + val);
        }
    }
    // endregion

    // region ATRIBUTOS
    private final HashMap<String, String> tokens; // referencia (lexema -> tipo)
    private final List<TokenInfo> tokensEnOrden;  // salida principal
    private final List<String> erroresLexicos;
    // endregion

    // region CONSTRUCTOR
    public Token() {
        this.tokens = new HashMap<>();
        this.tokensEnOrden = new ArrayList<>();
        this.erroresLexicos = new ArrayList<>();
    }
    // endregion

    // region METODOS - ALMACENAMIENTO
    /**
     * Almacena un token en la lista ordenada y opcionalmente en el HashMap.
     *
     * @param tipo tipo del token
     * @param lexema lexema (valor)
     * @param linea línea
     * @param colInicio columna inicial
     * @param colFin columna final
     */
    public void agregarToken(TipoToken tipo, String lexema, int linea, int colInicio, int colFin) {
        TokenInfo info = new TokenInfo(tipo, lexema, linea, colInicio, colFin);
        tokensEnOrden.add(info);

        // patrón de referencia: guardar en HashMap
        if (lexema != null && !lexema.isEmpty() && tipo != TipoToken.DESCONOCIDO) {
            if (!tokens.containsKey(lexema)) {
                tokens.put(lexema, tipo.name());
            }
        }
    }

    /**
     * Limpia tokens y errores de un análisis previo.
     */
    public void limpiar() {
        tokens.clear();
        tokensEnOrden.clear();
        erroresLexicos.clear();
    }
    // endregion

    // region METODOS - ERRORES
    /**
     * Reporta un error léxico con el formato recomendado.
     *
     * @param linea línea del error
     * @param col columna del error
     * @param descripcion descripción del error
     */
    public void reportarError(int linea, int col, String descripcion) {
        erroresLexicos.add("line " + linea + ", col " + col + ": ERROR " + descripcion);
    }

    /**
     * Indica si hay errores.
     *
     * @return true si existen errores
     */
    public boolean hayErrores() {
        return !erroresLexicos.isEmpty();
    }

    /**
     * Retorna la lista de errores.
     *
     * @return errores
     */
    public List<String> getErrores() {
        return erroresLexicos;
    }
    // endregion

    // region METODOS - GETTERS
    /**
     * Obtiene tokens en orden.
     *
     * @return lista
     */
    public List<TokenInfo> getTokensEnOrden() {
        return tokensEnOrden;
    }

    /**
     * Obtiene el HashMap de tokens (lexema -> tipo).
     *
     * @return hashmap
     */
    public HashMap<String, String> getTokensMap() {
        return tokens;
    }
    // endregion

    // region METODOS - IMPRESION
    /**
     * Imprime tokens (HashMap) como en la referencia.
     */
    public void mostrarTokensResumen() {
        if (tokens.isEmpty()) {
            System.out.println("No hay tokens almacenados.");
            return;
        }
        System.out.println("\n------ Tokens Encontrados (Resumen) ------");
        int contador = 1;
        for (String valor : tokens.keySet()) {
            System.out.println(contador + ". " + valor + " (tipo: " + tokens.get(valor) + ")");
            contador++;
        }
        System.out.println();
    }

    /**
     * Imprime tokens en orden de aparición.
     */
    public void mostrarTokensEnOrden() {
        if (tokensEnOrden.isEmpty()) {
            System.out.println("No hay tokens generados.");
            return;
        }
        System.out.println("\n------ Tokens (En orden de aparicion) ------");
        for (TokenInfo t : tokensEnOrden) {
            System.out.println(t.toString());
        }
        System.out.println();
    }

    /**
     * Imprime errores en consola.
     */
    public void mostrarErrores() {
        if (erroresLexicos.isEmpty()) {
            System.out.println("No se encontraron errores lexicos.");
            return;
        }
        System.out.println("\n------ ERRORES LEXICOS ENCONTRADOS ------");
        for (String e : erroresLexicos) {
            System.out.println(e);
        }
        System.out.println();
    }
    // endregion
}