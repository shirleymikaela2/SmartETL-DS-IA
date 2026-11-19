import algoritmos.Busqueda;
import algoritmos.Ordenamiento;
import estructuras.ArbolBST;
import estructuras.Cola;
import estructuras.Grafo;
import estructuras.ListaEnlazada;
import estructuras.ListaSecuencial;
import etl.Extract;
import etl.Load;
import etl.Transform;
import ia.AnalizadorIA;
import ia.GeneradorFlujograma;
import model.RegistroEducativo;
import model.RegistroLimpio;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    private static final Scanner SC = new Scanner(System.in);

    // Estado compartido del sistema
    private static Cola<RegistroEducativo> colaETL;
    private static Transform.ResultadoTransform resultadoETL;
    private static ArbolBST<RegistroLimpio> bst;
    private static Grafo<String> grafo;
    private static String primerVerticeGrafo = "ZONA 3";

    public static void main(String[] args) {
        int opcion = -1;
        while (opcion != 0) {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opcion: ");
            System.out.println();
            try {
                switch (opcion) {
                    // --- ETL ---
                    case 1 -> ejecutarExtract();
                    case 2 -> mostrarCola();
                    case 3 -> ejecutarTransform();
                    case 4 -> mostrarValidos();
                    case 5 -> mostrarPilaErrores();
                    case 6 -> ejecutarLoad();
                    // --- BUSQUEDA Y ORDENAMIENTO ---
                    case 7 -> buscarRegistro();
                    case 8 -> ordenarYComparar();
                    // --- ARBOL BST ---
                    case 9 -> construirBST();
                    case 10 -> buscarEnBST();
                    case 11 -> eliminarEnBST();
                    case 12 -> mostrarRecorridosBST();
                    case 13 -> mostrarMetricasBST();
                    // --- GRAFOS ---
                    case 14 -> construirGrafo();
                    case 15 -> mostrarConexionesGrafo();
                    case 16 -> ejecutarBFS();
                    case 17 -> ejecutarDFS();
                    case 18 -> buscarRutaGrafo();
                    case 19 -> detectarCiclosGrafo();
                    // --- INTELIGENCIA ARTIFICIAL ---
                    case 20 -> generarFlujogramasIA();
                    case 21 -> explicarBSTconIA();
                    case 22 -> explicarGrafoConIA();
                    case 23 -> analizarErroresConIA();
                    case 0 -> System.out.println("Saliendo de SmartETL-DS + IA...");
                    default -> System.out.println("Opcion no valida.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n====================================================");
        System.out.println("        SMARTETL-DS + IA - ESTRUCTURAS DE DATOS     ");
        System.out.println("====================================================");
        System.out.println("ETL (Cola, Lista, Pila, Archivos)");
        System.out.println("  1. Extraer datos a Cola FIFO (Extract)");
        System.out.println("  2. Mostrar Cola de extraccion");
        System.out.println("  3. Transformar datos (Cola -> Lista / Pila)");
        System.out.println("  4. Mostrar registros validos (ListaEnlazada)");
        System.out.println("  5. Mostrar Pila de errores (LIFO)");
        System.out.println("  6. Cargar resultados en output/ (Load)");
        System.out.println("BUSQUEDA Y ORDENAMIENTO");
        System.out.println("  7. Buscar registro (Secuencial vs Binaria)");
        System.out.println("  8. Ordenar y comparar (Insertion Sort vs Merge Sort)");
        System.out.println("ARBOL BINARIO DE BUSQUEDA (BST)");
        System.out.println("  9. Construir BST desde registros limpios");
        System.out.println(" 10. Buscar en BST (con metricas)");
        System.out.println(" 11. Eliminar nodo en BST");
        System.out.println(" 12. Recorridos BST (InOrden / PreOrden / PostOrden)");
        System.out.println(" 13. Altura y numero de nodos del BST");
        System.out.println("GRAFOS (Lista de Adyacencia, BFS y DFS)");
        System.out.println(" 14. Construir Grafo territorial MINEDUC");
        System.out.println(" 15. Mostrar conexiones (lista de adyacencia)");
        System.out.println(" 16. Recorrido BFS (usando Cola propia)");
        System.out.println(" 17. Recorrido DFS (usando Pila propia)");
        System.out.println(" 18. Buscar ruta entre dos vertices (BFS)");
        System.out.println(" 19. Detectar ciclos (y opcion de probar ciclo)");
        System.out.println("INTELIGENCIA ARTIFICIAL Y DIAGRAMAS");
        System.out.println(" 20. Generar los 7 flujogramas Mermaid en output/diagramas/");
        System.out.println(" 21. Explicar Arbol BST con IA");
        System.out.println(" 22. Explicar Grafo con IA");
        System.out.println(" 23. Analizar errores ETL con IA");
        System.out.println("  0. Salir");
        System.out.println("====================================================");
    }

    // ==================== ETL ====================

    private static void ejecutarExtract() throws Exception {
        System.out.println("Seleccione el dataset a cargar:");
        System.out.println("  1. data/muestra_con_errores.csv (14 filas: 8 validas + 6 errores - recomendado para defensa)");
        System.out.println("  2. data/muestra_30_registros.csv (30 filas limpias)");
        System.out.println("  3. Dataset historico completo MINEDUC (286.111 filas)");
        int sel = leerEntero("Dataset: ");

        Path archivo;
        if (sel == 1) {
            archivo = Path.of("data", "muestra_con_errores.csv");
        } else if (sel == 2) {
            archivo = Path.of("data", "muestra_30_registros.csv");
        } else if (sel == 3) {
            Path p1 = Path.of("data", "MINEDUC_RegistroAdministrativoHistorico_2009-2024Fin.csv");
            Path p2 = Path.of("data", "registro-administrativo-historico_2009-2024-fin.csv");
            archivo = Files.exists(p1) ? p1 : p2;
        } else {
            System.out.println("Seleccion cancelada.");
            return;
        }

        if (!Files.exists(archivo)) {
            System.out.println("No se encontro el archivo: " + archivo);
            return;
        }

        long inicio = System.nanoTime();
        Extract extract = new Extract();
        char sep = extract.detectarSeparador(archivo, StandardCharsets.UTF_8);
        colaETL = extract.extraerACola(archivo, StandardCharsets.UTF_8, sep);
        resultadoETL = null;
        bst = null;
        grafo = null;
        double seg = (System.nanoTime() - inicio) / 1e9;
        System.out.printf("EXTRACT completado: %d registros encolados en %.3f s (separador '%c').%n",
                colaETL.tamanio(), seg, sep);
    }

    private static void mostrarCola() {
        if (colaETL == null || colaETL.estaVacia()) {
            System.out.println("La cola esta vacia. Ejecute primero la opcion 1 (Extract).");
            return;
        }
        System.out.println("Registros en Cola FIFO (total=" + colaETL.tamanio() + "):");
        colaETL.mostrar(15);
    }

    private static void ejecutarTransform() {
        if (colaETL == null || colaETL.estaVacia()) {
            System.out.println("No hay registros en la cola. Ejecute primero la opcion 1 (Extract).");
            return;
        }
        long inicio = System.nanoTime();
        resultadoETL = new Transform().ejecutarDesdeCola(colaETL);
        double seg = (System.nanoTime() - inicio) / 1e9;
        System.out.printf("TRANSFORM completado en %.3f s.%n", seg);
        System.out.println("  Procesados : " + resultadoETL.procesados);
        System.out.println("  Validos    : " + resultadoETL.limpios.tamanio());
        System.out.println("  Errores    : " + resultadoETL.descartados);
    }

    private static void mostrarValidos() {
        if (!verificarTransform()) return;
        System.out.println("Registros validos en ListaEnlazada (total=" + resultadoETL.limpios.tamanio() + "):");
        int i = 0;
        for (RegistroLimpio r : resultadoETL.limpios) {
            if (i >= 15) {
                System.out.println("  ... (" + (resultadoETL.limpios.tamanio() - 15) + " registros adicionales)");
                break;
            }
            System.out.println("  [" + (i + 1) + "] " + r);
            i++;
        }
    }

    private static void mostrarPilaErrores() {
        if (!verificarTransform()) return;
        System.out.println("Pila de errores ETL (total=" + resultadoETL.errores.tamanio() + "):");
        resultadoETL.errores.mostrar(20);
    }

    private static void ejecutarLoad() throws Exception {
        if (!verificarTransform()) return;
        Path salida = Path.of("output");
        new Load().guardarTodo(resultadoETL, salida);
        System.out.println("LOAD completado. Archivos generados en output/:");
        System.out.println("  - output/registros_limpios.csv");
        System.out.println("  - output/errores_etl.txt");
        System.out.println("  - output/reporte_etl.txt");
    }

    // ==================== BUSQUEDA Y ORDENAMIENTO ====================

    private static void buscarRegistro() {
        if (!verificarTransform()) return;
        System.out.print("Ingrese clave unica (ej. 2009-2010 Fin|18H00001) o solo AMIE (ej. 18H00001): ");
        String entrada = SC.nextLine().trim();

        if (!entrada.contains("|")) {
            ListaEnlazada<RegistroLimpio> hallados = Busqueda.buscarTodosPorAmie(resultadoETL.limpios, entrada);
            System.out.println("Coincidencias por AMIE '" + entrada + "': " + hallados.tamanio());
            int c = 0;
            for (RegistroLimpio r : hallados) {
                if (c++ >= 10) break;
                System.out.println("  " + r);
            }
            return;
        }

        Busqueda.ResultadoBusqueda sec = Busqueda.secuencialPorClave(resultadoETL.limpios, entrada);
        ListaSecuencial<RegistroLimpio> ordenada = Ordenamiento.aListaSecuencial(resultadoETL.limpios, 0);
        Ordenamiento.mergeSort(ordenada, Ordenamiento.POR_CLAVE);
        Busqueda.ResultadoBusqueda bin = Busqueda.binariaPorClave(ordenada, entrada);

        System.out.printf("Secuencial -> encontrado=%s | comparaciones=%d | tiempo=%.4f ms%n",
                sec.encontrado != null, sec.comparaciones, sec.tiempoMs);
        System.out.printf("Binaria    -> encontrado=%s | comparaciones=%d | tiempo=%.4f ms%n",
                bin.encontrado != null, bin.comparaciones, bin.tiempoMs);
        if (bin.encontrado != null) {
            System.out.println("Registro: " + bin.encontrado);
        }
    }

    private static void ordenarYComparar() {
        if (!verificarTransform()) return;
        int limite = Math.min(resultadoETL.limpios.tamanio(), 2000);
        System.out.println("Comparando Insertion Sort O(n^2) vs Merge Sort O(n log n) sobre n=" + limite
                + " registros por Total_Estudiantes DESC:");

        ListaSecuencial<RegistroLimpio> l1 = Ordenamiento.aListaSecuencial(resultadoETL.limpios, limite);
        ListaSecuencial<RegistroLimpio> l2 = Ordenamiento.aListaSecuencial(resultadoETL.limpios, limite);

        Ordenamiento.MetricasOrden m1 = Ordenamiento.insertionSort(l1, Ordenamiento.POR_ESTUDIANTES_DESC);
        Ordenamiento.MetricasOrden m2 = Ordenamiento.mergeSort(l2, Ordenamiento.POR_ESTUDIANTES_DESC);

        System.out.println("  " + m1);
        System.out.println("  " + m2);
        System.out.println("Top 5 instituciones con mas estudiantes:");
        l2.mostrar(5);
    }

    // ==================== ARBOL BST ====================

    private static void construirBST() {
        if (!verificarTransform()) return;
        int limite = leerEntero("Cantidad maxima de registros a indexar (0 para todos, ej. 500): ");
        bst = new ArbolBST<RegistroLimpio>(Ordenamiento.POR_CLAVE);
        int i = 0;
        for (RegistroLimpio r : resultadoETL.limpios) {
            if (limite > 0 && i >= limite) break;
            bst.insertar(r);
            i++;
        }
        System.out.println("BST construido -> Nodos: " + bst.contarNodos() + " | Altura: " + bst.altura());
    }

    private static void buscarEnBST() {
        if (!verificarBST()) return;
        System.out.print("Anio lectivo (ej. 2009-2010 Fin): ");
        String anio = SC.nextLine().trim();
        System.out.print("AMIE (ej. 18H00001): ");
        String amie = SC.nextLine().trim().toUpperCase();

        RegistroLimpio ref = new RegistroLimpio(anio, "", "", "", "", "", amie, "", "", "", 1, 1, 0, 0);
        ArbolBST.ResultadoBusquedaBST<RegistroLimpio> r = bst.buscarConMetricas(ref);
        System.out.printf("BST -> encontrado=%s | comparaciones=%d | tiempo=%.4f ms%n",
                r.encontrado != null, r.comparaciones, r.tiempoMs);
        if (r.encontrado != null) {
            System.out.println("Registro: " + r.encontrado);
        }
    }

    private static void eliminarEnBST() {
        if (!verificarBST()) return;
        System.out.print("Anio lectivo a eliminar (ej. 2009-2010 Fin): ");
        String anio = SC.nextLine().trim();
        System.out.print("AMIE a eliminar (ej. 18H00001): ");
        String amie = SC.nextLine().trim().toUpperCase();

        RegistroLimpio ref = new RegistroLimpio(anio, "", "", "", "", "", amie, "", "", "", 1, 1, 0, 0);
        boolean ok = bst.eliminar(ref);
        System.out.println(ok ? "Nodo eliminado. Nodos restantes: " + bst.contarNodos()
                              : "No se encontro esa clave en el BST.");
    }

    private static void mostrarRecorridosBST() {
        if (!verificarBST()) return;
        System.out.println("1. InOrden | 2. PreOrden | 3. PostOrden");
        int tipo = leerEntero("Recorrido: ");
        ListaEnlazada<RegistroLimpio> lista = switch (tipo) {
            case 1 -> bst.inOrden();
            case 2 -> bst.preOrden();
            case 3 -> bst.postOrden();
            default -> null;
        };
        if (lista == null) return;
        int i = 0;
        for (RegistroLimpio r : lista) {
            if (i >= 15) {
                System.out.println("  ... (" + (lista.tamanio() - 15) + " nodos mas)");
                break;
            }
            System.out.println("  " + r.getClaveUnica() + " - " + r.getNombreInstitucion());
            i++;
        }
    }

    private static void mostrarMetricasBST() {
        if (!verificarBST()) return;
        System.out.println("Nodos totales : " + bst.contarNodos());
        System.out.println("Altura actual : " + bst.altura());
        if (bst.getRaiz() != null) {
            System.out.println("Clave raiz    : " + bst.getRaiz().dato.getClaveUnica());
        }
    }

    // ==================== GRAFOS ====================

    private static void construirGrafo() {
        if (!verificarTransform()) return;
        int limite = leerEntero("Cantidad maxima de registros para construir el grafo (ej. 20): ");
        if (limite <= 0) limite = 20;

        grafo = new Grafo<>();
        int i = 0;
        for (RegistroLimpio r : resultadoETL.limpios) {
            if (i >= limite) break;
            if (i == 0) primerVerticeGrafo = r.getZona();
            grafo.agregarArista(r.getZona(), r.getProvincia());
            grafo.agregarArista(r.getProvincia(), r.getCanton());
            grafo.agregarArista(r.getCanton(), r.getNombreInstitucion());
            i++;
        }
        System.out.println("Grafo construido -> Vertices: " + grafo.contarVertices()
                + " | Aristas: " + grafo.contarAristas());
    }

    private static void mostrarConexionesGrafo() {
        if (!verificarGrafo()) return;
        grafo.mostrarConexiones();
    }

    private static void ejecutarBFS() {
        if (!verificarGrafo()) return;
        System.out.print("Vertice inicial (Enter para '" + primerVerticeGrafo + "'): ");
        String ini = SC.nextLine().trim();
        if (ini.isEmpty()) ini = primerVerticeGrafo;
        ListaEnlazada<String> orden = grafo.bfs(ini);
        System.out.println("Recorrido BFS (" + orden.tamanio() + " vertices):");
        for (String v : orden) System.out.println("  -> " + v);
    }

    private static void ejecutarDFS() {
        if (!verificarGrafo()) return;
        System.out.print("Vertice inicial (Enter para '" + primerVerticeGrafo + "'): ");
        String ini = SC.nextLine().trim();
        if (ini.isEmpty()) ini = primerVerticeGrafo;
        ListaEnlazada<String> orden = grafo.dfs(ini);
        System.out.println("Recorrido DFS (" + orden.tamanio() + " vertices):");
        for (String v : orden) System.out.println("  -> " + v);
    }

    private static void buscarRutaGrafo() {
        if (!verificarGrafo()) return;
        System.out.print("Vertice origen (ej. ZONA 3): ");
        String origen = SC.nextLine().trim();
        System.out.print("Vertice destino (ej. COLEGIO BOLIVAR): ");
        String destino = SC.nextLine().trim();
        ListaEnlazada<String> ruta = grafo.buscarRutaBFS(origen, destino);
        if (ruta.estaVacia()) {
            System.out.println("No existe ruta dirigida entre '" + origen + "' y '" + destino + "'.");
        } else {
            System.out.print("Ruta encontrada: ");
            for (int i = 0; i < ruta.tamanio(); i++) {
                if (i > 0) System.out.print(" -> ");
                System.out.print(ruta.obtener(i));
            }
            System.out.println();
        }
    }

    private static void detectarCiclosGrafo() {
        if (!verificarGrafo()) return;
        System.out.println("Tiene ciclos actualmente?: " + (grafo.tieneCiclos() ? "SI" : "NO"));
        System.out.print("Desea agregar una arista manual para probar ciclos? (s/n): ");
        if ("s".equalsIgnoreCase(SC.nextLine().trim())) {
            System.out.print("Origen: ");
            String o = SC.nextLine().trim();
            System.out.print("Destino: ");
            String d = SC.nextLine().trim();
            grafo.agregarArista(o, d);
            System.out.println("Arista agregada. Tiene ciclos ahora?: " + (grafo.tieneCiclos() ? "SI" : "NO"));
        }
    }

    // ==================== IA Y FLUJOGRAMAS ====================

    private static void generarFlujogramasIA() throws Exception {
        Path dir = Path.of("output", "diagramas");
        GeneradorFlujograma.generarTodos(resultadoETL, bst, grafo, primerVerticeGrafo, dir);
        System.out.println("7 flujogramas Mermaid generados en output/diagramas/:");
        System.out.println("  01_etl.md, 02_cola.md, 03_pila.md, 04_lista_enlazada.md, 05_bst.md, 06_grafo_bfs.md, 07_grafo_dfs.md");
    }

    private static void explicarBSTconIA() {
        if (!verificarBST()) return;
        System.out.println(AnalizadorIA.explicarBST(bst));
    }

    private static void explicarGrafoConIA() {
        if (!verificarGrafo()) return;
        System.out.println(AnalizadorIA.explicarGrafo(grafo));
    }

    private static void analizarErroresConIA() {
        if (!verificarTransform()) return;
        System.out.println(AnalizadorIA.analizarErroresETL(resultadoETL));
    }

    // ==================== UTILIDADES ====================

    private static boolean verificarTransform() {
        if (resultadoETL == null) {
            System.out.println("Debe ejecutar primero Extract (1) y Transform (3).");
            return false;
        }
        return true;
    }

    private static boolean verificarBST() {
        if (bst == null || bst.estaVacio()) {
            System.out.println("Debe construir primero el BST (opcion 9).");
            return false;
        }
        return true;
    }

    private static boolean verificarGrafo() {
        if (grafo == null || grafo.contarVertices() == 0) {
            System.out.println("Debe construir primero el Grafo (opcion 14).");
            return false;
        }
        return true;
    }

    private static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        String linea = SC.nextLine().trim();
        try {
            return Integer.parseInt(linea);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}