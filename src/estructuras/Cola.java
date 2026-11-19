package estructuras;

import java.util.NoSuchElementException;
import java.util.Objects;

/** Cola FIFO implementada con nodos propios. Operaciones en O(1). */
public class Cola<T> {
    private Nodo<T> frente;
    private Nodo<T> fin;
    private int cantidad;

    public void encolar(T dato) {
        Objects.requireNonNull(dato, "La cola no admite null");
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            frente = nuevo;
        } else {
            fin.siguiente = nuevo;
        }
        fin = nuevo;
        cantidad++;
    }

    public T desencolar() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola esta vacia");
        }
        T dato = frente.dato;
        frente = frente.siguiente;
        if (frente == null) {
            fin = null;
        }
        cantidad--;
        return dato;
    }

    public T frente() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola esta vacia");
        }
        return frente.dato;
    }

    public boolean estaVacia() { return cantidad == 0; }
    public int tamanio() { return cantidad; }

    public void mostrar() {
        mostrar(20);
    }

    public void mostrar(int limite) {
        if (estaVacia()) {
            System.out.println("La cola esta vacia.");
            return;
        }
        Nodo<T> actual = frente;
        int i = 0;
        while (actual != null && i < limite) {
            System.out.println("  [" + (i + 1) + "] " + actual.dato);
            actual = actual.siguiente;
            i++;
        }
        if (cantidad > limite) {
            System.out.println("  ... (" + (cantidad - limite) + " elementos adicionales en cola)");
        }
    }
}