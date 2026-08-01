package leyendasolvidadas.dominio.misiones;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.dominio.eventos.*;


/** Clase base de las misiones de expedición. */
public abstract class Mision {
    private final MisionId id;
    private final String nombre, descripcion;
    private final Dificultad dificultad;
    private final int oroRecompensa, xpRecompensa;
    private final Item itemRecompensa;
    private EstadoMision estado = EstadoMision.EN_CURSO;
    private Region region;

    public Mision(MisionId id, String nombre, String descripcion, Dificultad dificultad,
                  int oroRecompensa, int xpRecompensa, Item itemRecompensa) {
        if (id == null) throw new IllegalArgumentException("El identificador de misión es obligatorio");
        this.id = id;
        this.nombre = nombre; this.descripcion = descripcion; this.dificultad = dificultad;
        this.oroRecompensa = oroRecompensa; this.xpRecompensa = xpRecompensa;
        this.itemRecompensa = itemRecompensa;
    }
    public MisionId getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Dificultad getDificultad() { return dificultad; }
    public int getOroRecompensa() { return oroRecompensa; }
    public int getXpRecompensa() { return xpRecompensa; }
    public Item getItemRecompensa() { return itemRecompensa; }
    public EstadoMision getEstado() { return estado; }
    public Region getRegion() { return region; }
    public Mision enRegion(Region region) { this.region = region; return this; }
    protected void completar() {
        if (estado == EstadoMision.EN_CURSO) {
            estado = EstadoMision.COMPLETADA;
            BusEventos.publicar("¡OBJETIVO CUMPLIDO! Puedes volver a la aldea con la cabeza alta.", TipoMensaje.RECOMPENSA);
        }
    }
    public void fracasar() { estado = EstadoMision.FRACASADA; }
    public boolean estaCompletada() { return estado == EstadoMision.COMPLETADA; }

    /** Texto de progreso para la interfaz. */
    public abstract String progreso();
    /** Indica si esta mision necesita habitacion OBJETIVO en el mapa. */
    public boolean requiereObjetivo() { return false; }

    // Las subclases sobrescriben únicamente los eventos que necesitan.
    public void notificarMuerte(Enemigo e) {}
    public void notificarVisita(int visitadas, int totales) {}
    public void notificarObjetivo() {}
}
