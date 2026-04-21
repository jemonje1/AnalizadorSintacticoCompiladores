package Archivo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Clase encargada del manejo de archivos para el compilador MiniLang.
 * Valida extensiones .mlng y gestiona la creación de archivos .out.
 */
public class ArchivoMiniLang {

    private final String extensionPermitida;

    public ArchivoMiniLang() {
        this.extensionPermitida = ".mlng";
    }

    /**
     * Valida que la ruta exista y que el archivo tenga la extensión permitida.
     * * @param rutaArchivo Ruta proporcionada por el usuario.
     * @return Path objeto de la ruta validada.
     * @throws IOException Si el archivo no existe o la extensión es incorrecta.
     */
    public Path validarArchivo(String rutaArchivo) throws IOException {
        Path ruta = Paths.get(rutaArchivo);

        if (!Files.exists(ruta)) {
            throw new IOException("Error: El archivo no existe en la ruta especificada: " + rutaArchivo);
        }

        if (!esExtensionValida(rutaArchivo)) {
            throw new IOException("Error: Extension invalida. El archivo debe ser de tipo " + extensionPermitida);
        }

        return ruta;
    }

    /**
     * Verifica si el nombre del archivo termina con la extensión permitida.
     */
    public boolean esExtensionValida(String rutaArchivo) {
        return rutaArchivo != null && rutaArchivo.toLowerCase().endsWith(extensionPermitida);
    }

    /**
     * Genera la ruta para el archivo de salida (.out) en la misma carpeta del original.
     * * @param rutaEntrada String de la ruta del archivo original.
     * @return Path de la ruta donde se escribirá el reporte.
     */
    public Path obtenerRutaSalida(String rutaEntrada) {
        Path entrada = Paths.get(rutaEntrada);
        String nombreArchivo = entrada.getFileName().toString();

        // Remover la extensión original y agregar .out
        int indicePunto = nombreArchivo.lastIndexOf('.');
        String nombreBase = (indicePunto >= 0) ? nombreArchivo.substring(0, indicePunto) : nombreArchivo;
        String nombreSalida = nombreBase + ".out";

        // Si el archivo no tiene carpeta padre (está en la raíz del proyecto)
        if (entrada.getParent() == null) {
            return Paths.get(nombreSalida);
        }

        // Resolver la nueva ruta en el mismo directorio que el archivo original
        return entrada.getParent().resolve(nombreSalida);
    }

    /**
     * Método de conveniencia para leer todo el contenido de un archivo de texto.
     */
    public String leerContenido(String ruta) throws IOException {
        Path path = validarArchivo(ruta);
        return Files.readString(path, StandardCharsets.UTF_8);
    }
}