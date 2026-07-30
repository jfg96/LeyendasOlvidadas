package leyendasolvidadas.aplicacion;

import leyendasolvidadas.dominio.campana.CapituloCampana;

/** Primera presentación y decisión del capítulo Las campanas de Valdesombra. */
public final class ServicioCapituloUno {
    public enum Actitud { CONFIAR, DESCONFIAR, PRESIONAR }
    private static final String PRESENTADO = "cap1.padre_tome_presentado";

    public boolean requierePresentacion(EstadoJuego estado) {
        return estado.getProgresoCampana().getCapitulo() == CapituloCampana.CAMPANAS_DE_VALDESOMBRA
                && !estado.getProgresoCampana().haDecidido(PRESENTADO);
    }

    public void conocerPadreTome(EstadoJuego estado, Actitud actitud) {
        if (!requierePresentacion(estado))
            throw new IllegalStateException("La presentacion de Padre Tome no esta disponible");
        estado.getProgresoCampana().registrarDecision("cap1.padre_tome."
                + actitud.name().toLowerCase());
        estado.getProgresoCampana().registrarDecision(PRESENTADO);
    }
}
