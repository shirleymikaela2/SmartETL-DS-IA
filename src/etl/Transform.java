package etl;

import estructuras.Cola;
import estructuras.ListaEnlazada;
import estructuras.Pila;
import model.DatasetEducativo;
import model.ErrorETL;
import model.RegistroEducativo;
import model.RegistroLimpio;
import model.RegistroMatricula;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Transform {

    public static class ResultadoTransform {
        public final ListaEnlazada<RegistroLimpio> limpios;
        public final Pila<ErrorETL> errores;
        public final int procesados;
        public final int descartados;
        public final Map<String, Integer> frecuenciaErrores;

        public ResultadoTransform(ListaEnlazada<RegistroLimpio> limpios,
                                  Pila<ErrorETL> errores,
                                  int procesados,
                                  int descartados,
                                  Map<String, Integer> frecuenciaErrores) {
            this.limpios = limpios;
            this.errores = errores;
            this.procesados = procesados;
            this.descartados = descartados;
            this.frecuenciaErrores = frecuenciaErrores;
        }
    }

    /** Compatibilidad directa con DatasetEducativo: encola y procesa via Cola FIFO. */
    public ResultadoTransform ejecutar(DatasetEducativo dataset) {
        Cola<RegistroEducativo> cola = new Cola<>();
        for (RegistroEducativo r : dataset.getRegistros()) {
            cola.encolar(r);
        }
        return ejecutarDesdeCola(cola);
    }

    /** Flujo oficial ETL: desencola de Cola, inserta validos en ListaEnlazada y apila errores en Pila. */
    public ResultadoTransform ejecutarDesdeCola(Cola<RegistroEducativo> cola) {
        ListaEnlazada<RegistroLimpio> resultado = new ListaEnlazada<>();
        Pila<ErrorETL> pilaErrores = new Pila<>();
        // Indice auxiliar O(1) para detectar claves duplicadas (Anio_lectivo|AMIE) en 286k registros.
        Set<String> clavesVistas = new HashSet<>();
        Map<String, Integer> frecuencias = new LinkedHashMap<>();

        int procesados = 0;
        int descartados = 0;

        while (!cola.estaVacia()) {
            RegistroEducativo reg = cola.desencolar();
            procesados++;

            if (!(reg instanceof RegistroMatricula)) {
                registrarError(pilaErrores, frecuencias, reg.getNumeroFila(), "N/A",
                        "TIPO_INVALIDO", "El registro no es de tipo RegistroMatricula");
                descartados++;
                continue;
            }

            RegistroMatricula rm = (RegistroMatricula) reg;
            String anio = limpiarTexto(rm.getAnioLectivo());
            String amie = limpiarTexto(rm.getAmie()).toUpperCase();
            String institucion = limpiarTexto(rm.getNombreInstitucion());
            String provincia = limpiarTexto(rm.getProvincia()).toUpperCase();
            String clave = anio + "|" + amie;

            // 1. Validar identificadores y campos obligatorios
            if (anio.isEmpty() || amie.isEmpty() || institucion.isEmpty() || provincia.isEmpty()) {
                registrarError(pilaErrores, frecuencias, rm.getNumeroFila(), clave,
                        "DATOS_INCOMPLETOS", "Anio_lectivo, AMIE, Nombre_Institucion o Provincia vacios");
                descartados++;
                continue;
            }

            // 2. Detectar duplicados por clave compuesta (Anio_lectivo|AMIE)
            if (!clavesVistas.add(clave)) {
                registrarError(pilaErrores, frecuencias, rm.getNumeroFila(), clave,
                        "DUPLICADO", "Registro duplicado para la clave " + clave);
                descartados++;
                continue;
            }

            // 3. Parsear campos numericos eliminando separador de miles '.'
            int total, prom, noProm, aband;
            try {
                total = parsearEnteroMineduc(rm.getTotalEstudiantes());
                prom = parsearEnteroMineduc(rm.getPromovidos());
                noProm = parsearEnteroMineduc(rm.getNoPromovidos());
                aband = parsearEnteroMineduc(rm.getAbandono());
            } catch (NumberFormatException e) {
                registrarError(pilaErrores, frecuencias, rm.getNumeroFila(), clave,
                        "FORMATO_NUMERICO", "Valor no numerico en contadores de estudiantes");
                descartados++;
                continue;
            }

            // 4. Validar rangos y consistencia matematica
            if (total <= 0 || prom < 0 || noProm < 0 || aband < 0) {
                registrarError(pilaErrores, frecuencias, rm.getNumeroFila(), clave,
                        "RANGO_INVALIDO", "Total debe ser > 0 y subtotales >= 0");
                descartados++;
                continue;
            }

            if (prom + noProm + aband != total) {
                registrarError(pilaErrores, frecuencias, rm.getNumeroFila(), clave,
                        "INCONSISTENCIA_SUMA",
                        String.format("Prom(%d)+NoProm(%d)+Aband(%d) != Total(%d)",
                                prom, noProm, aband, total));
                descartados++;
                continue;
            }

            RegistroLimpio limpio = new RegistroLimpio(
                    anio,
                    limpiarTexto(rm.getZona()).toUpperCase(),
                    provincia,
                    limpiarTexto(rm.getCanton()).toUpperCase(),
                    limpiarTexto(rm.getParroquia()).toUpperCase(),
                    institucion,
                    amie,
                    limpiarTexto(rm.getSostenimiento()),
                    limpiarTexto(rm.getArea()),
                    limpiarTexto(rm.getRegimenEscolar()),
                    total, prom, noProm, aband
            );
            resultado.insertar(limpio);
        }

        return new ResultadoTransform(resultado, pilaErrores, procesados, descartados, frecuencias);
    }

    private void registrarError(Pila<ErrorETL> pila, Map<String, Integer> frecuencias,
                                long fila, String clave, String categoria, String detalle) {
        pila.apilar(new ErrorETL(fila, clave, categoria, detalle));
        frecuencias.merge(categoria, 1, Integer::sum);
    }

    private String limpiarTexto(String texto) {
        if (texto == null) return "";
        return texto.trim().replaceAll("\\s{2,}", " ");
    }

    private int parsearEnteroMineduc(String valor) {
        if (valor == null) throw new NumberFormatException("Valor nulo");
        String sinMiles = valor.trim().replace(".", "");
        return Integer.parseInt(sinMiles);
    }
}