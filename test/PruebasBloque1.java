import estructuras.Cola;
import estructuras.ListaEnlazada;
import estructuras.ListaSecuencial;
import estructuras.Pila;
import etl.Extract;
import etl.Transform;
import model.RegistroEducativo;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class PruebasBloque1 {
    private static int pasadas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("=== PRUEBAS BLOQUE 1 (40%: COLA, PILA, LISTAS Y ETL ERRORES) ===\n");

        // 1. Cola FIFO
        Cola<String> cola = new Cola<>();
        cola.encolar("R1");
        cola.encolar("R2");
        afirmar("Cola FIFO frente es R1", "R1".equals(cola.frente()));
        afirmar("Cola FIFO desencola R1 primero", "R1".equals(cola.desencolar()));
        afirmar("Cola FIFO desencola R2 segundo", "R2".equals(cola.desencolar()));
        afirmar("Cola queda vacia", cola.estaVacia());

        // 2. Pila LIFO
        Pila<String> pila = new Pila<>();
        pila.apilar("E1");
        pila.apilar("E2");
        afirmar("Pila LIFO cima es E2", "E2".equals(pila.cima()));
        afirmar("Pila LIFO desapila E2 primero", "E2".equals(pila.desapilar()));
        afirmar("Pila LIFO desapila E1 segundo", "E1".equals(pila.desapilar()));
        afirmar("Pila queda vacia", pila.estaVacia());

        // 3. ListaSecuencial y ListaEnlazada.eliminar()
        ListaSecuencial<Integer> seq = new ListaSecuencial<>(2);
        seq.insertar(10); seq.insertar(20); seq.insertar(30);
        afirmar("ListaSecuencial redimensiona y guarda 3 elementos", seq.tamanio() == 3 && seq.obtener(2) == 30);
        afirmar("ListaSecuencial elimina indice 1 (20)", seq.eliminar(1) == 20 && seq.tamanio() == 2);

        ListaEnlazada<String> enlazada = new ListaEnlazada<>();
        enlazada.insertar("A"); enlazada.insertar("B"); enlazada.insertar("C");
        afirmar("ListaEnlazada elimina 'B'", enlazada.eliminar(s -> s.equals("B")) && enlazada.tamanio() == 2);
        afirmar("ListaEnlazada elimina ultimo 'C'", enlazada.eliminar(s -> s.equals("C")) && enlazada.tamanio() == 1);

        // 4. Flujo completo con muestra_con_errores.csv (8 validos + 6 errores)
        Extract extract = new Extract();
        Cola<RegistroEducativo> colaETL = extract.extraerACola(
                Path.of("data/muestra_con_errores.csv"), StandardCharsets.UTF_8, ';');
        afirmar("Extract encola 14 registros", colaETL.tamanio() == 14);

        Transform.ResultadoTransform res = new Transform().ejecutarDesdeCola(colaETL);
        afirmar("Cola queda vacia tras Transform", colaETL.estaVacia());
        afirmar("Transform valida 8 registros correctos", res.limpios.tamanio() == 8);
        afirmar("Transform apila 6 errores en Pila<ErrorETL>", res.errores.tamanio() == 6);
        afirmar("Frecuencia de DATOS_INCOMPLETOS es 2", res.frecuenciaErrores.getOrDefault("DATOS_INCOMPLETOS", 0) == 2);
        afirmar("Frecuencia de DUPLICADO es 1", res.frecuenciaErrores.getOrDefault("DUPLICADO", 0) == 1);

        System.out.println("\n--- Pila de errores detectados (LIFO) ---");
        res.errores.mostrar();

        System.out.println("\nPasadas: " + pasadas + " | Fallidas: " + fallidas);
        if (fallidas > 0) System.exit(1);
    }

    private static void afirmar(String desc, boolean cond) {
        if (cond) { System.out.println("[OK]    " + desc); pasadas++; }
        else      { System.out.println("[FALLO] " + desc); fallidas++; }
    }
}