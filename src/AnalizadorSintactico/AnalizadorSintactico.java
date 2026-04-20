package AnalizadorSintactico;

import AnalizadorLexico.LexerCup;
import Archivo.ArchivoMiniLang;
import java.io.Reader;


//Coordina la ejecución del análisis léxico y sintáctico.*/
public class AnalizadorSintactico {

    private final ArchivoMiniLang archivoMiniLang;
    private boolean analisisCorrecto;

    public AnalizadorSintactico() {
        this.archivoMiniLang = new ArchivoMiniLang();
        this.analisisCorrecto = false;
    }

    
     
/*naliza un archivo MiniLang usando JFlex y CUP.*
@param rutaArchivo ruta del archivo .mlng*/
public void analizarArchivo(String rutaArchivo) {
    this.analisisCorrecto = false;

        try {
            Reader lector = archivoMiniLang.abrirReader(rutaArchivo);

            LexerCup lexer = new LexerCup(lector);
            Sintax parser = new Sintax(lexer);

            parser.parse();
            analisisCorrecto = true;

        } catch (Exception e) {
            System.out.println("Error durante el analisis: " + e.getMessage());
        }
    }

    /**
     
Muestra el resultado final del análisis.*/
  public void mostrarResultadoFinal() {
      if (analisisCorrecto) {
          System.out.println("OK");} else {
          System.out.println("El analisis finalizo con errores.");}}
}