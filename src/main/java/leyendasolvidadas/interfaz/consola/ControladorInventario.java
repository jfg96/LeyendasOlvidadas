package leyendasolvidadas.interfaz.consola;

import leyendasolvidadas.dominio.combate.Personaje;
import leyendasolvidadas.dominio.compania.Inventario;
import leyendasolvidadas.dominio.mundo.FuenteLuz;
import leyendasolvidadas.dominio.objetos.*;

/** Presenta y resuelve las decisiones de mochila de la interfaz de consola. */
public final class ControladorInventario {
    private ControladorInventario() {}

    public static void mostrar(Inventario inventario) {
        UI.seccion("MOCHILA (" + inventario.getItems().size() + "/" + Inventario.CAPACIDAD
                + ")  Oro: " + UI.pintar(inventario.getOro() + " reales", UI.AMARILLO));
        if (inventario.getItems().isEmpty()) { UI.log(UI.pintar("(vacia)", UI.TENUE)); return; }
        for (int i = 0; i < inventario.getItems().size(); i++) {
            Item item = inventario.getItems().get(i);
            System.out.printf("  %2d. %-34s %s%n", i + 1, UI.item(item), UI.pintar(item.descripcion(), UI.TENUE));
        }
    }

    public static boolean menuUsar(Inventario inventario, Personaje personaje, FuenteLuz luz) {
        mostrar(inventario);
        if (inventario.getItems().isEmpty()) return false;
        System.out.println("\n  Elige objeto (0 para cerrar la mochila):");
        int opcion = UI.leerOpcion(0, inventario.getItems().size());
        if (opcion == 0) return false;
        Item item = inventario.getItems().get(opcion - 1);
        if (item instanceof Pocion pocion) {
            if (pocion.usar(personaje, luz)) { inventario.getItems().remove(item); return true; }
            return false;
        }
        if (item instanceof Arma arma) {
            Arma anterior = personaje.getArma();
            personaje.setArma(arma); inventario.getItems().remove(item);
            if (anterior != null) inventario.getItems().add(anterior);
            UI.log("Empunas " + UI.item(item) + ".");
            return true;
        }
        if (item instanceof Armadura armadura) {
            Armadura anterior = personaje.getArmadura();
            personaje.setArmadura(armadura); inventario.getItems().remove(item);
            if (anterior != null) inventario.getItems().add(anterior);
            UI.log("Te enfundas " + UI.item(item) + ".");
            return true;
        }
        if (item instanceof Amuleto amuleto) {
            Amuleto anterior = personaje.getAmuleto();
            personaje.setAmuleto(amuleto); inventario.getItems().remove(item);
            if (anterior != null) inventario.getItems().add(anterior);
            UI.log("Te cuelgas " + UI.item(item) + ".");
            return true;
        }
        return false;
    }
}
