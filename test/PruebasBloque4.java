import estructuras.Grafo;
import estructuras.ListaEnlazada;

public class PruebasBloque4 {
    private static int pasadas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {
        System.out.println("=== PRUEBAS BLOQUE 4 (85%: GRAFOS, BFS, DFS, RUTAS Y CICLOS) ===\n");

        Grafo<String> grafo = new Grafo<>();

        // Jerarquia territorial MINEDUC / Derivacion institucional
        grafo.agregarArista("ZONA 3", "TUNGURAHUA");
        grafo.agregarArista("TUNGURAHUA", "AMBATO");
        grafo.agregarArista("AMBATO", "UNIDAD EDUCATIVA AMBATO");
        grafo.agregarArista("AMBATO", "COLEGIO BOLIVAR");

        afirmar("Grafo cuenta con 5 vertices", grafo.contarVertices() == 5);
        afirmar("Grafo cuenta con 4 aristas", grafo.contarAristas() == 4);
        afirmar("Vertice ZONA 3 existe", grafo.buscarVertice("ZONA 3") != null);

        // BFS desde ZONA 3
        ListaEnlazada<String> bfsOrden = grafo.bfs("ZONA 3");
        afirmar("BFS recorre los 5 vertices", bfsOrden.tamanio() == 5);
        afirmar("BFS visita ZONA 3 primero", "ZONA 3".equals(bfsOrden.obtener(0)));

        // DFS desde ZONA 3
        ListaEnlazada<String> dfsOrden = grafo.dfs("ZONA 3");
        afirmar("DFS recorre los 5 vertices", dfsOrden.tamanio() == 5);

        // Busqueda de rutas
        ListaEnlazada<String> ruta = grafo.buscarRutaBFS("ZONA 3", "COLEGIO BOLIVAR");
        afirmar("Existe ruta ZONA 3 -> COLEGIO BOLIVAR (4 nodos)",
                ruta.tamanio() == 4
                && "ZONA 3".equals(ruta.obtener(0))
                && "COLEGIO BOLIVAR".equals(ruta.obtener(3)));
        afirmar("No existe ruta inversa COLEGIO BOLIVAR -> ZONA 3 (antes del ciclo)",
                !grafo.existeRuta("COLEGIO BOLIVAR", "ZONA 3"));

        // Deteccion de ciclos (sin ciclo)
        afirmar("Grafo jerarquico no tiene ciclos", !grafo.tieneCiclos());

        // Agregar arista que forma ciclo: COLEGIO BOLIVAR -> ZONA 3
        grafo.agregarArista("COLEGIO BOLIVAR", "ZONA 3");
        afirmar("Grafo detecta correctamente dependencia circular (ciclo)", grafo.tieneCiclos());

        System.out.println("\n--- Conexiones del grafo ---");
        grafo.mostrarConexiones();

        System.out.println("\nPasadas: " + pasadas + " | Fallidas: " + fallidas);
        if (fallidas > 0) System.exit(1);
    }

    private static void afirmar(String desc, boolean cond) {
        if (cond) { System.out.println("[OK]    " + desc); pasadas++; }
        else      { System.out.println("[FALLO] " + desc); fallidas++; }
    }
}