package estructuras;

import java.util.Comparator;
import java.util.Objects;

/**
 * Arbol Binario de Busqueda (BST) generico e iterativo.
 * Reutiliza Pila, Cola y ListaEnlazada propias para evitar desbordamiento de pila
 * incluso cuando las claves ingresan ordenadas.
 */
public class ArbolBST<T> {
    private NodoArbol<T> raiz;
    private int cantidad;
    private final Comparator<T> comparador;

    public static class ResultadoBusquedaBST<T> {
        public final T encontrado;
        public final int comparaciones;
        public final double tiempoMs;

        public ResultadoBusquedaBST(T encontrado, int comparaciones, double tiempoMs) {
            this.encontrado = encontrado;
            this.comparaciones = comparaciones;
            this.tiempoMs = tiempoMs;
        }
    }

    public ArbolBST(Comparator<T> comparador) {
        this.comparador = Objects.requireNonNull(comparador, "El comparador es obligatorio");
    }

    /** Inserta un elemento de forma iterativa. Devuelve false si la clave ya existe. */
    public boolean insertar(T dato) {
        Objects.requireNonNull(dato, "El BST no admite null");
        if (raiz == null) {
            raiz = new NodoArbol<>(dato);
            cantidad++;
            return true;
        }

        NodoArbol<T> actual = raiz;
        while (true) {
            int cmp = comparador.compare(dato, actual.dato);
            if (cmp == 0) {
                return false; // clave duplicada
            } else if (cmp < 0) {
                if (actual.izquierdo == null) {
                    actual.izquierdo = new NodoArbol<>(dato);
                    cantidad++;
                    return true;
                }
                actual = actual.izquierdo;
            } else {
                if (actual.derecho == null) {
                    actual.derecho = new NodoArbol<>(dato);
                    cantidad++;
                    return true;
                }
                actual = actual.derecho;
            }
        }
    }

    /** Busca un elemento por clave de forma iterativa. */
    public T buscar(T referencia) {
        return buscarConMetricas(referencia).encontrado;
    }

    /** Busca midiendo comparaciones y tiempo para contrastar contra busqueda secuencial. */
    public ResultadoBusquedaBST<T> buscarConMetricas(T referencia) {
        Objects.requireNonNull(referencia, "La referencia de busqueda es obligatoria");
        long inicio = System.nanoTime();
        int comparaciones = 0;
        NodoArbol<T> actual = raiz;

        while (actual != null) {
            comparaciones++;
            int cmp = comparador.compare(referencia, actual.dato);
            if (cmp == 0) {
                double ms = (System.nanoTime() - inicio) / 1e6;
                return new ResultadoBusquedaBST<>(actual.dato, comparaciones, ms);
            } else if (cmp < 0) {
                actual = actual.izquierdo;
            } else {
                actual = actual.derecho;
            }
        }

        double ms = (System.nanoTime() - inicio) / 1e6;
        return new ResultadoBusquedaBST<>(null, comparaciones, ms);
    }

    /** Elimina un nodo por clave (casos: hoja, 1 hijo o 2 hijos usando sucesor InOrden). */
    public boolean eliminar(T referencia) {
        Objects.requireNonNull(referencia, "La referencia a eliminar es obligatoria");
        NodoArbol<T> padre = null;
        NodoArbol<T> actual = raiz;

        while (actual != null) {
            int cmp = comparador.compare(referencia, actual.dato);
            if (cmp == 0) {
                break;
            }
            padre = actual;
            actual = (cmp < 0) ? actual.izquierdo : actual.derecho;
        }

        if (actual == null) {
            return false; // no encontrado
        }

        // Caso 3: dos hijos -> reemplazar dato por el minimo del subarbol derecho
        if (actual.izquierdo != null && actual.derecho != null) {
            NodoArbol<T> padreSucesor = actual;
            NodoArbol<T> sucesor = actual.derecho;
            while (sucesor.izquierdo != null) {
                padreSucesor = sucesor;
                sucesor = sucesor.izquierdo;
            }
            actual.dato = sucesor.dato;
            padre = padreSucesor;
            actual = sucesor;
        }

        // Casos 1 y 2: cero o un hijo
        NodoArbol<T> hijo = (actual.izquierdo != null) ? actual.izquierdo : actual.derecho;
        if (padre == null) {
            raiz = hijo;
        } else if (padre.izquierdo == actual) {
            padre.izquierdo = hijo;
        } else {
            padre.derecho = hijo;
        }

        cantidad--;
        return true;
    }

    /** Recorrido InOrden (Izq - Raiz - Der) iterativo con Pila propia. */
    public ListaEnlazada<T> inOrden() {
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        Pila<NodoArbol<T>> pila = new Pila<>();
        NodoArbol<T> actual = raiz;

        while (actual != null || !pila.estaVacia()) {
            while (actual != null) {
                pila.apilar(actual);
                actual = actual.izquierdo;
            }
            actual = pila.desapilar();
            resultado.insertar(actual.dato);
            actual = actual.derecho;
        }
        return resultado;
    }

    /** Recorrido PreOrden (Raiz - Izq - Der) iterativo con Pila propia. */
    public ListaEnlazada<T> preOrden() {
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        if (raiz == null) return resultado;

        Pila<NodoArbol<T>> pila = new Pila<>();
        pila.apilar(raiz);

        while (!pila.estaVacia()) {
            NodoArbol<T> actual = pila.desapilar();
            resultado.insertar(actual.dato);
            if (actual.derecho != null) pila.apilar(actual.derecho);
            if (actual.izquierdo != null) pila.apilar(actual.izquierdo);
        }
        return resultado;
    }

    /** Recorrido PostOrden (Izq - Der - Raiz) iterativo con dos Pilas propias. */
    public ListaEnlazada<T> postOrden() {
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        if (raiz == null) return resultado;

        Pila<NodoArbol<T>> pila1 = new Pila<>();
        Pila<NodoArbol<T>> pila2 = new Pila<>();
        pila1.apilar(raiz);

        while (!pila1.estaVacia()) {
            NodoArbol<T> actual = pila1.desapilar();
            pila2.apilar(actual);
            if (actual.izquierdo != null) pila1.apilar(actual.izquierdo);
            if (actual.derecho != null) pila1.apilar(actual.derecho);
        }
        while (!pila2.estaVacia()) {
            resultado.insertar(pila2.desapilar().dato);
        }
        return resultado;
    }

    /** Altura del arbol (0 si esta vacio, 1 si solo tiene raiz), calculada por niveles con Cola propia. */
    public int altura() {
        if (raiz == null) return 0;
        Cola<NodoArbol<T>> cola = new Cola<>();
        cola.encolar(raiz);
        int altura = 0;

        while (!cola.estaVacia()) {
            int nodosNivel = cola.tamanio();
            altura++;
            for (int i = 0; i < nodosNivel; i++) {
                NodoArbol<T> actual = cola.desencolar();
                if (actual.izquierdo != null) cola.encolar(actual.izquierdo);
                if (actual.derecho != null) cola.encolar(actual.derecho);
            }
        }
        return altura;
    }

    public int contarNodos() { return cantidad; }
    public boolean estaVacio() { return cantidad == 0; }
    public NodoArbol<T> getRaiz() { return raiz; }
}