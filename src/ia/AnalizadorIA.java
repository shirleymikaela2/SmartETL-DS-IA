package ia;

import estructuras.ArbolBST;
import estructuras.Grafo;
import etl.Transform;
import model.RegistroLimpio;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

/** Capa de analisis asistido por IA (Gemini API opcional + motor analitico local de respaldo). */
public class AnalizadorIA {

    private static final Path ARCHIVO_EVIDENCIA = Path.of("output", "ia_prompts.txt");

    public static String analizarErroresETL(Transform.ResultadoTransform res) {
        String prompt = GeneradorPrompt.promptAnalisisErrores(res);
        String respuestaApi = consultarGeminiSiDisponible(prompt);
        String respuesta = (respuestaApi != null) ? respuestaApi : analizarErroresLocal(res);
        guardarSilencioso("ANALISIS_ERRORES_ETL", prompt, respuesta);
        return respuesta;
    }

    public static String explicarBST(ArbolBST<RegistroLimpio> bst) {
        String prompt = GeneradorPrompt.promptExplicarBST(bst);
        String respuestaApi = consultarGeminiSiDisponible(prompt);
        String respuesta = (respuestaApi != null) ? respuestaApi : explicarBSTLocal(bst);
        guardarSilencioso("EXPLICACION_BST", prompt, respuesta);
        return respuesta;
    }

    public static String explicarGrafo(Grafo<String> grafo) {
        String prompt = GeneradorPrompt.promptExplicarGrafo(grafo);
        String respuestaApi = consultarGeminiSiDisponible(prompt);
        String respuesta = (respuestaApi != null) ? respuestaApi : explicarGrafoLocal(grafo);
        guardarSilencioso("EXPLICACION_GRAFO", prompt, respuesta);
        return respuesta;
    }

    private static String analizarErroresLocal(Transform.ResultadoTransform res) {
        StringBuilder sb = new StringBuilder();
        sb.append("[Analisis IA - Motor Local]\n");
        if (res.descartados == 0) {
            sb.append("El lote procesado (").append(res.procesados)
              .append(" registros) paso el 100% de las reglas de calidad sin errores en la Pila.\n");
            return sb.toString();
        }
        double pctError = (res.descartados * 100.0) / Math.max(1, res.procesados);
        String categoriaDominante = "N/A";
        int maxFrec = 0;
        for (Map.Entry<String, Integer> e : res.frecuenciaErrores.entrySet()) {
            if (e.getValue() > maxFrec) {
                maxFrec = e.getValue();
                categoriaDominante = e.getKey();
            }
        }
        sb.append(String.format("Se detectaron %d errores sobre %d registros (%.2f%% de rechazo).\n",
                res.descartados, res.procesados, pctError));
        sb.append("La causa principal es [").append(categoriaDominante).append("] con ")
          .append(maxFrec).append(" ocurrencias.\n");
        sb.append("Recomendacion: aplicar validacion en origen antes de la carga CSV y mantener ")
          .append("la Pila<ErrorETL> para auditar el ultimo error ingresado en O(1).");
        return sb.toString();
    }

    private static String explicarBSTLocal(ArbolBST<RegistroLimpio> bst) {
        int n = bst.contarNodos();
        int h = bst.altura();
        int alturaIdeal = (n == 0) ? 0 : (int) Math.ceil(Math.log(n + 1) / Math.log(2));
        String estado = (h <= alturaIdeal * 1.5 + 1) ? "razonablemente balanceado" : "desbalanceado (tiende a lista)";
        String raizClave = (bst.getRaiz() != null) ? bst.getRaiz().dato.getClaveUnica() : "N/A";

        return "[Explicacion IA BST - Motor Local]\n"
                + "El BST contiene " + n + " nodos con raiz '" + raizClave + "' y altura real h=" + h
                + " (altura optima teorica ~" + alturaIdeal + ").\n"
                + "Diagnostico estructural: el arbol esta " + estado + ".\n"
                + "Complejidad: la busqueda tarda O(h)=" + h + " comparaciones en el peor caso, "
                + "frente a O(n)=" + n + " en una busqueda secuencial sobre ListaEnlazada. "
                + "La implementacion iterativa evita StackOverflowError incluso si las claves llegan ordenadas.";
    }

    private static String explicarGrafoLocal(Grafo<String> grafo) {
        int v = grafo.contarVertices();
        int a = grafo.contarAristas();
        boolean ciclos = grafo.tieneCiclos();

        return "[Explicacion IA Grafo - Motor Local]\n"
                + "El grafo dirigido modela la jerarquia territorial y derivacion educativa MINEDUC con "
                + v + " vertices y " + a + " aristas almacenadas en listas de adyacencia propias.\n"
                + "Integridad de dependencias: "
                + (ciclos ? "SE DETECTARON CICLOS (dependencia circular que requiere revision)."
                          : "SIN CICLOS (estructura aciclica valida).") + "\n"
                + "BFS reutiliza Cola<T> para explorar por niveles y hallar rutas mas cortas en saltos; "
                + "DFS reutiliza Pila<T> para explorar ramas en profundidad.";
    }

    private static String consultarGeminiSiDisponible(String prompt) {
        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            return null;
        }
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key="
                    + apiKey.trim();
            String jsonBody = "{\"contents\":[{\"parts\":[{\"text\":\""
                    + escaparJson(prompt) + "\"}]}]}";

            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                String body = resp.body();
                int idx = body.indexOf("\"text\": \"");
                if (idx != -1) {
                    int inicio = idx + 9;
                    int fin = body.indexOf("\"", inicio);
                    if (fin != -1) {
                        return "[Respuesta Gemini API]\n"
                                + body.substring(inicio, fin).replace("\\n", "\n");
                    }
                }
            }
        } catch (Exception ignored) {
            // Si falla red o cuota, cae automaticamente al motor local
        }
        return null;
    }

    private static String escaparJson(String texto) {
        return texto.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private static void guardarSilencioso(String titulo, String prompt, String respuesta) {
        try {
            GeneradorPrompt.guardarEvidenciaPrompt(ARCHIVO_EVIDENCIA, titulo, prompt, respuesta);
        } catch (Exception ignored) {
        }
    }
}