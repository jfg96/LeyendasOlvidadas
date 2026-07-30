package leyendasolvidadas.dominio.compania;

import leyendasolvidadas.dominio.campana.CapituloCampana;

/** Identidades irrepetibles con una historia personal propia. */
public enum MercenarioUnico {
    EL_RETORNADO("El Retornado", CapituloCampana.CAMPANAS_DE_VALDESOMBRA),
    SOR_EREA("Sor Erea", CapituloCampana.CAMINOS_DE_ANIMAS),
    XOAN_DAS_NAVALLAS("Xoán das Navallas", CapituloCampana.CAMINOS_DE_ANIMAS),
    A_FILLA_DO_LOBO("A Filla do Lobo", CapituloCampana.CAMPANAS_DE_VALDESOMBRA),
    MARTINO_EL_TUERTO("Martiño el Tuerto", CapituloCampana.DEUDA_DE_LOS_VIVOS);

    private final String nombre;
    private final CapituloCampana capitulo;
    MercenarioUnico(String nombre, CapituloCampana capitulo) { this.nombre = nombre; this.capitulo = capitulo; }
    public String getNombre() { return nombre; }
    public CapituloCampana getCapitulo() { return capitulo; }
    public String id() { return name().toLowerCase(); }
}
