package leyendasolvidadas.dominio.mundo;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.infraestructura.*;
import leyendasolvidadas.interfaz.consola.*;

/**
 * Curiosidades: hallazgos interactivos de las expediciones. Cada uno plantea
 * una eleccion con riesgo y recompensa, al estilo de los "curios".
 */
public final class Evento {
    private Evento() {}

    /** Lanza una curiosidad aleatoria sobre el heroe. */
    public static void curioAleatorio(Personaje h, Expedicion exp) {
        switch (Rng.entre(0, 4)) {
            case 0: altar(h); break;
            case 1: osario(h, exp); break;
            case 2: fuente(h); break;
            case 3: ahorcado(h, exp); break;
            default: hoguera(h, exp); break;
        }
    }

    private static void altar(Personaje h) {
        UI.seccion("UN ALTAR OLVIDADO");
        UI.log("Una talla de la Virgen cubierta de polvo. Las velas llevan anos apagadas.");
        System.out.println("  1. Rezar en silencio   2. Profanar el cepillo   3. Seguir de largo");
        switch (UI.leerOpcion(1, 3)) {
            case 1:
                h.aliviarEstres(20);
                UI.log(UI.pintar("Una paz antigua te envuelve (-20 estres).", UI.VERDE));
                break;
            case 2:
                int oro = Rng.entre(20, 60);
                h.getInventario().ganarOro(oro);
                h.sufrirEstres(15);
                UI.log(UI.pintar("+" + oro + " reales... pero la culpa pesa (+15 estres).", UI.AMARILLO));
                break;
            default: UI.log("Te santiguas y sigues tu camino.");
        }
    }

    private static void osario(Personaje h, Expedicion exp) {
        UI.seccion("UN OSARIO REMOVIDO");
        UI.log("Huesos apilados con extrano cuidado. Algo brilla entre las costillas.");
        System.out.println("  1. Rebuscar entre los huesos   2. Dejarlos descansar");
        if (UI.leerOpcion(1, 2) == 1) {
            if (Rng.prob(55)) {
                Item premio = Rng.prob(50) ? Amuleto.aleatorio(exp.getBonusRareza())
                        : Arma.aleatoria(h.getNivel(), exp.getBonusRareza());
                UI.log("Encuentras " + premio.nombreColoreado() + ".");
                h.getInventario().anadir(premio);
            } else {
                UI.log(UI.pintar("Los huesos CHILLAN. Retrocedes horrorizado.", UI.ROJO));
                h.sufrirEstres(18);
            }
        } else UI.log("Los muertos agradecen tu respeto.");
    }

    private static void fuente(Personaje h) {
        UI.seccion("UNA FUENTE ENCANTADA");
        UI.log("Agua negra y quieta. Dicen que las xanas conceden dones... o males.");
        System.out.println("  1. Beber   2. Llenar la cantimplora y marchar");
        if (UI.leerOpcion(1, 2) == 1) {
            if (Rng.prob(60)) {
                h.curar(h.getVidaMax() * 0.35);
                h.setRecurso(h.getRecursoMax());
                UI.log(UI.pintar("El agua sabe a gloria: vida y " + h.nombreRecurso().toLowerCase() + " restaurados.", UI.VERDE));
            } else {
                h.aplicarEfecto(TipoEfecto.VENENO, 3, 3 + h.getNivel() / 2.0);
                UI.log(UI.pintar("El agua estaba corrupta. Te envenenas.", UI.ROJO));
            }
        } else UI.log("Mejor no tentar a las xanas.");
    }

    private static void ahorcado(Personaje h, Expedicion exp) {
        UI.seccion("UN CUERPO EN EL ARBOL");
        UI.log("Un desdichado cuelga de una rama. Su zurron aun parece lleno.");
        System.out.println("  1. Registrar el zurron   2. Descolgarlo y darle sepultura");
        if (UI.leerOpcion(1, 2) == 1) {
            int oro = Rng.entre(15, 45);
            h.getInventario().ganarOro(oro);
            h.getInventario().anadir(Pocion.vida());
            h.sufrirEstres(10);
            UI.log(UI.pintar("+" + oro + " reales y una pocion... su rostro te perseguira (+10 estres).", UI.AMARILLO));
        } else {
            h.aliviarEstres(12);
            UI.log(UI.pintar("Cavas una fosa humilde. Tu conciencia descansa (-12 estres).", UI.VERDE));
        }
    }

    private static void hoguera(Personaje h, Expedicion exp) {
        UI.seccion("UNA HOGUERA RECIENTE");
        UI.log("Brasas aun tibias. Quien acampara aqui dejo lena y trapos de brea.");
        exp.subirLuz(25);
        UI.log(UI.pintar("Avivas tu antorcha con las brasas (+25 de luz).", UI.AMARILLO));
        if (Rng.prob(40)) {
            h.getInventario().anadir(Pocion.antorcha());
            UI.log("Ademas guardas una antorcha de repuesto.");
        }
    }

    /** Trampa de pasillo: se intenta esquivar, si no castiga cuerpo y mente. */
    public static void trampa(Personaje h) {
        UI.log(UI.pintar("¡CLAC! Un cepo oculto salta bajo tus pies...", UI.ROJO));
        if (Rng.prob(h.esquivaActual() + 25)) {
            UI.log(UI.pintar("Lo esquivas con un quiebro. El corazon a mil.", UI.VERDE));
            h.sufrirEstres(3);
        } else {
            double d = h.recibirDanio(8 + h.getNivel() * 2, true);
            h.sufrirEstres(8);
            if (Rng.prob(40)) h.aplicarEfecto(TipoEfecto.SANGRADO, 2, 2 + h.getNivel() / 2.0);
            UI.log(UI.pintar("El hierro te muerde: " + (int) d + " de danio (+8 estres).", UI.ROJO));
        }
    }

    /** Cofre de pasillo o de sala del tesoro. Los mimicos existen. */
    public static java.util.List<Enemigo> cofre(Personaje h, Expedicion exp, boolean grande) {
        UI.seccion(grande ? "UN ARCON FERRADO" : "UN COFRE POLVORIENTO");
        if (!grande && Rng.prob(12)) {
            UI.log(UI.pintar("¡El cofre abre unas fauces llenas de dientes!", UI.ROJO));
            Enemigo mimico = new Enemigo("Arca Mimica", h.getNivel() + 1, true);
            mimico.anadirMovimiento(MovimientoEnemigo.golpe("Dentellada", 1.2, 3));
            mimico.anadirMovimiento(new MovimientoEnemigo("Chirrido", 0, null, 0, 0, 0, 9, 1,
                    new int[]{1, 2, 3}, false, false));
            return java.util.List.of(mimico);
        }
        int oro = (grande ? Rng.entre(60, 120) : Rng.entre(20, 50)) + h.getNivel() * 5;
        h.getInventario().ganarOro(oro);
        UI.log(UI.pintar("+" + oro + " reales.", UI.AMARILLO));
        if (grande || Rng.prob(60)) {
            Item it;
            switch (Rng.entre(0, 3)) {
                case 0: it = Arma.aleatoria(h.getNivel(), exp.getBonusRareza() + (grande ? 15 : 0)); break;
                case 1: it = Armadura.aleatoria(h.getNivel(), exp.getBonusRareza() + (grande ? 15 : 0)); break;
                case 2: it = Amuleto.aleatorio(exp.getBonusRareza() + (grande ? 15 : 0)); break;
                default: it = Rng.prob(50) ? Pocion.vida() : Pocion.laudano();
            }
            UI.log("Dentro hallas " + it.nombreColoreado() + " " + UI.pintar("(" + it.descripcion() + ")", UI.TENUE));
            h.getInventario().anadir(it);
        }
        return null;
    }
}
