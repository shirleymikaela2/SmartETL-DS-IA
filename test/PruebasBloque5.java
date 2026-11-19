import algoritmos.Ordenamiento;
import estructuras.ArbolBST;
import estructuras.Cola;
import estructuras.Grafo;
import etl.Extract;
import etl.Transform;
import ia.AnalizadorIA;
import ia.GeneradorFlujograma;
import model.RegistroEducativo;
import model.RegistroLimpio;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class PruebasBloque5 {
    private static int pasadas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("=== PRUEBAS BLOQUE 5 (95%: CAPA DE IA + 7 FLUJOGRAMAS MERMAID) ===\n");

        Extract extract = new Extract();
        Cola<RegistroEducativo> cola = extract.extraerACola(
                Path.of("data/muestra_con_errores.csv"), StandardCharsets.UTF_8, ';');
        Transform.ResultadoTransform res = new Transform().ejecutarDesdeCola(cola);

        ArbolBST<RegistroLimpio> bst = new ArbolBST<RegistroLimpio>(Ordenamiento.POR_CLAVE);
        Grafo<String> grafo = new Grafo<>();
        for (RegistroLimpio r : res.limpios) {
            bst.insertar(r);
            grafo.agregarArista(r.getZona(), r.getProvincia());
            grafo.agregarArista(r.getProvincia(), r.getCanton());
            grafo.agregarArista(r.getCanton(), r.getNombreInstitucion());
        }

        Path dirDiag = Path.of("output", "diagramas");
        GeneradorFlujograma.generarTodos(res, bst, grafo, "ZONA 3", dirDiag);

        String[] archivos = {
            "01_etl.md", "02_cola.md", "03_pila.md", "04_lista_enlazada.md",
            "05_bst.md", "06_grafo_bfs.md", "07_grafo_dfs.md"
        };
        for (String nombre : archivos) {
            afirmar("Diagrama generado: output/diagramas/" + nombre, Files.exists(dirDiag.resolve(nombre)));
        }

        String analisisErr = AnalizadorIA.analizarErroresETL(res);
        String explicBst = AnalizadorIA.explicarBST(bst);
        String explicGrafo = AnalizadorIA.explicarGrafo(grafo);

        afirmar("Analisis de errores IA generado", analisisErr != null && !analisisErr.isBlank());
        afirmar("Explicacion de BST IA generada", explicBst != null && !explicBst.isBlank());
        afirmar("Explicacion de Grafo IA generada", explicGrafo != null && !explicGrafo.isBlank());
        afirmar("Evidencia de prompts guardada en output/ia_prompts.txt",
                Files.exists(Path.of("output", "ia_prompts.txt")));

        System.out.println("\n" + analisisErr);
        System.out.println("\n" + explicBst);
        System.out.println("\n" + explicGrafo);

        System.out.println("\nPasadas: " + pasadas + " | Fallidas: " + fallidas);
        if (fallidas > 0) System.exit(1);
    }

    private static void afirmar(String desc, boolean cond) {
        if (cond) { System.out.println("[OK]    " + desc); pasadas++; }
        else      { System.out.println("[FALLO] " + desc); fallidas++; }
    }
}