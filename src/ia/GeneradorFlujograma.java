package ia;

import estructuras.ArbolBST;
import estructuras.Grafo;
import estructuras.ListaEnlazada;
import estructuras.NodoArbol;
import etl.Transform;
import model.RegistroLimpio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Genera los 7 flujogramas Mermaid obligatorios en output/diagramas/ a partir de ejecucion real. */
public class GeneradorFlujograma {

    public static void generarTodos(Transform.ResultadoTransform res,
                                    ArbolBST<RegistroLimpio> bst,
                                    Grafo<String> grafo,
                                    String nodoInicioGrafo,
                                    Path carpetaDiagramas) throws IOException {
        Files.createDirectories(carpetaDiagramas);
        guardar(carpetaDiagramas.resolve("01_etl.md"), "Flujograma 1: ETL Completo (Dinamico)", generarETL(res));
        guardar(carpetaDiagramas.resolve("02_cola.md"), "Flujograma 2: Cola FIFO", generarCola());
        guardar(carpetaDiagramas.resolve("03_pila.md"), "Flujograma 3: Pila LIFO", generarPila());
        guardar(carpetaDiagramas.resolve("04_lista_enlazada.md"), "Flujograma 4: Lista Enlazada", generarLista());
        guardar(carpetaDiagramas.resolve("05_bst.md"), "Flujograma 5: Arbol BST (Dinamico)", generarBST(bst));
        guardar(carpetaDiagramas.resolve("06_grafo_bfs.md"), "Flujograma 6: Grafo + BFS (Dinamico)",
                generarBFS(grafo, nodoInicioGrafo));
        guardar(carpetaDiagramas.resolve("07_grafo_dfs.md"), "Flujograma 7: Grafo + DFS (Dinamico)",
                generarDFS(grafo, nodoInicioGrafo));
    }

    /** 1. Flujograma dinamico del ETL con conteos reales de la corrida. */
    public static String generarETL(Transform.ResultadoTransform res) {
        int proc = (res != null) ? res.procesados : 0;
        int val = (res != null) ? res.limpios.tamanio() : 0;
        int err = (res != null) ? res.descartados : 0;

        return "flowchart TD\n"
                + "    A([Inicio ETL]) --> B[Extract: Leer CSV MINEDUC]\n"
                + "    B --> C[Encolar en Cola FIFO: " + proc + " registros]\n"
                + "    C --> D{Cola vacia?}\n"
                + "    D -->|No| E[Desencolar registro]\n"
                + "    E --> F{Valido, sin duplicado y suma correcta?}\n"
                + "    F -->|Si| G[Insertar en ListaEnlazada: " + val + " validos]\n"
                + "    F -->|No| H[Apilar en Pila de Errores: " + err + " errores]\n"
                + "    G --> D\n"
                + "    H --> D\n"
                + "    D -->|Si| I[Load: Exportar CSV, errores_etl.txt y reporte_etl.txt]\n"
                + "    I --> J([Fin ETL])\n";
    }

    /** 2. Flujograma de Cola FIFO. */
    public static String generarCola() {
        return "flowchart TD\n"
                + "    A([Operacion Cola FIFO]) --> B{Tipo de operacion}\n"
                + "    B -->|encolar| C[Crear Nodo nuevo]\n"
                + "    C --> D{estaVacia?}\n"
                + "    D -->|Si| E[frente = nuevo y fin = nuevo]\n"
                + "    D -->|No| F[fin.siguiente = nuevo y fin = nuevo]\n"
                + "    B -->|desencolar| G{estaVacia?}\n"
                + "    G -->|Si| H[Lanzar excepcion]\n"
                + "    G -->|No| I[Tomar frente.dato y frente = frente.siguiente]\n"
                + "    I --> J{frente == null?}\n"
                + "    J -->|Si| K[fin = null]\n"
                + "    E --> L([cantidad++])\n"
                + "    F --> L\n"
                + "    K --> M([cantidad--])\n"
                + "    J -->|No| M\n";
    }

    /** 3. Flujograma de Pila LIFO. */
    public static String generarPila() {
        return "flowchart TD\n"
                + "    A([Operacion Pila LIFO]) --> B{Tipo de operacion}\n"
                + "    B -->|apilar| C[Crear Nodo nuevo]\n"
                + "    C --> D[nuevo.siguiente = cima]\n"
                + "    D --> E[cima = nuevo]\n"
                + "    E --> F([cantidad++])\n"
                + "    B -->|desapilar| G{estaVacia?}\n"
                + "    G -->|Si| H[Lanzar excepcion]\n"
                + "    G -->|No| I[Tomar cima.dato y cima = cima.siguiente]\n"
                + "    I --> J([cantidad--])\n";
    }

    /** 4. Flujograma de Lista Enlazada. */
    public static String generarLista() {
        return "flowchart TD\n"
                + "    A([Operacion ListaEnlazada]) --> B{Operacion}\n"
                + "    B -->|insertar O(1)| C[Crear Nodo y enlazar en fin.siguiente]\n"
                + "    B -->|buscar O(n)| D[Recorrer desde inicio evaluando criterio]\n"
                + "    D --> E{Coincide?}\n"
                + "    E -->|Si| F([Retornar dato])\n"
                + "    E -->|No| G[actual = actual.siguiente]\n"
                + "    B -->|eliminar O(n)| H[Mantener puntero anterior y actual]\n"
                + "    H --> I[Reconectar anterior.siguiente = actual.siguiente]\n";
    }

    /** 5. Flujograma dinamico del Arbol BST con nodos reales (hasta 12 nodos para legibilidad). */
    public static String generarBST(ArbolBST<RegistroLimpio> bst) {
        StringBuilder sb = new StringBuilder("flowchart TD\n");
        if (bst == null || bst.estaVacio()) {
            sb.append("    A([BST vacio]) --> B[Ejecutar ETL y construir BST]\n");
            return sb.toString();
        }
        sb.append("    INFO[\"BST Real | Nodos: ").append(bst.contarNodos())
          .append(" | Altura: ").append(bst.altura()).append("\"]\n");
        int[] limite = {12};
        construirNodosBST(bst.getRaiz(), sb, limite);
        return sb.toString();
    }

    private static void construirNodosBST(NodoArbol<RegistroLimpio> nodo, StringBuilder sb, int[] restante) {
        if (nodo == null || restante[0] <= 0) return;
        restante[0]--;
        String id = limpiarId(nodo.dato.getAmie());
        sb.append("    N_").append(id).append("[\"").append(nodo.dato.getAmie()).append("\"]\n");

        if (nodo.izquierdo != null && restante[0] > 0) {
            String izqId = limpiarId(nodo.izquierdo.dato.getAmie());
            sb.append("    N_").append(id).append(" -->|Izq| N_").append(izqId).append("\n");
            construirNodosBST(nodo.izquierdo, sb, restante);
        }
        if (nodo.derecho != null && restante[0] > 0) {
            String derId = limpiarId(nodo.derecho.dato.getAmie());
            sb.append("    N_").append(id).append(" -->|Der| N_").append(derId).append("\n");
            construirNodosBST(nodo.derecho, sb, restante);
        }
    }

    /** 6. Flujograma dinamico de Grafo + BFS con traza real de visita. */
    public static String generarBFS(Grafo<String> grafo, String inicio) {
        StringBuilder sb = new StringBuilder("flowchart TD\n");
        sb.append("    A([Inicio BFS con Cola]) --> B[Encolar inicio y marcar visitado]\n");
        sb.append("    B --> C{Cola vacia?}\n");
        sb.append("    C -->|No| D[Desencolar vertice y encolar adyacentes no visitados]\n");
        sb.append("    D --> C\n");
        sb.append("    C -->|Si| E([Fin BFS])\n");

        if (grafo != null && inicio != null) {
            ListaEnlazada<String> orden = grafo.bfs(inicio);
            int limite = Math.min(8, orden.tamanio());
            if (limite > 0) {
                sb.append("    subgraph Traza_BFS_Real\n");
                for (int i = 0; i < limite; i++) {
                    sb.append("        B").append(i).append("[\"").append(i + 1).append(". ")
                      .append(limpiarEtiqueta(orden.obtener(i))).append("\"]\n");
                    if (i > 0) {
                        sb.append("        B").append(i - 1).append(" --> B").append(i).append("\n");
                    }
                }
                sb.append("    end\n");
            }
        }
        return sb.toString();
    }

    /** 7. Flujograma dinamico de Grafo + DFS con traza real de visita. */
    public static String generarDFS(Grafo<String> grafo, String inicio) {
        StringBuilder sb = new StringBuilder("flowchart TD\n");
        sb.append("    A([Inicio DFS con Pila]) --> B[Apilar inicio]\n");
        sb.append("    B --> C{Pila vacia?}\n");
        sb.append("    C -->|No| D[Desapilar vertice]\n");
        sb.append("    D --> E{Visitado?}\n");
        sb.append("    E -->|No| F[Marcar visitado y apilar adyacentes]\n");
        sb.append("    F --> C\n");
        sb.append("    E -->|Si| C\n");
        sb.append("    C -->|Si| G([Fin DFS])\n");

        if (grafo != null && inicio != null) {
            ListaEnlazada<String> orden = grafo.dfs(inicio);
            int limite = Math.min(8, orden.tamanio());
            if (limite > 0) {
                sb.append("    subgraph Traza_DFS_Real\n");
                for (int i = 0; i < limite; i++) {
                    sb.append("        D").append(i).append("[\"").append(i + 1).append(". ")
                      .append(limpiarEtiqueta(orden.obtener(i))).append("\"]\n");
                    if (i > 0) {
                        sb.append("        D").append(i - 1).append(" --> D").append(i).append("\n");
                    }
                }
                sb.append("    end\n");
            }
        }
        return sb.toString();
    }

    private static void guardar(Path archivo, String titulo, String mermaid) throws IOException {
        String contenido = "# " + titulo + "\n\n```mermaid\n" + mermaid + "```\n";
        Files.writeString(archivo, contenido, StandardCharsets.UTF_8);
    }

    private static String limpiarId(String texto) {
        return texto.replaceAll("[^a-zA-Z0-9]", "_");
    }

    private static String limpiarEtiqueta(String texto) {
        return texto.replace("\"", "'");
    }
}