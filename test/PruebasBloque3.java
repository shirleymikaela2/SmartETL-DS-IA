import algoritmos.Ordenamiento;
import estructuras.ArbolBST;
import estructuras.Cola;
import estructuras.ListaEnlazada;
import etl.Extract;
import etl.Transform;
import model.RegistroEducativo;
import model.RegistroLimpio;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;

public class PruebasBloque3 {
    private static int pasadas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("=== PRUEBAS BLOQUE 3 (70%: ARBOL BINARIO DE BUSQUEDA - BST) ===\n");

        // 1. Pruebas estructurales con enteros
        ArbolBST<Integer> bstInt = new ArbolBST<Integer>(Comparator.naturalOrder());
        int[] valores = {50, 30, 70, 20, 40, 60, 80};
        for (int v : valores) bstInt.insertar(v);

        afirmar("BST cuenta 7 nodos", bstInt.contarNodos() == 7);
        afirmar("BST rechaza duplicado (50)", !bstInt.insertar(50) && bstInt.contarNodos() == 7);
        afirmar("Altura de arbol balanceado de 7 nodos es 3", bstInt.altura() == 3);
        afirmar("Buscar 40 encuentra 40", Integer.valueOf(40).equals(bstInt.buscar(40)));
        afirmar("Buscar 99 devuelve null", bstInt.buscar(99) == null);

        afirmar("InOrden es [20, 30, 40, 50, 60, 70, 80]",
                "[20, 30, 40, 50, 60, 70, 80]".equals(aTexto(bstInt.inOrden())));
        afirmar("PreOrden es [50, 30, 20, 40, 70, 60, 80]",
                "[50, 30, 20, 40, 70, 60, 80]".equals(aTexto(bstInt.preOrden())));
        afirmar("PostOrden es [20, 40, 30, 60, 80, 70, 50]",
                "[20, 40, 30, 60, 80, 70, 50]".equals(aTexto(bstInt.postOrden())));

        // Eliminacion: hoja (20), dos hijos (50 - raiz)
        afirmar("Eliminar hoja (20) funciona", bstInt.eliminar(20) && bstInt.contarNodos() == 6);
        afirmar("Eliminar raiz con 2 hijos (50) funciona", bstInt.eliminar(50) && bstInt.contarNodos() == 5);
        afirmar("InOrden tras eliminar 20 y 50 es [30, 40, 60, 70, 80]",
                "[30, 40, 60, 70, 80]".equals(aTexto(bstInt.inOrden())));

        // 2. Indexacion de registros MINEDUC en BST por ClaveUnica
        Extract extract = new Extract();
        Cola<RegistroEducativo> cola = extract.extraerACola(
                Path.of("data/muestra_con_errores.csv"), StandardCharsets.UTF_8, ';');
        Transform.ResultadoTransform res = new Transform().ejecutarDesdeCola(cola);

        ArbolBST<RegistroLimpio> bstMineduc = new ArbolBST<>(Ordenamiento.POR_CLAVE);
        for (RegistroLimpio r : res.limpios) {
            bstMineduc.insertar(r);
        }

        afirmar("BST MINEDUC indexa los 8 registros limpios", bstMineduc.contarNodos() == 8);

        RegistroLimpio claveRef = new RegistroLimpio(
                "2009-2010 Fin", "", "", "", "", "", "18H00001", "", "", "", 1, 1, 0, 0);
        ArbolBST.ResultadoBusquedaBST<RegistroLimpio> b = bstMineduc.buscarConMetricas(claveRef);
        afirmar("BST encuentra 18H00001 (UNIDAD EDUCATIVA AMBATO)",
                b.encontrado != null && "18H00001".equals(b.encontrado.getAmie()));
        System.out.printf("  Busqueda BST 18H00001 -> comparaciones=%d | tiempo=%.4f ms | altura=%d%n",
                b.comparaciones, b.tiempoMs, bstMineduc.altura());

        System.out.println("\nPasadas: " + pasadas + " | Fallidas: " + fallidas);
        if (fallidas > 0) System.exit(1);
    }

    private static <T> String aTexto(ListaEnlazada<T> lista) {
        StringBuilder sb = new StringBuilder("[");
        boolean primero = true;
        for (T elem : lista) {
            if (!primero) sb.append(", ");
            sb.append(elem);
            primero = false;
        }
        return sb.append("]").toString();
    }

    private static void afirmar(String desc, boolean cond) {
        if (cond) { System.out.println("[OK]    " + desc); pasadas++; }
        else      { System.out.println("[FALLO] " + desc); fallidas++; }
    }
}