import java.util.ArrayList;
import java.util.List;

/**
 * Motor de combate por turnos y filas, al estilo Darkest Dungeon:
 * los enemigos ocupan las filas 1-3 y cada habilidad alcanza filas concretas.
 * La oscuridad de la expedicion endurece el combate pero mejora el botin.
 */
public class Combate {

    /** Resultado posible de un combate. */
    public enum Resultado { VICTORIA, DERROTA, HUIDA }

    private final Personaje heroe;
    private final List<Enemigo> enemigos; // el indice + 1 es la fila actual
    private final Expedicion exp;         // null en combates fuera de expedicion
    private final GestorMisiones gestor;
    private int ronda = 1;

    public Combate(Personaje heroe, List<Enemigo> enemigos, Expedicion exp, GestorMisiones gestor) {
        this.heroe = heroe;
        this.enemigos = new ArrayList<>(enemigos);
        this.exp = exp;
        this.gestor = gestor;
    }

    private int luz() { return exp != null ? exp.getLuz() : 60; }
    private double multDanioEnemigo() { return luz() < 15 ? 1.2 : 1.0; }
    private int estresExtra() { return luz() < 15 ? 3 : luz() < 40 ? 1 : 0; }

    /** Ejecuta el combate completo. */
    public Resultado ejecutar(boolean emboscada) {
        UI.limpiar();
        UI.titulo("¡EMBOSCADA EN LA PENUMBRA!".substring(emboscada ? 0 : 1).isEmpty()
                ? "COMBATE" : (emboscada ? "¡TE EMBOSCAN EN LA PENUMBRA!" : "¡COMBATE!"));
        for (Enemigo e : enemigos)
            UI.log(UI.pintar((e.esElite() ? "☠ " : "• ") + e.getNombre()
                    + " (niv " + e.getNivel() + ")", e.esElite() ? UI.MAGENTA : UI.RESET));
        if (emboscada) {
            UI.log(UI.pintar("¡La oscuridad les da el primer golpe!", UI.ROJO));
            heroe.sufrirEstres(6);
            turnoEnemigos();
            if (!heroe.estaVivo()) return derrota();
        }
        UI.pausa();

        while (heroe.estaVivo() && !enemigos.isEmpty()) {
            Resultado r = turnoHeroe();
            if (r != null) return r;
            if (enemigos.isEmpty()) break;
            turnoEnemigos();
            if (!heroe.estaVivo()) return derrota();
            ronda++;
        }
        return victoria();
    }

    // ------------------------------------------------------------------ heroe
    private Resultado turnoHeroe() {
        heroe.tickEfectos();
        if (!heroe.estaVivo()) return derrota();
        heroe.setRecurso(heroe.getRecurso() + heroe.getRegenRecurso());
        for (Habilidad h : heroe.getHabilidades()) if (h.cdActual > 0) h.cdActual--;

        if (heroe.tieneEfecto(TipoEfecto.ATURDIDO)) {
            UI.log(UI.pintar("Estas aturdido y pierdes el turno...", UI.ROJO));
            UI.pausa();
            return null;
        }
        // Aflicciones mentales
        if ("PARANOIA".equals(heroe.getAflixion()) && Rng.prob(20)) {
            UI.log(UI.pintar("La paranoia te paraliza: \"¡Hay algo detras!\" Pierdes el turno.", UI.MAGENTA));
            UI.pausa();
            return null;
        }
        if ("DESESPERACION".equals(heroe.getAflixion()) && Rng.prob(15)) {
            UI.log(UI.pintar("La desesperacion te consume: te haces sangre en las unas.", UI.MAGENTA));
            heroe.recibirDanio(4, true);
            heroe.sufrirEstres(4);
            UI.pausa();
            return null;
        }

        while (true) {
            render();
            List<Habilidad> habs = heroe.getHabilidades();
            for (int i = 0; i < habs.size(); i++) {
                Habilidad h = habs.get(i);
                String estado = h.cdActual > 0 ? UI.pintar(" [enfriando " + h.cdActual + "]", UI.ROJO)
                        : heroe.getRecurso() < h.coste ? UI.pintar(" [sin " + heroe.nombreRecurso().toLowerCase() + "]", UI.ROJO) : "";
                System.out.printf("  %d. %-20s %s(coste %d, filas %s)%s  %s%n", i + 1, h.nombre,
                        UI.c(UI.TENUE), h.coste, h.filas.length == 0 ? "-" : h.filasTexto(),
                        UI.c(UI.RESET) + estado, UI.pintar(h.desc, UI.TENUE));
            }
            System.out.println("  5. Mochila (usar objeto)");
            System.out.println("  6. Recuperar aliento  " + UI.pintar("(recupera recurso y algo de vida)", UI.TENUE));
            System.out.println("  7. Huir del combate");
            int op = UI.leerOpcion(1, 7);

            if (op <= 4) {
                Habilidad h = habs.get(op - 1);
                if (!h.disponible(heroe)) { UI.log(UI.pintar("Aun no puedes usar esa tecnica.", UI.ROJO)); continue; }
                if (usarHabilidad(h)) { UI.pausa(); return null; }
            } else if (op == 5) {
                if (heroe.getInventario().menuUsar(heroe, exp)) { UI.pausa(); return null; }
            } else if (op == 6) {
                heroe.setRecurso(heroe.getRecurso() + heroe.getRecursoMax() * 0.4);
                heroe.curar(heroe.getVidaMax() * 0.10);
                heroe.aliviarEstres(4);
                UI.log("Tomas aire tras tu guardia: recuperas " + heroe.nombreRecurso().toLowerCase() + " y aliento.");
                UI.pausa();
                return null;
            } else {
                int prob = 45 + heroe.getVelocidad() * 3;
                if (Rng.prob(prob)) {
                    UI.log(UI.pintar("Escapas entre las sombras... pero el miedo te persigue (+8 estres).", UI.MAGENTA));
                    heroe.sufrirEstres(8);
                    UI.pausa();
                    return Resultado.HUIDA;
                }
                UI.log(UI.pintar("¡Te cortan la retirada!", UI.ROJO));
                UI.pausa();
                return null;
            }
        }
    }

    /** Resuelve una habilidad del heroe. @return true si consume el turno. */
    private boolean usarHabilidad(Habilidad h) {
        heroe.setRecurso(heroe.getRecurso() - h.coste);
        h.cdActual = h.cooldown;

        if (h.estresPropio != 0) {
            if (h.estresPropio < 0) heroe.aliviarEstres(-h.estresPropio);
            else heroe.sufrirEstres(h.estresPropio);
        }
        if (h.sobreSi) {
            heroe.aplicarEfecto(h.efecto, h.durEfecto, h.potEfecto);
            UI.log(heroe.getNombre() + " usa " + UI.pintar(h.nombre, UI.CIAN) + ": gana "
                    + h.efecto.getNombre() + " durante " + h.durEfecto + " turnos.");
            return true;
        }
        List<Enemigo> objetivos = objetivosValidos(h);
        if (objetivos.isEmpty()) {
            UI.log(UI.pintar("Ningun enemigo esta al alcance de esa tecnica.", UI.ROJO));
            heroe.setRecurso(heroe.getRecurso() + h.coste); // devolver coste
            h.cdActual = 0;
            return false;
        }
        List<Enemigo> golpeados;
        if (h.aoe) golpeados = objetivos;
        else if (objetivos.size() == 1) golpeados = objetivos;
        else {
            System.out.println("  ¿A quien golpeas?");
            for (int i = 0; i < objetivos.size(); i++) {
                Enemigo e = objetivos.get(i);
                System.out.printf("  %d. [fila %d] %s (%d PV)%n", i + 1,
                        enemigos.indexOf(e) + 1, e.getNombre(), (int) e.getVida());
            }
            golpeados = List.of(objetivos.get(UI.leerOpcion(1, objetivos.size()) - 1));
        }
        UI.log(heroe.getNombre() + " usa " + UI.pintar(h.nombre, UI.CIAN) + ".");
        for (Enemigo e : new ArrayList<>(golpeados)) golpear(h, e);
        return true;
    }

    private List<Enemigo> objetivosValidos(Habilidad h) {
        List<Enemigo> lista = new ArrayList<>();
        for (int i = 0; i < enemigos.size(); i++) {
            int fila = i + 1;
            for (int f : h.filas) if (f == fila) { lista.add(enemigos.get(i)); break; }
        }
        return lista;
    }

    private void golpear(Habilidad h, Enemigo e) {
        if (Rng.prob(e.esquivaActual())) {
            UI.log(UI.pintar(e.getNombre() + " esquiva el golpe.", UI.TENUE));
            return;
        }
        boolean crit = Rng.prob(heroe.criticoActual() + h.critBonus);
        double danio = heroe.ataqueBase() * h.mult * heroe.modDanioSaliente() * Rng.variacion();
        if (crit) danio *= 1.6;
        if (h.mult > 0) {
            double real = e.recibirDanio(danio, false);
            UI.log((crit ? UI.pintar("¡CRITICO! ", UI.AMARILLO) : "")
                    + e.getNombre() + " sufre " + (int) real + " de danio. ["
                    + (int) e.getVida() + "/" + (int) e.getVidaMax() + "]");
            if (crit) { heroe.aliviarEstres(3); }
            if (h.robo > 0) {
                double robado = real * h.robo;
                heroe.curar(robado);
                heroe.setRecurso(heroe.getRecurso() + 15);
                UI.log(UI.pintar("Absorbes " + (int) robado + " PV de su esencia.", UI.VERDE));
            }
        }
        if (h.efecto != null && e.estaVivo() && Rng.prob(h.probEfecto)) {
            double pot = h.efecto == TipoEfecto.QUEMADURA || h.efecto == TipoEfecto.SANGRADO
                    ? 3 + heroe.getNivel() : h.potEfecto;
            e.aplicarEfecto(h.efecto, h.durEfecto, pot);
            UI.log(UI.pintar(e.getNombre() + " queda " + h.efecto.getNombre().toLowerCase() + ".", UI.MAGENTA));
        }
        if (!e.estaVivo()) procesarMuerte(e);
    }

    private void procesarMuerte(Enemigo e) {
        UI.log(UI.pintar("☠ " + e.getNombre() + " cae abatido.", UI.VERDE));
        int oro = e.getOro();
        if (heroe.getAmuleto() != null && heroe.getAmuleto().getDon() == Amuleto.Don.CODICIA)
            oro = (int) (oro * (1 + heroe.getAmuleto().getPotencia() / 100.0));
        heroe.getInventario().ganarOro(oro);
        UI.log(UI.pintar("+" + oro + " reales.", UI.AMARILLO));
        heroe.ganarExperiencia(e.getXpRecompensa());
        heroe.aliviarEstres(2);
        Item botin = e.soltarBotin(heroe,
                exp != null ? exp.getMultBotin() : 1.0,
                exp != null ? exp.getBonusRareza() : 0);
        if (botin != null) {
            UI.log("Botin: " + botin.nombreColoreado() + " " + UI.pintar("(" + botin.descripcion() + ")", UI.TENUE));
            heroe.getInventario().anadir(botin);
        }
        enemigos.remove(e);
        if (gestor != null) gestor.notificarMuerte(e);
    }

    // --------------------------------------------------------------- enemigos
    private void turnoEnemigos() {
        for (Enemigo e : new ArrayList<>(enemigos)) {
            if (!e.estaVivo()) continue;
            System.out.println();
            e.tickEfectos();
            if (!e.estaVivo()) { procesarMuerte(e); continue; }
            if (e.tieneEfecto(TipoEfecto.ATURDIDO)) {
                UI.log(UI.pintar(e.getNombre() + " esta aturdido y pierde el turno.", UI.CIAN));
                continue;
            }
            if (e instanceof Jefe) ((Jefe) e).comprobarFase();

            int fila = enemigos.indexOf(e) + 1;
            MovimientoEnemigo mov = e.elegirMovimiento(fila);

            if (mov.seCura) {
                double cura = e.getVidaMax() * 0.15;
                e.curar(cura);
                UI.log(e.getNombre() + " usa " + mov.nombre + " y recupera " + (int) cura + " PV.");
                continue;
            }
            if (mov.sobreSi) {
                e.aplicarEfecto(mov.efecto, mov.durEfecto, mov.potEfecto);
                UI.log(e.getNombre() + " usa " + UI.pintar(mov.nombre, UI.ROJO) + " y se envalentona.");
                if (mov.estres > 0) sufrirTerror(mov);
                continue;
            }
            UI.log(e.getNombre() + " usa " + UI.pintar(mov.nombre, UI.ROJO) + ".");
            if (mov.mult > 0) {
                if (Rng.prob(heroe.esquivaActual())) {
                    UI.log(UI.pintar("¡Lo esquivas por un pelo!", UI.VERDE));
                } else {
                    boolean crit = Rng.prob(8 + (luz() < 15 ? 7 : 0));
                    double danio = e.getDanioBase() * mov.mult * e.multFase()
                            * multDanioEnemigo() * e.modDanioSaliente() * Rng.variacion();
                    if (crit) danio *= 1.6;
                    double real = heroe.recibirDanio(danio, false);
                    UI.log((crit ? UI.pintar("¡GOLPE CRITICO! ", UI.ROJO) : "")
                            + "Sufres " + (int) real + " de danio. ["
                            + (int) heroe.getVida() + "/" + (int) heroe.getVidaMax() + " PV]");
                    if (crit) heroe.sufrirEstres(8);
                    if (mov.efecto != null && Rng.prob(mov.probEfecto)) {
                        heroe.aplicarEfecto(mov.efecto, mov.durEfecto, mov.potEfecto);
                        UI.log(UI.pintar("Quedas " + mov.efecto.getNombre().toLowerCase() + ".", UI.ROJO));
                    }
                }
            }
            if (mov.estres > 0) sufrirTerror(mov);
        }
    }

    private void sufrirTerror(MovimientoEnemigo mov) {
        int total = mov.estres + estresExtra();
        heroe.sufrirEstres(total);
        UI.log(UI.pintar("El horror cala en tus huesos (+" + total + " estres).", UI.MAGENTA));
    }

    // ------------------------------------------------------------- resultados
    private Resultado victoria() {
        System.out.println(UI.pintar("\n  ══════ VICTORIA ══════", UI.AMARILLO));
        heroe.aliviarEstres(5);
        UI.pausa();
        return Resultado.VICTORIA;
    }
    private Resultado derrota() {
        System.out.println(UI.pintar("\n  ☠☠☠  CAES DERROTADO. La oscuridad te reclama...  ☠☠☠", UI.ROJO));
        UI.pausa();
        return Resultado.DERROTA;
    }

    // ------------------------------------------------------------------ panel
    private void render() {
        UI.limpiar();
        UI.seccion("RONDA " + ronda + "   Luz: " + (exp != null ? exp.luzTexto() : "-"));
        System.out.println(UI.pintar("  ─── ENEMIGOS ───", UI.ROJO));
        for (int i = 0; i < enemigos.size(); i++) {
            Enemigo e = enemigos.get(i);
            String nombre = (e.esElite() ? UI.pintar(e.getNombre(), UI.MAGENTA) : e.getNombre());
            System.out.printf("  [%d] %-32s %s%s%n", i + 1, nombre,
                    UI.barra("", e.getVida(), e.getVidaMax(), UI.ROJO), e.efectosTexto());
        }
        System.out.println(UI.pintar("\n  ─── " + heroe.getNombre().toUpperCase() + "  (niv "
                + heroe.getNivel() + ") ───", UI.CIAN));
        System.out.println("  " + UI.barra("Vida", heroe.getVida(), heroe.getVidaMax(), UI.VERDE));
        System.out.println("  " + UI.barra(heroe.nombreRecurso(), heroe.getRecurso(), heroe.getRecursoMax(), UI.AZUL));
        System.out.println("  " + UI.barra("Cordura", heroe.getCordura(), 100, UI.MAGENTA)
                + (heroe.getAflixion() != null ? UI.pintar("  [" + heroe.getAflixion() + "]",
                    "VIRTUD".equals(heroe.getAflixion()) ? UI.AMARILLO : UI.MAGENTA) : "")
                + heroe.efectosTexto());
        UI.seccion("TU TURNO");
    }
}
