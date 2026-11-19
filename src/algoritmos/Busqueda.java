package algoritmos;

import estructuras.ListaEnlazada;
import estructuras.ListaSecuencial;
import model.RegistroLimpio;

/** Algoritmos de busqueda: secuencial en ListaEnlazada y binaria en ListaSecuencial ordenada. */
public class Busqueda {

    public static class ResultadoBusqueda {
        public final RegistroLimpio encontrado;
        public final int comparaciones;
        public final double tiempoMs;

        public ResultadoBusqueda(RegistroLimpio encontrado, int comparaciones, double tiempoMs) {
            this.encontrado = encontrado;
            this.comparaciones = comparaciones;
            this.tiempoMs = tiempoMs;
        }
    }

    /** Busqueda secuencial O(n) por clave unica (Anio_lectivo|AMIE) en ListaEnlazada. */
    public static ResultadoBusqueda secuencialPorClave(ListaEnlazada<RegistroLimpio> lista, String clave) {
        long inicio = System.nanoTime();
        int comparaciones = 0;

        for (RegistroLimpio r : lista) {
            comparaciones++;
            if (r.getClaveUnica().equalsIgnoreCase(clave)) {
                double ms = (System.nanoTime() - inicio) / 1e6;
                return new ResultadoBusqueda(r, comparaciones, ms);
            }
        }
        double ms = (System.nanoTime() - inicio) / 1e6;
        return new ResultadoBusqueda(null, comparaciones, ms);
    }

    /** Busqueda secuencial O(n) de todas las coincidencias por AMIE. */
    public static ListaEnlazada<RegistroLimpio> buscarTodosPorAmie(
            ListaEnlazada<RegistroLimpio> lista, String amie) {
        ListaEnlazada<RegistroLimpio> coincidencias = new ListaEnlazada<>();
        for (RegistroLimpio r : lista) {
            if (r.getAmie().equalsIgnoreCase(amie)) {
                coincidencias.insertar(r);
            }
        }
        return coincidencias;
    }

    /** Busqueda binaria O(log n) por clave unica sobre una ListaSecuencial previamente ordenada por clave. */
    public static ResultadoBusqueda binariaPorClave(ListaSecuencial<RegistroLimpio> ordenada, String clave) {
        long inicio = System.nanoTime();
        int comparaciones = 0;
        int izq = 0;
        int der = ordenada.tamanio() - 1;

        while (izq <= der) {
            int mid = izq + (der - izq) / 2;
            RegistroLimpio actual = ordenada.obtener(mid);
            comparaciones++;
            int cmp = actual.getClaveUnica().compareToIgnoreCase(clave);

            if (cmp == 0) {
                double ms = (System.nanoTime() - inicio) / 1e6;
                return new ResultadoBusqueda(actual, comparaciones, ms);
            } else if (cmp < 0) {
                izq = mid + 1;
            } else {
                der = mid - 1;
            }
        }
        double ms = (System.nanoTime() - inicio) / 1e6;
        return new ResultadoBusqueda(null, comparaciones, ms);
    }
}