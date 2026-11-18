import estructuras.ListaEnlazada;
import etl.Extract;
import model.DatasetEducativo;
import model.RegistroEducativo;
import model.RegistroMatricula;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
 
public class PruebasHito1 {

    private static int pasadas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {
        System.out.println("=== EJECUTANDO PRUEBAS AUTOMATIZADAS - HITO 1 ===\n");

        probarListaEnlazada();
        probarExtraccionMuestra30();
        probarIntegracionYConsulta();

        System.out.println("\n=== RESUMEN DE PRUEBAS ===");
        System.out.println("Pasadas : " + pasadas);
        System.out.println("Fallidas: " + fallidas);

        if (fallidas > 0) {
            System.exit(1);
        }
    }

    private static void probarListaEnlazada() {
        ListaEnlazada<String> lista = new ListaEnlazada<>();
        afirmar("Lista inicia vacia", lista.estaVacia() && lista.tamanio() == 0);

        lista.insertar("01B00019");
        lista.insertar("01B00020");
        afirmar("Tamanio tras 2 inserciones es 2", lista.tamanio() == 2);
        afirmar("obtener(0) devuelve primer elemento", "01B00019".equals(lista.obtener(0)));
        afirmar("obtener(1) devuelve segundo elemento", "01B00020".equals(lista.obtener(1)));

        String encontrado = lista.buscar(s -> s.equals("01B00020"));
        afirmar("buscar() encuentra elemento existente", "01B00020".equals(encontrado));

        String noEncontrado = lista.buscar(s -> s.equals("INEXISTENTE"));
        afirmar("buscar() devuelve null si no existe", noEncontrado == null);

        boolean rechazoNull = false;
        try {
            lista.insertar(null);
        } catch (NullPointerException e) {
            rechazoNull = true;
        }
        afirmar("insertar(null) lanza NullPointerException", rechazoNull);
    }

    private static void probarExtraccionMuestra30() {
        try {
            Extract extract = new Extract();
            Path archivo = Path.of("data/muestra_30_registros.csv");

            char sep = extract.detectarSeparador(archivo, StandardCharsets.UTF_8);
            afirmar("Separador detectado en muestra es ';'", sep == ';');

            DatasetEducativo ds = extract.leerMineduc(archivo, StandardCharsets.UTF_8, sep);
            afirmar("Esquema tiene exactamente 23 columnas", ds.getEsquema().cantidad() == 23);
            afirmar("Muestra carga exactamente 30 registros", ds.getRegistros().tamanio() == 30);
        } catch (Exception e) {
            afirmar("Extraccion de muestra sin excepciones: " + e.getMessage(), false);
        }
    }

    private static void probarIntegracionYConsulta() {
        try {
            Extract extract = new Extract();
            Path archivo = Path.of("data/muestra_30_registros.csv");
            DatasetEducativo ds = extract.leerMineduc(archivo, StandardCharsets.UTF_8, ';');

            RegistroEducativo primero = ds.getRegistros().obtener(0);
            afirmar("El registro es instancia de RegistroMatricula", primero instanceof RegistroMatricula);

            RegistroMatricula rm = (RegistroMatricula) primero;
            afirmar("Primer AMIE es 01B00019", "01B00019".equals(rm.getAmie()));
            afirmar("Primera provincia es AZUAY", "AZUAY".equals(rm.getProvincia()));
            afirmar("Total_Estudiantes del primer registro es 47", "47".equals(rm.getTotalEstudiantes()));

            RegistroEducativo buscado = ds.getRegistros().buscar(
                r -> r.getValor("AMIE").equals("01B00021")
            );
            afirmar("Busqueda en lista por AMIE 01B00021 funciona", buscado != null);
        } catch (Exception e) {
            afirmar("Integracion y consulta sin excepciones: " + e.getMessage(), false);
        }
    }

    private static void afirmar(String descripcion, boolean condicion) {
        if (condicion) {
            System.out.println("[OK]    " + descripcion);
            pasadas++;
        } else {
            System.out.println("[FALLO] " + descripcion);
            fallidas++;
        }
    }
}