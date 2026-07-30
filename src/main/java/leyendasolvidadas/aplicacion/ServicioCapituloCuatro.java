package leyendasolvidadas.aplicacion;

import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.mundo.Region;

/** Reglas de El libro de los nombres y preparación de la procesión final. */
public final class ServicioCapituloCuatro {
    public enum CustodiaLibro { PADRE_TOME, ALDARA, COMPANIA }
    public enum JusticiaFamilias { REVELAR_CULPABLES, PROTEGER_DESCENDIENTES, EXIGIR_REPARACION }
    public enum NombreCientoTrece { ANTEPASADO_DEL_HEROE, INES_LA_DESMEMORIADA }
    public enum PreparacionRitual { SAL_Y_FUEGO, RELIQUIAS, CAMPANAS }

    public boolean requierePresentacion(EstadoJuego estado) {
        return esCapitulo(estado) && !estado.getProgresoCampana().haDecidido("cap4.libro_abierto");
    }

    public void abrirLibro(EstadoJuego estado, CustodiaLibro custodia) {
        if (!requierePresentacion(estado)) throw new IllegalStateException("El Libro ya está abierto");
        estado.getProgresoCampana().registrarDecision("cap4.custodia." + custodia.name().toLowerCase());
        estado.getProgresoCampana().registrarDecision("cap4.libro_abierto");
    }

    /** Registra uno de los tres testimonios necesarios. Devuelve cuántos se han reunido. */
    public int registrarHallazgo(EstadoJuego estado, Region region) {
        if (!esCapitulo(estado)) return 0;
        String fragmento = switch (region) {
            case BOSQUE_DE_LOS_AHORCADOS -> "patibulos";
            case CAMINO_DE_LOS_DIFUNTOS -> "peregrinos";
            case PAZO_DE_SOUTOMAIOR -> "familias";
            default -> null;
        };
        if (fragmento == null) return numeroFragmentos(estado);
        estado.getProgresoCampana().registrarDecision("cap4.fragmento." + fragmento);
        if (numeroFragmentos(estado) == 3)
            estado.getProgresoCampana().registrarDecision("cap4.ritual_disponible");
        return numeroFragmentos(estado);
    }

    public boolean puedeCelebrarRitual(EstadoJuego estado) {
        return esCapitulo(estado) && estado.getProgresoCampana().haDecidido("cap4.ritual_disponible");
    }

    public void completar(EstadoJuego estado, JusticiaFamilias justicia, NombreCientoTrece nombre,
                          PreparacionRitual preparacion) {
        if (!puedeCelebrarRitual(estado))
            throw new IllegalStateException("Aún faltan nombres por reconstruir");
        estado.getProgresoCampana().registrarDecision("cap4.justicia." + justicia.name().toLowerCase());
        estado.getProgresoCampana().registrarDecision("cap4.nombre113." + nombre.name().toLowerCase());
        estado.getProgresoCampana().registrarDecision("cap4.preparacion." + preparacion.name().toLowerCase());
        estado.getProgresoCampana().registrarDecision("cap4.ciento_doce_nombres_devuelto");
        estado.getProgresoCampana().avanzarA(CapituloCampana.ULTIMA_PROCESION);
        estado.getRegistroCampana().anotar("Capítulo IV: el nombre 113 fue reconocido como "
                + nombre.name().toLowerCase().replace('_', ' ') + ".");
    }

    private int numeroFragmentos(EstadoJuego estado) {
        return (int) estado.getProgresoCampana().getDecisiones().stream()
                .filter(id -> id.startsWith("cap4.fragmento.")).count();
    }

    private boolean esCapitulo(EstadoJuego estado) {
        return estado.getProgresoCampana().getCapitulo() == CapituloCampana.LIBRO_DE_LOS_NOMBRES;
    }
}
