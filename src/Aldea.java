import java.util.ArrayList;
import java.util.List;

/**
 * La aldea: refugio entre expediciones. Tablon de misiones, ermita,
 * taberna, herreria y gestion del equipo.
 */
public class Aldea {
    private EstadoJuego estado;

    public Aldea(EstadoJuego estado) { this.estado = estado; }
    public EstadoJuego getEstado() { return estado; }

    /**
     * Bucle de la aldea. @return la expedicion elegida, o null si el jugador sale del juego.
     */
    public Expedicion bucle() {
        while (true) {
            Personaje h = estado.getJugador();
            UI.limpiar();
            UI.titulo("ALDEA DE VALDESOMBRA — Semana " + estado.getSemana());
            System.out.println("  " + UI.barra("Vida", h.getVida(), h.getVidaMax(), UI.VERDE)
                    + "   " + UI.barra("Cordura", h.getCordura(), 100, UI.MAGENTA));
            UI.log(h.getNombre() + ", nivel " + h.getNivel() + " (" + h.getExperiencia() + "/"
                    + h.xpNecesaria() + " XP)   Oro: " + UI.pintar(h.getInventario().getOro() + " reales", UI.AMARILLO)
                    + "   Parajes limpiados: " + estado.getExpedicionesGanadas());
            UI.log("Formacion: " + estado.getCompania().getFormacionActiva().stream()
                    .map(Personaje::getNombre).reduce((a, b) -> a + " / " + b).orElse("-") );
            System.out.println();
            System.out.println("  1. Tablon de encargos " + UI.pintar("(partir de expedicion)", UI.TENUE));
            System.out.println("  2. Ermita " + UI.pintar("(curar cuerpo y alma)", UI.TENUE));
            System.out.println("  3. Taberna " + UI.pintar("(rumores, vino y dados)", UI.TENUE));
            System.out.println("  4. Herreria " + UI.pintar("(comprar, vender, forjar)", UI.TENUE));
            System.out.println("  5. Mochila y equipo");
            System.out.println("  6. Gestionar compania " + UI.pintar("(contratar y formar grupo)", UI.TENUE));
            System.out.println("  7. Guardar partida");
            System.out.println("  8. Cargar partida");
            System.out.println("  9. Guardar y salir del juego");
            switch (UI.leerOpcion(1, 9)) {
                case 1: {
                    Expedicion e = tablon();
                    if (e != null) return e;
                    break;
                }
                case 2: {
                    Personaje paciente = elegirMiembro("\u00bfQuien necesita la ermita?");
                    if (paciente != null) ermita(paciente);
                    break;
                }
                case 3: taberna(h); break;
                case 4: herreria(h); break;
                case 5: gestionarEquipo(); break;
                case 6: gestionarCompania(); break;
                case 7: GuardarCargar.guardar(estado); UI.pausa(); break;
                case 8: cargarPartida(); break;
                case 9: GuardarCargar.guardar(estado); return null;
            }
        }
    }

    private void gestionarEquipo() {
        Personaje elegido = elegirMiembro("¿Quien revisa el equipo?");
        if (elegido != null) estado.getCompania().getInventario().menuUsar(elegido, null);
        UI.pausa();
    }

    private void gestionarCompania() {
        while (true) {
            UI.limpiar();
            mostrarCompania();
            System.out.println("  1. Contratar aventurero");
            System.out.println("  2. Preparar formacion activa");
            System.out.println("  3. Despedir aventurero");
            System.out.println("  0. Volver a la plaza");
            switch (UI.leerOpcion(0, 3)) {
                case 0: return;
                case 1: contratar(); break;
                case 2: prepararFormacion(); break;
                case 3: despedir(); break;
            }
        }
    }

    private void mostrarCompania() {
        Compania compania = estado.getCompania();
        UI.seccion("LA COMPANIA  Plantilla " + compania.getPlantilla().size() + "/" + Compania.MAX_PLANTILLA);
        for (int i = 0; i < compania.getPlantilla().size(); i++) {
            Personaje p = compania.getPlantilla().get(i);
            String marcas = compania.esProtagonista(p) ? " [PROTAGONISTA]" : "";
            if (compania.getFormacionActiva().contains(p)) marcas += " [ACTIVO]";
            UI.log((i + 1) + ". " + p.getNombre() + " — " + p.getClass().getSimpleName()
                    + " niv " + p.getNivel() + UI.pintar(marcas, UI.CIAN));
        }
        UI.log("Tesoreria: " + UI.pintar(compania.getInventario().getOro() + " reales", UI.AMARILLO));
    }

    private void contratar() {
        Compania compania = estado.getCompania();
        if (compania.plantillaLlena()) {
            UI.log(UI.pintar("La compania ya tiene seis miembros.", UI.ROJO));
            UI.pausa();
            return;
        }
        List<Personaje> candidatos = estado.getCandidatos();
        if (candidatos.isEmpty()) {
            UI.log(UI.pintar("No quedan aventureros disponibles esta semana.", UI.ROJO));
            UI.pausa();
            return;
        }
        UI.seccion("AVENTUREROS EN BUSCA DE COMPANIA");
        for (int i = 0; i < candidatos.size(); i++) {
            Personaje p = candidatos.get(i);
            UI.log((i + 1) + ". " + p.getNombre() + " — " + p.getClass().getSimpleName()
                    + " niv " + p.getNivel() + "  "
                    + UI.pintar(EstadoJuego.costeContratacion(p) + " reales", UI.AMARILLO));
        }
        System.out.println("  0. Volver");
        int op = UI.leerOpcion(0, candidatos.size());
        if (op == 0) return;
        Personaje candidato = candidatos.get(op - 1);
        int coste = EstadoJuego.costeContratacion(candidato);
        if (!compania.getInventario().gastarOro(coste)) {
            UI.log(UI.pintar("No hay suficientes reales en la tesoreria.", UI.ROJO));
        } else if (compania.contratar(candidato)) {
            candidatos.remove(candidato);
            UI.log(UI.pintar(candidato.getNombre() + " se une a la compania.", UI.VERDE));
        } else {
            compania.getInventario().ganarOro(coste);
        }
        UI.pausa();
    }

    private void prepararFormacion() {
        Compania compania = estado.getCompania();
        List<Personaje> disponibles = new ArrayList<>(compania.getPlantilla());
        disponibles.remove(compania.getProtagonista());
        List<Personaje> formacion = new ArrayList<>();
        formacion.add(compania.getProtagonista());

        while (formacion.size() < Compania.MAX_FORMACION && !disponibles.isEmpty()) {
            UI.seccion("ELIGE ACOMPANANTE " + formacion.size() + " DE " + (Compania.MAX_FORMACION - 1));
            for (int i = 0; i < disponibles.size(); i++) {
                Personaje p = disponibles.get(i);
                UI.log((i + 1) + ". " + p.getNombre() + " — " + p.getClass().getSimpleName()
                        + " niv " + p.getNivel());
            }
            System.out.println("  0. Terminar formacion");
            int op = UI.leerOpcion(0, disponibles.size());
            if (op == 0) break;
            formacion.add(disponibles.remove(op - 1));
        }
        compania.prepararFormacion(formacion);
        UI.log(UI.pintar("Formacion preparada con " + formacion.size() + " integrante(s).", UI.VERDE));
        UI.pausa();
    }

    private void despedir() {
        Personaje elegido = elegirMiembro("¿A quien quieres despedir?");
        if (elegido == null) return;
        if (estado.getCompania().esProtagonista(elegido)) {
            UI.log(UI.pintar("El protagonista no puede abandonar su propia leyenda.", UI.ROJO));
        } else if (UI.confirmar("¿Despedir a " + elegido.getNombre() + "?")) {
            estado.getCompania().despedir(elegido);
            UI.log(UI.pintar(elegido.getNombre() + " abandona Valdesombra.", UI.TENUE));
        }
        UI.pausa();
    }

    private Personaje elegirMiembro(String titulo) {
        List<Personaje> miembros = estado.getCompania().getPlantilla();
        UI.seccion(titulo);
        for (int i = 0; i < miembros.size(); i++) {
            Personaje p = miembros.get(i);
            UI.log((i + 1) + ". " + p.getNombre() + " — " + p.getClass().getSimpleName());
        }
        System.out.println("  0. Cancelar");
        int op = UI.leerOpcion(0, miembros.size());
        return op == 0 ? null : miembros.get(op - 1);
    }

    private void cargarPartida() {
        if (!GuardarCargar.existePartida()) {
            UI.log(UI.pintar("No hay ninguna partida guardada que cargar.", UI.ROJO));
            UI.pausa();
            return;
        }
        if (!UI.confirmar("¿Cargar la partida guardada? Perderas el progreso no guardado")) return;

        EstadoJuego cargado = GuardarCargar.cargar();
        if (cargado != null) {
            estado = cargado;
            UI.log(UI.pintar("Partida cargada. Regresas a Valdesombra.", UI.VERDE));
        }
        UI.pausa();
    }

    // ---------------------------------------------------------------- tablon
    private Expedicion tablon() {
        Personaje h = estado.getJugador();
        if (!estado.getCompania().estaCompleta()) {
            UI.log(UI.pintar("Necesitas una formacion de tres antes de partir.", UI.ROJO));
            UI.log("Contrata acompanantes y prepara el grupo desde Gestionar compania.");
            UI.pausa();
            return null;
        }
        UI.limpiar();
        UI.seccion("TABLON DE ENCARGOS");
        boolean finalDisponible = estado.getExpedicionesGanadas() >= 4 && !estado.isCampanaGanada();
        Mision[] ofertas = new Mision[3];
        Dificultad[] difs = {Dificultad.FACIL, Dificultad.MEDIA, Dificultad.DIFICIL};
        for (int i = 0; i < 3; i++) {
            ofertas[i] = GestorMisiones.generar(estado.getCompania().nivelMedio(), difs[i]);
            System.out.printf("  %d. [%s] %-24s %s%n", i + 1,
                    UI.pintar(difs[i].getTitulo(), difs[i] == Dificultad.FACIL ? UI.VERDE
                            : difs[i] == Dificultad.MEDIA ? UI.AMARILLO : UI.ROJO),
                    ofertas[i].getNombre(),
                    UI.pintar(ofertas[i].getOroRecompensa() + " reales, " + ofertas[i].getXpRecompensa() + " XP", UI.TENUE));
            UI.log(UI.pintar("   " + ofertas[i].getDescripcion(), UI.TENUE));
        }
        if (finalDisponible)
            System.out.println(UI.pintar("  4. ✝ LA ULTIMA PROCESION — La Santa Compania te espera. Fin de la campana.", UI.MAGENTA));
        System.out.println("  0. Volver a la plaza");
        int max = finalDisponible ? 4 : 3;
        int op = UI.leerOpcion(0, max);
        if (op == 0) return null;
        Mision elegida = (op == 4)
                ? new MisionJefe(Dificultad.DIFICIL, 500, 1000, Amuleto.aleatorio(30), true)
                : ofertas[op - 1];
        if (!UI.confirmar("¿Partir hacia '" + elegida.getNombre() + "'?")) return null;
        return new Expedicion(estado.getCompania(), elegida, elegida.getDificultad());
    }

    // ---------------------------------------------------------------- ermita
    private void ermita(Personaje h) {
        UI.limpiar();
        UI.seccion("LA ERMITA DEL SANTO OLVIDADO");
        UI.log("La ermitaña te recibe con un gesto sereno.");
        int costeCura = 15 + h.getNivel() * 5;
        int costeCalma = 25;
        System.out.println("  1. Sanar las heridas por completo (" + costeCura + " reales)");
        System.out.println("  2. Confesion y rezo: -35 estres (" + costeCalma + " reales)");
        System.out.println("  0. Salir");
        switch (UI.leerOpcion(0, 2)) {
            case 1:
                if (h.getInventario().gastarOro(costeCura)) {
                    h.setVida(h.getVidaMax());
                    h.limpiarEfectosNegativos();
                    UI.log(UI.pintar("Tus heridas se cierran bajo los unguentos.", UI.VERDE));
                } else UI.log(UI.pintar("No te llega el oro.", UI.ROJO));
                break;
            case 2:
                if (h.getInventario().gastarOro(costeCalma)) {
                    h.aliviarEstres(35);
                    UI.log(UI.pintar("Las palabras pesan menos tras decirlas en voz alta (-35 estres).", UI.VERDE));
                } else UI.log(UI.pintar("No te llega el oro.", UI.ROJO));
                break;
        }
        UI.pausa();
    }

    // --------------------------------------------------------------- taberna
    private void taberna(Personaje h) {
        UI.limpiar();
        UI.seccion("TABERNA \"EL CANDIL TORCIDO\"");
        System.out.println("  1. Un vaso de vino: -15 estres (10 reales)");
        System.out.println("  2. Escuchar rumores (gratis)");
        System.out.println("  3. Jugar a los dados (apuesta lo que quieras)");
        System.out.println("  0. Salir");
        switch (UI.leerOpcion(0, 3)) {
            case 1:
                if (h.getInventario().gastarOro(10)) {
                    h.aliviarEstres(15);
                    UI.log(UI.pintar("El vino aspero calienta el pecho (-15 estres).", UI.VERDE));
                } else UI.log(UI.pintar("Ni para vino te queda.", UI.ROJO));
                break;
            case 2: {
                String[] rumores = {
                    "\"Dicen que cuanto mas negra la noche, mas rico el botin... y mas larga la procesion.\"",
                    "\"La Meiga cobra en recuerdos. No la mires a los ojos.\"",
                    "\"El Cuelebre duerme sobre oro. Lo dificil no es entrar, es salir.\"",
                    "\"Si oyes campanillas de madrugada, reza: la Compania busca a quien lleve su vela.\"",
                    "\"Un buen amuleto vale mas que cien espadas. Un mal amuleto... tambien cobra.\"",
                    "\"Acampa cuando puedas, forastero. La cabeza se quiebra antes que el espinazo.\""};
                UI.log(UI.pintar(Rng.elegir(List.of(rumores)), UI.CIAN));
                break;
            }
            case 3: {
                System.out.println("  ¿Cuanto apuestas? (tienes " + h.getInventario().getOro() + " reales)");
                int apuesta = UI.leerOpcion(0, Math.max(0, h.getInventario().getOro()));
                if (apuesta == 0) { UI.log("Hoy no es dia de tentar la suerte."); break; }
                h.getInventario().gastarOro(apuesta);
                if (Rng.prob(45)) {
                    h.getInventario().ganarOro(apuesta * 2);
                    UI.log(UI.pintar("¡Seis y seis! Doblas la apuesta: +" + apuesta * 2 + " reales.", UI.AMARILLO));
                } else {
                    UI.log(UI.pintar("Los dados te dan la espalda. Pierdes " + apuesta + " reales.", UI.ROJO));
                    h.sufrirEstres(3);
                }
                break;
            }
        }
        UI.pausa();
    }

    // -------------------------------------------------------------- herreria
    private void herreria(Personaje h) {
        while (true) {
            UI.limpiar();
            UI.seccion("HERRERIA DE LA VIUDA FERREIRO   Oro: " + h.getInventario().getOro());
            List<Item> ofertas = estado.getOfertasHerreria();
            System.out.println("  — Genero de la semana —");
            for (int i = 0; i < ofertas.size(); i++) {
                Item it = ofertas.get(i);
                System.out.printf("  %d. %-34s %-32s %s%n", i + 1, it.nombreColoreado(),
                        UI.pintar(it.descripcion(), UI.TENUE),
                        UI.pintar(it.getValorOro() + " reales", UI.AMARILLO));
            }
            int costeForja = h.getArma() != null ? 50 * (h.getArma().getMejoras() + 1) : 0;
            System.out.println("  4. Vender objetos de la mochila (mitad de su valor)");
            if (h.getArma() != null)
                System.out.println("  5. Forjar tu arma: +3 de danio (" + costeForja + " reales)");
            System.out.println("  0. Salir");
            int op = UI.leerOpcion(0, 5);
            if (op == 0) return;
            if (op <= 3 && op <= ofertas.size()) {
                Item it = ofertas.get(op - 1);
                if (h.getInventario().gastarOro(it.getValorOro())) {
                    if (h.getInventario().anadir(it)) {
                        ofertas.remove(it);
                        UI.log("Compras " + it.nombreColoreado() + ".");
                    } else h.getInventario().ganarOro(it.getValorOro());
                } else UI.log(UI.pintar("No te llega el oro.", UI.ROJO));
                UI.pausa();
            } else if (op == 4) {
                vender(h);
            } else if (op == 5 && h.getArma() != null) {
                if (h.getInventario().gastarOro(costeForja)) {
                    h.getArma().mejorar();
                    UI.log(UI.pintar("El martillo canta: " + h.getArma().getNombre()
                            + " ahora hace +" + (int) h.getArma().getDanio() + " de danio.", UI.VERDE));
                } else UI.log(UI.pintar("No te llega el oro.", UI.ROJO));
                UI.pausa();
            }
        }
    }
    private void vender(Personaje h) {
        List<Item> items = h.getInventario().getItems();
        if (items.isEmpty()) { UI.log("La mochila esta vacia."); UI.pausa(); return; }
        h.getInventario().mostrar();
        System.out.println("  ¿Que vendes? (0 para nada)");
        int op = UI.leerOpcion(0, items.size());
        if (op == 0) return;
        Item it = items.remove(op - 1);
        int precio = it.getValorOro() / 2;
        h.getInventario().ganarOro(precio);
        UI.log("Vendes " + it.nombreColoreado() + " por " + precio + " reales.");
        UI.pausa();
    }
}
