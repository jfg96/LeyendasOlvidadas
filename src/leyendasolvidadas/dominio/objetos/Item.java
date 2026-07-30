package leyendasolvidadas.dominio.objetos;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.infraestructura.*;
import leyendasolvidadas.interfaz.consola.*;

import java.io.Serializable;

/** Clase base de todos los objetos del juego. */
public abstract class Item implements Serializable {
    private final String nombre;
    private final Rareza rareza;
    private final int valorOro;

    public Item(String nombre, Rareza rareza, int valorOro) {
        this.nombre = nombre; this.rareza = rareza;
        this.valorOro = (int) (valorOro * rareza.getMult());
    }
    public String getNombre() { return nombre; }
    public Rareza getRareza() { return rareza; }
    public int getValorOro() { return valorOro; }
    public String nombreColoreado() { return UI.pintar(nombre, rareza.getColor()); }
    /** Descripcion corta de sus propiedades. */
    public abstract String descripcion();
}
