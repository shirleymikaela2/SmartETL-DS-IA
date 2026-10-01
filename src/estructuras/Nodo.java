package estructuras;

/** Nodo interno: contiene un dato y la referencia al siguiente nodo. */
class Nodo<T> {
    T dato;
    Nodo<T> siguiente;
    Nodo(T dato) { this.dato = dato; }
}
