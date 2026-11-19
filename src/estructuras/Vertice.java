package estructuras;

import java.util.Objects;

/** Vertice de un grafo dirigido con lista de adyacencia propia. */
public class Vertice<T> {
    public final T dato;
    public final ListaEnlazada<Vertice<T>> adyacentes;
    public boolean visitado;

    public Vertice(T dato) {
        this.dato = Objects.requireNonNull(dato, "El dato del vertice no puede ser null");
        this.adyacentes = new ListaEnlazada<>();
        this.visitado = false;
    }

    public void agregarAdyacente(Vertice<T> destino) {
        Objects.requireNonNull(destino, "El vertice destino no puede ser null");
        for (Vertice<T> v : adyacentes) {
            if (v.dato.equals(destino.dato)) {
                return; // evita aristas duplicadas
            }
        }
        adyacentes.insertar(destino);
    }

    @Override
    public String toString() {
        return String.valueOf(dato);
    }
}