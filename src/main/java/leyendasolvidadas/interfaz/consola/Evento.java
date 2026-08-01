package leyendasolvidadas.interfaz.consola;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.infraestructura.*;
import leyendasolvidadas.interfaz.consola.*;

/** Resuelve los hallazgos aleatorios de una expedición. */
public final class Evento {
    private Evento() {}

    /** Elige y resuelve un hallazgo al azar. */
    public static void curioAleatorio(Personaje h, Expedicion exp) {
        switch (h.getAzar().entre(0, 4)) {
            case 0: altar(h); break;
            case 1: osario(h, exp); break;
            case 2: fuente(h); break;
            case 3: ahorcado(h, exp); break;
            default: hoguera(h, exp); break;
        }
    }

    public static void curioBosque(Personaje h, Expedicion exp) {
        if (h.getAzar().probabilidad(50)) {
            UI.seccion("UNA VOZ ENTRE LOS ÁRBOLES");
            UI.log("Desde la niebla, alguien imita la voz de un compañero y pide que os separéis.");
            System.out.println("  1. Responder a la voz   2. Atar al grupo con una cuerda y seguir");
            if (UI.leerOpcion(1, 2) == 1) {
                h.sufrirEstres(14);
                UI.log(UI.pintar("La voz responde con tu propio nombre (+14 estrés).", UI.ROJO));
            } else {
                h.aliviarEstres(5); UI.log(UI.pintar("Nadie abandona la formación (-5 estrés).", UI.VERDE));
            }
        } else {
            UI.seccion("LAS SOGAS SIN CUERPO");
            UI.log("Docenas de sogas vacías oscilan aunque no sopla viento.");
            System.out.println("  1. Cortarlas   2. Pasar por debajo sin tocarlas");
            if (UI.leerOpcion(1, 2) == 1) {
                h.getInventario().ganarOro(25); h.sufrirEstres(8);
                UI.log(UI.pintar("Dentro de un nudo hallas 25 reales y un diente humano.", UI.AMARILLO));
            } else UI.log("Las sogas giran para seguiros con sus nudos.");
        }
    }

    public static void curioBranas(Personaje h, Expedicion exp) {
        UI.seccion("UN REFLEJO QUE NO ES TUYO");
        UI.log("En el agua aparece tu rostro ahogado, con una página del Libro entre los dientes.");
        System.out.println("  1. Meter la mano en el agua   2. Romper el reflejo con una piedra");
        if (UI.leerOpcion(1, 2) == 1) {
            if (h.getAzar().probabilidad(55)) { h.aliviarEstres(8); exp.subirLuz(15); UI.log(UI.pintar("Rescatas una vela de cobre (+15 luz).", UI.VERDE)); }
            else { h.aplicarEfecto(TipoEfecto.VENENO, 3, 3); UI.log(UI.pintar("Algo te muerde bajo el agua.", UI.ROJO)); }
        } else { h.sufrirEstres(6); UI.log("Cada fragmento sigue mirándote."); }
    }

    public static void curioCamino(Personaje h, Expedicion exp) {
        UI.seccion("UN NOMBRE BAJO LA CENIZA");
        UI.log("Las piedras forman letras que desaparecen cuando intentas leerlas.");
        System.out.println("  1. Pronunciar el nombre   2. Copiarlo sin decirlo");
        if (UI.leerOpcion(1, 2) == 1) { h.sufrirEstres(12); h.curar(18); UI.log(UI.pintar("Un muerto recuerda quién fue (+18 PV, +12 estrés).", UI.MAGENTA)); }
        else { h.aliviarEstres(5); UI.log(UI.pintar("La tinta conserva lo que la voz habría perdido.", UI.CIAN)); }
    }
    public static void curioMinas(Personaje h, Expedicion exp) {
        UI.seccion("UNA VETA QUE LATE"); UI.log("El mineral palpita como una vena bajo la roca.");
        System.out.println("  1. Extraerlo   2. Sellarlo con sal");
        if (UI.leerOpcion(1,2)==1) { h.getInventario().ganarOro(45); h.recibirDanio(7,true); UI.log(UI.pintar("Obtienes mineral por valor de 45 reales, pero la roca muerde.", UI.AMARILLO)); }
        else { h.aliviarEstres(8); UI.log(UI.pintar("El latido se detiene.", UI.VERDE)); }
    }
    public static void curioPazo(Personaje h, Expedicion exp) {
        UI.seccion("UN RETRATO SIN ROSTRO"); UI.log("Bajo la pintura raspada aparece la lista de quienes cobraron tras el incendio.");
        System.out.println("  1. Arrancar el lienzo   2. Dejar una copia falsa");
        if (UI.leerOpcion(1,2)==1) { h.sufrirEstres(8); UI.log(UI.pintar("Los ojos borrados os siguen por el corredor.", UI.MAGENTA)); }
        else { h.aliviarEstres(5); UI.log(UI.pintar("Los criados tardarán en descubrir el engaño.", UI.CIAN)); }
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
                int oro = h.getAzar().entre(20, 60);
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
            if (h.getAzar().probabilidad(55)) {
                Item premio = h.getAzar().probabilidad(50) ? Amuleto.aleatorio(exp.getBonusRareza(), h.getAzar())
                        : Arma.aleatoria(h.getNivel(), exp.getBonusRareza(), h.getAzar());
                UI.log("Encuentras " + UI.item(premio) + ".");
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
            if (h.getAzar().probabilidad(60)) {
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
            int oro = h.getAzar().entre(15, 45);
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
        if (h.getAzar().probabilidad(40)) {
            h.getInventario().anadir(Pocion.antorcha());
            UI.log("Ademas guardas una antorcha de repuesto.");
        }
    }

    /** Resuelve una trampa de pasillo. */
    public static void trampa(Personaje h) {
        UI.log(UI.pintar("¡CLAC! Un cepo oculto salta bajo tus pies...", UI.ROJO));
        if (h.getAzar().probabilidad(h.esquivaActual() + 25)) {
            UI.log(UI.pintar("Lo esquivas con un quiebro. El corazon a mil.", UI.VERDE));
            h.sufrirEstres(3);
        } else {
            double d = h.recibirDanio(8 + h.getNivel() * 2, true);
            h.sufrirEstres(8);
            if (h.getAzar().probabilidad(40)) h.aplicarEfecto(TipoEfecto.SANGRADO, 2, 2 + h.getNivel() / 2.0);
            UI.log(UI.pintar("El hierro te muerde: " + (int) d + " de danio (+8 estres).", UI.ROJO));
        }
    }

    /** Abre un cofre y resuelve su posible contenido. */
    public static java.util.List<Enemigo> cofre(Personaje h, Expedicion exp, boolean grande) {
        UI.seccion(grande ? "UN ARCON FERRADO" : "UN COFRE POLVORIENTO");
        if (!grande && h.getAzar().probabilidad(12)) {
            UI.log(UI.pintar("¡El cofre abre unas fauces llenas de dientes!", UI.ROJO));
            Enemigo mimico = new Enemigo("Arca Mimica", h.getNivel() + 1, true);
            mimico.anadirMovimiento(MovimientoEnemigo.golpe("Dentellada", 1.2, 3));
            mimico.anadirMovimiento(new MovimientoEnemigo("Chirrido", 0, null, 0, 0, 0, 9, 1,
                    new int[]{1, 2, 3}, false, false));
            return java.util.List.of(mimico);
        }
        int oro = (grande ? h.getAzar().entre(60, 120) : h.getAzar().entre(20, 50)) + h.getNivel() * 5;
        h.getInventario().ganarOro(oro);
        UI.log(UI.pintar("+" + oro + " reales.", UI.AMARILLO));
        if (grande || h.getAzar().probabilidad(60)) {
            Item it;
            switch (h.getAzar().entre(0, 3)) {
                case 0: it = Arma.aleatoria(h.getNivel(), exp.getBonusRareza() + (grande ? 15 : 0), h.getAzar()); break;
                case 1: it = Armadura.aleatoria(h.getNivel(), exp.getBonusRareza() + (grande ? 15 : 0), h.getAzar()); break;
                case 2: it = Amuleto.aleatorio(exp.getBonusRareza() + (grande ? 15 : 0), h.getAzar()); break;
                default: it = h.getAzar().probabilidad(50) ? Pocion.vida() : Pocion.laudano();
            }
            UI.log("Dentro hallas " + UI.item(it) + " " + UI.pintar("(" + it.descripcion() + ")", UI.TENUE));
            h.getInventario().anadir(it);
        }
        return null;
    }
}
