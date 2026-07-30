package leyendasolvidadas.dominio.misiones;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.dominio.eventos.*;

/** Mision de exterminio: acabar con N criaturas, las que sean. */
public class MisionCaza extends Mision {
    private final int objetivo;
    private int muertes = 0;

    public MisionCaza(Dificultad dif, int objetivo, int oro, int xp, Item item) {
        super("Batida de Caza", "Exterminar " + objetivo + " criaturas del paraje.", dif, oro, xp, item);
        this.objetivo = objetivo;
    }
    @Override public void notificarMuerte(Enemigo e) {
        if (estaCompletada()) return;
        muertes++;
        BusEventos.publicar("Batida: " + muertes + "/" + objetivo + " presas cobradas.", TipoMensaje.PROGRESO);
        if (muertes >= objetivo) completar();
    }
    @Override public String progreso() { return "Presas: " + muertes + "/" + objetivo; }
}
