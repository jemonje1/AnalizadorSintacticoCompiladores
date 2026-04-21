# MiniLang - Compilador/Analizador

MiniLang es un compilador/analizador léxico y sintáctico diseñado para procesar un lenguaje simplificado con tipado fuerte, estructuras de control y validación rigurosa de indentación.

---

## Descripción de la Gramática

La gramática de MiniLang sigue una estructura formal BNF con las siguientes características principales:

### Elementos Léxicos

**Palabras Reservadas:**
- Tipos de datos: `int`, `float`, `string`, `bool`
- Control de flujo: `if`, `else`, `while`, `for`
- Entrada/Salida: `read`, `write`
- Valores booleanos: `true`, `false`

**Operadores:**
- Aritméticos: `+`, `-`, `*`, `/`
- Asignación: `=`
- Comparación: `<`, `>`, `<=`, `>=`, `==`, `!=`
- Lógicos: `!` (negación)
- Incremento/Decremento: `++`, `--`

**Símbolos especiales:**
- Delimitadores: `{`, `}`, `(`, `)`
- Separadores: `;`, `,`
- Símbolos de indentación: `NEWLINE`, `INDENT`, `DEDENT`

### Reglas de Producción Principal

```
Program        → Stmts
Stmts          → Stmt Stmts | ε
Stmt           → Declaration | Assignment | ControlFlow | FunctionCall | Statement
Declaration    → Type ID IGUAL Value PYC
Assignment     → ID IGUAL Expression PYC
ControlFlow    → IfStmt | WhileStmt | ForStmt
IfStmt         → IF PARENIZQ Condition PARENDER NEWLINE INDENT Stmts DEDENT [ELSE...]
WhileStmt      → WHILE PARENIZQ Condition PARENDER NEWLINE INDENT Stmts DEDENT
Condition      → Expression (Comparison Expression)*
Expression     → Term ((SUMA | RESTA) Term)*
Term           → Factor ((MULT | DIV) Factor)*
```

### Decisiones de Diseño

1. **Tipado Fuerte:** Todo identificador debe ser declarado con su tipo antes de su uso.
2. **Indentación Significativa:** La indentación define bloques de código (similar a Python), eliminando la necesidad de llaves para bloques simples.
3. **Manejo de Errores Temprano:** Los errores se detectan en tres fases:
   - Fase Léxica: Tokens inválidos
   - Fase de Indentación: Errores de consistencia de niveles
   - Fase Sintáctica: Violaciones de reglas gramaticales

---

## Flujo del Compilador

entrada.mlng -> analisis lexico -> pila identacion -> analisis sintactico -> Generar .out con reporte de errores

### Fase 1: Análisis Léxico
- Entrada: Archivo `.mlng` de texto plano
- Proceso: Tokenización mediante JFlex
- Salida: Lista de tokens con tipo, lexema, línea y columna
- Errores: Tokens desconocidos, símbolos inválidos

### Fase 2: Validación de Indentación (Pila)
- Entrada: Tokens válidos del análisis léxico
- Proceso: Validación de niveles de indentación usando una pila
- Salida: Confirmación de indentaciones válidas o errores específicos
- Características:
  - Máximo 5 niveles de indentación permitidos
  - Verifica que cada nivel abierto se cierre correctamente
  - Detecta indentaciones inconsistentes

### Fase 3: Análisis Sintáctico
- Entrada: Tokens validados
- Proceso: Parser LR(1) basado en la gramática
- Salida: Árbol de sintaxis abstracto (ASA) o reporte de errores
- Errores: Violaciones gramaticales, uso de identificadores no declarados

### Fase 4: Generación de Reporte
- Salida: Archivo `.out` con:
  - Resumen de errores encontrados (léxicos, indentación, sintácticos)
  - Línea y columna exacta de cada error
  - Descripción del error
  - Estado final del análisis (ACEPTADO o RECHAZADO)

---

## Funcionamiento de la Pila de Indentación

### Concepto

La pila de indentación valida que el código respete una estructura consistente de niveles de anidamiento. Funciona **después** del análisis léxico y **antes** del análisis sintáctico.

### Niveles de Indentación

- **Nivel 0:** Sin indentación (raíz del programa)
- **Nivel 1:** Primera indentación
- **Nivel 2:** Segunda indentación
- **Nivel 3:** Tercera indentación
- **Nivel 4:** Cuarta indentación
- **Nivel 5:** Quinta indentación (máximo permitido)

### Algoritmo

1. Inicia con pila vacía (nivel 0)
2. Cuando encuentra un `INDENT` (aumento de indentación):
   - Verifica que el nuevo nivel no exceda 5
   - Inserta el nuevo nivel en la pila
3. Cuando encuentra un `DEDENT` (disminución de indentación):
   - Extrae un nivel de la pila
   - Valida que exista un nivel anterior que coincida
4. Al final del archivo:
   - La pila debe estar vacía (nivel 0)
   - Si no, hay indentaciones no cerradas

### Ejemplos de Validación

**Ejemplo Correcto:**
```
while(i==j){              # Nivel 0
    int a = 1;            # Nivel 1
    int b = 2;            # Nivel 1
        int c = a + b;    # Nivel 2
                          # Vuelve a Nivel 0
        String text = "hola"; # Nivel 1 (INCORRECTO en este contexto)
}
```
Pila: [0] → [0,1] → [0,1,2] → [0] → Estado: VÁLIDO

**Ejemplo con Error:**
```
if(expr==0){              # Nivel 0
    int a = 0;            # Nivel 1
        String b = "hola";# Nivel 2
            if(i>j){int c = 5;} # Nivel 3
                int d = 9;       # Nivel 4
                    bool y = false; # Nivel 5
                        String cadena = "Final"; # Nivel 6 (ERROR)
}
```
Pila: [0] → [0,1] → [0,1,2] → ... → ERROR: Nivel 6 excede máximo de 5

---

## Manejo de Errores

### Tipos de Errores

#### 1. Errores Léxicos
**Causa:** Caracteres o secuencias no reconocidas por JFlex

**Ejemplo:**
```
int @ variable;  # @ no es un token válido
```

**Reporte:**
```
ERROR LÉXICO: Línea 1, Columna 5
Token desconocido: '@'
```

#### 2. Errores de Indentación

**Tipo A: Indentación Inconsistente**
```
if(expresion) {
    int a = b;
        String l = o;  # Error: indentación inconsistente
}
```

**Reporte:**
```
ERROR INDENTACIÓN: Línea 4, Columna 1
Nivel de indentación: 1
Causa: Cierre de bloque en nivel incorrecto
```

**Tipo B: Indentación no Cerrada**
```
while(m == p){
    write("mensaje");
        if(expresion){
    int a = 1;
}
    write("Segundo");  # Error: nivel 2 no se cerró antes
```

**Reporte:**
```
ERROR INDENTACIÓN: Línea 3, Columna 1
Nivel de indentación: 2
Causa: Indentación no cerrada correctamente
```

**Tipo C: Exceso de Indentación**
```
write("opciones");
    write("1.")
        read a;
            write("2.");  # Error: nivel 3 excede el máximo de 5
```

**Reporte:**
```
ERROR INDENTACIÓN: Línea 4, Columna 1
Nivel de indentación: 3
Causa: Exceso de indentación (máximo permitido: 5)
```

**Tipo D: Indentación no Cerrada al Final**
```
if(expr==0){
    int a = 0;
        String b = "hola";
            if(i>j){int c = 5;}
                int d = 9;
}
    write("fin");  # Error: archivo termina en nivel 1
```

**Reporte:**
```
ERROR INDENTACIÓN: Fin de archivo
Pila final: [1]
Causa: Indentación no cerrada correctamente. El archivo debe terminar en nivel 0.
```

#### 3. Errores Sintácticos
**Causa:** Violación de reglas gramaticales

**Ejemplo:**
```
int a = ;  # Falta la expresión después de =
```

**Reporte:**
```
ERROR SINTÁCTICO: Línea 1, Columna 10
Token inesperado: ';'
Esperado: INTNUM | ID | PARENIZQ
```

### Estrategia de Recuperación de Errores

El compilador adopta una estrategia de **reporte completo**:
1. Intenta continuar procesando tras cada error
2. Recopila todos los errores encontrados
3. Genera reporte consolidado al final
4. Marca el archivo como RECHAZADO si hay cualquier error

---

## Estructura del Proyecto

```
MiniLang/
├── src/
│   ├── App.java                    # Punto de entrada
│   ├── Inicio.java                 # Inicialización del programa
│   ├── AnalizadorLexico/
│   │   ├── AnalizadorLexico.java   # Implementación del análisis léxico
│   │   └── Token.java              # Definición de tokens
│   ├── AnalizadorSintactico/
│   │   ├── AnalizadorSintactico.java # Análisis sintáctico LR(1)
│   │   ├── Parser.java             # Parser
│   │   ├── Grafo.java              # Tabla de transiciones
│   │   ├── Estado.java             # Estados del autómata
│   │   └── Reglas.java             # Reglas gramaticales
│   ├── Stack/
│   │   └── PilaIdentacion.java     # Validación de indentación
│   └── Archivo/
│       └── ArchivoMiniLang.java    # Manejo de archivos
├── bin/                            # Compilados
└── README.md                       # Este archivo
```

---

## Uso

### Compilación
```bash
javac -d bin src/**/*.java
```

### Ejecución
```bash
java -cp bin App
```

### Entrada
- Archivos con extensión `.mlng` que contengan código fuente en MiniLang

### Salida
- Archivo `.out` con reporte de análisis y errores detectados

---

## Ejemplo Completo de Análisis

**Archivo entrada.mlng:**
```
int numero = 0;
read(numero);
if (numero > 0)
    write("Positivo");
else
    write("No es positivo");
```

**Salida esperada (reporte .out):**
```
========== REPORTE DE ANÁLISIS ==========

ANÁLISIS LÉXICO: ✓ ACEPTADO
- 27 tokens generados correctamente

ANÁLISIS INDENTACIÓN: ✓ ACEPTADO
- Estructura de indentación válida
- Máximo nivel alcanzado: 1/5

ANÁLISIS SINTÁCTICO: ✓ ACEPTADO
- Cumple con todas las reglas gramaticales

=========================================
ESTADO FINAL: PROGRAMA ACEPTADO
=========================================
```

---

## Notas Importantes

- La indentación se cuenta por **espacios en blanco** (preferiblemente 4 espacios por nivel)
- Un archivo no puede contener errores en ninguna de las tres fases
- El programa es recursivo y evalúa la estructura completa antes de aceptar
- Todos los identificadores deben estar declarados antes de usarse

---

**Fecha de creación:** 2026
**Versión:** 1.0
**Lenguaje:** Java
