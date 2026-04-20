package AnalizadorLexico;

import java_cup.runtime.Symbol;
import java.util.LinkedList;
import java.util.Queue;
import Stack.PilaIdentacion;

%%

%public
%class LexerCup
%cup
%line
%column
%unicode

%{
    /*
     * Atributos del analizador léxico
     */
    private final Queue<Symbol> tokensPendientes = new LinkedList<>();
    private final PilaIdentacion pilaIndentacion = new PilaIdentacion(5);

    private boolean inicioLinea = true;
    private int contadorLlaves = 0;

    /*
     * Métodos de apoyo
     */
    private Symbol symbol(Token.TipoToken tipo) {
        return new Symbol(sym.valueOf(tipo.name()), yyline + 1, yycolumn + 1, yytext());
    }

    private Symbol symbol(Token.TipoToken tipo, Object valor) {
        return new Symbol(sym.valueOf(tipo.name()), yyline + 1, yycolumn + 1, valor);
    }

    private void registrarError(String mensaje) {
        System.out.println("line " + (yyline + 1) + ", col " + (yycolumn + 1) + ": ERROR " + mensaje);
    }
%}

/*
 * Expresiones regulares
 */
LETRA = [a-zA-Z]
DIGITO = [0-9]
ID = {LETRA}({LETRA}|{DIGITO})*
ENTERO = {DIGITO}+
FLOTANTE = {DIGITO}+"."{DIGITO}+
PORCENTAJE = {DIGITO}+"%"
ESPACIOS = [ \r\f]+
TAB = \t
SALTO = \n
CADENA = \"([^\"\n])*\" 

%%

/*
 * Palabras reservadas
 */
"int"       { return symbol(Token.TipoToken.INT); }
"float"     { return symbol(Token.TipoToken.FLOAT); }
"string"    { return symbol(Token.TipoToken.STRING); }
"bool"      { return symbol(Token.TipoToken.BOOL); }
"if"        { return symbol(Token.TipoToken.IF); }
"else"      { return symbol(Token.TipoToken.ELSE); }
"for"       { return symbol(Token.TipoToken.FOR); }
"while"     { return symbol(Token.TipoToken.WHILE); }
"true"      { return symbol(Token.TipoToken.TRUE); }
"false"     { return symbol(Token.TipoToken.FALSE); }
"Read"      { return symbol(Token.TipoToken.READ); }
"Write"     { return symbol(Token.TipoToken.WRITE); }

/*
 * Operadores dobles
 */
"<="        { return symbol(Token.TipoToken.MEIGUAL); }
">="        { return symbol(Token.TipoToken.MAIGUAL); }
"=="        { return symbol(Token.TipoToken.ESIGUAL); }
"!="        { return symbol(Token.TipoToken.NOIGUAL); }
"++"        { return symbol(Token.TipoToken.INC); }
"--"        { return symbol(Token.TipoToken.DEC); }

/*
 * Operadores y símbolos
 */
"{"         { 
                contadorLlaves++; 
                return symbol(Token.TipoToken.LLAVEIZQ); 
            }

"}"         {
                contadorLlaves--;
                if (contadorLlaves < 0) {
                    registrarError("Llave de cierre sin apertura");
                    contadorLlaves = 0;
                }
                return symbol(Token.TipoToken.LLAVEDER);
            }

"("         { return symbol(Token.TipoToken.PARENIZQ); }
")"         { return symbol(Token.TipoToken.PARENDER); }
";"         { return symbol(Token.TipoToken.PYC); }
","         { return symbol(Token.TipoToken.COMA); }

"+"         { return symbol(Token.TipoToken.SUM); }
"-"         { return symbol(Token.TipoToken.REST); }
"*"         { return symbol(Token.TipoToken.MULT); }
"/"         { return symbol(Token.TipoToken.DIV); }
"="         { return symbol(Token.TipoToken.IGUAL); }

"<"         { return symbol(Token.TipoToken.MENOR); }
">"         { return symbol(Token.TipoToken.MAYOR); }
"!"         { return symbol(Token.TipoToken.NOT); }

/*
 * Literales
 */
{FLOTANTE}  { return symbol(Token.TipoToken.FLOATNUM); }
{PORCENTAJE}{ return symbol(Token.TipoToken.PERNUM); }
{ENTERO}    { return symbol(Token.TipoToken.INTNUM); }
{CADENA}    { return symbol(Token.TipoToken.STRINGWORD); }

/*
 * Identificadores
 */
{ID}        { return symbol(Token.TipoToken.ID); }

/*
 * Espacios y saltos
 */
{ESPACIOS}  { /* ignorar */ }

{TAB}       { 
                /* La lógica de indentación se implementa después del merge */
            }

{SALTO}     {
                inicioLinea = true;
                return symbol(Token.TipoToken.NEWLINE);
            }

/*
 * Comentarios
 */
"#".*       { /* ignorar comentario */ }

/*
 * Errores
 */
.           {
                registrarError("Caracter inesperado '" + yytext() + "'");
                return symbol(Token.TipoToken.DESCONOCIDO);
            }

/*
 * Fin de archivo
 */
<<EOF>>     {
                if (contadorLlaves > 0) {
                    registrarError("Faltan llaves de cierre");
                }
                return symbol(Token.TipoToken.EOF);
            }