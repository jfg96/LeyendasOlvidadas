package leyendasolvidadas.interfaz.consola;

import java.util.List;
import leyendasolvidadas.aplicacion.VistaCombate;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.Inventario;
import leyendasolvidadas.dominio.mundo.FuenteLuz;

/** Renderizado y decisiones de combate para terminal. */
public class VistaCombateConsola implements VistaCombate {
    @Override public void mostrarInicio(boolean emboscada, List<Enemigo> enemigos) {
        UI.limpiar();
        UI.titulo(emboscada ? "¡TE EMBOSCAN EN LA PENUMBRA!" : "¡COMBATE!");
        for (Enemigo enemigo : enemigos)
            UI.log(UI.pintar((enemigo.esElite() ? "☠ " : "• ") + enemigo.getNombre()
                    + " (niv " + enemigo.getNivel() + ")", enemigo.esElite() ? UI.MAGENTA : UI.RESET));
    }

    @Override public void mostrarEstado(int ronda, int luz, List<Personaje> heroes,
                                        List<Enemigo> enemigos, Personaje actor) {
        UI.limpiar();
        UI.seccion("RONDA " + ronda + "   TURNO DE " + actor.getNombre().toUpperCase()
                + "   Luz: " + luz);
        System.out.println(UI.pintar("  ─── ENEMIGOS ───", UI.ROJO));
        for (int i = 0; i < enemigos.size(); i++) {
            Enemigo enemigo = enemigos.get(i);
            System.out.printf("  [%d] %-24s %s%s%n", i + 1, enemigo.getNombre(),
                    UI.barra("", enemigo.getVida(), enemigo.getVidaMax(), UI.ROJO), enemigo.efectosTexto());
            if (enemigo.getIntencion() != null)
                UI.log(UI.pintar("      Intención: " + enemigo.getIntencion().getNombre(), UI.AMARILLO));
        }
        System.out.println(UI.pintar("\n  ─── COMPANIA ───", UI.CIAN));
        for (int i = 0; i < heroes.size(); i++) {
            Personaje heroe = heroes.get(i);
            System.out.printf("  [%d] %-16s %s  %s%s%n", i + 1, heroe.getNombre(),
                    UI.barra("Vida", heroe.getVida(), heroe.getVidaMax(), heroe.estaVivo() ? UI.VERDE : UI.ROJO),
                    UI.barra("Cordura", heroe.getCordura(), 100, UI.MAGENTA), heroe.efectosTexto());
        }
    }

    @Override public int elegirAccion(Personaje heroe, List<Habilidad> habilidades) {
        for (int i = 0; i < habilidades.size(); i++) {
            Habilidad habilidad = habilidades.get(i);
            String estado = habilidad.getCooldownActual() > 0
                    ? " [enfriando " + habilidad.getCooldownActual() + "]"
                    : heroe.getRecurso() < habilidad.getCoste() ? " [sin recurso]" : "";
            System.out.printf("  %d. %-20s (coste %d)%s  %s%n", i + 1, habilidad.getNombre(),
                    habilidad.getCoste(), UI.pintar(estado, UI.ROJO),
                    UI.pintar(habilidad.getDescripcion(), UI.TENUE));
        }
        System.out.println("  5. Mochila\n  6. Recuperar aliento\n  7. Huir del combate");
        return UI.leerOpcion(1, 7);
    }

    @Override public Personaje elegirAliado(List<Personaje> aliados) {
        System.out.println("  ¿A quien ayudas?");
        for (int i = 0; i < aliados.size(); i++) System.out.println("  " + (i + 1) + ". " + aliados.get(i).getNombre());
        return aliados.get(UI.leerOpcion(1, aliados.size()) - 1);
    }

    @Override public Enemigo elegirEnemigo(List<Enemigo> alcanzables, List<Enemigo> formacion) {
        if (alcanzables.size() == 1) return alcanzables.get(0);
        System.out.println("  ¿A quien golpeas?");
        for (int i = 0; i < alcanzables.size(); i++) {
            Enemigo enemigo = alcanzables.get(i);
            System.out.printf("  %d. [fila %d] %s (%d PV)%n", i + 1,
                    formacion.indexOf(enemigo) + 1, enemigo.getNombre(), (int) enemigo.getVida());
        }
        return alcanzables.get(UI.leerOpcion(1, alcanzables.size()) - 1);
    }

    @Override public boolean usarInventario(Inventario inventario, Personaje personaje, FuenteLuz luz) {
        return ControladorInventario.menuUsar(inventario, personaje, luz);
    }

    @Override public void pausa() { UI.pausa(); }
}
