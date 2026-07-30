package leyendasolvidadas.dominio.misiones;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.infraestructura.*;
import leyendasolvidadas.interfaz.consola.*;

import java.io.Serializable;

/**
 * Clase base de las misiones de expedicion. Las subclases reaccionan a los
 * eventos del mundo mediante los metodos notificar*.
 */
public abstract class Mision implements Serializable {
    private final String nombre, descripcion;
    private final Dificultad dificultad;
    private final int oroRecompensa, xpRecompensa;
    private final Item itemRecompensa;
    private EstadoMision estado = EstadoMision.EN_CURSO;

    public Mision(String nombre, String descripcion, Dificultad dificultad,
                  int oroRecompensa, int xpRecompensa, Item itemRecompensa) {
        this.nombre = nombre; this.descripcion = descripcion; this.dificultad = dificultad;
        this.oroRecompensa = oroRecompensa; this.xpRecompensa = xpRecompensa;
        this.itemRecompensa = itemRecompensa;
    }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Dificultad getDificultad() { return dificultad; }
    public int getOroRecompensa() { return oroRecompensa; }
    public int getXpRecompensa() { return xpRecompensa; }
    public Item getItemRecompensa() { return itemRecompensa; }
    public EstadoMision getEstado() { return estado; }
    protected void completar() {
        if (estado == EstadoMision.EN_CURSO) {
            estado = EstadoMision.COMPLETADA;
            System.out.println(UI.pintar("\n  ✦ ¡OBJETIVO CUMPLIDO! Puedes volver a la aldea con la cabeza alta. ✦", UI.AMARILLO));
        }
    }
    public void fracasar() { estado = EstadoMision.FRACASADA; }
    public boolean estaCompletada() { return estado == EstadoMision.COMPLETADA; }

    /** Texto de progreso para la interfaz. */
    public abstract String progreso();
    /** Indica si esta mision necesita habitacion OBJETIVO en el mapa. */
    public boolean requiereObjetivo() { return false; }

    // Ganchos de eventos (por defecto no hacen nada)
    public void notificarMuerte(Enemigo e) {}
    public void notificarVisita(int visitadas, int totales) {}
    public void notificarObjetivo() {}
}
