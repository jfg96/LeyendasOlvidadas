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

/**
 * Coordina el inicio de la partida y el bucle principal del juego.
 *
 * @author jfg96
 */
public class Juego {
    private final RepositorioPartidas repositorioPartidas;
    private final leyendasolvidadas.dominio.eventos.PublicadorEventos eventos;
    private final FuenteAzar azar;
    private EstadoJuego estado;

    public Juego(RepositorioPartidas repositorioPartidas) {
        this(repositorioPartidas, leyendasolvidadas.dominio.eventos.PublicadorEventos.silencioso(),
                FuenteAzar.global());
    }

    public Juego(RepositorioPartidas repositorioPartidas,
                 leyendasolvidadas.dominio.eventos.PublicadorEventos eventos) {
        this(repositorioPartidas, eventos, FuenteAzar.global());
    }

    public Juego(RepositorioPartidas repositorioPartidas,
                 leyendasolvidadas.dominio.eventos.PublicadorEventos eventos, FuenteAzar azar) {
        this.repositorioPartidas = repositorioPartidas;
        this.eventos = eventos;
        this.azar = azar;
    }

    public void iniciarJuego() {
        UI.limpiar();
        System.out.println(UI.pintar(
            "\n   ██╗     ███████╗██╗   ██╗███████╗███╗   ██╗██████╗  █████╗ ███████╗\n" +
            "   ██║     ██╔════╝╚██╗ ██╔╝██╔════╝████╗  ██║██╔══██╗██╔══██╗██╔════╝\n" +
            "   ██║     █████╗   ╚████╔╝ █████╗  ██╔██╗ ██║██║  ██║███████║███████╗\n" +
            "   ██║     ██╔══╝    ╚██╔╝  ██╔══╝  ██║╚██╗██║██║  ██║██╔══██║╚════██║\n" +
            "   ███████╗███████╗   ██║   ███████╗██║ ╚████║██████╔╝██║  ██║███████║\n" +
            "   ╚══════╝╚══════╝   ╚═╝   ╚══════╝╚═╝  ╚═══╝╚═════╝ ╚═╝  ╚═╝╚══════╝", UI.AMARILLO));
        System.out.println(UI.pintar("                    O L V I D A D A S  —  La Compania", UI.MAGENTA));
        System.out.println(UI.pintar("\n   La niebla ha devorado los caminos. Las campanas doblan solas.\n" +
                "   Alguien tiene que llevar la vela... y devolverla.\n", UI.TENUE));

        if (repositorioPartidas.existePartida()) {
            System.out.println("  1. Continuar partida\n  2. Nueva partida");
            if (UI.leerOpcion(1, 2) == 1) estado = repositorioPartidas.cargar();
        }
        if (estado == null) {
            estado = new EstadoJuego();
            estado.configurarEventos(eventos);
            estado.configurarAzar(azar);
            estado.setJugador(crearPersonaje());
            estado.renovarHerreria();
            estado.renovarContratacion();
        }
        estado.configurarEventos(eventos);
        estado.configurarAzar(azar);
        UI.pausa();
        new PrologoConsola(repositorioPartidas).jugar(estado);
        new CapituloUnoConsola(repositorioPartidas).presentarSiPendiente(estado);
        bucle();
    }

    private Personaje crearPersonaje() {
        UI.seccion("ELIGE TU LEYENDA");
        System.out.println("  1. " + UI.pintar("Alabardero", UI.ROJO) + " — Veterano de la Vieja Guardia. Acero, aguante y temple.");
        System.out.println("  2. " + UI.pintar("Animero", UI.AZUL) + "    — Llama a las animas con su campanilla. Fragil, letal, roba la esencia.");
        System.out.println("  3. " + UI.pintar("Bandolero", UI.VERDE) + "  — Forajido de la Sierra. Rapido, critico y escurridizo.");
        System.out.println("  4. " + UI.pintar("Meiga", UI.MAGENTA) + "      — Curandera. Ensalmos que regeneran y calman; drena la esencia.");
        System.out.println("  5. " + UI.pintar("Montero", UI.CIAN) + "    — Ballestero de las branas. Artilleria de retaguardia y trampas.");
        System.out.println("  6. " + UI.pintar("Gaitero", UI.AMARILLO) + "    — Juglar de romerias. Envalentona, calma el horror y debilita.");
        System.out.println("  7. " + UI.pintar("Lobishome", UI.ROJO) + "  — Bestia de niebla. Desangra, devora y se enfurece. Mata o cae.");
        System.out.println("  8. " + UI.pintar("Zahori", UI.AZUL) + "     — Vidente de presagios. Debilita, aturde y marca a la horda.");
        System.out.println("  9. " + UI.pintar("Fraile", UI.VERDE) + "     — Exorcista errante. Aturde, quema y se ampara tras la fe.");
        int clase = UI.leerOpcion(1, 9);
        String nombre = UI.leerTexto("¿Tu nombre, forastero?");
        Personaje p = FabricaHeroes.crear(clase, nombre);
        p.setPersonalidadMecanica(RasgoMecanico.TEMPLE_DE_HIERRO, DefectoMecanico.DESCONFIANZA);
        // Permite contratar dos compañeros y comprar provisiones al comenzar.
        p.getInventario().ganarOro(180);
        p.getInventario().anadir(Pocion.vida());
        p.getInventario().anadir(Pocion.antorcha());
        UI.log(UI.pintar("\nBienvenido a Valdesombra, " + nombre + ". Que la vela no se te apague.", UI.CIAN));
        return p;
    }

    private void bucle() {
        Aldea aldea = new Aldea(estado, repositorioPartidas);
        while (true) {
            new CapituloDosConsola(repositorioPartidas).presentarSiPendiente(estado);
            new CapituloTresConsola(repositorioPartidas).presentarSiPendiente(estado);
            new CapituloCuatroConsola(repositorioPartidas).presentarSiPendiente(estado);
            new CapituloCincoConsola(repositorioPartidas).presentarSiPendiente(estado);
            Expedicion exp = aldea.bucle();
            estado = aldea.getEstado();
            if (exp == null) {
                UI.log("La aldea te vera volver. O eso espera.");
                return;
            }
            Mision mision = exp.getGestor().getMision();
            Expedicion.Resultado r = exp.explorar();
            resolverVuelta(r, mision);
            if (estado.isCampanaGanada() && mision instanceof MisionJefe && ((MisionJefe) mision).esFinal()) {
                new CapituloCincoConsola(repositorioPartidas).mostrarEpilogo(estado);
            }
        }
    }

    private void resolverVuelta(Expedicion.Resultado r, Mision mision) {
        UI.limpiar();
        switch (r) {
            case EXITO -> UI.titulo("REGRESO TRIUNFAL");
            case ABANDONO -> UI.titulo("RETIRADA");
            case MUERTE -> UI.titulo("TE ARRASTRAN DE VUELTA");
        }
        ResultadoExpedicion resultado = switch (r) {
            case EXITO -> ResultadoExpedicion.VICTORIA;
            case ABANDONO -> ResultadoExpedicion.ABANDONO;
            case MUERTE -> ResultadoExpedicion.DERROTA;
        };
        ServicioResolucionExpedicion.Resolucion resolucion =
                new ServicioResolucionExpedicion().resolver(estado, mision, resultado);

        switch (r) {
            case EXITO: {
                UI.log(UI.pintar("Cobras el encargo: +" + resolucion.oroRecibido() + " reales.", UI.AMARILLO));
                if (resolucion.itemRecibido() != null) {
                    UI.log("Te entregan ademas " + UI.item(resolucion.itemRecibido()) + ".");
                }
                if (resolucion.progresoCapituloUno() > 0) {
                    UI.log(UI.pintar("El Bosque de los Ahorcados recuerda vuestro paso ("
                            + resolucion.progresoCapituloUno() + "/3).", UI.CIAN));
                    if (resolucion.progresoCapituloUno() == 3)
                        UI.log(UI.pintar("En una soga encontráis el mismo símbolo que llevaba Lúa: tres caminantes sin rostro.", UI.MAGENTA));
                }
                if (resolucion.progresoCapituloDos() > 0) UI.log(UI.pintar("Pistas recuperadas en " + mision.getRegion().getNombre()
                        + " (" + resolucion.progresoCapituloDos() + "/2).", UI.CIAN));
                if (resolucion.progresoCapituloTres() > 0) UI.log(UI.pintar("Pruebas recuperadas en " + mision.getRegion().getNombre()
                        + " (" + resolucion.progresoCapituloTres() + "/2).", UI.CIAN));
                if (resolucion.fragmentosLibro() > 0) UI.log(UI.pintar("Testimonios del Libro reconstruidos ("
                        + resolucion.fragmentosLibro() + "/3).", UI.CIAN));
                if (resolucion.requiereFinal())
                    new CapituloCincoConsola(repositorioPartidas).resolverFinal(estado);
                if (resolucion.requiereDesenlacePersonal() && mision instanceof MisionPersonal personal)
                    new MisionesPersonalesConsola(repositorioPartidas).resolver(estado, personal.getMercenario());
                if (resolucion.requiereCierreCapituloUno()) cerrarCapituloUno();
                if (resolucion.requiereCierreCapituloDos())
                    new CapituloDosConsola(repositorioPartidas).cerrar(estado);
                if (resolucion.requiereCierreCapituloTres())
                    new CapituloTresConsola(repositorioPartidas).cerrar(estado);
                if (resolucion.requiereCierreCapituloCuatro())
                    new CapituloCuatroConsola(repositorioPartidas).cerrar(estado);
                break;
            }
            case ABANDONO:
                UI.log("Vuelves con las manos casi vacias y la mirada baja. Habra otra semana.");
                break;
            case MUERTE: {
                UI.log(UI.pintar("Unos carboneros te encuentran medio muerto en el camino.", UI.ROJO));
                UI.log(UI.pintar("Los fisicos de la aldea cobran caro: pierdes "
                        + resolucion.oroPerdido() + " reales.", UI.ROJO));
                break;
            }
        }
        UI.pausa();
    }

    private void cerrarCapituloUno() {
        UI.seccion("INÉS, LA DESMEMORIADA");
        UI.log("Al caer el rey, una joven peregrina sale de entre las raíces. No recuerda su nombre,");
        UI.log("pero reconoce el símbolo y oye a los Sin Rostro caminar hacia las Brañas.");
        System.out.println("  1. Protegerla bajo la custodia de la compañía.");
        System.out.println("  2. Interrogarla antes de llevarla a Valdesombra.");
        System.out.println("  3. Confiarla a Padre Tomé y observar su reacción.");
        ServicioCapituloUno.ActitudInes actitud = UI.elegirEnum(ServicioCapituloUno.ActitudInes.class, 1, 3);
        new ServicioCapituloUno().completarBosque(estado, actitud);
        UI.log(UI.pintar("CAPÍTULO I COMPLETADO — Brañas Hundidas y Camino de los Difuntos desbloqueados.", UI.AMARILLO));
    }

}
