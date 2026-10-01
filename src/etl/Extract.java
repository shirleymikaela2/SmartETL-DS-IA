package etl;

import java.io.IOException;

public class Extract {

    private final LectorCsv lector;

    public Extract() {
        lector = new LectorCsv();
    }

    public void ejecutar(String ruta) throws IOException {
        System.out.println("Iniciando EXTRACT...");
        lector.leer(ruta);
        System.out.println("Extraccion terminada.");
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
