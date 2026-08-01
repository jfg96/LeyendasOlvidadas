package leyendasolvidadas.interfaz.consola;

import leyendasolvidadas.aplicacion.EstadoJuego;
import leyendasolvidadas.aplicacion.ServicioTablonMisiones;
import leyendasolvidadas.dominio.misiones.Mision;
import leyendasolvidadas.dominio.mundo.Dificultad;

import java.util.List;

/** Presenta y confirma los encargos disponibles. */
final class TablonMisionesConsola {
    Mision elegir(EstadoJuego estado) {
        if (!estado.getCompania().estaCompleta()) {
            UI.log(UI.pintar("Necesitas una formacion de tres antes de partir.", UI.ROJO));
            UI.log("Contrata acompanantes y prepara el grupo desde Gestionar compania.");
            UI.pausa();
            return null;
        }
        UI.limpiar();
        UI.seccion("TABLON DE ENCARGOS");
        ServicioTablonMisiones.Tablon tablon = new ServicioTablonMisiones().generar(estado);
        List<Mision> ordinarias = tablon.ordinarias();
        for (int i = 0; i < ordinarias.size(); i++) {
            Mision mision = ordinarias.get(i);
            Dificultad dificultad = mision.getDificultad();
            System.out.printf("  %d. [%s] %-24s %s%n", i + 1,
                    UI.pintar(dificultad.getTitulo(), dificultad == Dificultad.FACIL ? UI.VERDE
                            : dificultad == Dificultad.MEDIA ? UI.AMARILLO : UI.ROJO),
                    mision.getNombre(), UI.pintar(mision.getOroRecompensa() + " reales, "
                            + mision.getXpRecompensa() + " XP", UI.TENUE));
            UI.log(UI.pintar("   " + mision.getDescripcion(), UI.TENUE));
        }
        for (int i = 0; i < tablon.especiales().size(); i++) {
            Mision mision = tablon.especiales().get(i);
            System.out.println(UI.pintar("  " + (i + 4) + ". ☠ " + mision.getNombre().toUpperCase()
                    + " — " + mision.getDescripcion(), UI.MAGENTA));
        }
        System.out.println("  0. Volver a la plaza");
        int opcion = UI.leerOpcion(0, 3 + tablon.especiales().size());
        if (opcion == 0) return null;
        Mision elegida = opcion > 3 ? tablon.especiales().get(opcion - 4) : ordinarias.get(opcion - 1);
        return UI.confirmar("¿Partir hacia '" + elegida.getNombre() + "'?") ? elegida : null;
    }
}
