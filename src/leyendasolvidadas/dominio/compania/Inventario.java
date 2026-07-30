package leyendasolvidadas.dominio.compania;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.eventos.*;

import java.util.ArrayList;
import java.util.List;

/** Mochila del heroe: objetos y oro. */
public class Inventario {
    public static final int CAPACIDAD = 24;
    private final List<Item> items = new ArrayList<>();
    private int oro = 0;

    public int getOro() { return oro; }
    public void ganarOro(int cantidad) { oro += cantidad; }
    /** Intenta gastar oro. @return true si habia suficiente. */
    public boolean gastarOro(int cantidad) {
        if (oro < cantidad) return false;
        oro -= cantidad; return true;
    }
    public List<Item> getItems() { return items; }

    /** Restaura el contenido desde un formato de guardado externo. */
    public void restaurar(int oro, List<Item> nuevosItems) {
        this.oro = Math.max(0, oro);
        items.clear();
        for (Item item : nuevosItems) if (items.size() < CAPACIDAD) items.add(item);
    }

    public boolean anadir(Item item) {
        if (item == null) return false;
        if (items.size() >= CAPACIDAD) {
            BusEventos.publicar("La mochila esta llena. " + item.getNombre() + " se queda atras.", TipoMensaje.PELIGRO);
            return false;
        }
        items.add(item);
        return true;
    }

}
