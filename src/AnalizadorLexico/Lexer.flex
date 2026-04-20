package AnalizadorLexico;

import static AnalizadorLexico.Token.TipoToken.*;
import java.io.PrintWriter;

%%
%class Lexer
%type Token.TipoToken
%line
%column
%unicode

%{
    public String lexeme;
%}

/* Expresiones Regulares */
LETRA = [a-zA-Z]
DIGITO = [0-9]
ID = {LETRA}({LETRA}|{DIGITO})*
ENTERO = {DIGITO}+
FLOTANTE = {DIGITO}+"."{DIGITO}+
CADENA = \"([^\"\n])*\"
ESPACIOS = [ \t\r\f]+

%%

/* Palabras Reservadas */
"int"       { lexeme=yytext(); return INT; }
"float"     { lexeme=yytext(); return FLOAT; }
"string"    { lexeme=yytext(); return STRING; }
"bool"      { lexeme=yytext(); return BOOL; }
"if"        { lexeme=yytext(); return IF; }
"else"      { lexeme=yytext(); return ELSE; }
"for"       { lexeme=yytext(); return FOR; }
"while"     { lexeme=yytext(); return WHILE; }
"true"      { lexeme=yytext(); return TRUE; }
"false"     { lexeme=yytext(); return FALSE; }
"Read"      { lexeme=yytext(); return READ; }
"Write"     { lexeme=yytext(); return WRITE; }

/* Operadores y Símbolos */
"+"         { lexeme=yytext(); return SUM; }
"-"         { lexeme=yytext(); return REST; }
"*"         { lexeme=yytext(); return MULT; }
"/"         { lexeme=yytext(); return DIV; }
"="         { lexeme=yytext(); return IGUAL; }
"=="        { lexeme=yytext(); return ESIGUAL; }
"!="        { lexeme=yytext(); return NOIGUAL; }
"<"         { lexeme=yytext(); return MENOR; }
">"         { lexeme=yytext(); return MAYOR; }
"<="        { lexeme=yytext(); return MEIGUAL; }
">="        { lexeme=yytext(); return MAIGUAL; }
"++"        { lexeme=yytext(); return INC; }
"--"        { lexeme=yytext(); return DEC; }
"!"         { lexeme=yytext(); return NOT; }
"{"         { lexeme=yytext(); return LLAVEIZQ; }
"}"         { lexeme=yytext(); return LLAVEDER; }
"("         { lexeme=yytext(); return PARENIZQ; }
")"         { lexeme=yytext(); return PARENDER; }
";"         { lexeme=yytext(); return PYC; }
","         { lexeme=yytext(); return COMA; }

/* Literales */
{ID}        { lexeme=yytext(); return ID; }
{ENTERO}    { lexeme=yytext(); return INTNUM; }
{FLOTANTE}  { lexeme=yytext(); return FLOATNUM; }
{CADENA}    { lexeme=yytext(); return STRINGWORD; }

/* Saltos y Comentarios */
\n          { return NEWLINE; }
"#" [^\n]* { /* Ignorar */ }
{ESPACIOS}  { /* Ignorar */ }

.           { return DESCONOCIDO; }