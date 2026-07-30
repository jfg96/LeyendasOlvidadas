import java.io.Serializable;

/**
 * Coordina la mision activa de la expedicion y reenvia los eventos del mundo.
 */
public class GestorMisiones implements Serializable {
    private Mision misionActual;

    public void asignar(Mision m) { misionActual = m; }
    public Mision getMision() { return misionActual; }
    public boolean hayMisionCompletada() {
        return misionActual != null && misionActual.estaCompletada();
    }
    public void notificarMuerte(Enemigo e) { if (misionActual != null) misionActual.notificarMuerte(e); }
    public void notificarVisita(int v, int t) { if (misionActual != null) misionActual.notificarVisita(v, t); }
    public void notificarObjetivo() { if (misionActual != null) misionActual.notificarObjetivo(); }
    public void notificarEntrada() {
        if (misionActual instanceof MisionReliquia) ((MisionReliquia) misionActual).notificarEntrada();
    }
    public void mostrarResumen() {
        if (misionActual == null) { UI.log("Sin encargo activo."); return; }
        UI.seccion("ENCARGO: " + misionActual.getNombre() + " [" + misionActual.getDificultad().getTitulo() + "]");
        UI.log(misionActual.getDescripcion());
        UI.log(UI.pintar("Progreso: " + misionActual.progreso(), UI.CIAN));
        UI.log(UI.pintar("Recompensa: " + misionActual.getOroRecompensa() + " reales, "
                + misionActual.getXpRecompensa() + " XP"
                + (misionActual.getItemRecompensa() != null
                    ? ", " + misionActual.getItemRecompensa().getNombre() : ""), UI.AMARILLO));
    }

    /** Genera una mision aleatoria acorde al nivel del heroe. */
    public static Mision generar(int nivelHeroe, Dificultad dif) {
        int niv = nivelHeroe + dif.getNivelExtra();
        int oro = (30 + niv * 12) * (dif.ordinal() + 1);
        int xp = (40 + niv * 20) * (dif.ordinal() + 1);
        Item premio = Rng.prob(50) ? null
                : Rng.prob(50) ? Amuleto.aleatorio(10 + dif.ordinal() * 8)
                : Arma.aleatoria(niv, 10 + dif.ordinal() * 8);
        switch (Rng.entre(0, 3)) {
            case 0: return new MisionCaza(dif, 3 + dif.ordinal() * 2, oro, xp, premio);
            case 1: return new MisionExploracion(dif, oro, xp, premio);
            case 2: return new MisionReliquia(dif, oro, xp, premio);
            default: return new MisionJefe(dif, (int) (oro * 1.4), (int) (xp * 1.4),
                    premio != null ? premio : Amuleto.aleatorio(15), false);
        }
    }
}
