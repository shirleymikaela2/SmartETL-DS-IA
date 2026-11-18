package etl;

import estructuras.ListaEnlazada;
import model.DatasetEducativo;
import model.RegistroEducativo;
import model.RegistroLimpio;
import model.RegistroMatricula;

public class Transform {

    public static class ResultadoTransform {
        public final ListaEnlazada<RegistroLimpio> limpios;
        public final int descartados;

        public ResultadoTransform(ListaEnlazada<RegistroLimpio> limpios, int descartados) {
            this.limpios = limpios;
            this.descartados = descartados;
        }
    }

    public ResultadoTransform ejecutar(DatasetEducativo dataset) {
        ListaEnlazada<RegistroLimpio> resultado = new ListaEnlazada<>();
        int descartados = 0;

        for (RegistroEducativo reg : dataset.getRegistros()) {
            if (!(reg instanceof RegistroMatricula)) {
                descartados++;
                continue;
            }
            RegistroMatricula rm = (RegistroMatricula) reg;
            try {
                int total = parsearEnteroMineduc(rm.getTotalEstudiantes());
                int prom = parsearEnteroMineduc(rm.getPromovidos());
                int noProm = parsearEnteroMineduc(rm.getNoPromovidos());
                int aband = parsearEnteroMineduc(rm.getAbandono());

                if (total <= 0 || prom < 0 || noProm < 0 || aband < 0 || (prom + noProm + aband != total)) {
                    descartados++;
                    continue;
                }

                RegistroLimpio limpio = new RegistroLimpio(
                        limpiarTexto(rm.getAnioLectivo()),
                        limpiarTexto(rm.getZona()).toUpperCase(),
                        limpiarTexto(rm.getProvincia()).toUpperCase(),
                        limpiarTexto(rm.getCanton()).toUpperCase(),
                        limpiarTexto(rm.getParroquia()).toUpperCase(),
                        limpiarTexto(rm.getNombreInstitucion()),
                        limpiarTexto(rm.getAmie()).toUpperCase(),
                        limpiarTexto(rm.getSostenimiento()),
                        limpiarTexto(rm.getArea()),
                        limpiarTexto(rm.getRegimenEscolar()),
                        total, prom, noProm, aband
                );
                resultado.insertar(limpio);
            } catch (NumberFormatException e) {
                descartados++;
            }
        }
        return new ResultadoTransform(resultado, descartados);
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