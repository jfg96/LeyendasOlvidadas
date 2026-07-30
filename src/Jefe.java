import java.util.ArrayList;
import java.util.List;

/**
 * Jefe: enemigo mayor con dos fases. Al caer a media vida se enfurece,
 * limpia sus males y desbloquea movimientos nuevos.
 */
public class Jefe extends Enemigo {
    private boolean faseDos = false;
    private final List<MovimientoEnemigo> movimientosFase2 = new ArrayList<>();
    private final String gritoFase2;

    public Jefe(String nombre, int nivel, String gritoFase2) {
        super(nombre, nivel, true);
        this.gritoFase2 = gritoFase2;
        setVidaMaxBase(getVidaMaxBase() * 2);
        setVida(getVidaMax());
    }
    public void anadirMovimientoFase2(MovimientoEnemigo m) { movimientosFase2.add(m); }
    public boolean enFaseDos() { return faseDos; }

    /** Comprueba el cambio de fase. @return true si acaba de entrar en fase 2. */
    public boolean comprobarFase() {
        if (!faseDos && getVida() <= getVidaMax() * 0.5) {
            faseDos = true;
            limpiarEfectosNegativos();
            for (MovimientoEnemigo m : movimientosFase2) anadirMovimiento(m);
            System.out.println(UI.pintar("\n  ╔═══ ¡" + getNombre().toUpperCase() + " DESATA SU FURIA! ═══╗", UI.ROJO));
            System.out.println(UI.pintar("  \"" + gritoFase2 + "\"", UI.MAGENTA));
            return true;
        }
        return false;
    }
    @Override public double multFase() { return faseDos ? 1.3 : 1.0; }
    @Override public Item soltarBotin(Personaje jugador, double multBotin, int bonusRareza) {
        // Los jefes siempre sueltan una reliquia notable.
        return Rng.prob(50) ? Arma.aleatoria(getNivel(), 25) : Amuleto.aleatorio(25);
    }
}
