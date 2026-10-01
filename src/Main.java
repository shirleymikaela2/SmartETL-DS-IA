import model.*;
import etl.Extract;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Scanner;

public final class Main {
    private static DatasetEducativo dataset;
    private static final Scanner entrada=new Scanner(System.in);
    public static void main(String[] args) {
        while(true) {
            System.out.println("\nSMARTETL MINEDUC - HITO 1");
            System.out.println("1 Cargar CSV | 2 Ver columnas | 3 Mostrar pagina | 4 Consultar | 5 Contar | 0 Salir");
            String opcion=leer("Opcion: ");
            if(opcion.equals("0")) break;
            try {
                switch(opcion) {
                    case "1": cargar(); break;
                    case "2": columnas(); break;
                    case "3": mostrar(); break;
                    case "4": consultar(); break;
                    case "5": System.out.println("Total: "+(dataset==null?0:dataset.getRegistros().tamanio())); break;
                    default: System.out.println("Opcion incorrecta");
                }
            } catch(Exception e) {
                System.out.println("ERROR: "+e.getMessage());
                System.out.println("La ultima carga completa sigue disponible.");
            }
        }
    }
    private static String leer(String mensaje) {
        System.out.print(mensaje);
        if(!entrada.hasNextLine()) System.exit(0);
        return entrada.nextLine();
    }
    private static void cargar() throws Exception {
        String ruta=leer("Ruta CSV (Enter: data/MINEDUC_RegistroAdministrativoHistorico_2009-2024Fin.csv): ");
        if(ruta.isBlank()) ruta="data/MINEDUC_RegistroAdministrativoHistorico_2009-2024Fin.csv";
        if(ruta.startsWith("\"") && ruta.endsWith("\"")) ruta=ruta.substring(1,ruta.length()-1);
        String charset=leer("Codificacion (Enter: UTF-8, alternativa: windows-1252): ");
        Charset codificacion=Charset.forName(charset.isBlank()?"UTF-8":charset);
        String sep=leer("Separador (Enter: detectar, coma, punto y coma o tab): ");
        Extract extractor=new Extract(); Path archivo=Path.of(ruta);
        char separador;
        if(sep.isBlank()) separador=extractor.detectarSeparador(archivo,codificacion);
        else if(sep.equals(",")||sep.equalsIgnoreCase("coma")) separador=',';
        else if(sep.equals(";")||sep.equalsIgnoreCase("punto y coma")) separador=';';
        else if(sep.equalsIgnoreCase("tab")) separador='\t';
        else throw new IllegalArgumentException("Separador no admitido");
        long inicio=System.nanoTime();
        DatasetEducativo nuevo=extractor.leerMineduc(archivo,codificacion,separador);
        dataset=nuevo; // Solo sustituye al terminar: nunca publica una carga parcial.
        System.out.printf("Carga completa: %d registros, %d columnas, %.3f s%n",
            dataset.getRegistros().tamanio(),dataset.getEsquema().cantidad(),
            (System.nanoTime()-inicio)/1e9);
        System.out.println("Separador: "+(separador=='\t'?"tab":separador));
    }
    private static void exigirCarga() {
        if(dataset==null) throw new IllegalStateException("Primero cargue un CSV");
    }
    private static void columnas() {
        exigirCarga();
        for(int i=0;i<dataset.getEsquema().cantidad();i++)
            System.out.println((i+1)+". "+dataset.getEsquema().columna(i));
    }
    private static void mostrar() {
        exigirCarga();
        int desde=Integer.parseInt(leer("Posicion inicial (1..n): "));
        int cantidad=Integer.parseInt(leer("Cantidad (1..100): "));
        if(desde<1 || desde>dataset.getRegistros().tamanio() || cantidad<1 || cantidad>100)
            throw new IllegalArgumentException("Rango incorrecto");
        int posicion=0,mostrados=0;
        for(RegistroEducativo r:dataset.getRegistros()) {
            if(++posicion<desde) continue;
            System.out.println(r); if(++mostrados==cantidad) break;
        }
        System.out.println("Mostrados: "+mostrados);
    }
    private static void consultar() {
        columnas();
        int indice=Integer.parseInt(leer("Numero de columna: "))-1;
        if(indice<0 || indice>=dataset.getEsquema().cantidad())
            throw new IllegalArgumentException("Columna incorrecta");
        String valor=leer("Valor exacto (respeta espacios y mayusculas): ");
        int coincidencias=0;
        for(RegistroEducativo r:dataset.getRegistros()) {
            if(r.getValor(indice).equals(valor)) {
                if(coincidencias<20) System.out.println(r);
                coincidencias++;
            }
        }
        System.out.println("Coincidencias: "+coincidencias+" (se muestran hasta 20)");
    }
}
