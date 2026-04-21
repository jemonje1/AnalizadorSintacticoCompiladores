package AnalizadorSintactico;

import AnalizadorLexico.Token;

import java.util.List;
import java.util.Set;

public class AnalizadorSintactico {
    private final Grafo grafo;
    private final Parser parser;

    public AnalizadorSintactico() {
        this.grafo = new Grafo();
        construirGramaticaMiniLang();
        this.parser = new Parser(grafo);
    }

    private void construirGramaticaMiniLang() {
        Set<String> FIN_EXPR = Set.of(
                "PARENDER", "PYC", "COMA", "ESIGUAL", "NOIGUAL", "MEIGUAL",
                "MAIGUAL", "MENOR", "MAYOR", "DEDENT", "EOF"
        );

        Set<String> FIN_COND = Set.of("PARENDER");
        Set<String> FIN_STMT = Set.of("INT", "FLOAT", "STRING", "BOOL", "IF", "ELSE", "FOR", "WHILE",
                "READ", "WRITE", "COMMENT", "LLAVEIZQ", "LLAVEDER", "DEDENT", "EOF");

        grafo.agregarRegla(new Reglas(1, "Status", List.of("TRUE"), Set.of("PYC")));
        grafo.agregarRegla(new Reglas(2, "Status", List.of("FALSE"), Set.of("PYC")));

        grafo.agregarRegla(new Reglas(3, "Symb", List.of("INC"), Set.of("PARENDER")));
        grafo.agregarRegla(new Reglas(4, "Symb", List.of("DEC"), Set.of("PARENDER")));

        grafo.agregarRegla(new Reglas(5, "C", List.of("INTNUM"), FIN_EXPR));
        grafo.agregarRegla(new Reglas(6, "C", List.of("FLOATNUM"), FIN_EXPR));
        grafo.agregarRegla(new Reglas(7, "C", List.of("PERNUM"), FIN_EXPR));
        grafo.agregarRegla(new Reglas(8, "C", List.of("ID"), FIN_EXPR));
        grafo.agregarRegla(new Reglas(9, "C", List.of("PARENIZQ", "E", "PARENDER"), FIN_EXPR));

        grafo.agregarRegla(new Reglas(10, "A", List.of("A", "MULT", "C"), FIN_EXPR));
        grafo.agregarRegla(new Reglas(11, "A", List.of("A", "DIV", "C"), FIN_EXPR));
        grafo.agregarRegla(new Reglas(12, "A", List.of("C"), FIN_EXPR));

        grafo.agregarRegla(new Reglas(13, "E", List.of("E", "SUM", "A"), FIN_EXPR));
        grafo.agregarRegla(new Reglas(14, "E", List.of("E", "REST", "A"), FIN_EXPR));
        grafo.agregarRegla(new Reglas(15, "E", List.of("A"), FIN_EXPR));

        grafo.agregarRegla(new Reglas(16, "Num_Condition", List.of("E", "ESIGUAL", "E"), FIN_COND));
        grafo.agregarRegla(new Reglas(17, "Num_Condition", List.of("E", "MEIGUAL", "E"), FIN_COND));
        grafo.agregarRegla(new Reglas(18, "Num_Condition", List.of("E", "MAIGUAL", "E"), FIN_COND));
        grafo.agregarRegla(new Reglas(19, "Num_Condition", List.of("E", "MENOR", "E"), FIN_COND));
        grafo.agregarRegla(new Reglas(20, "Num_Condition", List.of("E", "MAYOR", "E"), FIN_COND));
        grafo.agregarRegla(new Reglas(21, "Num_Condition", List.of("E", "NOIGUAL", "E"), FIN_COND));

        grafo.agregarRegla(new Reglas(22, "Condition", List.of("NOT", "ID"), Set.of("PARENDER")));
        grafo.agregarRegla(new Reglas(23, "Condition", List.of("Num_Condition"), Set.of("PARENDER")));
        grafo.agregarRegla(new Reglas(24, "Condition", List.of("ID"), Set.of("PARENDER")));

        grafo.agregarRegla(new Reglas(25, "Int_Dec", List.of("INT", "ID", "IGUAL", "INTNUM", "PYC"), FIN_STMT));
        grafo.agregarRegla(new Reglas(26, "Float_Dec", List.of("FLOAT", "ID", "IGUAL", "FLOATNUM", "PYC"), FIN_STMT));
        grafo.agregarRegla(new Reglas(27, "Float_Dec", List.of("FLOAT", "ID", "IGUAL", "PERNUM", "PYC"), FIN_STMT));
        grafo.agregarRegla(new Reglas(28, "Float_Dec", List.of("FLOAT", "ID", "IGUAL", "INTNUM", "PYC"), FIN_STMT));
        grafo.agregarRegla(new Reglas(29, "String_Dec", List.of("STRING", "ID", "IGUAL", "STRINGWORD", "PYC"), FIN_STMT));
        grafo.agregarRegla(new Reglas(30, "Bool_Dec", List.of("BOOL", "ID", "IGUAL", "Status", "PYC"), FIN_STMT));

        grafo.agregarRegla(new Reglas(31, "Dec_Stmt", List.of("Int_Dec"), FIN_STMT));
        grafo.agregarRegla(new Reglas(32, "Dec_Stmt", List.of("Float_Dec"), FIN_STMT));
        grafo.agregarRegla(new Reglas(33, "Dec_Stmt", List.of("String_Dec"), FIN_STMT));
        grafo.agregarRegla(new Reglas(34, "Dec_Stmt", List.of("Bool_Dec"), FIN_STMT));

        grafo.agregarRegla(new Reglas(35, "Write_Item", List.of("STRINGWORD"), Set.of("COMA", "PARENDER")));
        grafo.agregarRegla(new Reglas(36, "Write_Item", List.of("E"), Set.of("COMA", "PARENDER")));

        grafo.agregarRegla(new Reglas(37, "Write_List", List.of("Write_List", "COMA", "Write_Item"), Set.of("PARENDER")));
        grafo.agregarRegla(new Reglas(38, "Write_List", List.of("Write_Item"), Set.of("PARENDER")));

        grafo.agregarRegla(new Reglas(39, "Write_Stmt", List.of("WRITE", "PARENIZQ", "Write_List", "PARENDER", "PYC"), FIN_STMT));
        grafo.agregarRegla(new Reglas(40, "Write_Stmt", List.of("WRITE", "PARENIZQ", "Write_List", "PARENDER"), FIN_STMT));

        grafo.agregarRegla(new Reglas(41, "Read_Stmt", List.of("READ", "PARENIZQ", "ID", "PARENDER", "PYC"), FIN_STMT));
        grafo.agregarRegla(new Reglas(42, "Read_Stmt", List.of("READ", "PARENIZQ", "ID", "PARENDER"), FIN_STMT));

        grafo.agregarRegla(new Reglas(43, "Comment_Stmt", List.of("COMMENT"), FIN_STMT));

        grafo.agregarRegla(new Reglas(44, "Stmts", List.of(), Set.of("DEDENT", "LLAVEDER", "EOF")));
        grafo.agregarRegla(new Reglas(45, "Stmts", List.of("Stmt"), Set.of("DEDENT", "LLAVEDER", "EOF")));
        grafo.agregarRegla(new Reglas(46, "Stmts", List.of("Stmts", "Stmt"), Set.of("DEDENT", "LLAVEDER", "EOF")));

        grafo.agregarRegla(new Reglas(47, "Block", List.of("LLAVEIZQ", "Stmts", "LLAVEDER"), FIN_STMT));
        grafo.agregarRegla(new Reglas(48, "Block", List.of("LLAVEIZQ", "LLAVEDER"), FIN_STMT));

        grafo.agregarRegla(new Reglas(49, "Else_Stmt", List.of("ELSE", "NEWLINE", "INDENT", "Stmts", "DEDENT"), FIN_STMT));
        grafo.agregarRegla(new Reglas(50, "Else_Stmt", List.of("ELSE", "NEWLINE", "INDENT", "DEDENT"), FIN_STMT));

        grafo.agregarRegla(new Reglas(51, "If_Stmt", List.of("IF", "PARENIZQ", "Condition", "PARENDER", "NEWLINE", "INDENT", "Stmts", "DEDENT", "Else_Stmt"), FIN_STMT));
        grafo.agregarRegla(new Reglas(52, "If_Stmt", List.of("IF", "PARENIZQ", "Condition", "PARENDER", "NEWLINE", "INDENT", "Stmts", "DEDENT"), FIN_STMT));
        grafo.agregarRegla(new Reglas(53, "If_Stmt", List.of("IF", "PARENIZQ", "Condition", "PARENDER", "NEWLINE", "INDENT", "DEDENT"), FIN_STMT));

        grafo.agregarRegla(new Reglas(54, "While_Stmt", List.of("WHILE", "PARENIZQ", "Condition", "PARENDER", "NEWLINE", "INDENT", "Stmts", "DEDENT"), FIN_STMT));
        grafo.agregarRegla(new Reglas(55, "While_Stmt", List.of("WHILE", "PARENIZQ", "Condition", "PARENDER", "NEWLINE", "INDENT", "DEDENT"), FIN_STMT));

        grafo.agregarRegla(new Reglas(56, "For_Stmt", List.of("FOR", "PARENIZQ", "Int_Dec", "Num_Condition", "PYC", "ID", "Symb", "PARENDER", "NEWLINE", "INDENT", "Stmts", "DEDENT"), FIN_STMT));
        grafo.agregarRegla(new Reglas(57, "For_Stmt", List.of("FOR", "PARENIZQ", "Int_Dec", "Num_Condition", "PYC", "ID", "Symb", "PARENDER", "NEWLINE", "INDENT", "DEDENT"), FIN_STMT));

        grafo.agregarRegla(new Reglas(58, "Stmt", List.of("Block"), FIN_STMT));
        grafo.agregarRegla(new Reglas(59, "Stmt", List.of("Dec_Stmt"), FIN_STMT));
        grafo.agregarRegla(new Reglas(60, "Stmt", List.of("If_Stmt"), FIN_STMT));
        grafo.agregarRegla(new Reglas(61, "Stmt", List.of("For_Stmt"), FIN_STMT));
        grafo.agregarRegla(new Reglas(62, "Stmt", List.of("While_Stmt"), FIN_STMT));
        grafo.agregarRegla(new Reglas(63, "Stmt", List.of("Comment_Stmt"), FIN_STMT));
        grafo.agregarRegla(new Reglas(64, "Stmt", List.of("Write_Stmt"), FIN_STMT));
        grafo.agregarRegla(new Reglas(65, "Stmt", List.of("Read_Stmt"), FIN_STMT));

        grafo.agregarRegla(new Reglas(66, "Program", List.of("Stmts"), Set.of("EOF")));
    }

    public boolean analizar(List<Token> tokens) {
        return parser.parsear(tokens);
    }

    public Parser getParser() {
        return parser;
    }
}