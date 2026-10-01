package model;
import estructuras.ListaEnlazada;

public final class DatasetEducativo {
    private final EsquemaCsv esquema;
    private final ListaEnlazada<RegistroEducativo> registros;
    private final char separador;
    public DatasetEducativo(EsquemaCsv esquema,
            ListaEnlazada<RegistroEducativo> registros, char separador) {
        this.esquema=esquema; this.registros=registros; this.separador=separador;
    }
    public EsquemaCsv getEsquema() { return esquema; }
    public ListaEnlazada<RegistroEducativo> getRegistros() { return registros; }
    public char getSeparador() { return separador; }
}
