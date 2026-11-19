package model;

public final class RegistroLimpio {
    private final String anioLectivo;
    private final String zona;
    private final String provincia;
    private final String canton;
    private final String parroquia;
    private final String nombreInstitucion;
    private final String amie;
    private final String sostenimiento;
    private final String area;
    private final String regimenEscolar;
    private final int totalEstudiantes;
    private final int promovidos;
    private final int noPromovidos;
    private final int abandono;

    public RegistroLimpio(String anioLectivo, String zona, String provincia,
                          String canton, String parroquia, String nombreInstitucion,
                          String amie, String sostenimiento, String area,
                          String regimenEscolar, int totalEstudiantes,
                          int promovidos, int noPromovidos, int abandono) {
        this.anioLectivo = anioLectivo;
        this.zona = zona;
        this.provincia = provincia;
        this.canton = canton;
        this.parroquia = parroquia;
        this.nombreInstitucion = nombreInstitucion;
        this.amie = amie;
        this.sostenimiento = sostenimiento;
        this.area = area;
        this.regimenEscolar = regimenEscolar;
        this.totalEstudiantes = totalEstudiantes;
        this.promovidos = promovidos;
        this.noPromovidos = noPromovidos;
        this.abandono = abandono;
    }

    public String getClaveUnica() { return anioLectivo + "|" + amie; }
    public String getAnioLectivo() { return anioLectivo; }
    public String getZona() { return zona; }
    public String getProvincia() { return provincia; }
    public String getCanton() { return canton; }
    public String getParroquia() { return parroquia; }
    public String getNombreInstitucion() { return nombreInstitucion; }
    public String getAmie() { return amie; }
    public String getSostenimiento() { return sostenimiento; }
    public String getArea() { return area; }
    public String getRegimenEscolar() { return regimenEscolar; }
    public int getTotalEstudiantes() { return totalEstudiantes; }
    public int getPromovidos() { return promovidos; }
    public int getNoPromovidos() { return noPromovidos; }
    public int getAbandono() { return abandono; }

    public double getTasaAbandono() {
        return totalEstudiantes == 0 ? 0.0 : (abandono * 100.0) / totalEstudiantes;
    }

    public double getTasaPromocion() {
        return totalEstudiantes == 0 ? 0.0 : (promovidos * 100.0) / totalEstudiantes;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s | Est=%d (Prom=%d, NoProm=%d, Aband=%d, %%Aband=%.2f%%)",
                anioLectivo, amie, zona, nombreInstitucion,
                totalEstudiantes, promovidos, noPromovidos, abandono, getTasaAbandono());
    }
}