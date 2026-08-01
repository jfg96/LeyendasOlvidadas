package leyendasolvidadas.dominio.misiones;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.mundo.*;

/** Mision de jefe: dar caza a la bestia que domina el paraje. */
public class MisionJefe extends Mision {
    private final boolean esFinal;
    private boolean jefeMuerto = false;

    public MisionJefe(Dificultad dif, int oro, int xp, Item item, boolean esFinal) {
        this(esFinal ? MisionId.ULTIMA_PROCESION : MisionId.CABEZA_BESTIA,
              esFinal ? "La Ultima Procesion" : "Cabeza de la Bestia",
              esFinal ? "Enfrentarse a la Santa Compania y romper la maldicion."
                      : "Abatir al senor del paraje en su guarida.", dif, oro, xp, item, esFinal);
    }
    public MisionJefe(MisionId id, String nombre, String descripcion, Dificultad dif,
                      int oro, int xp, Item item, boolean esFinal) {
        super(id, nombre, descripcion, dif, oro, xp, item);
        this.esFinal = esFinal;
    }
    public boolean esFinal() { return esFinal; }
    @Override public boolean requiereObjetivo() { return true; }
    @Override public void notificarMuerte(Enemigo e) {
        if (e instanceof Jefe) { jefeMuerto = true; completar(); }
    }
    @Override public String progreso() {
        return jefeMuerto ? "La bestia ha caido" : "La guarida esta marcada (♦)";
    }
}
