package AnalizadorLexico;

import AnalizadorSintactico.sym;
import Stack.PilaIdentacion;
import java_cup.runtime.Symbol;
import java.util.LinkedList;
import java.util.Queue;

%%

%public
%class LexerCup
%cup
%line
%column
%unicode
%state INICIO_LINEA

%{
    /*
     * Atributos del analizador léxico
     */
    private final PilaIdentacion pilaIndentacion = new PilaIdentacion(5);
    private final Queue<Symbol> tokensPendientes = new LinkedList<>();

    private int contadorLlaves = 0;
    private int nivelActualLinea = 0;
    private boolean nivelProcesado = false;

    /*
     * Devuelve el simbolo correspondiente al token
     */
    private Symbol symbol(Token.TipoToken tipo) {
        return new Symbol(obtenerSimbolo(tipo), yyline + 1, yycolumn + 1, yytext());
    }

    private Symbol symbol(Token.TipoToken tipo, Object valor) {
        return new Symbol(obtenerSimbolo(tipo), yyline + 1, yycolumn + 1, valor);
    }

    private int obtenerSimbolo(Token.TipoToken tipo) {
        try {
            return sym.class.getField(tipo.name()).getInt(null);
        } catch (Exception e) {
            throw new RuntimeException("No existe simbolo para el token: " + tipo.name());
        }
    }

    /*
     * Reporta errores léxicos
     */
    private void registrarError(String mensaje) {
        System.out.println("line " + (yyline + 1) + ", col " + (yycolumn + 1) + ": ERROR " + mensaje);
    }

    /*
     * Valida la indentación detectada al inicio de la línea.
     * Además de validar con la pila, genera tokens INDENT y DEDENT.
     */
    private void procesarIndentacionActual() {
        if (nivelProcesado) {
            return;
        }

        if (!pilaIndentacion.nivelValido(nivelActualLinea)) {
            registrarError("Nivel de identacion " + nivelActualLinea
                    + " excede el limite de " + pilaIndentacion.getMaxNiveles());
            nivelProcesado = true;
            return;
        }

        int nivelAnterior = pilaIndentacion.getNivelActual();

        if (nivelActualLinea > nivelAnterior) {
            pilaIndentacion.apilarNivel(nivelActualLinea);
            tokensPendientes.offer(symbol(Token.TipoToken.INDENT));
        } else if (nivelActualLinea < nivelAnterior) {
            if (!pilaIndentacion.contieneNivel(nivelActualLinea)) {
                registrarError("El nivel de identacion " + nivelActualLinea
                        + " no corresponde a una apertura previa");
                nivelProcesado = true;
                return;
            }

            while (pilaIndentacion.getNivelActual() > nivelActualLinea) {
                pilaIndentacion.desapilarNivel();
                tokensPendientes.offer(symbol(Token.TipoToken.DEDENT));
            }
        }

        nivelProcesado = true;
    }
%}

%init{
    yybegin(INICIO_LINEA);
%init}

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
SALTO = \n
CADENA = \"([^\"\n])*\" 

%%

/*
 * Si hay tokens pendientes (por ejemplo DEDENT), se devuelven primero.
 */
<YYINITIAL,INICIO_LINEA> <<EOF>> {
    procesarIndentacionActual();

    while (!pilaIndentacion.estaEnBase()) {
        pilaIndentacion.desapilarNivel();
        tokensPendientes.offer(symbol(Token.TipoToken.DEDENT));
    }

    if (!tokensPendientes.isEmpty()) {
        return tokensPendientes.poll();
    }

    if (contadorLlaves > 0) {
        registrarError("Faltan llaves de cierre");
    }

    pilaIndentacion.validarCierreFinal(yyline + 1);
    return symbol(Token.TipoToken.EOF);
}

/*
 * Inicio de línea: conteo de indentación
 */
<INICIO_LINEA> "\t"      { nivelActualLinea++; }
<INICIO_LINEA> "    "    { nivelActualLinea++; }
<INICIO_LINEA> " "       { registrarError("Identacion incompleta. Use tabulaciones o grupos de 4 espacios."); }

<INICIO_LINEA> {SALTO}   {
                            nivelActualLinea = 0;
                            nivelProcesado = false;
                            return symbol(Token.TipoToken.NEWLINE);
                        }

<INICIO_LINEA> "#" [^\n]* {
                            procesarIndentacionActual();
                            yybegin(YYINITIAL);
                        }

<INICIO_LINEA> .         {
                            procesarIndentacionActual();
                            yybegin(YYINITIAL);
                            yypushback(yylength());

                            if (!tokensPendientes.isEmpty()) {
                                return tokensPendientes.poll();
                            }
                        }

/*
 * Palabras reservadas
 */
<YYINITIAL> "int"        { return symbol(Token.TipoToken.INT); }
<YYINITIAL> "float"      { return symbol(Token.TipoToken.FLOAT); }
<YYINITIAL> "string"     { return symbol(Token.TipoToken.STRING); }
<YYINITIAL> "bool"       { return symbol(Token.TipoToken.BOOL); }
<YYINITIAL> "if"         { return symbol(Token.TipoToken.IF); }
<YYINITIAL> "else"       { return symbol(Token.TipoToken.ELSE); }
<YYINITIAL> "for"        { return symbol(Token.TipoToken.FOR); }
<YYINITIAL> "while"      { return symbol(Token.TipoToken.WHILE); }
<YYINITIAL> "true"       { return symbol(Token.TipoToken.TRUE); }
<YYINITIAL> "false"      { return symbol(Token.TipoToken.FALSE); }
<YYINITIAL> "Read"       { return symbol(Token.TipoToken.READ); }
<YYINITIAL> "Write"      { return symbol(Token.TipoToken.WRITE); }

/*
 * Operadores dobles
 */
<YYINITIAL> "<="         { return symbol(Token.TipoToken.MEIGUAL); }
<YYINITIAL> ">="         { return symbol(Token.TipoToken.MAIGUAL); }
<YYINITIAL> "=="         { return symbol(Token.TipoToken.ESIGUAL); }
<YYINITIAL> "!="         { return symbol(Token.TipoToken.NOIGUAL); }
<YYINITIAL> "++"         { return symbol(Token.TipoToken.INC); }
<YYINITIAL> "--"         { return symbol(Token.TipoToken.DEC); }

/*
 * Operadores y símbolos
 */
<YYINITIAL> "{"          { contadorLlaves++; return symbol(Token.TipoToken.LLAVEIZQ); }

<YYINITIAL> "}"          {
                            contadorLlaves--;
                            if (contadorLlaves < 0) {
                                registrarError("Llave de cierre sin apertura");
                                contadorLlaves = 0;
                            }
                            return symbol(Token.TipoToken.LLAVEDER);
                         }

<YYINITIAL> "("          { return symbol(Token.TipoToken.PARENIZQ); }
<YYINITIAL> ")"          { return symbol(Token.TipoToken.PARENDER); }
<YYINITIAL> ";"          { return symbol(Token.TipoToken.PYC); }
<YYINITIAL> ","          { return symbol(Token.TipoToken.COMA); }

<YYINITIAL> "+"          { return symbol(Token.TipoToken.SUM); }
<YYINITIAL> "-"          { return symbol(Token.TipoToken.REST); }
<YYINITIAL> "*"          { return symbol(Token.TipoToken.MULT); }
<YYINITIAL> "/"          { return symbol(Token.TipoToken.DIV); }
<YYINITIAL> "="          { return symbol(Token.TipoToken.IGUAL); }

<YYINITIAL> "<"          { return symbol(Token.TipoToken.MENOR); }
<YYINITIAL> ">"          { return symbol(Token.TipoToken.MAYOR); }
<YYINITIAL> "!"          { return symbol(Token.TipoToken.NOT); }

/*
 * Literales
 */
<YYINITIAL> {FLOTANTE}   { return symbol(Token.TipoToken.FLOATNUM); }
<YYINITIAL> {PORCENTAJE} { return symbol(Token.TipoToken.PERNUM); }
<YYINITIAL> {ENTERO}     { return symbol(Token.TipoToken.INTNUM); }
<YYINITIAL> {CADENA}     { return symbol(Token.TipoToken.STRINGWORD); }

/*
 * Identificadores
 */
<YYINITIAL> {ID}         { return symbol(Token.TipoToken.ID); }

/*
 * Espacios internos y comentarios
 */
<YYINITIAL> {ESPACIOS}   { /* ignorar */ }
<YYINITIAL> "#" [^\n]*   { /* ignorar comentario */ }

/*
 * Saltos de línea
 */
<YYINITIAL> {SALTO}      {
                            nivelActualLinea = 0;
                            nivelProcesado = false;
                            yybegin(INICIO_LINEA);
                            return symbol(Token.TipoToken.NEWLINE);
                         }

/*
 * Errores
 */
<YYINITIAL> .            {
                            registrarError("Caracter inesperado '" + yytext() + "'");
                            return symbol(Token.TipoToken.DESCONOCIDO);
                         }