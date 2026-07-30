/**
 * Controlador principal (singleton): titulo, creacion del heroe y bucle
 * aldea <-> expedicion hasta romper la maldicion de la Santa Compania.
 *
 * @author Carlos Fernandez Gavino
 * @version 3.0 (Leyendas Olvidadas: La Compania)
 */
public class Juego {
    private static Juego instancia;
    private EstadoJuego estado;

    private Juego() {}
    public static Juego getInstancia() {
        if (instancia == null) instancia = new Juego();
        return instancia;
    }
    public EstadoJuego getEstado() { return estado; }

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

        if (GuardarCargar.existePartida()) {
            System.out.println("  1. Continuar partida\n  2. Nueva partida");
            if (UI.leerOpcion(1, 2) == 1) estado = GuardarCargar.cargar();
        }
        if (estado == null) {
            estado = new EstadoJuego();
            estado.setJugador(crearPersonaje());
            estado.renovarHerreria();
        }
        UI.pausa();
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
        Personaje p = switch (clase) {
            case 1 -> new Alabardero(nombre, new Arma("Alabarda Mellada", 6, Rareza.COMUN));
            case 2 -> new Animero(nombre);
            case 3 -> new Bandolero(nombre, new Arma("Daga Cachicuerna", 5, Rareza.COMUN));
            case 4 -> new Meiga(nombre);
            case 5 -> new Montero(nombre);
            case 6 -> new Gaitero(nombre);
            case 7 -> new Lobishome(nombre);
            case 8 -> new Zahori(nombre);
            default -> new Fraile(nombre);
        };
        p.getInventario().ganarOro(40);
        p.getInventario().anadir(Pocion.vida());
        p.getInventario().anadir(Pocion.antorcha());
        UI.log(UI.pintar("\nBienvenido a Valdesombra, " + nombre + ". Que la vela no se te apague.", UI.CIAN));
        return p;
    }

    private void bucle() {
        Aldea aldea = new Aldea(estado);
        while (true) {
            Expedicion exp = aldea.bucle();
            if (exp == null) {
                UI.log("La aldea te vera volver. O eso espera.");
                return;
            }
            Mision mision = exp.getGestor().getMision();
            Expedicion.Resultado r = exp.explorar();
            estado.avanzarSemana();
            estado.renovarHerreria();
            resolverVuelta(r, mision);
            if (estado.isCampanaGanada() && mision instanceof MisionJefe && ((MisionJefe) mision).esFinal()) {
                pantallaFinal();
                // La partida continua en modo libre si el jugador quiere.
            }
        }
    }

    private void resolverVuelta(Expedicion.Resultado r, Mision mision) {
        Personaje h = estado.getJugador();
        UI.limpiar();
        switch (r) {
            case EXITO: {
                UI.titulo("REGRESO TRIUNFAL");
                estado.registrarVictoria();
                h.getInventario().ganarOro(mision.getOroRecompensa());
                UI.log(UI.pintar("Cobras el encargo: +" + mision.getOroRecompensa() + " reales.", UI.AMARILLO));
                h.ganarExperiencia(mision.getXpRecompensa());
                if (mision.getItemRecompensa() != null) {
                    UI.log("Te entregan ademas " + mision.getItemRecompensa().nombreColoreado() + ".");
                    h.getInventario().anadir(mision.getItemRecompensa());
                }
                h.aliviarEstres(20);
                if (mision instanceof MisionJefe && ((MisionJefe) mision).esFinal())
                    estado.setCampanaGanada(true);
                break;
            }
            case ABANDONO:
                UI.titulo("RETIRADA");
                UI.log("Vuelves con las manos casi vacias y la mirada baja. Habra otra semana.");
                break;
            case MUERTE: {
                UI.titulo("TE ARRASTRAN DE VUELTA");
                int perdido = h.getInventario().getOro() / 2;
                h.getInventario().gastarOro(perdido);
                h.setVida(h.getVidaMax() * 0.5);
                h.limpiarEfectos();
                h.resetMental();
                h.aliviarEstres(30);
                UI.log(UI.pintar("Unos carboneros te encuentran medio muerto en el camino.", UI.ROJO));
                UI.log(UI.pintar("El fisico de la aldea cobra caro: pierdes " + perdido + " reales.", UI.ROJO));
                break;
            }
        }
        UI.pausa();
    }

    private void pantallaFinal() {
        UI.limpiar();
        System.out.println(UI.pintar("\n  ═══════════════════════════════════════════════════════", UI.AMARILLO));
        System.out.println(UI.pintar("        LA VELA SE APAGA. LA PROCESION SE DETIENE.", UI.MAGENTA));
        System.out.println(UI.pintar("  ═══════════════════════════════════════════════════════", UI.AMARILLO));
        UI.log("");
        UI.log("La Santa Compania se deshace en jirones de niebla. Las campanas de");
        UI.log("Valdesombra, por primera vez en anos, doblan por los vivos.");
        UI.log("");
        UI.log(UI.pintar("Tu nombre, " + estado.getJugador().getNombre()
                + ", ya es una leyenda... y las leyendas nunca se olvidan.", UI.AMARILLO));
        UI.log("");
        UI.log(UI.pintar("(Puedes seguir jugando en modo libre: siempre habra parajes que limpiar.)", UI.TENUE));
        UI.pausa();
    }
}
