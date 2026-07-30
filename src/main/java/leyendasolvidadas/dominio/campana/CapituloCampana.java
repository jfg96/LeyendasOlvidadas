package leyendasolvidadas.dominio.campana;

/** Hitos estables de la historia principal, en su orden narrativo. */
public enum CapituloCampana {
    PROLOGO("La novena campanada"),
    CAMPANAS_DE_VALDESOMBRA("Las campanas de Valdesombra"),
    CAMINOS_DE_ANIMAS("Los caminos de ánimas"),
    DEUDA_DE_LOS_VIVOS("La deuda de los vivos"),
    LIBRO_DE_LOS_NOMBRES("El libro de los nombres"),
    ULTIMA_PROCESION("La última procesión"),
    EPILOGO("Epílogo");

    private final String titulo;

    CapituloCampana(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() { return titulo; }

    public boolean puedeAvanzarA(CapituloCampana destino) {
        return destino != null && destino.ordinal() == ordinal() + 1;
    }
}
