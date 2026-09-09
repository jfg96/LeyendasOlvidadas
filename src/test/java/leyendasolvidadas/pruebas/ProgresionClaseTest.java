package leyendasolvidadas.pruebas;

import leyendasolvidadas.dominio.combate.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Fija en el tiempo la progresión por clase extraída al refactorizar los héroes. */
class ProgresionClaseTest {

    @Test
    void cadaClaseMantieneSuCrecimientoYDanioBase() {
        comprobarCrecimiento(new Alabardero("Aldo", null), 22, 2, 10, 14);
        comprobarCrecimiento(new Animero("Nog"), 12, 0, 20, 11);
        comprobarCrecimiento(new Bandolero("Rua", null), 16, 0, 15, 11);
        comprobarCrecimiento(new Fraile("Xan"), 18, 2, 12, 11);
        comprobarCrecimiento(new Gaitero("Bieito"), 16, 0, 14, 11);
        comprobarCrecimiento(new Lobishome("Feroz"), 18, 0, 12, 13);
        comprobarCrecimiento(new Meiga("Bel"), 14, 0, 18, 10);
        comprobarCrecimiento(new Montero("Caz"), 15, 0, 15, 12);
        comprobarCrecimiento(new Zahori("Ou"), 12, 0, 18, 10);
    }

    private static void comprobarCrecimiento(Personaje heroe, int crecimientoVida, int crecimientoDefensa,
                                             int crecimientoRecurso, double danioBase) {
        heroe.setArma(null);
        double vida = heroe.getVidaMaxBase();
        int defensa = heroe.getDefensaBase();
        double recurso = heroe.getRecursoMax();

        heroe.subirNivel();

        assertEquals(vida + crecimientoVida, heroe.getVidaMaxBase(), "crecimiento de vida");
        assertEquals(defensa + crecimientoDefensa, heroe.getDefensaBase(), "crecimiento de defensa");
        assertEquals(recurso + crecimientoRecurso, heroe.getRecursoMax(), "crecimiento de recurso");
        assertEquals(danioBase + 3.0 * heroe.getNivel(), heroe.ataqueBase(), 0.0001,
                "el danio de clase crece 3 por nivel sin equipo");
    }

    @Test
    void laProgresionVaciaNoCreaCrecimiento() {
        Enemigo enemigo = new Enemigo("Sin clase", 3, false);
        double maxVida = enemigo.getVidaMaxBase();
        enemigo.subirNivel();
        assertEquals(maxVida, enemigo.getVidaMaxBase());
    }
}