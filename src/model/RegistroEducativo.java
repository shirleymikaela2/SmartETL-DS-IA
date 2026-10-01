package model;

/** Registro crudo general. Clase base de RegistroMatricula. */
public class RegistroEducativo {
    private final long numeroFila;
    private final EsquemaCsv esquema;
    private final String[] valores;
    public RegistroEducativo(long numeroFila, EsquemaCsv esquema, String[] valores) {
        if (valores.length != esquema.cantidad())
            throw new IllegalArgumentException("Cantidad de campos incorrecta");
        this.numeroFila = numeroFila;
        this.esquema = esquema;
        this.valores = valores.clone();
    }
    /** Posicion logica en el CSV, cabecera=1. NO es un ID oficial de MINEDUC. */
    public long getNumeroFila() { return numeroFila; }
    public String getValor(int indice) { return valores[indice]; }
    public String getValor(String columna) {
        int i = esquema.indice(columna);
        if (i < 0) throw new IllegalArgumentException("Columna inexistente: " + columna);
        return valores[i];
    }
    @Override public String toString() {
        StringBuilder texto = new StringBuilder("Fila " + numeroFila);
        for (int i=0; i<valores.length; i++)
            texto.append(" | ").append(esquema.columna(i)).append("=").append(valores[i]);
        return texto.toString();
    }
}
