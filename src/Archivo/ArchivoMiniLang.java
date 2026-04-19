package Archivo;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Manejo de archivos MiniLang (.mlng).
 */
public class ArchivoMiniLang {

    // region METODOS - LECTURA
    /**
     * Valida que la ruta exista y que el archivo tenga extensión .mlng.
     *
     * @param rutaArchivo ruta a validar
     * @return Path del archivo
     * @throws IOException si el archivo no existe o no es .mlng
     */
    public Path validarArchivo(String rutaArchivo) throws IOException {
        Path path = Paths.get(rutaArchivo);
        if (!Files.exists(path)) {
            throw new IOException("El archivo no existe: " + rutaArchivo);
        }
        if (!rutaArchivo.toLowerCase().endsWith(".mlng")) {
            throw new IOException("Extensión invalida. Debe ser .mlng");
        }
        return path;
    }

    /**
     * Lee el archivo línea por línea respetando UTF-8.
     *
     * @param rutaArchivo ruta del archivo .mlng
     * @return lista de líneas del archivo
     * @throws IOException error de lectura
     */
    public List<String> leerLineas(String rutaArchivo) throws IOException {
        Path path = validarArchivo(rutaArchivo);
        List<String> lineas = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // readLine() no incluye el salto, lo manejamos en el lexer
                lineas.add(linea);
            }
        }

        return lineas;
    }
    // endregion

    // region METODOS - SALIDA
    /**
     * Genera la ruta del archivo .out en la misma carpeta del .mlng.
     *
     * @param rutaArchivo ruta del archivo de entrada
     * @return ruta del archivo de salida
     */
    public Path obtenerRutaSalida(String rutaArchivo) {
        Path in = Paths.get(rutaArchivo);
        String nombre = in.getFileName().toString();
        int idx = nombre.lastIndexOf('.');
        String base = (idx >= 0) ? nombre.substring(0, idx) : nombre;
        String outName = base + ".out";
        return (in.getParent() == null) ? Paths.get(outName) : in.getParent().resolve(outName);
    }
    // endregion
}