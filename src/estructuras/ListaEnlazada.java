package estructuras;

import java.util.Objects;
import java.util.function.Predicate;

/** Lista propia. Las referencias inicio y fin permiten insertar al final en O(1). */
public class ListaEnlazada<T> implements Iterable<T> {
    private Nodo<T> inicio;
    private Nodo<T> fin;
    private int cantidad;

    public void insertar(T dato) {
        Objects.requireNonNull(dato, "La lista no admite null");
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            inicio = nuevo;
        } else {
            fin.siguiente = nuevo;
        }
        fin = nuevo;
        cantidad++;
    }
    public T obtener(int indice) {
        if (indice < 0 || indice >= cantidad) {
            throw new IndexOutOfBoundsException("Indice fuera de la lista: " + indice);
        }
        Nodo<T> actual = inicio;
        for (int i = 0; i < indice; i++) { actual = actual.siguiente; }
        return actual.dato;
    }
    /** Recorre secuencialmente hasta encontrar la primera coincidencia. */
    public T buscar(Predicate<T> criterio) {
        Objects.requireNonNull(criterio, "El criterio es obligatorio");
        Nodo<T> actual = inicio;
        while (actual != null) {
            if (criterio.test(actual.dato)) { return actual.dato; }
            actual = actual.siguiente;
        }
        return null;
    }
    @Override public java.util.Iterator<T> iterator() {
        return new java.util.Iterator<T>() {
            private Nodo<T> actual = inicio;
            public boolean hasNext() { return actual != null; }
            public T next() {
                if (actual == null) throw new java.util.NoSuchElementException();
                T dato=actual.dato; actual=actual.siguiente; return dato;
            }
        };
    }
    public int tamanio() { return cantidad; }
    public boolean estaVacia() { return cantidad == 0; }
    public void mostrar() {
        if (estaVacia()) { System.out.println("La lista esta vacia."); return; }
        Nodo<T> actual = inicio;
        while (actual != null) {
            System.out.println(actual.dato);
            actual = actual.siguiente;
        }
    }
}
