package leyendasolvidadas.dominio.misiones;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.infraestructura.*;
import leyendasolvidadas.interfaz.consola.*;

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
        UI.log(UI.pintar("Batida: " + muertes + "/" + objetivo + " presas cobradas.", UI.CIAN));
        if (muertes >= objetivo) completar();
    }
    @Override public String progreso() { return "Presas: " + muertes + "/" + objetivo; }
}
