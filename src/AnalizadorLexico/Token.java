package AnalizadorLexico;

public class Token {
    public enum TipoToken {
        // Palabras reservadas
        INT, FLOAT, STRING, BOOL, IF, ELSE, FOR, WHILE, TRUE, FALSE, READ, WRITE,
        // Identificadores y literales
        ID, INTNUM, FLOATNUM, PERNUM, STRINGWORD,
        // Operadores y símbolos
        LLAVEIZQ, LLAVEDER, PARENIZQ, PARENDER, PYC, COMA,
        SUM, REST, MULT, DIV, IGUAL,
        MENOR, MAYOR, MEIGUAL, MAIGUAL, ESIGUAL, NOIGUAL,
        INC, DEC, NOT,
        // Control de flujo espacial
        NEWLINE, INDENT, DEDENT,
        // Finalización
        EOF, DESCONOCIDO
    }

    private final TipoToken tipo;
    private final String lexema;
    private final int linea;
    private final int columna;

    public Token(TipoToken tipo, String lexema, int linea, int columna) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.linea = linea;
        this.columna = columna;
    }

    public TipoToken getTipo() { return tipo; }
    public String getLexema() { return lexema; }
    public int getLinea() { return linea; }
    public int getColumna() { return columna; }

    @Override
    public String toString() {
        return String.format("line %d, col %d: [%s, \"%s\"]", linea, columna, tipo, lexema);
    }
}