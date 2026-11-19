package estructuras;

import java.util.Objects;

/** Lista secuencial basada en arreglo dinamico propio para comparacion y ordenamiento. */
public class ListaSecuencial<T> {
    private Object[] elementos;
    private int cantidad;

    public ListaSecuencial() {
        this(16);
    }

    public ListaSecuencial(int capacidadInicial) {
        if (capacidadInicial <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser positiva");
        }
        this.elementos = new Object[capacidadInicial];
        this.cantidad = 0;
    }

    public void insertar(T dato) {
        Objects.requireNonNull(dato, "La lista secuencial no admite null");
        asegurarCapacidad(cantidad + 1);
        elementos[cantidad++] = dato;
    }

    @SuppressWarnings("unchecked")
    public T obtener(int indice) {
        validarIndice(indice);
        return (T) elementos[indice];
    }

    public void establecer(int indice, T dato) {
        validarIndice(indice);
        Objects.requireNonNull(dato, "La lista secuencial no admite null");
        elementos[indice] = dato;
    }

    @SuppressWarnings("unchecked")
    public T eliminar(int indice) {
        validarIndice(indice);
        T eliminado = (T) elementos[indice];
        for (int i = indice; i < cantidad - 1; i++) {
            elementos[i] = elementos[i + 1];
        }
        elementos[--cantidad] = null;
        return eliminado;
    }

    public int tamanio() { return cantidad; }
    public boolean estaVacia() { return cantidad == 0; }

    public void mostrar(int limite) {
        if (estaVacia()) {
            System.out.println("La lista secuencial esta vacia.");
            return;
        }
        int tope = Math.min(cantidad, limite);
        for (int i = 0; i < tope; i++) {
            System.out.println("  [" + i + "] " + elementos[i]);
        }
    }

    private void asegurarCapacidad(int minima) {
        if (minima <= elementos.length) return;
        int nuevaCapacidad = Math.max(elementos.length * 2, minima);
        Object[] nuevo = new Object[nuevaCapacidad];
        System.arraycopy(elementos, 0, nuevo, 0, cantidad);
        elementos = nuevo;
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= cantidad) {
            throw new IndexOutOfBoundsException("Indice fuera de rango: " + indice);
        }
    }
}