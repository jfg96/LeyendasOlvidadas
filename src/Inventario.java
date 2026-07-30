import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Mochila del heroe: objetos y oro. */
public class Inventario implements Serializable {
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

    public boolean anadir(Item item) {
        if (item == null) return false;
        if (items.size() >= CAPACIDAD) {
            UI.log(UI.pintar("La mochila esta llena. " + item.getNombre() + " se queda atras.", UI.ROJO));
            return false;
        }
        items.add(item);
        return true;
    }

    public void mostrar() {
        UI.seccion("MOCHILA (" + items.size() + "/" + CAPACIDAD + ")  Oro: "
                + UI.pintar(oro + " reales", UI.AMARILLO));
        if (items.isEmpty()) { UI.log(UI.pintar("(vacia)", UI.TENUE)); return; }
        for (int i = 0; i < items.size(); i++) {
            Item it = items.get(i);
            System.out.printf("  %2d. %-34s %s%n", i + 1, it.nombreColoreado(), UI.pintar(it.descripcion(), UI.TENUE));
        }
    }

    /**
     * Menu de uso de la mochila: consumir pociones o equipar piezas.
     * @param exp expedicion actual o null si estamos en la aldea.
     * @return true si se ha consumido un turno/accion.
     */
    public boolean menuUsar(Personaje p, Expedicion exp) {
        mostrar();
        if (items.isEmpty()) return false;
        System.out.println("\n  Elige objeto (0 para cerrar la mochila):");
        int op = UI.leerOpcion(0, items.size());
        if (op == 0) return false;
        Item it = items.get(op - 1);
        if (it instanceof Pocion) {
            if (((Pocion) it).usar(p, exp)) { items.remove(it); return true; }
            return false;
        }
        if (it instanceof Arma) {
            Arma vieja = p.getArma();
            p.setArma((Arma) it); items.remove(it);
            if (vieja != null) items.add(vieja);
            UI.log("Empunas " + it.nombreColoreado() + ".");
            return true;
        }
        if (it instanceof Armadura) {
            Armadura vieja = p.getArmadura();
            p.setArmadura((Armadura) it); items.remove(it);
            if (vieja != null) items.add(vieja);
            UI.log("Te enfundas " + it.nombreColoreado() + ".");
            return true;
        }
        if (it instanceof Amuleto) {
            Amuleto viejo = p.getAmuleto();
            p.setAmuleto((Amuleto) it); items.remove(it);
            if (viejo != null) items.add(viejo);
            UI.log("Te cuelgas " + it.nombreColoreado() + ".");
            return true;
        }
        return false;
    }
}
