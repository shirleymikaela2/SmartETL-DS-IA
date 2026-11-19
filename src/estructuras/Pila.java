package estructuras;

import java.util.NoSuchElementException;
import java.util.Objects;

/** Pila LIFO implementada con nodos propios. Operaciones en O(1). */
public class Pila<T> {
    private Nodo<T> cima;
    private int cantidad;

    public void apilar(T dato) {
        Objects.requireNonNull(dato, "La pila no admite null");
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.siguiente = cima;
        cima = nuevo;
        cantidad++;
    }

    public T desapilar() {
        if (estaVacia()) {
            throw new NoSuchElementException("La pila esta vacia");
        }
        T dato = cima.dato;
        cima = cima.siguiente;
        cantidad--;
        return dato;
    }

    public T cima() {
        if (estaVacia()) {
            throw new NoSuchElementException("La pila esta vacia");
        }
        return cima.dato;
    }

    public boolean estaVacia() { return cantidad == 0; }
    public int tamanio() { return cantidad; }

    public void mostrar() {
        mostrar(20);
    }

    public void mostrar(int limite) {
        if (estaVacia()) {
            System.out.println("La pila esta vacia.");
            return;
        }
        Nodo<T> actual = cima;
        int i = 0;
        while (actual != null && i < limite) {
            System.out.println("  [cima-" + i + "] " + actual.dato);
            actual = actual.siguiente;
            i++;
        }
        if (cantidad > limite) {
            System.out.println("  ... (" + (cantidad - limite) + " elementos adicionales en pila)");
        }
    }
}