package leyendasolvidadas.dominio.objetos;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.mundo.*;


/** Clase base de todos los objetos del juego. */
public abstract class Item {
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
    /** Descripcion corta de sus propiedades. */
    public abstract String descripcion();
}
