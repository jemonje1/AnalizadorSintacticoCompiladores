package Archivo;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manejo de archivos MiniLang (.mlng).
 */
public class ArchivoMiniLang {

    private final String extensionPermitida;

    public ArchivoMiniLang() {
        this.extensionPermitida = ".mlng";
    }

    /**
     * Valida que la ruta exista y que el archivo tenga extensión .mlng.
     *
     * @param rutaArchivo ruta del archivo
     * @return Path del archivo
     * @throws IOException si la ruta no existe o la extensión es inválida
     */
    public Path validarArchivo(String rutaArchivo) throws IOException {
        Path rutaValida = Paths.get(rutaArchivo);

        if (!Files.exists(rutaValida)) {
            throw new IOException("El archivo no existe: " + rutaArchivo);
        }

        if (!esExtensionValida(rutaArchivo)) {
            throw new IOException("La extensión del archivo es inválida. Debe ser " + extensionPermitida);
        }

        return rutaValida;
    }

    /**
     * Abre un Reader para el archivo MiniLang.
     *
     * @param rutaArchivo ruta del archivo
     * @return Reader listo para el lexer
     * @throws IOException si ocurre un error al abrir el archivo
     */
    public Reader abrirReader(String rutaArchivo) throws IOException {
        Path rutaValida = validarArchivo(rutaArchivo);
        return Files.newBufferedReader(rutaValida, StandardCharsets.UTF_8);
    }

    /**
     * Verifica si la ruta tiene la extensión permitida.
     *
     * @param rutaArchivo ruta del archivo
     * @return true si la extensión es válida
     */
    public boolean esExtensionValida(String rutaArchivo) {
        return rutaArchivo != null && rutaArchivo.toLowerCase().endsWith(extensionPermitida);
    }

    /**
     * Genera la ruta del archivo de salida.
     *
     * @param rutaArchivo ruta del archivo de entrada
     * @return ruta del archivo .out
     */
    public Path obtenerRutaSalida(String rutaArchivo) {
        Path rutaEntrada = Paths.get(rutaArchivo);
        String nombreArchivo = rutaEntrada.getFileName().toString();

        int indicePunto = nombreArchivo.lastIndexOf('.');
        String nombreBase = (indicePunto >= 0) ? nombreArchivo.substring(0, indicePunto) : nombreArchivo;

        String nombreSalida = nombreBase + ".out";

        if (rutaEntrada.getParent() == null) {
            return Paths.get(nombreSalida);
        }

        return rutaEntrada.getParent().resolve(nombreSalida);
    }
}