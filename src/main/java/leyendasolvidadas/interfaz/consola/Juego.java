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
 * Controlador principal (singleton): titulo, creacion del heroe y bucle
 * aldea <-> expedicion hasta romper la maldicion de la Santa Compania.
 *
 * @author Carlos Fernandez Gavino
 * @version 3.0 (Leyendas Olvidadas: La Compania)
 */
public class Juego {
    private final RepositorioPartidas repositorioPartidas;
    private EstadoJuego estado;

    public Juego(RepositorioPartidas repositorioPartidas) {
        this.repositorioPartidas = repositorioPartidas;
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
            estado.setJugador(crearPersonaje());
            estado.renovarHerreria();
            estado.renovarContratacion();
        }
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
        // Capital suficiente para fundar una compania de tres y conservar
        // margen para provisiones o curacion durante la primera semana.
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
            estado.avanzarSemana();
            estado.renovarHerreria();
            estado.renovarContratacion();
            resolverVuelta(r, mision);
            if (estado.isCampanaGanada() && mision instanceof MisionJefe && ((MisionJefe) mision).esFinal()) {
                new CapituloCincoConsola(repositorioPartidas).mostrarEpilogo(estado);
                // La partida continua en modo libre si el jugador quiere.
            }
        }
    }

    private void resolverVuelta(Expedicion.Resultado r, Mision mision) {
        Compania compania = estado.getCompania();
        java.util.List<Personaje> grupo = compania.getFormacionActiva();
        UI.limpiar();
        switch (r) {
            case EXITO: {
                UI.titulo("REGRESO TRIUNFAL");
                estado.registrarVictoria();
                new ServicioCompania().registrarConvivencia(compania, ServicioCompania.ResultadoExpedicion.VICTORIA);
                compania.getInventario().ganarOro(mision.getOroRecompensa());
                UI.log(UI.pintar("Cobras el encargo: +" + mision.getOroRecompensa() + " reales.", UI.AMARILLO));
                for (Personaje heroe : grupo) heroe.ganarExperiencia(mision.getXpRecompensa());
                if (mision.getItemRecompensa() != null) {
                    UI.log("Te entregan ademas " + UI.item(mision.getItemRecompensa()) + ".");
                    compania.getInventario().anadir(mision.getItemRecompensa());
                }
                for (Personaje heroe : grupo) heroe.aliviarEstres(20);
                int victoriasBosque = new ServicioCapituloUno().registrarVictoria(estado, mision.getRegion());
                if (victoriasBosque > 0) {
                    UI.log(UI.pintar("El Bosque de los Ahorcados recuerda vuestro paso ("
                            + victoriasBosque + "/3).", UI.CIAN));
                    if (victoriasBosque == 3)
                        UI.log(UI.pintar("En una soga encontráis el mismo símbolo que llevaba Lúa: tres caminantes sin rostro.", UI.MAGENTA));
                }
                int progresoDos = new ServicioCapituloDos().registrarVictoria(estado, mision.getRegion());
                if (progresoDos > 0) UI.log(UI.pintar("Pistas recuperadas en " + mision.getRegion().getNombre()
                        + " (" + progresoDos + "/2).", UI.CIAN));
                int progresoTres = new ServicioCapituloTres().registrarVictoria(estado, mision.getRegion());
                if (progresoTres > 0) UI.log(UI.pintar("Pruebas recuperadas en " + mision.getRegion().getNombre()
                        + " (" + progresoTres + "/2).", UI.CIAN));
                if (estado.getProgresoCampana().getCapitulo()
                        == leyendasolvidadas.dominio.campana.CapituloCampana.LIBRO_DE_LOS_NOMBRES) {
                    int nombres = new ServicioCapituloCuatro().registrarHallazgo(estado, mision.getRegion());
                    if (nombres > 0) UI.log(UI.pintar("Testimonios del Libro reconstruidos ("
                            + nombres + "/3).", UI.CIAN));
                }
                if (mision instanceof MisionJefe && ((MisionJefe) mision).esFinal())
                    new CapituloCincoConsola(repositorioPartidas).resolverFinal(estado);
                if (mision.getNombre().equals("El rey de las sogas")) cerrarCapituloUno();
                if (mision.getNombre().equals("Los sudarios de Aldara"))
                    new ServicioCapituloDos().registrarJefe(estado, Region.BRANAS_HUNDIDAS);
                if (mision.getNombre().equals("Las puertas del hospital"))
                    new ServicioCapituloDos().registrarJefe(estado, Region.CAMINO_DE_LOS_DIFUNTOS);
                if (new ServicioCapituloDos().puedeCerrar(estado)
                        && estado.getProgresoCampana().getCapitulo() == leyendasolvidadas.dominio.campana.CapituloCampana.CAMINOS_DE_ANIMAS)
                    new CapituloDosConsola(repositorioPartidas).cerrar(estado);
                if (mision.getNombre().equals("La campana del capataz"))
                    new ServicioCapituloTres().registrarJefe(estado, Region.MINAS_DE_SAN_LOURENZO);
                if (mision.getNombre().equals("La cripta de los Soutomaior"))
                    new ServicioCapituloTres().registrarJefe(estado, Region.PAZO_DE_SOUTOMAIOR);
                if (new ServicioCapituloTres().puedeCerrar(estado)
                        && estado.getProgresoCampana().getCapitulo() == leyendasolvidadas.dominio.campana.CapituloCampana.DEUDA_DE_LOS_VIVOS)
                    new CapituloTresConsola(repositorioPartidas).cerrar(estado);
                if (mision.getNombre().equals("La vigilia de los ciento doce"))
                    new CapituloCuatroConsola(repositorioPartidas).cerrar(estado);
                break;
            }
            case ABANDONO:
                new ServicioCompania().registrarConvivencia(compania, ServicioCompania.ResultadoExpedicion.ABANDONO);
                UI.titulo("RETIRADA");
                UI.log("Vuelves con las manos casi vacias y la mirada baja. Habra otra semana.");
                break;
            case MUERTE: {
                new ServicioCompania().registrarConvivencia(compania, ServicioCompania.ResultadoExpedicion.DERROTA);
                UI.titulo("TE ARRASTRAN DE VUELTA");
                int perdido = compania.getInventario().getOro() / 2;
                compania.getInventario().gastarOro(perdido);
                for (Personaje heroe : grupo) {
                    heroe.setVida(heroe.getVidaMax() * 0.5);
                    heroe.limpiarEfectos();
                    heroe.resetMental();
                    heroe.aliviarEstres(30);
                }
                UI.log(UI.pintar("Unos carboneros te encuentran medio muerto en el camino.", UI.ROJO));
                UI.log(UI.pintar("Los fisicos de la aldea cobran caro: pierdes " + perdido + " reales.", UI.ROJO));
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
        ServicioCapituloUno.ActitudInes actitud = ServicioCapituloUno.ActitudInes.values()[UI.leerOpcion(1, 3) - 1];
        new ServicioCapituloUno().completarBosque(estado, actitud);
        UI.log(UI.pintar("CAPÍTULO I COMPLETADO — Brañas Hundidas y Camino de los Difuntos desbloqueados.", UI.AMARILLO));
    }

}
