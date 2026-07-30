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
        UI.titulo("COMBATE · RONDA " + ronda);
        UI.turno(actor.getNombre(), actor.getClass().getSimpleName() + " · " + actor.nombreRecurso()
                + " " + (int)actor.getRecurso() + "/" + (int)actor.getRecursoMax() + " · Luz " + luz);
        UI.seccion("ENEMIGOS · las filas determinan el alcance");
        for (int i = 0; i < enemigos.size(); i++) {
            Enemigo enemigo = enemigos.get(i);
            System.out.printf("  [F%d] %-22s %s%s%n", i + 1, enemigo.getNombre(),
                    UI.barra("", enemigo.getVida(), enemigo.getVidaMax(), UI.ROJO), enemigo.efectosTexto());
            if (enemigo.getIntencion() != null)
                UI.log(UI.pintar("     " + iconoIntencion(enemigo.getIntencion()) + " Intención: "
                        + enemigo.getIntencion().getNombre(), UI.AMARILLO));
        }
        UI.seccion("COMPAÑÍA");
        for (int i = 0; i < heroes.size(); i++) {
            Personaje heroe = heroes.get(i);
            String marca = heroe == actor ? UI.pintar("▶", UI.CIAN) : " ";
            System.out.printf("  %s [%d] %-14s %s  %s%s%n", marca, i + 1, heroe.getNombre(),
                    UI.barra("Vida", heroe.getVida(), heroe.getVidaMax(), heroe.estaVivo() ? UI.VERDE : UI.ROJO),
                    UI.barra("Cordura", heroe.getCordura(), 100, UI.MAGENTA), heroe.efectosTexto());
        }
    }

    @Override public int elegirAccion(Personaje heroe, List<Habilidad> habilidades) {
        UI.seccion("ELIGE UNA ACCIÓN PARA " + heroe.getNombre().toUpperCase());
        for (int i = 0; i < habilidades.size(); i++) {
            Habilidad habilidad = habilidades.get(i);
            String estado = habilidad.getCooldownActual() > 0
                    ? " [enfriando " + habilidad.getCooldownActual() + "]"
                    : heroe.getRecurso() < habilidad.getCoste() ? " [sin recurso]" : "";
            String alcance = alcance(habilidad);
            if (estado.isEmpty()) UI.opcion(i + 1, habilidad.getNombre(), "coste " + habilidad.getCoste() + " · " + alcance);
            else UI.opcionDeshabilitada(i + 1, habilidad.getNombre(), estado.trim().replace("[", "").replace("]", ""));
            UI.log(UI.pintar("    " + habilidad.getDescripcion(), UI.TENUE));
        }
        UI.opcion(5, "Mochila", "consume el turno si usas un objeto");
        UI.opcion(6, "Recuperar aliento", "+recurso · +10 % vida · -4 estrés");
        UI.opcion(7, "Huir", "intento conjunto con penalización de estrés");
        return UI.leerOpcion(1, 7);
    }

    @Override public Personaje elegirAliado(List<Personaje> aliados) {
        UI.seccion("ELIGE OBJETIVO ALIADO");
        for (int i = 0; i < aliados.size(); i++) System.out.println("  " + (i + 1) + ". " + aliados.get(i).getNombre());
        return aliados.get(UI.leerOpcion(1, aliados.size()) - 1);
    }

    @Override public Enemigo elegirEnemigo(List<Enemigo> alcanzables, List<Enemigo> formacion) {
        if (alcanzables.size() == 1) {
            UI.log(UI.pintar("Objetivo: " + alcanzables.get(0).getNombre() + " (único al alcance).", UI.CIAN));
            return alcanzables.get(0);
        }
        UI.seccion("ELIGE OBJETIVO ENEMIGO");
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

    private String alcance(Habilidad h) {
        if (h.esSobreSi()) return h.esSobreAliado() ? "aliado" : "propio";
        return (h.esArea() ? "área · " : "") + "filas " + h.filasTexto();
    }

    private String iconoIntencion(MovimientoEnemigo m) {
        if (m.seCura() || m.esSobreSi()) return "◆";
        if (m.getEfecto() == TipoEfecto.VENENO || m.getEfecto() == TipoEfecto.SANGRADO
                || m.getEfecto() == TipoEfecto.QUEMADURA) return "☣";
        if (m.getEstres() > 0) return "◉";
        if (m.getEfecto() != null) return "◇";
        return m.getMultiplicador() >= 1.3 ? "‼" : "⚔";
    }
}
