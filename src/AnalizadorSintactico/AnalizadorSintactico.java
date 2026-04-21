package AnalizadorSintactico;

import AnalizadorLexico.Token;
import java.util.Arrays;
import java.util.List;

public class AnalizadorSintactico {
    private Grafo grafo;
    private Parser parser;

    public AnalizadorSintactico() {
        this.grafo = new Grafo();
        this.parser = new Parser(grafo);
        construirGramaticaMiniLang();
    }

    private void construirGramaticaMiniLang() {
        // Crear estados iniciales necesarios
        for (int i = 0; i <= 200; i++) grafo.agregarEstado(new Estado(i));

        // --- DEFINICIÓN DE REGLAS (Basadas en Documentación) --- [cite: 16-36]
        Reglas r1 = new Reglas(1, "Program", List.of("Stmts"));
        Reglas r2 = new Reglas(2, "Stmts", List.of("Stmt", "Stmts"));
        Reglas r3 = new Reglas(3, "Stmts", List.of()); // Épsilon

        // Declaraciones [cite: 27-36]
        Reglas rInt = new Reglas(4, "Stmt", List.of("INT", "ID", "IGUAL", "INTNUM", "PYC"));
        Reglas rFloat = new Reglas(5, "Stmt", List.of("FLOAT", "ID", "IGUAL", "FLOATNUM", "PYC"));
        Reglas rStr = new Reglas(6, "Stmt", List.of("STRING", "ID", "IGUAL", "STRINGWORD", "PYC"));

        // Estructuras de Control (Uso de NEWLINE, INDENT, DEDENT) [cite: 39-43]
        Reglas rIf = new Reglas(7, "If_Stmt", Arrays.asList("IF", "PARENIZQ", "Condition", "PARENDER", "NEWLINE", "INDENT", "Stmts", "DEDENT"));
        Reglas rWhile = new Reglas(8, "While_Stmt", Arrays.asList("WHILE", "PARENIZQ", "Condition", "PARENDER", "NEWLINE", "INDENT", "Stmts", "DEDENT"));

        // --- CONFIGURACIÓN DEL GRAFO (Ejemplo de transiciones) ---
        grafo.agregarGoto(0, "Program", 1);
        grafo.agregarGoto(0, "Stmts", 2);
        grafo.agregarAceptar(1, "EOF");

        // Transiciones para declaración 'int'
        grafo.agregarShift(0, "INT", 3);
        grafo.agregarShift(3, "ID", 4);
        grafo.agregarShift(4, "IGUAL", 5);
        grafo.agregarShift(5, "INTNUM", 6);
        grafo.agregarShift(6, "PYC", 7);
        grafo.agregarReduce(7, "EOF", rInt);
        grafo.agregarReduce(7, "INT", rInt);
        grafo.agregarReduce(7, "IF", rInt);
        grafo.agregarReduce(7, "DEDENT", rInt);

        // Aquí se deben completar el resto de transiciones Shift/Reduce 
        // siguiendo la tabla LR(1) generada para MiniLang.
    }

    public boolean analizar(List<Token> tokens) {
        return parser.parsear(tokens);
    }

    public Parser getParser() { return parser; }
}