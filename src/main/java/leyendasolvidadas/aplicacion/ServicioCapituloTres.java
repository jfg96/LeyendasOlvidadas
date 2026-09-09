package leyendasolvidadas.aplicacion;

import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.mundo.*;

/** Reglas de La deuda de los vivos y del primer ataque a Valdesombra. */
public final class ServicioCapituloTres extends ServicioCapitulo {
    public enum ActitudGonzalo { ACEPTAR_AYUDA, RECHAZAR, FINGIR_LEALTAD }
    public enum Alianza { PADRE_TOME, ALDARA, DON_GONZALO }
    public enum Defensa { ARCHIVO, ERMITA, HERRERIA }

    @Override
    protected CapituloCampana capitulo() {
        return CapituloCampana.DEUDA_DE_LOS_VIVOS;
    }
    @Override
    protected boolean estaPresentado(EstadoJuego e) {
        return e.getProgresoCampana().haDecidido("cap3.gonzalo_presentado");
    }
    public void conocerGonzalo(EstadoJuego e, ActitudGonzalo actitud) {
        comprobarPresentacion(e, "Don Gonzalo ya se ha presentado");
        e.getProgresoCampana().registrarDecision("cap3.gonzalo." + actitud.name().toLowerCase());
        e.getProgresoCampana().registrarDecision("cap3.gonzalo_presentado");
        if (actitud == ActitudGonzalo.ACEPTAR_AYUDA) e.getCompania().getInventario().ganarOro(100);
    }
    public int registrarVictoria(EstadoJuego e, Region region) {
        if (!enCapitulo(e)
                || (region != Region.MINAS_DE_SAN_LOURENZO && region != Region.PAZO_DE_SOUTOMAIOR)) return 0;
        String z = region == Region.MINAS_DE_SAN_LOURENZO ? "minas" : "pazo";
        int n = (int)e.getProgresoCampana().getDecisiones().stream().filter(x -> x.startsWith("cap3."+z+".victoria.")).count();
        if (n < 2) e.getProgresoCampana().registrarDecision("cap3." + z + ".victoria." + (++n));
        if (n == 2) e.getProgresoCampana().registrarDecision("cap3." + z + ".jefe_disponible");
        return n;
    }
    public void registrarJefe(EstadoJuego e, Region region) {
        e.getProgresoCampana().registrarDecision(region == Region.MINAS_DE_SAN_LOURENZO
                ? "cap3.capataz_derrotado" : "cap3.cripta_soutomaior_abierta");
    }
    public boolean puedeCerrar(EstadoJuego e) {
        return e.getProgresoCampana().haDecidido("cap3.capataz_derrotado")
                && e.getProgresoCampana().haDecidido("cap3.cripta_soutomaior_abierta");
    }
    public void completar(EstadoJuego e, Defensa defensa, Alianza alianza) {
        if (!puedeCerrar(e)) throw new IllegalStateException("La Falange sigue oculta");
        e.getProgresoCampana().registrarDecision("cap3.defensa." + defensa.name().toLowerCase());
        e.getProgresoCampana().registrarDecision("cap3.alianza." + alianza.name().toLowerCase());
        e.getProgresoCampana().registrarDecision("cap3.falange_en_pazo");
        e.getProgresoCampana().registrarDecision("cap3.capitan_en_compana");
        EdificioAldea protegido = switch (defensa) { case ARCHIVO -> EdificioAldea.ARCHIVO; case ERMITA -> EdificioAldea.ERMITA; case HERRERIA -> EdificioAldea.HERRERIA; };
        for (EdificioAldea edificio : new EdificioAldea[]{EdificioAldea.ARCHIVO, EdificioAldea.ERMITA, EdificioAldea.HERRERIA})
            if (edificio != protegido) e.getEstadoAldea().danar(edificio);
        cerrar(e, CapituloCampana.LIBRO_DE_LOS_NOMBRES, "Capítulo III: la compañía defendió "
                + defensa.name().toLowerCase() + " y selló una alianza con "
                + alianza.name().toLowerCase().replace('_', ' ') + ".");
    }
}
