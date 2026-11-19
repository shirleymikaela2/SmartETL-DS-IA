import etl.Extract;
import etl.Transform;
import model.DatasetEducativo;
import model.RegistroLimpio;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class PruebasTransform {

    private static int pasadas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {
        System.out.println("=== EJECUTANDO PRUEBAS DE TRANSFORM (HITO 2) ===\n");

        probarTransformMuestra30();
        probarTransformHistoricoCompleto();

        System.out.println("\n=== RESUMEN DE PRUEBAS TRANSFORM ===");
        System.out.println("Pasadas : " + pasadas);
        System.out.println("Fallidas: " + fallidas);

        if (fallidas > 0) {
            System.exit(1);
        }
    }

    private static void probarTransformMuestra30() {
        try {
            Extract extract = new Extract();
            DatasetEducativo ds = extract.leerMineduc(
                    Path.of("data/muestra_30_registros.csv"), StandardCharsets.UTF_8, ';');

            Transform.ResultadoTransform res = new Transform().ejecutar(ds);
            afirmar("Muestra 30: todos los registros limpios (30)", res.limpios.tamanio() == 30);
            afirmar("Muestra 30: cero descartados", res.descartados == 0);

            RegistroLimpio primero = res.limpios.obtener(0);
            afirmar("Zona normalizada a mayusculas ('ZONA 6')", "ZONA 6".equals(primero.getZona()));
            afirmar("Total estudiantes entero == 47", primero.getTotalEstudiantes() == 47);

            RegistroLimpio segundo = res.limpios.obtener(1);
            afirmar("Dobles espacios eliminados en Nombre_Institucion",
                    !segundo.getNombreInstitucion().contains("  "));
        } catch (Exception e) {
            afirmar("Transform muestra 30 sin excepciones: " + e.getMessage(), false);
        }
    }

    private static void probarTransformHistoricoCompleto() {
        Path rutaPrincipal = Path.of("data/MINEDUC_RegistroAdministrativoHistorico_2009-2024Fin.csv");
        Path rutaAlterna = Path.of("data/registro-administrativo-historico_2009-2024-fin.csv");
        Path archivo = Files.exists(rutaPrincipal) ? rutaPrincipal : rutaAlterna;

        if (!Files.exists(archivo)) {
            System.out.println("[AVISO] No se encontro el CSV historico completo en data/; omitiendo prueba de 286k.");
            return;
        }

        try {
            long inicio = System.nanoTime();
            Extract extract = new Extract();
            DatasetEducativo ds = extract.leerMineduc(archivo, StandardCharsets.UTF_8, ';');
            Transform.ResultadoTransform res = new Transform().ejecutar(ds);
            double segundos = (System.nanoTime() - inicio) / 1e9;

            System.out.printf("Tiempo Extract + Transform (286k): %.3f s%n", segundos);
            afirmar("Historico completo: exactamente 286.111 registros limpios",
                    res.limpios.tamanio() == 286111);
            afirmar("Historico completo: cero registros descartados", res.descartados == 0);

            // Verifica un registro real con separador de miles ('1.010' -> 1010)
            RegistroLimpio conMiles = res.limpios.buscar(
                    r -> "01H00061".equals(r.getAmie()) && "2009-2010 Fin".equals(r.getAnioLectivo()));
            afirmar("Parseo correcto de miles ('1.010' -> 1010) en AMIE 01H00061",
                    conMiles != null && conMiles.getTotalEstudiantes() == 1010);
        } catch (Exception e) {
            afirmar("Transform historico completo sin excepciones: " + e.getMessage(), false);
        }
    }

    private static void afirmar(String descripcion, boolean condicion) {
        if (condicion) {
            System.out.println("[OK]    " + descripcion);
            pasadas++;
        } else {
            System.out.println("[FALLO] " + descripcion);
            fallidas++;
        }
    }
}