package leyendasolvidadas.aplicacion;

import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.mundo.Region;

/** Primera presentación y decisión del capítulo Las campanas de Valdesombra. */
public final class ServicioCapituloUno {
    public enum Actitud { CONFIAR, DESCONFIAR, PRESIONAR }
    public enum ActitudInes { PROTEGER, INTERROGAR, CONFIAR_EN_TOME }
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

    public int registrarVictoria(EstadoJuego estado, Region region) {
        if (region != Region.BOSQUE_DE_LOS_AHORCADOS
                || estado.getProgresoCampana().getCapitulo() != CapituloCampana.CAMPANAS_DE_VALDESOMBRA) return 0;
        int victorias = (int) estado.getProgresoCampana().getDecisiones().stream()
                .filter(id -> id.startsWith("cap1.bosque.victoria.")).count();
        if (victorias < 3) {
            victorias++;
            estado.getProgresoCampana().registrarDecision("cap1.bosque.victoria." + victorias);
        }
        if (victorias == 3) estado.getProgresoCampana().registrarDecision("cap1.simbolo_peregrinos_descubierto");
        return victorias;
    }

    public void completarBosque(EstadoJuego estado, ActitudInes actitud) {
        if (estado.getProgresoCampana().getCapitulo() != CapituloCampana.CAMPANAS_DE_VALDESOMBRA
                || !estado.getProgresoCampana().haDecidido("cap1.simbolo_peregrinos_descubierto"))
            throw new IllegalStateException("El desenlace del Bosque todavía no está disponible");
        estado.getProgresoCampana().registrarDecision("cap1.rei_derrotado");
        estado.getProgresoCampana().registrarDecision("cap1.ines." + actitud.name().toLowerCase());
        estado.getProgresoCampana().registrarDecision("cap1.ines_en_valdesombra");
        estado.getProgresoCampana().avanzarA(CapituloCampana.CAMINOS_DE_ANIMAS);
    }
}
