package leyendasolvidadas.dominio.combate;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;

import java.util.ArrayList;
import java.util.List;

/** Enemigo con movimientos ponderados según su posición. */
public class Enemigo extends Personaje {
    private final double danioBase;
    private final int xpRecompensa;
    private final int oroMin, oroMax;
    private final boolean elite;
    private final List<MovimientoEnemigo> movimientos = new ArrayList<>();
    private int filaPreferida;
    private transient MovimientoEnemigo intencion;

    public Enemigo(String nombre, int nivel, boolean elite) {
        super(nombre, nivel, (30 + 18 * nivel) * (elite ? 1.5 : 1.0),
                1 + nivel / 2, 5, 5, 3, 0, 0);
        this.danioBase = (6 + 3 * nivel) * (elite ? 1.25 : 1.0);
        this.xpRecompensa = (int) (15 * nivel * (elite ? 1.6 : 1.0));
        this.oroMin = 3 + nivel * 2; this.oroMax = 8 + nivel * 4;
        this.elite = elite;
        this.filaPreferida = 1;
    }
    public boolean esElite() { return elite; }
    public int getXpRecompensa() { return xpRecompensa; }
    public int getOro() { return Rng.entre(oroMin, oroMax) * (elite ? 2 : 1); }
    public double getDanioBase() { return danioBase; }
    public void anadirMovimiento(MovimientoEnemigo m) { movimientos.add(m); }
    public void setFilaPreferida(int f) { filaPreferida = f; }
    public int getFilaPreferida() { return filaPreferida; }

    /** Multiplicador de dano (los jefes lo aumentan por fases). */
    public double multFase() { return 1.0; }

    /** IA: elige un movimiento usable desde su fila, ponderado por peso. */
    public MovimientoEnemigo elegirMovimiento(int fila) {
        List<MovimientoEnemigo> usables = new ArrayList<>();
        int total = 0;
        for (MovimientoEnemigo m : movimientos) {
            if (m.usableDesde(fila)) { usables.add(m); total += m.peso; }
        }
        if (usables.isEmpty()) return MovimientoEnemigo.golpe("Zarpazo desesperado", 0.8, 1);
        int tirada = Rng.entre(1, total);
        for (MovimientoEnemigo m : usables) {
            tirada -= m.peso;
            if (tirada <= 0) return m;
        }
        return usables.get(0);
    }
    public void prepararIntencion(int fila) { intencion = elegirMovimiento(fila); }
    public MovimientoEnemigo getIntencion() { return intencion; }
    public MovimientoEnemigo consumirIntencion(int fila) {
        MovimientoEnemigo elegida = intencion != null ? intencion : elegirMovimiento(fila);
        intencion = null; return elegida;
    }

    /**
     * Botin al morir: oro siempre; objeto segun suerte y luz de la expedicion.
     * @param multBotin multiplicador de probabilidad (oscuridad = mas botin).
     * @param bonusRareza mejora de rareza por oscuridad.
     */
    public Item soltarBotin(Personaje jugador, double multBotin, int bonusRareza) {
        double prob = (elite ? 0.65 : 0.30) * multBotin;
        if (Math.random() >= prob) return null;
        int tirada = Rng.entre(1, 100);
        if (tirada <= 30) return jugador instanceof Animero && Rng.prob(50)
                ? Pocion.tonico() : Pocion.vida();
        if (tirada <= 45) return Pocion.antorcha();
        if (tirada <= 55) return Pocion.laudano();
        if (tirada <= 75) return Arma.aleatoria(getNivel(), bonusRareza);
        if (tirada <= 90) return Armadura.aleatoria(getNivel(), bonusRareza);
        return Amuleto.aleatorio(bonusRareza);
    }

    @Override public double ataqueBase() { return danioBase; }
    @Override public String nombreRecurso() { return "-"; }
}
