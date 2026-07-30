package leyendasolvidadas.aplicacion;

import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.mundo.Region;

/** Progreso y decisiones de Los caminos de ánimas. */
public final class ServicioCapituloDos {
    public enum ActitudAldara { ESCUCHAR, DESAFIAR, PACTAR }
    public enum Verdad { REVELAR, OCULTAR, NEGOCIAR }
    private static final String PRESENTADO = "cap2.aldara_presentada";

    public boolean requierePresentacion(EstadoJuego estado) {
        return estado.getProgresoCampana().getCapitulo() == CapituloCampana.CAMINOS_DE_ANIMAS
                && !estado.getProgresoCampana().haDecidido(PRESENTADO);
    }

    public void conocerAldara(EstadoJuego estado, ActitudAldara actitud) {
        if (!requierePresentacion(estado)) throw new IllegalStateException("Aldara ya se ha presentado");
        estado.getProgresoCampana().registrarDecision("cap2.aldara." + actitud.name().toLowerCase());
        estado.getProgresoCampana().registrarDecision(PRESENTADO);
    }

    public int registrarVictoria(EstadoJuego estado, Region region) {
        if (estado.getProgresoCampana().getCapitulo() != CapituloCampana.CAMINOS_DE_ANIMAS
                || (region != Region.BRANAS_HUNDIDAS && region != Region.CAMINO_DE_LOS_DIFUNTOS)) return 0;
        String zona = region == Region.BRANAS_HUNDIDAS ? "branas" : "camino";
        int total = (int) estado.getProgresoCampana().getDecisiones().stream()
                .filter(id -> id.startsWith("cap2." + zona + ".victoria.")).count();
        if (total < 2) estado.getProgresoCampana().registrarDecision("cap2." + zona + ".victoria." + (++total));
        if (total == 2) estado.getProgresoCampana().registrarDecision("cap2." + zona + ".jefe_disponible");
        return total;
    }

    public void registrarJefe(EstadoJuego estado, Region region) {
        if (region != Region.BRANAS_HUNDIDAS && region != Region.CAMINO_DE_LOS_DIFUNTOS)
            throw new IllegalArgumentException("La región no pertenece al capítulo II");
        String id = region == Region.BRANAS_HUNDIDAS ? "cap2.lavandeira_derrotada" : "cap2.hospitalario_derrotado";
        estado.getProgresoCampana().registrarDecision(id);
        estado.getProgresoCampana().registrarDecision(region == Region.BRANAS_HUNDIDAS
                ? "cap2.libro.paginas_aldara" : "cap2.libro.paginas_hospital");
    }

    public boolean puedeCerrar(EstadoJuego estado) {
        return estado.getProgresoCampana().haDecidido("cap2.lavandeira_derrotada")
                && estado.getProgresoCampana().haDecidido("cap2.hospitalario_derrotado");
    }

    public void completar(EstadoJuego estado, Verdad verdad) {
        if (!puedeCerrar(estado)) throw new IllegalStateException("El Libro de los Nombres sigue incompleto");
        estado.getProgresoCampana().registrarDecision("cap2.verdad." + verdad.name().toLowerCase());
        estado.getProgresoCampana().registrarDecision("cap2.matanza_revelada");
        estado.getProgresoCampana().avanzarA(CapituloCampana.DEUDA_DE_LOS_VIVOS);
        estado.getRegistroCampana().anotar("Capítulo II: la verdad sobre los peregrinos se resolvió mediante "
                + verdad.name().toLowerCase() + ".");
    }
}
