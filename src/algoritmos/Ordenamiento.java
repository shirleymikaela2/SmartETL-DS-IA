package algoritmos;

import estructuras.ListaEnlazada;
import estructuras.ListaSecuencial;
import model.RegistroLimpio;

import java.util.Comparator;

/** Algoritmos de ordenamiento propios: Insertion Sort O(n^2) y Merge Sort O(n log n). */
public class Ordenamiento {

    public static final Comparator<RegistroLimpio> POR_CLAVE =
            Comparator.comparing(RegistroLimpio::getClaveUnica, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<RegistroLimpio> POR_ESTUDIANTES_DESC =
            Comparator.comparingInt(RegistroLimpio::getTotalEstudiantes).reversed();

    public static final Comparator<RegistroLimpio> POR_TASA_ABANDONO_DESC =
            Comparator.comparingDouble(RegistroLimpio::getTasaAbandono).reversed();

    public static final Comparator<RegistroLimpio> POR_INSTITUCION =
            Comparator.comparing(RegistroLimpio::getNombreInstitucion, String.CASE_INSENSITIVE_ORDER);

    public static class MetricasOrden {
        public final String algoritmo;
        public final int elementos;
        public final long comparaciones;
        public final double tiempoMs;

        public MetricasOrden(String algoritmo, int elementos, long comparaciones, double tiempoMs) {
            this.algoritmo = algoritmo;
            this.elementos = elementos;
            this.comparaciones = comparaciones;
            this.tiempoMs = tiempoMs;
        }

        @Override
        public String toString() {
            return String.format("%s | n=%d | comparaciones=%d | tiempo=%.3f ms",
                    algoritmo, elementos, comparaciones, tiempoMs);
        }
    }

    /** Convierte una ListaEnlazada a ListaSecuencial (con limite opcional <= 0 para copiar todo). */
    public static ListaSecuencial<RegistroLimpio> aListaSecuencial(
            ListaEnlazada<RegistroLimpio> origen, int limite) {
        int cap = (limite > 0) ? Math.min(limite, origen.tamanio()) : Math.max(16, origen.tamanio());
        ListaSecuencial<RegistroLimpio> copia = new ListaSecuencial<>(cap);
        int i = 0;
        for (RegistroLimpio r : origen) {
            if (limite > 0 && i >= limite) break;
            copia.insertar(r);
            i++;
        }
        return copia;
    }

    /** Algoritmo 1: Insertion Sort O(n^2). */
    public static MetricasOrden insertionSort(
            ListaSecuencial<RegistroLimpio> lista, Comparator<RegistroLimpio> comp) {
        long inicio = System.nanoTime();
        long comparaciones = 0;
        int n = lista.tamanio();

        for (int i = 1; i < n; i++) {
            RegistroLimpio clave = lista.obtener(i);
            int j = i - 1;
            while (j >= 0) {
                comparaciones++;
                if (comp.compare(lista.obtener(j), clave) > 0) {
                    lista.establecer(j + 1, lista.obtener(j));
                    j--;
                } else {
                    break;
                }
            }
            lista.establecer(j + 1, clave);
        }

        double ms = (System.nanoTime() - inicio) / 1e6;
        return new MetricasOrden("Insertion Sort", n, comparaciones, ms);
    }

    /** Algoritmo 2: Merge Sort O(n log n). */
    public static MetricasOrden mergeSort(
            ListaSecuencial<RegistroLimpio> lista, Comparator<RegistroLimpio> comp) {
        long inicio = System.nanoTime();
        long[] contador = {0};
        int n = lista.tamanio();

        if (n > 1) {
            RegistroLimpio[] aux = new RegistroLimpio[n];
            mergeSortRec(lista, aux, 0, n - 1, comp, contador);
        }

        double ms = (System.nanoTime() - inicio) / 1e6;
        return new MetricasOrden("Merge Sort", n, contador[0], ms);
    }

    private static void mergeSortRec(ListaSecuencial<RegistroLimpio> lista, RegistroLimpio[] aux,
                                     int izq, int der, Comparator<RegistroLimpio> comp, long[] contador) {
        if (izq >= der) return;
        int mid = izq + (der - izq) / 2;
        mergeSortRec(lista, aux, izq, mid, comp, contador);
        mergeSortRec(lista, aux, mid + 1, der, comp, contador);
        mezclar(lista, aux, izq, mid, der, comp, contador);
    }

    private static void mezclar(ListaSecuencial<RegistroLimpio> lista, RegistroLimpio[] aux,
                                int izq, int mid, int der, Comparator<RegistroLimpio> comp, long[] contador) {
        for (int k = izq; k <= der; k++) {
            aux[k] = lista.obtener(k);
        }
        int i = izq;
        int j = mid + 1;
        for (int k = izq; k <= der; k++) {
            if (i > mid) {
                lista.establecer(k, aux[j++]);
            } else if (j > der) {
                lista.establecer(k, aux[i++]);
            } else {
                contador[0]++;
                if (comp.compare(aux[i], aux[j]) <= 0) {
                    lista.establecer(k, aux[i++]);
                } else {
                    lista.establecer(k, aux[j++]);
                }
            }
        }
    }
}