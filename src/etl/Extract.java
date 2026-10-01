
package etl;

import estructuras.ListaEnlazada;
import model.DatasetEducativo;
import model.EsquemaCsv;
import model.RegistroEducativo;
import model.RegistroMatricula;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Extract {

    private final LectorCsv lector;

    public Extract() {
        lector = new LectorCsv();
    }

    // Conserva el funcionamiento anterior del modulo Extract.
    public void ejecutar(String ruta) throws IOException {
        System.out.println("Iniciando EXTRACT...");
        lector.leer(ruta);
        System.out.println("Extraccion terminada.");
    }

    // Detecta el separador a partir de la cabecera.
    public char detectarSeparador(Path archivo, Charset codificacion)
            throws IOException {

        try (BufferedReader lectorArchivo =
                Files.newBufferedReader(archivo, codificacion)) {

            String encabezado = lectorArchivo.readLine();

            if (encabezado == null) {
                throw new IOException("El CSV esta vacio");
            }

            char[] candidatos = {',', ';', '\t'};
            char elegido = ',';
            int mayorCantidad = 0;

            for (char candidato : candidatos) {
                int cantidad = 0;

                for (int i = 0; i < encabezado.length(); i++) {
                    if (encabezado.charAt(i) == candidato) {
                        cantidad++;
                    }
                }

                if (cantidad > mayorCantidad) {
                    mayorCantidad = cantidad;
                    elegido = candidato;
                }
            }

            if (mayorCantidad == 0) {
                throw new IOException(
                    "No se pudo detectar el separador del CSV"
                );
            }

            return elegido;
        }
    }

    // Lee el CSV y devuelve el dataset completo.
    public DatasetEducativo leerMineduc(
            Path archivo,
            Charset codificacion,
            char separador) throws IOException {

        try (BufferedReader lectorArchivo =
                Files.newBufferedReader(archivo, codificacion)) {

            String encabezado = lectorArchivo.readLine();

            if (encabezado == null) {
                throw new IOException("El CSV esta vacio");
            }

            // Elimina el BOM si el archivo lo contiene.
            if (encabezado.startsWith("\uFEFF")) {
                encabezado = encabezado.substring(1);
            }

            String[] columnas = dividirCsv(encabezado, separador);
            EsquemaCsv esquema = new EsquemaCsv(columnas);

            // Comprueba que sean las 23 columnas del MINEDUC.
            RegistroMatricula.validarEsquema(esquema);

            ListaEnlazada<RegistroEducativo> registros =
                    new ListaEnlazada<>();

            String linea;
            long numeroFila = 1;

            while ((linea = lectorArchivo.readLine()) != null) {
                numeroFila++;

                if (linea.isBlank()) {
                    continue;
                }

                String[] valores = dividirCsv(linea, separador);

                if (valores.length != esquema.cantidad()) {
                    throw new IOException(
                        "Cantidad incorrecta de columnas en la fila "
                        + numeroFila + ": se esperaban "
                        + esquema.cantidad() + " y se encontraron "
                        + valores.length
                    );
                }

                RegistroMatricula registro;

                try {
                    registro = new RegistroMatricula(
                        numeroFila, esquema, valores
                    );
                } catch (IllegalArgumentException e) {
                    throw new IOException(
                        "Error en la fila " + numeroFila
                        + ": " + e.getMessage(), e
                    );
                }

                registros.insertar(registro);
            }

            return new DatasetEducativo(
                esquema, registros, separador
            );
        }
    }

    // Divide una fila respetando campos entre comillas.
    private String[] dividirCsv(String linea, char separador)
            throws IOException {

        List<String> campos = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean entreComillas = false;

        for (int i = 0; i < linea.length(); i++) {
            char actual = linea.charAt(i);

            if (actual == '"') {
                if (entreComillas
                        && i + 1 < linea.length()
                        && linea.charAt(i + 1) == '"') {

                    campo.append('"');
                    i++;
                } else {
                    entreComillas = !entreComillas;
                }

            } else if (actual == separador && !entreComillas) {
                campos.add(campo.toString());
                campo.setLength(0);

            } else {
                campo.append(actual);
            }
        }

        if (entreComillas) {
            throw new IOException(
                "Campo CSV con comillas sin cerrar"
            );
        }

        campos.add(campo.toString());

        return campos.toArray(new String[0]);
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Indica la ruta del CSV.");
            return;
        }

        try {
            new Extract().ejecutar(args[0]);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
