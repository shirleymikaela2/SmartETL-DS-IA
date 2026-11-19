package ia;

import estructuras.ArbolBST;
import estructuras.Grafo;
import estructuras.ListaEnlazada;
import estructuras.Vertice;
import etl.Transform;
import model.RegistroLimpio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

/** Construye y guarda evidencia de prompts tecnicos a partir del estado real de las estructuras. */
public class GeneradorPrompt {

    public static String promptAnalisisErrores(Transform.ResultadoTransform res) {
        StringBuilder sb = new StringBuilder();
        sb.append("Actua como ingeniero de datos. Analiza los siguientes resultados agregados del ETL MINEDUC:\n");
        sb.append("- Registros procesados: ").append(res.procesados).append("\n");
        sb.append("- Registros validos: ").append(res.limpios.tamanio()).append("\n");
        sb.append("- Total de errores: ").append(res.descartados).append("\n");
        sb.append("- Frecuencia por categoria:\n");
        if (res.frecuenciaErrores.isEmpty()) {
            sb.append("  * Ningun error detectado.\n");
        } else {
            for (Map.Entry<String, Integer> e : res.frecuenciaErrores.entrySet()) {
                sb.append("  * ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
            }
        }
        sb.append("Explica los patrones de error y recomienda acciones concretas de calidad de datos.");
        return sb.toString();
    }

    public static String promptExplicarBST(ArbolBST<RegistroLimpio> bst) {
        StringBuilder sb = new StringBuilder();
        sb.append("Explica el estado actual de este Arbol Binario de Busqueda (BST) del proyecto SmartETL-DS:\n");
        sb.append("- Total de nodos: ").append(bst.contarNodos()).append("\n");
        sb.append("- Altura actual: ").append(bst.altura()).append("\n");
        if (bst.getRaiz() != null) {
            sb.append("- Raiz (clave): ").append(bst.getRaiz().dato.getClaveUnica()).append("\n");
        }
        sb.append("- Primeras claves en InOrden: ");
        ListaEnlazada<RegistroLimpio> inOrden = bst.inOrden();
        int limite = Math.min(8, inOrden.tamanio());
        for (int i = 0; i < limite; i++) {
            if (i > 0) sb.append(", ");
            sb.append(inOrden.obtener(i).getAmie());
        }
        sb.append("\nEvalua si el arbol tiende a desbalancearse y su impacto en la complejidad O(h).");
        return sb.toString();
    }

    public static String promptExplicarGrafo(Grafo<String> grafo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Explica la topologia de este grafo dirigido territorial MINEDUC:\n");
        sb.append("- Vertices: ").append(grafo.contarVertices()).append("\n");
        sb.append("- Aristas: ").append(grafo.contarAristas()).append("\n");
        sb.append("- Contiene ciclos: ").append(grafo.tieneCiclos() ? "SI" : "NO").append("\n");
        sb.append("- Lista de adyacencia (muestra):\n");
        int mostrados = 0;
        for (Vertice<String> v : grafo.getVertices()) {
            if (mostrados++ >= 10) break;
            sb.append("  * ").append(v.dato).append(" -> ");
            boolean primero = true;
            for (Vertice<String> ady : v.adyacentes) {
                if (!primero) sb.append(", ");
                sb.append(ady.dato);
                primero = false;
            }
            if (primero) sb.append("(sin salidas)");
            sb.append("\n");
        }
        sb.append("Describe que representan las rutas, BFS, DFS y el significado de ciclos.");
        return sb.toString();
    }

    public static void guardarEvidenciaPrompt(Path archivo, String titulo, String prompt, String respuesta)
            throws IOException {
        if (archivo.getParent() != null) {
            Files.createDirectories(archivo.getParent());
        }
        String bloque = "\n========================================\n"
                + "EVIDENCIA IA: " + titulo + "\n"
                + "--- PROMPT ---\n" + prompt + "\n"
                + "--- RESPUESTA ---\n" + respuesta + "\n";
        Files.writeString(archivo, bloque, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}