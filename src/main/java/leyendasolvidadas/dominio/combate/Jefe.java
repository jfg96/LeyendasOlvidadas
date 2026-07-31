package leyendasolvidadas.dominio.combate;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.eventos.*;

import java.util.ArrayList;
import java.util.List;

/** Enemigo con una segunda fase al alcanzar la mitad de vida. */
public class Jefe extends Enemigo {
    private boolean faseDos = false;
    private final List<MovimientoEnemigo> movimientosFase2 = new ArrayList<>();
    private final String gritoFase2;

    public Jefe(String nombre, int nivel, String gritoFase2) {
        super(nombre, nivel, true);
        this.gritoFase2 = gritoFase2;
        // Compensa la ventaja de acciones del grupo de héroes.
        setVidaMaxBase(getVidaMaxBase() * 3);
        setVida(getVidaMax());
    }
    public void anadirMovimientoFase2(MovimientoEnemigo m) { movimientosFase2.add(m); }
    public boolean enFaseDos() { return faseDos; }

    /**
     * Activa la segunda fase cuando corresponde.
     *
     * @return {@code true} si la fase se ha activado en esta llamada
     */
    public boolean comprobarFase() {
        if (!faseDos && getVida() <= getVidaMax() * 0.5) {
            faseDos = true;
            limpiarEfectosNegativos();
            for (MovimientoEnemigo m : movimientosFase2) anadirMovimiento(m);
            BusEventos.publicar("¡" + getNombre().toUpperCase() + " DESATA SU FURIA! \"" + gritoFase2 + "\"", TipoMensaje.HORROR);
            return true;
        }
        return false;
    }
    @Override public double multFase() { return faseDos ? 1.3 : 1.0; }
    @Override public Item soltarBotin(Personaje jugador, double multBotin, int bonusRareza) {
        return Rng.prob(50) ? Arma.aleatoria(getNivel(), 25) : Amuleto.aleatorio(25);
    }
}
