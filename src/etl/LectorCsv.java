package etl;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class LectorCsv {

    public void leer(String ruta) throws IOException {

        try (BufferedReader lector =
                Files.newBufferedReader(
                    Path.of(ruta),
                    StandardCharsets.UTF_8)) {

            String encabezado = lector.readLine();

            if (encabezado == null) {
                throw new IOException("El CSV esta vacio");
            }

            System.out.println("Columnas: " + encabezado);

            String linea;
            int total = 0;

            while ((linea = lector.readLine()) != null) {
                if (!linea.isBlank()) {
                    total++;
                }
            }

            System.out.println("Registros leidos: " + total);
        }
    }
}
