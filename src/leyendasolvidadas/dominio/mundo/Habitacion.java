package leyendasolvidadas.dominio.mundo;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.infraestructura.*;
import leyendasolvidadas.interfaz.consola.*;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/** Estancia del mapa de expedicion, con conexiones a otras habitaciones. */
public class Habitacion implements Serializable {
    private final int x, y;
    private final TipoHabitacion tipo;
    private final TipoSala ambiente;
    private boolean visitada = false;
    private boolean conocida = false; // aparece en el mapa como "?"
    private boolean resuelta = false; // su contenido ya se ha gastado
    private final Map<Character, Habitacion> conexiones = new LinkedHashMap<>();

    public Habitacion(int x, int y, TipoHabitacion tipo) {
        this.x = x; this.y = y; this.tipo = tipo;
        int azar = Rng.entre(0, 9);
        this.ambiente = azar < 2 ? TipoSala.ESCARCHA : azar < 4 ? TipoSala.NIEBLA
                : azar < 5 ? TipoSala.BENDICION : TipoSala.NORMAL;
    }
    public int getX() { return x; }
    public int getY() { return y; }
    public TipoHabitacion getTipo() { return tipo; }
    public boolean estaVisitada() { return visitada; }
    public void visitar() { visitada = true; conocida = true; }
    public boolean esConocida() { return conocida; }
    public void descubrir() { conocida = true; }
    public boolean estaResuelta() { return resuelta; }
    public void resolver() { resuelta = true; }
    public Map<Character, Habitacion> getConexiones() { return conexiones; }
    public void conectar(char dir, Habitacion otra) { conexiones.put(dir, otra); }

    /** Efecto ambiental al entrar por primera vez. */
    public void aplicarAmbiente(Personaje p) {
        switch (ambiente) {
            case ESCARCHA:
                p.setRecurso(p.getRecurso() - 15);
                UI.log(UI.pintar("Un frio sepulcral drena 15 de " + p.nombreRecurso().toLowerCase() + ".", UI.CIAN));
                break;
            case NIEBLA:
                p.recibirDanio(6, true);
                p.sufrirEstres(3);
                UI.log(UI.pintar("Una niebla mefitica te corroe (-6 PV, +3 estres).", UI.ROJO));
                break;
            case BENDICION:
                p.curar(15);
                p.aliviarEstres(5);
                UI.log(UI.pintar("Un rayo de luz te reconforta (+15 PV, -5 estres).", UI.VERDE));
                break;
            default: // sin efecto
        }
    }

    /** Simbolo para el minimapa. */
    public String simbolo(Habitacion actual) {
        if (this == actual) return UI.pintar("@", UI.AMARILLO + UI.NEGRITA);
        if (!conocida) return " ";
        if (!visitada) return UI.pintar("?", UI.CIAN);
        switch (tipo) {
            case ENTRADA: return UI.pintar("E", UI.VERDE);
            case OBJETIVO: return UI.pintar("♦", UI.MAGENTA);
            case CAMPAMENTO: return resuelta ? UI.pintar("^", UI.TENUE) : UI.pintar("^", UI.AMARILLO);
            default: return UI.pintar("·", UI.TENUE);
        }
    }
}
