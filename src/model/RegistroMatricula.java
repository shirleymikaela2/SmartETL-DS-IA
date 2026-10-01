package model;

/** Registro institucional por año lectivo. Conserva valores originales en Extract. */
public final class RegistroMatricula extends RegistroEducativo {
    private static final String[] CABECERA = {"Anio_lectivo","Zona","Provincia","Cod_Provincia","Canton","Cod_Canton","Parroquia","Cod_Parroquia","Nombre_Institucion","AMIE","Escolarizacion","Tipo_Educacion","Sostenimiento","area","Regimen_Escolar","Jurisdiccion","Modalidad","Jornada","Acceso_Edificio","Total_Estudiantes","Promovidos","No promovidos","Abandono"};
    private final String anioLectivo;
    private final String zona;
    private final String provincia;
    private final String codigoProvincia;
    private final String canton;
    private final String codigoCanton;
    private final String parroquia;
    private final String codigoParroquia;
    private final String nombreInstitucion;
    private final String amie;
    private final String escolarizacion;
    private final String tipoEducacion;
    private final String sostenimiento;
    private final String area;
    private final String regimenEscolar;
    private final String jurisdiccion;
    private final String modalidad;
    private final String jornada;
    private final String accesoEdificio;
    private final String totalEstudiantes;
    private final String promovidos;
    private final String noPromovidos;
    private final String abandono;
    public RegistroMatricula(long fila, EsquemaCsv esquema, String[] valores) {
        super(fila, esquema, valores);
        validarEsquema(esquema);
        this.anioLectivo=valores[0];
        this.zona=valores[1];
        this.provincia=valores[2];
        this.codigoProvincia=valores[3];
        this.canton=valores[4];
        this.codigoCanton=valores[5];
        this.parroquia=valores[6];
        this.codigoParroquia=valores[7];
        this.nombreInstitucion=valores[8];
        this.amie=valores[9];
        this.escolarizacion=valores[10];
        this.tipoEducacion=valores[11];
        this.sostenimiento=valores[12];
        this.area=valores[13];
        this.regimenEscolar=valores[14];
        this.jurisdiccion=valores[15];
        this.modalidad=valores[16];
        this.jornada=valores[17];
        this.accesoEdificio=valores[18];
        this.totalEstudiantes=valores[19];
        this.promovidos=valores[20];
        this.noPromovidos=valores[21];
        this.abandono=valores[22];
    }
    public String getAnioLectivo() { return anioLectivo; }
    public String getZona() { return zona; }
    public String getProvincia() { return provincia; }
    public String getCodigoProvincia() { return codigoProvincia; }
    public String getCanton() { return canton; }
    public String getCodigoCanton() { return codigoCanton; }
    public String getParroquia() { return parroquia; }
    public String getCodigoParroquia() { return codigoParroquia; }
    public String getNombreInstitucion() { return nombreInstitucion; }
    public String getAmie() { return amie; }
    public String getEscolarizacion() { return escolarizacion; }
    public String getTipoEducacion() { return tipoEducacion; }
    public String getSostenimiento() { return sostenimiento; }
    public String getArea() { return area; }
    public String getRegimenEscolar() { return regimenEscolar; }
    public String getJurisdiccion() { return jurisdiccion; }
    public String getModalidad() { return modalidad; }
    public String getJornada() { return jornada; }
    public String getAccesoEdificio() { return accesoEdificio; }
    public String getTotalEstudiantes() { return totalEstudiantes; }
    public String getPromovidos() { return promovidos; }
    public String getNoPromovidos() { return noPromovidos; }
    public String getAbandono() { return abandono; }
    /** Clave del registro en este archivo, comprobada al inspeccionarlo. */
    public String getClaveCompuesta() { return anioLectivo+"|"+amie; }
    public static void validarEsquema(EsquemaCsv esquema) {
        if(esquema.cantidad()!=CABECERA.length)
            throw new IllegalArgumentException("MINEDUC requiere 23 columnas");
        for(int i=0;i<CABECERA.length;i++)
            if(!CABECERA[i].equals(esquema.columna(i)))
                throw new IllegalArgumentException("Cabecera MINEDUC inesperada en columna "+(i+1)
                    +": se esperaba "+CABECERA[i]+", llego "+esquema.columna(i));
    }
    @Override public String toString() {
        return getClaveCompuesta()+" | "+nombreInstitucion+" | "+provincia+" / "+canton
            +" | estudiantes="+totalEstudiantes+" | promovidos="+promovidos
            +" | no promovidos="+noPromovidos+" | abandono="+abandono;
    }
}
