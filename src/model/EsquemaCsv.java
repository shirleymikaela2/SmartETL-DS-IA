package model;

/** Cabecera compartida: evita copiarla para cada registro del archivo grande. */
public final class EsquemaCsv {
    private final String[] columnas;
    public EsquemaCsv(String[] columnas) {
        if (columnas.length == 0) throw new IllegalArgumentException("Cabecera vacia");
        this.columnas = columnas.clone();
        for (String c : columnas) if (c.isBlank())
            throw new IllegalArgumentException("Columna sin nombre");
    }
    public int cantidad() { return columnas.length; }
    public String columna(int indice) { return columnas[indice]; }
    public int indice(String nombre) {
        for (int i=0; i<columnas.length; i++)
            if (columnas[i].equals(nombre)) return i;
        return -1;
    }
}
