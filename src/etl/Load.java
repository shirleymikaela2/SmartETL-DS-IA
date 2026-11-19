package etl;

import estructuras.ListaEnlazada;
import estructuras.Pila;
import model.ErrorETL;
import model.RegistroLimpio;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/** Fase LOAD: persiste registros limpios, pila de errores y reporte estadistico en output/. */
public class Load {

    public void guardarTodo(Transform.ResultadoTransform resultado, Path carpetaSalida) throws IOException {
        Files.createDirectories(carpetaSalida);
        guardarRegistrosLimpios(resultado.limpios, carpetaSalida.resolve("registros_limpios.csv"));
        guardarErrores(resultado.errores, carpetaSalida.resolve("errores_etl.txt"));
        guardarReporte(resultado, carpetaSalida.resolve("reporte_etl.txt"));
    }

    public void guardarRegistrosLimpios(ListaEnlazada<RegistroLimpio> limpios, Path archivo) throws IOException {
        Files.createDirectories(archivo.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            bw.write("Anio_lectivo;Zona;Provincia;Canton;Parroquia;Nombre_Institucion;AMIE;"
                    + "Sostenimiento;Area;Regimen_Escolar;Total_Estudiantes;Promovidos;"
                    + "No_Promovidos;Abandono;Tasa_Promocion;Tasa_Abandono");
            bw.newLine();

            for (RegistroLimpio r : limpios) {
                bw.write(String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%d;%d;%d;%d;%.2f;%.2f",
                        r.getAnioLectivo(), r.getZona(), r.getProvincia(), r.getCanton(),
                        r.getParroquia(), r.getNombreInstitucion(), r.getAmie(),
                        r.getSostenimiento(), r.getArea(), r.getRegimenEscolar(),
                        r.getTotalEstudiantes(), r.getPromovidos(), r.getNoPromovidos(),
                        r.getAbandono(), r.getTasaPromocion(), r.getTasaAbandono()));
                bw.newLine();
            }
        }
    }

    public void guardarErrores(Pila<ErrorETL> pilaErrores, Path archivo) throws IOException {
        Files.createDirectories(archivo.getParent());
        Pila<ErrorETL> auxiliar = new Pila<>();

        try (BufferedWriter bw = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            bw.write("=== ERRORES DETECTADOS EN FASE TRANSFORM (ORDEN LIFO) ===");
            bw.newLine();
            bw.write("Total de errores: " + pilaErrores.tamanio());
            bw.newLine();
            bw.newLine();

            int orden = 1;
            while (!pilaErrores.estaVacia()) {
                ErrorETL err = pilaErrores.desapilar();
                bw.write(String.format("%d. %s", orden++, err.toString()));
                bw.newLine();
                auxiliar.apilar(err);
            }
        } finally {
            // Restaura la pila original intacta
            while (!auxiliar.estaVacia()) {
                pilaErrores.apilar(auxiliar.desapilar());
            }
        }
    }

    public void guardarReporte(Transform.ResultadoTransform res, Path archivo) throws IOException {
        Files.createDirectories(archivo.getParent());
        long totalEstudiantes = 0;
        long totalPromovidos = 0;
        long totalAbandono = 0;

        for (RegistroLimpio r : res.limpios) {
            totalEstudiantes += r.getTotalEstudiantes();
            totalPromovidos += r.getPromovidos();
            totalAbandono += r.getAbandono();
        }

        double tasaPromGlobal = totalEstudiantes == 0 ? 0.0 : (totalPromovidos * 100.0) / totalEstudiantes;
        double tasaAbandGlobal = totalEstudiantes == 0 ? 0.0 : (totalAbandono * 100.0) / totalEstudiantes;

        try (BufferedWriter bw = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            bw.write("=== REPORTE TECNICO ETL - SMARTETL-DS + IA ===");
            bw.newLine();
            bw.write("Registros procesados : " + res.procesados);
            bw.newLine();
            bw.write("Registros validos    : " + res.limpios.tamanio());
            bw.newLine();
            bw.write("Registros con error  : " + res.descartados);
            bw.newLine();
            bw.newLine();
            bw.write("--- Metricas agregadas (registros validos) ---");
            bw.newLine();
            bw.write("Total estudiantes    : " + totalEstudiantes);
            bw.newLine();
            bw.write("Total promovidos     : " + totalPromovidos + String.format(" (%.2f%%)", tasaPromGlobal));
            bw.newLine();
            bw.write("Total abandono       : " + totalAbandono + String.format(" (%.2f%%)", tasaAbandGlobal));
            bw.newLine();
            bw.newLine();
            bw.write("--- Frecuencia de errores por categoria ---");
            bw.newLine();
            if (res.frecuenciaErrores.isEmpty()) {
                bw.write("Sin errores registrados.");
                bw.newLine();
            } else {
                for (Map.Entry<String, Integer> entry : res.frecuenciaErrores.entrySet()) {
                    bw.write(String.format("  - %-22s: %d", entry.getKey(), entry.getValue()));
                    bw.newLine();
                }
            }
        }
    }
}