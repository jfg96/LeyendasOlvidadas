package leyendasolvidadas.dominio.combate;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;


/** Ataque o accion que puede realizar un enemigo, con peso para la IA. */
public class MovimientoEnemigo {
    final String nombre;
    final double mult;          // multiplicador de dano (0 = no dana)
    final TipoEfecto efecto;    // efecto que puede aplicar (o null)
    final int probEfecto, durEfecto;
    final double potEfecto;
    final int estres;           // estres que inflige al heroe
    final int peso;             // peso para la eleccion aleatoria
    final int[] filasUso;       // filas desde las que es usable
    final boolean seCura;       // el enemigo se cura en lugar de atacar
    final boolean sobreSi;      // aplica el efecto sobre si mismo (buff)

    public MovimientoEnemigo(String nombre, double mult, TipoEfecto efecto, int probEfecto,
                             int durEfecto, double potEfecto, int estres, int peso,
                             int[] filasUso, boolean seCura, boolean sobreSi) {
        this.nombre = nombre; this.mult = mult; this.efecto = efecto; this.probEfecto = probEfecto;
        this.durEfecto = durEfecto; this.potEfecto = potEfecto; this.estres = estres; this.peso = peso;
        this.filasUso = filasUso; this.seCura = seCura; this.sobreSi = sobreSi;
    }
    /** Atajo para golpes simples. */
    public static MovimientoEnemigo golpe(String nombre, double mult, int peso) {
        return new MovimientoEnemigo(nombre, mult, null, 0, 0, 0, 0, peso, new int[]{1,2,3}, false, false);
    }
    public boolean usableDesde(int fila) {
        for (int f : filasUso) if (f == fila) return true;
        return false;
    }
    public String getNombre() { return nombre; }
    public double getMultiplicador() { return mult; }
    public TipoEfecto getEfecto() { return efecto; }
    public int getProbabilidadEfecto() { return probEfecto; }
    public int getDuracionEfecto() { return durEfecto; }
    public double getPotenciaEfecto() { return potEfecto; }
    public int getEstres() { return estres; }
    public boolean seCura() { return seCura; }
    public boolean esSobreSi() { return sobreSi; }
}
