package leyendasolvidadas.dominio.misiones;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.dominio.eventos.*;

/** Mision de rescate: recuperar una reliquia del fondo del paraje y volver a la entrada. */
public class MisionReliquia extends Mision {
    private boolean recogida = false;
    private boolean entregada = false;

    public MisionReliquia(Dificultad dif, int oro, int xp, Item item) {
        this(MisionId.RELIQUIA_PERDIDA, "La Reliquia Perdida",
                "Recuperar la reliquia robada y regresar a la entrada.", dif, oro, xp, item);
    }
    public MisionReliquia(MisionId id, String nombre, String descripcion, Dificultad dif,
                          int oro, int xp, Item item) {
        super(id, nombre, descripcion, dif, oro, xp, item);
    }
    @Override public boolean requiereObjetivo() { return true; }
    @Override public void notificarObjetivo() {
        if (!recogida) {
            recogida = true;
            BusEventos.publicar("Tomas la reliquia sagrada. ¡Vuelve a la ENTRADA (E) para consagrarla!", TipoMensaje.RECOMPENSA);
        }
    }
    /** Llamado al pisar la entrada. */
    public void notificarEntrada() {
        if (recogida && !entregada) { entregada = true; completar(); }
    }
    @Override public String progreso() {
        return recogida ? "Reliquia en mano: vuelve a la entrada" : "Busca la camara marcada (♦)";
    }
}
