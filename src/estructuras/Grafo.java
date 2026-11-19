package estructuras;

/**
 * Grafo dirigido con lista de adyacencia propia.
 * Reutiliza ListaEnlazada, Cola (para BFS y rutas) y Pila (para DFS y reconstruccion de rutas).
 */
public class Grafo<T> {
    private final ListaEnlazada<Vertice<T>> vertices;

    private static class NodoRuta<T> {
        final Vertice<T> vertice;
        final NodoRuta<T> padre;

        NodoRuta(Vertice<T> vertice, NodoRuta<T> padre) {
            this.vertice = vertice;
            this.padre = padre;
        }
    }

    public Grafo() {
        this.vertices = new ListaEnlazada<>();
    }

    public Vertice<T> agregarVertice(T dato) {
        Vertice<T> existente = buscarVertice(dato);
        if (existente != null) {
            return existente;
        }
        Vertice<T> nuevo = new Vertice<>(dato);
        vertices.insertar(nuevo);
        return nuevo;
    }

    public Vertice<T> buscarVertice(T dato) {
        for (Vertice<T> v : vertices) {
            if (v.dato.equals(dato)) {
                return v;
            }
        }
        return null;
    }

    public void agregarArista(T origen, T destino) {
        Vertice<T> vOrigen = agregarVertice(origen);
        Vertice<T> vDestino = agregarVertice(destino);
        vOrigen.agregarAdyacente(vDestino);
    }

    public void reiniciarEstados() {
        for (Vertice<T> v : vertices) {
            v.visitado = false;
        }
    }

    /** Busqueda en Anchura (BFS) reutilizando la Cola<T> propia. */
    public ListaEnlazada<T> bfs(T inicioDato) {
        reiniciarEstados();
        ListaEnlazada<T> recorrido = new ListaEnlazada<>();
        Vertice<T> inicio = buscarVertice(inicioDato);
        if (inicio == null) {
            return recorrido;
        }

        Cola<Vertice<T>> cola = new Cola<>();
        inicio.visitado = true;
        cola.encolar(inicio);

        while (!cola.estaVacia()) {
            Vertice<T> actual = cola.desencolar();
            recorrido.insertar(actual.dato);

            for (Vertice<T> ady : actual.adyacentes) {
                if (!ady.visitado) {
                    ady.visitado = true;
                    cola.encolar(ady);
                }
            }
        }
        return recorrido;
    }

    /** Busqueda en Profundidad (DFS) iterativa reutilizando la Pila<T> propia. */
    public ListaEnlazada<T> dfs(T inicioDato) {
        reiniciarEstados();
        ListaEnlazada<T> recorrido = new ListaEnlazada<>();
        Vertice<T> inicio = buscarVertice(inicioDato);
        if (inicio == null) {
            return recorrido;
        }

        Pila<Vertice<T>> pila = new Pila<>();
        pila.apilar(inicio);

        while (!pila.estaVacia()) {
            Vertice<T> actual = pila.desapilar();
            if (!actual.visitado) {
                actual.visitado = true;
                recorrido.insertar(actual.dato);

                for (Vertice<T> ady : actual.adyacentes) {
                    if (!ady.visitado) {
                        pila.apilar(ady);
                    }
                }
            }
        }
        return recorrido;
    }

    /** Encuentra la ruta mas corta (en numero de saltos) entre origen y destino usando BFS. */
    public ListaEnlazada<T> buscarRutaBFS(T origenDato, T destinoDato) {
        reiniciarEstados();
        ListaEnlazada<T> ruta = new ListaEnlazada<>();
        Vertice<T> origen = buscarVertice(origenDato);
        Vertice<T> destino = buscarVertice(destinoDato);
        if (origen == null || destino == null) {
            return ruta;
        }

        Cola<NodoRuta<T>> cola = new Cola<>();
        origen.visitado = true;
        cola.encolar(new NodoRuta<>(origen, null));

        NodoRuta<T> meta = null;
        while (!cola.estaVacia()) {
            NodoRuta<T> actual = cola.desencolar();
            if (actual.vertice.dato.equals(destino.dato)) {
                meta = actual;
                break;
            }
            for (Vertice<T> ady : actual.vertice.adyacentes) {
                if (!ady.visitado) {
                    ady.visitado = true;
                    cola.encolar(new NodoRuta<>(ady, actual));
                }
            }
        }

        if (meta != null) {
            Pila<T> inverso = new Pila<>();
            NodoRuta<T> cursor = meta;
            while (cursor != null) {
                inverso.apilar(cursor.vertice.dato);
                cursor = cursor.padre;
            }
            while (!inverso.estaVacia()) {
                ruta.insertar(inverso.desapilar());
            }
        }
        return ruta;
    }

    public boolean existeRuta(T origenDato, T destinoDato) {
        return !buscarRutaBFS(origenDato, destinoDato).estaVacia();
    }

    /** Deteccion de ciclos en grafo dirigido mediante DFS y pila de recursion. */
    public boolean tieneCiclos() {
        ListaEnlazada<Vertice<T>> visitadosGlobales = new ListaEnlazada<>();
        for (Vertice<T> v : vertices) {
            if (!contiene(visitadosGlobales, v)) {
                if (tieneCiclosDFS(v, new ListaEnlazada<>(), visitadosGlobales)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tieneCiclosDFS(Vertice<T> actual,
                                   ListaEnlazada<Vertice<T>> recursionStack,
                                   ListaEnlazada<Vertice<T>> visitadosGlobales) {
        recursionStack.insertar(actual);
        visitadosGlobales.insertar(actual);

        for (Vertice<T> ady : actual.adyacentes) {
            if (!contiene(visitadosGlobales, ady)) {
                if (tieneCiclosDFS(ady, recursionStack, visitadosGlobales)) {
                    return true;
                }
            } else if (contiene(recursionStack, ady)) {
                return true;
            }
        }
        recursionStack.eliminar(v -> v.dato.equals(actual.dato));
        return false;
    }

    private boolean contiene(ListaEnlazada<Vertice<T>> lista, Vertice<T> vertice) {
        for (Vertice<T> v : lista) {
            if (v.dato.equals(vertice.dato)) {
                return true;
            }
        }
        return false;
    }

    public void mostrarConexiones() {
        if (vertices.estaVacia()) {
            System.out.println("El grafo esta vacio.");
            return;
        }
        for (Vertice<T> v : vertices) {
            StringBuilder sb = new StringBuilder("  ").append(v.dato).append(" -> [");
            boolean primero = true;
            for (Vertice<T> ady : v.adyacentes) {
                if (!primero) sb.append(", ");
                sb.append(ady.dato);
                primero = false;
            }
            sb.append("]");
            System.out.println(sb);
        }
    }

    public int contarVertices() { return vertices.tamanio(); }

    public int contarAristas() {
        int total = 0;
        for (Vertice<T> v : vertices) {
            total += v.adyacentes.tamanio();
        }
        return total;
    }

    public ListaEnlazada<Vertice<T>> getVertices() { return vertices; }
}