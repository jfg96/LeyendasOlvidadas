package leyendasolvidadas.dominio.misiones;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.mundo.*;

/** Mision de reconocimiento: visitar casi todas las habitaciones del mapa. */
public class MisionExploracion extends Mision {
    private int visitadas, totales = 1;

    public MisionExploracion(Dificultad dif, int oro, int xp, Item item) {
        this(MisionId.CARTOGRAFIAR_PARAJE, "Cartografiar el Paraje", "Explorar el 90% de las estancias.",
                dif, oro, xp, item);
    }
    public MisionExploracion(MisionId id, String nombre, String descripcion, Dificultad dif,
                             int oro, int xp, Item item) {
        super(id, nombre, descripcion, dif, oro, xp, item);
    }
    private int necesarias() { return (int) Math.ceil(totales * 0.9); }
    @Override public void notificarVisita(int visitadas, int totales) {
        if (estaCompletada()) return;
        this.visitadas = visitadas; this.totales = totales;
        if (visitadas >= necesarias()) completar();
    }
    @Override public String progreso() { return "Estancias: " + visitadas + "/" + necesarias(); }
}
