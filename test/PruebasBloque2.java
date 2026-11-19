import algoritmos.Busqueda;
import algoritmos.Ordenamiento;
import estructuras.Cola;
import estructuras.ListaSecuencial;
import etl.Extract;
import etl.Load;
import etl.Transform;
import model.RegistroEducativo;
import model.RegistroLimpio;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class PruebasBloque2 {
    private static int pasadas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("=== PRUEBAS BLOQUE 2 (55%: LOAD, BUSQUEDA Y ORDENAMIENTO) ===\n");

        Extract extract = new Extract();
        Cola<RegistroEducativo> cola = extract.extraerACola(
                Path.of("data/muestra_con_errores.csv"), StandardCharsets.UTF_8, ';');
        Transform.ResultadoTransform res = new Transform().ejecutarDesdeCola(cola);

        // 1. Probar LOAD
        Path outDir = Path.of("output");
        new Load().guardarTodo(res, outDir);
        afirmar("Archivo output/registros_limpios.csv creado", Files.exists(outDir.resolve("registros_limpios.csv")));
        afirmar("Archivo output/errores_etl.txt creado", Files.exists(outDir.resolve("errores_etl.txt")));
        afirmar("Archivo output/reporte_etl.txt creado", Files.exists(outDir.resolve("reporte_etl.txt")));
        afirmar("Pila de errores se mantiene intacta tras Load (6)", res.errores.tamanio() == 6);

        // 2. Probar Insertion Sort y Merge Sort
        ListaSecuencial<RegistroLimpio> copia1 = Ordenamiento.aListaSecuencial(res.limpios, 0);
        ListaSecuencial<RegistroLimpio> copia2 = Ordenamiento.aListaSecuencial(res.limpios, 0);

        Ordenamiento.MetricasOrden mIns = Ordenamiento.insertionSort(copia1, Ordenamiento.POR_ESTUDIANTES_DESC);
        Ordenamiento.MetricasOrden mMer = Ordenamiento.mergeSort(copia2, Ordenamiento.POR_ESTUDIANTES_DESC);

        System.out.println("  " + mIns);
        System.out.println("  " + mMer);

        afirmar("Insertion Sort ordena mayor primero (1200)", copia1.obtener(0).getTotalEstudiantes() == 1200);
        afirmar("Merge Sort ordena mayor primero (1200)", copia2.obtener(0).getTotalEstudiantes() == 1200);
        afirmar("Ambos algoritmos producen el mismo minimo al final (24)",
                copia1.obtener(copia1.tamanio() - 1).getTotalEstudiantes() == 24
                && copia2.obtener(copia2.tamanio() - 1).getTotalEstudiantes() == 24);

        // 3. Probar Busqueda secuencial vs binaria
        ListaSecuencial<RegistroLimpio> porClave = Ordenamiento.aListaSecuencial(res.limpios, 0);
        Ordenamiento.mergeSort(porClave, Ordenamiento.POR_CLAVE);

        String claveObjetivo = "2009-2010 Fin|18H00001";
        Busqueda.ResultadoBusqueda bSec = Busqueda.secuencialPorClave(res.limpios, claveObjetivo);
        Busqueda.ResultadoBusqueda bBin = Busqueda.binariaPorClave(porClave, claveObjetivo);

        afirmar("Busqueda secuencial encuentra 18H00001", bSec.encontrado != null);
        afirmar("Busqueda binaria encuentra 18H00001", bBin.encontrado != null);
        afirmar("Busqueda binaria no encuentra clave inexistente",
                Busqueda.binariaPorClave(porClave, "2099-2100 Fin|99X99999").encontrado == null);

        System.out.println("\nPasadas: " + pasadas + " | Fallidas: " + fallidas);
        if (fallidas > 0) System.exit(1);
    }

    private static void afirmar(String desc, boolean cond) {
        if (cond) { System.out.println("[OK]    " + desc); pasadas++; }
        else      { System.out.println("[FALLO] " + desc); fallidas++; }
    }
}