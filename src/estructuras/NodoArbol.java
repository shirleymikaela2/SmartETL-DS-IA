package estructuras;

/** Nodo para Arbol Binario de Busqueda (BST). */
public class NodoArbol<T> {
    public T dato;
    public NodoArbol<T> izquierdo;
    public NodoArbol<T> derecho;

    public NodoArbol(T dato) {
        this.dato = dato;
    }
}