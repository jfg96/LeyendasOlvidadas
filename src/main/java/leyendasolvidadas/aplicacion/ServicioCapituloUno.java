package leyendasolvidadas.aplicacion;

import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.mundo.Region;

/** Primera presentación y decisión del capítulo Las campanas de Valdesombra. */
public final class ServicioCapituloUno extends ServicioCapitulo {
    public enum Actitud { CONFIAR, DESCONFIAR, PRESIONAR }
    public enum ActitudInes { PROTEGER, INTERROGAR, CONFIAR_EN_TOME }
    private static final String PRESENTADO = "cap1.padre_tome_presentado";

    @Override
    protected CapituloCampana capitulo() {
        return CapituloCampana.CAMPANAS_DE_VALDESOMBRA;
    }
    @Override
    protected boolean estaPresentado(EstadoJuego estado) {
        return estado.getProgresoCampana().haDecidido(PRESENTADO);
    }

    public void conocerPadreTome(EstadoJuego estado, Actitud actitud) {
        comprobarPresentacion(estado, "La presentacion de Padre Tome no esta disponible");
        estado.getProgresoCampana().registrarDecision("cap1.padre_tome."
                + actitud.name().toLowerCase());
        estado.getProgresoCampana().registrarDecision(PRESENTADO);
    }

    public int registrarVictoria(EstadoJuego estado, Region region) {
        if (region != Region.BOSQUE_DE_LOS_AHORCADOS || !enCapitulo(estado)) return 0;
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
        if (!enCapitulo(estado) || !estado.getProgresoCampana().haDecidido("cap1.simbolo_peregrinos_descubierto"))
            throw new IllegalStateException("El desenlace del Bosque todavía no está disponible");
        estado.getProgresoCampana().registrarDecision("cap1.rei_derrotado");
        estado.getProgresoCampana().registrarDecision("cap1.ines." + actitud.name().toLowerCase());
        estado.getProgresoCampana().registrarDecision("cap1.ines_en_valdesombra");
        cerrar(estado, CapituloCampana.CAMINOS_DE_ANIMAS, "Capítulo I: O Rei dos Aforcados cayó; Inés quedó bajo "
                + actitud.name().toLowerCase().replace('_', ' ') + ".");
    }
}
