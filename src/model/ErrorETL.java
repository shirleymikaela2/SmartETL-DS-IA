package model;

public final class ErrorETL {
    private final long numeroFila;
    private final String claveReferencia;
    private final String categoria;
    private final String detalle;

    public ErrorETL(long numeroFila, String claveReferencia, String categoria, String detalle) {
        this.numeroFila = numeroFila;
        this.claveReferencia = claveReferencia == null ? "SIN_CLAVE" : claveReferencia;
        this.categoria = categoria;
        this.detalle = detalle;
    }

    public long getNumeroFila() { return numeroFila; }
    public String getClaveReferencia() { return claveReferencia; }
    public String getCategoria() { return categoria; }
    public String getDetalle() { return detalle; }

    @Override
    public String toString() {
        return String.format("Fila %d | Clave=%s | [%s] %s",
                numeroFila, claveReferencia, categoria, detalle);
    }
}