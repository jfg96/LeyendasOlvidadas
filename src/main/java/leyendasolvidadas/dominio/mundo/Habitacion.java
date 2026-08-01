package leyendasolvidadas.dominio.mundo;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.eventos.*;

import java.util.LinkedHashMap;
import java.util.Map;

/** Estancia del mapa de expedicion, con conexiones a otras habitaciones. */
public class Habitacion {
    private final int x, y;
    private final TipoHabitacion tipo;
    private final TipoSala ambiente;
    private boolean visitada = false;
    private boolean conocida = false; // aparece en el mapa como "?"
    private boolean resuelta = false; // su contenido ya se ha gastado
    private final Map<Character, Habitacion> conexiones = new LinkedHashMap<>();
    private transient PublicadorEventos eventos = PublicadorEventos.silencioso();

    public Habitacion(int x, int y, TipoHabitacion tipo) {
        this.x = x; this.y = y; this.tipo = tipo;
        int azar = Rng.entre(0, 9);
        this.ambiente = azar < 2 ? TipoSala.ESCARCHA : azar < 4 ? TipoSala.NIEBLA
                : azar < 5 ? TipoSala.BENDICION : TipoSala.NORMAL;
    }
    public int getX() { return x; }
    public Habitacion configurarEventos(PublicadorEventos eventos) {
        this.eventos = eventos == null ? PublicadorEventos.silencioso() : eventos;
        return this;
    }
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
                eventos.publicar("Un frio sepulcral drena 15 de " + p.nombreRecurso().toLowerCase() + ".", TipoMensaje.PELIGRO);
                break;
            case NIEBLA:
                p.recibirDanio(6, true);
                p.sufrirEstres(3);
                eventos.publicar("Una niebla mefitica te corroe (-6 PV, +3 estres).", TipoMensaje.PELIGRO);
                break;
            case BENDICION:
                p.curar(15);
                p.aliviarEstres(5);
                eventos.publicar("Un rayo de luz te reconforta (+15 PV, -5 estres).", TipoMensaje.EXITO);
                break;
            default: // sin efecto
        }
    }

    /** Simbolo para el minimapa. */
    public String simbolo(Habitacion actual) {
        if (this == actual) return "@";
        if (!conocida) return " ";
        if (!visitada) return "?";
        switch (tipo) {
            case ENTRADA: return "E";
            case OBJETIVO: return "♦";
            case CAMPAMENTO: return "^";
            default: return "·";
        }
    }
}
