import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Motor de combate por formaciones de hasta tres heroes y tres enemigos. */
public class Combate {
    public enum Resultado { VICTORIA, DERROTA, HUIDA }

    private final List<Personaje> heroes;
    private final List<Enemigo> enemigos;
    private final Expedicion exp;
    private final GestorMisiones gestor;
    private final Inventario inventario;
    private int ronda = 1;

    /** Constructor de compatibilidad para encuentros de un solo heroe. */
    public Combate(Personaje heroe, List<Enemigo> enemigos, Expedicion exp, GestorMisiones gestor) {
        this(List.of(heroe), enemigos, exp, gestor, heroe.getInventario());
    }

    public Combate(List<Personaje> heroes, List<Enemigo> enemigos, Expedicion exp,
                   GestorMisiones gestor, Inventario inventario) {
        if (heroes == null || heroes.isEmpty())
            throw new IllegalArgumentException("El combate necesita al menos un heroe");
        this.heroes = new ArrayList<>(heroes);
        this.enemigos = new ArrayList<>(enemigos);
        this.exp = exp;
        this.gestor = gestor;
        this.inventario = inventario;
    }

    private int luz() { return exp != null ? exp.getLuz() : 60; }
    private double multDanioEnemigo() { return luz() < 15 ? 1.2 : 1.0; }
    private int estresExtra() { return luz() < 15 ? 3 : luz() < 40 ? 1 : 0; }
    private List<Personaje> heroesVivos() { return heroes.stream().filter(Personaje::estaVivo).toList(); }

    public Resultado ejecutar(boolean emboscada) {
        UI.limpiar();
        UI.titulo(emboscada ? "\u00a1TE EMBOSCAN EN LA PENUMBRA!" : "\u00a1COMBATE!");
        for (Enemigo e : enemigos)
            UI.log(UI.pintar((e.esElite() ? "\u2620 " : "\u2022 ") + e.getNombre()
                    + " (niv " + e.getNivel() + ")", e.esElite() ? UI.MAGENTA : UI.RESET));

        if (emboscada) {
            UI.log(UI.pintar("\u00a1La oscuridad les da el primer golpe!", UI.ROJO));
            for (Personaje h : heroesVivos()) h.sufrirEstres(6);
            for (Enemigo e : new ArrayList<>(enemigos)) if (e.estaVivo()) turnoEnemigo(e);
            if (heroesVivos().isEmpty()) return derrota();
        }
        UI.pausa();

        while (!heroesVivos().isEmpty() && !enemigos.isEmpty()) {
            Map<Personaje, Integer> iniciativa = new HashMap<>();
            List<Personaje> orden = new ArrayList<>(heroesVivos());
            orden.addAll(enemigos.stream().filter(Personaje::estaVivo).toList());
            for (Personaje p : orden) iniciativa.put(p, p.getVelocidad() + Rng.entre(0, 4));
            orden.sort(Comparator.comparingInt((Personaje p) -> iniciativa.get(p)).reversed());

            for (Personaje actor : orden) {
                if (!actor.estaVivo()) continue;
                if (actor instanceof Enemigo enemigo && !enemigos.contains(enemigo)) continue;
                if (!(actor instanceof Enemigo) && !heroes.contains(actor)) continue;

                boolean aturdido = actor.tieneEfecto(TipoEfecto.ATURDIDO);
                actor.tickEfectos();
                if (!actor.estaVivo()) {
                    if (actor instanceof Enemigo enemigo) procesarMuerte(enemigo);
                    if (heroesVivos().isEmpty()) return derrota();
                    continue;
                }
                if (aturdido) {
                    UI.log(UI.pintar(actor.getNombre() + " esta aturdido y pierde el turno.", UI.CIAN));
                    continue;
                }

                if (actor instanceof Enemigo enemigo) {
                    if (enemigo instanceof Jefe jefe) jefe.comprobarFase();
                    turnoEnemigo(enemigo);
                    if (enemigo instanceof Jefe jefe && jefe.enFaseDos()
                            && enemigo.estaVivo() && !heroesVivos().isEmpty()) {
                        UI.log(UI.pintar(jefe.getNombre() + " encadena otra accion en su frenesí.", UI.MAGENTA));
                        turnoEnemigo(enemigo);
                    }
                } else {
                    Resultado resultado = turnoHeroe(actor);
                    if (resultado != null) return resultado;
                }
                if (enemigos.isEmpty()) return victoria();
                if (heroesVivos().isEmpty()) return derrota();
            }
            ronda++;
        }
        return enemigos.isEmpty() ? victoria() : derrota();
    }

    private Resultado turnoHeroe(Personaje heroe) {
        heroe.setRecurso(heroe.getRecurso() + heroe.getRegenRecurso());
        for (Habilidad h : heroe.getHabilidades()) if (h.cdActual > 0) h.cdActual--;

        if ("PARANOIA".equals(heroe.getAflixion()) && Rng.prob(20)) {
            UI.log(UI.pintar(heroe.getNombre() + " se paraliza por la paranoia.", UI.MAGENTA));
            UI.pausa();
            return null;
        }
        if ("DESESPERACION".equals(heroe.getAflixion()) && Rng.prob(15)) {
            heroe.recibirDanio(4, true);
            heroe.sufrirEstres(4);
            UI.log(UI.pintar(heroe.getNombre() + " se hace dano presa de la desesperacion.", UI.MAGENTA));
            UI.pausa();
            return null;
        }

        while (true) {
            render(heroe);
            List<Habilidad> habilidades = heroe.getHabilidades();
            for (int i = 0; i < habilidades.size(); i++) {
                Habilidad h = habilidades.get(i);
                String estado = h.cdActual > 0 ? " [enfriando " + h.cdActual + "]"
                        : heroe.getRecurso() < h.coste ? " [sin recurso]" : "";
                System.out.printf("  %d. %-20s (coste %d)%s  %s%n", i + 1, h.nombre, h.coste,
                        UI.pintar(estado, UI.ROJO), UI.pintar(h.desc, UI.TENUE));
            }
            System.out.println("  5. Mochila");
            System.out.println("  6. Recuperar aliento");
            System.out.println("  7. Huir del combate");
            int op = UI.leerOpcion(1, 7);

            if (op <= 4) {
                Habilidad h = habilidades.get(op - 1);
                if (!h.disponible(heroe)) {
                    UI.log(UI.pintar("Aun no puedes usar esa tecnica.", UI.ROJO));
                    continue;
                }
                if (usarHabilidad(heroe, h)) { UI.pausa(); return null; }
            } else if (op == 5) {
                if (inventario.menuUsar(heroe, exp)) { UI.pausa(); return null; }
            } else if (op == 6) {
                heroe.setRecurso(heroe.getRecurso() + heroe.getRecursoMax() * 0.4);
                heroe.curar(heroe.getVidaMax() * 0.10);
                heroe.aliviarEstres(4);
                UI.log(heroe.getNombre() + " recupera el aliento.");
                UI.pausa();
                return null;
            } else {
                int prob = Math.min(90, 35 + heroesVivos().stream()
                        .mapToInt(Personaje::getVelocidad).sum() * 3);
                if (Rng.prob(prob)) {
                    for (Personaje miembro : heroesVivos()) miembro.sufrirEstres(8);
                    UI.log(UI.pintar("La compania escapa entre las sombras.", UI.MAGENTA));
                    UI.pausa();
                    return Resultado.HUIDA;
                }
                UI.log(UI.pintar("\u00a1Les cortan la retirada!", UI.ROJO));
                UI.pausa();
                return null;
            }
        }
    }

    private boolean usarHabilidad(Personaje heroe, Habilidad h) {
        heroe.setRecurso(heroe.getRecurso() - h.coste);
        h.cdActual = h.cooldown;
        if (h.estresPropio < 0) heroe.aliviarEstres(-h.estresPropio);
        else if (h.estresPropio > 0) heroe.sufrirEstres(h.estresPropio);

        if (h.sobreSi) {
            Personaje objetivo = h.sobreAliado ? elegirAliado() : heroe;
            if (objetivo == null) { devolverCoste(heroe, h); return false; }
            objetivo.aplicarEfecto(h.efecto, h.durEfecto, h.potEfecto);
            UI.log(heroe.getNombre() + " usa " + UI.pintar(h.nombre, UI.CIAN) + " sobre "
                    + objetivo.getNombre() + ".");
            return true;
        }

        List<Enemigo> objetivos = objetivosValidos(h);
        if (objetivos.isEmpty()) {
            UI.log(UI.pintar("Ningun enemigo esta al alcance.", UI.ROJO));
            devolverCoste(heroe, h);
            return false;
        }
        List<Enemigo> golpeados = h.aoe ? objetivos : List.of(elegirEnemigo(objetivos));
        UI.log(heroe.getNombre() + " usa " + UI.pintar(h.nombre, UI.CIAN) + ".");
        for (Enemigo e : new ArrayList<>(golpeados)) golpear(heroe, h, e);
        return true;
    }

    private void devolverCoste(Personaje heroe, Habilidad h) {
        heroe.setRecurso(heroe.getRecurso() + h.coste);
        h.cdActual = 0;
    }

    private Personaje elegirAliado() {
        List<Personaje> vivos = heroesVivos();
        System.out.println("  \u00bfA quien ayudas?");
        for (int i = 0; i < vivos.size(); i++)
            System.out.println("  " + (i + 1) + ". " + vivos.get(i).getNombre());
        return vivos.get(UI.leerOpcion(1, vivos.size()) - 1);
    }

    private Enemigo elegirEnemigo(List<Enemigo> objetivos) {
        if (objetivos.size() == 1) return objetivos.get(0);
        System.out.println("  \u00bfA quien golpeas?");
        for (int i = 0; i < objetivos.size(); i++) {
            Enemigo e = objetivos.get(i);
            System.out.printf("  %d. [fila %d] %s (%d PV)%n", i + 1,
                    enemigos.indexOf(e) + 1, e.getNombre(), (int) e.getVida());
        }
        return objetivos.get(UI.leerOpcion(1, objetivos.size()) - 1);
    }

    private List<Enemigo> objetivosValidos(Habilidad h) {
        List<Enemigo> lista = new ArrayList<>();
        for (int i = 0; i < enemigos.size(); i++)
            for (int fila : h.filas) if (fila == i + 1) { lista.add(enemigos.get(i)); break; }
        return lista;
    }

    private void golpear(Personaje heroe, Habilidad h, Enemigo enemigo) {
        if (Rng.prob(enemigo.esquivaActual())) {
            UI.log(UI.pintar(enemigo.getNombre() + " esquiva el golpe.", UI.TENUE));
            return;
        }
        boolean critico = Rng.prob(heroe.criticoActual() + h.critBonus);
        double danio = heroe.ataqueBase() * h.mult * heroe.modDanioSaliente() * Rng.variacion();
        if (critico) danio *= 1.6;
        if (h.mult > 0) {
            double real = enemigo.recibirDanio(danio, false);
            UI.log((critico ? UI.pintar("\u00a1CRITICO! ", UI.AMARILLO) : "") + enemigo.getNombre()
                    + " sufre " + (int) real + " de dano.");
            if (critico) heroe.aliviarEstres(3);
            if (h.robo > 0) heroe.curar(real * h.robo);
        }
        if (h.efecto != null && enemigo.estaVivo() && Rng.prob(h.probEfecto)) {
            double potencia = h.efecto == TipoEfecto.QUEMADURA || h.efecto == TipoEfecto.SANGRADO
                    ? 3 + heroe.getNivel() : h.potEfecto;
            enemigo.aplicarEfecto(h.efecto, h.durEfecto, potencia);
        }
        if (!enemigo.estaVivo()) procesarMuerte(enemigo);
    }

    private void turnoEnemigo(Enemigo enemigo) {
        List<Personaje> vivos = heroesVivos();
        if (vivos.isEmpty()) return;
        int fila = enemigos.indexOf(enemigo) + 1;
        MovimientoEnemigo movimiento = enemigo.elegirMovimiento(fila);
        Personaje objetivo = Rng.elegir(vivos);

        if (movimiento.seCura) {
            enemigo.curar(enemigo.getVidaMax() * 0.15);
            UI.log(enemigo.getNombre() + " usa " + movimiento.nombre + " y se cura.");
            return;
        }
        if (movimiento.sobreSi) {
            enemigo.aplicarEfecto(movimiento.efecto, movimiento.durEfecto, movimiento.potEfecto);
            UI.log(enemigo.getNombre() + " usa " + movimiento.nombre + ".");
            aplicarTerror(movimiento, objetivo);
            return;
        }
        UI.log(enemigo.getNombre() + " usa " + UI.pintar(movimiento.nombre, UI.ROJO)
                + " contra " + objetivo.getNombre() + ".");
        if (movimiento.mult > 0 && !Rng.prob(objetivo.esquivaActual())) {
            boolean critico = Rng.prob(8 + (luz() < 15 ? 7 : 0));
            double danio = enemigo.getDanioBase() * movimiento.mult * enemigo.multFase()
                    * multDanioEnemigo() * enemigo.modDanioSaliente() * Rng.variacion();
            if (critico) danio *= 1.6;
            double real = objetivo.recibirDanio(danio, false);
            UI.log(objetivo.getNombre() + " sufre " + (int) real + " de dano.");
            if (critico) objetivo.sufrirEstres(8);
            if (movimiento.efecto != null && Rng.prob(movimiento.probEfecto))
                objetivo.aplicarEfecto(movimiento.efecto, movimiento.durEfecto, movimiento.potEfecto);
        }
        aplicarTerror(movimiento, objetivo);
    }

    private void aplicarTerror(MovimientoEnemigo movimiento, Personaje objetivo) {
        if (movimiento.estres <= 0) return;
        int total = movimiento.estres + estresExtra();
        objetivo.sufrirEstres(total);
        UI.log(UI.pintar(objetivo.getNombre() + " sufre +" + total + " estres.", UI.MAGENTA));
    }

    private void procesarMuerte(Enemigo enemigo) {
        if (!enemigos.remove(enemigo)) return;
        UI.log(UI.pintar("\u2620 " + enemigo.getNombre() + " cae abatido.", UI.VERDE));
        int oro = enemigo.getOro();
        inventario.ganarOro(oro);
        for (Personaje heroe : heroes) heroe.ganarExperiencia(enemigo.getXpRecompensa());
        if (gestor != null) gestor.notificarMuerte(enemigo);
        Personaje referencia = heroes.get(0);
        Item botin = enemigo.soltarBotin(referencia, exp != null ? exp.getMultBotin() : 1.0,
                exp != null ? exp.getBonusRareza() : 0);
        if (botin != null) inventario.anadir(botin);
    }

    private Resultado victoria() {
        UI.log(UI.pintar("\n  \u2550\u2550\u2550\u2550\u2550\u2550 VICTORIA \u2550\u2550\u2550\u2550\u2550\u2550", UI.AMARILLO));
        for (Personaje heroe : heroesVivos()) heroe.aliviarEstres(5);
        UI.pausa();
        return Resultado.VICTORIA;
    }

    private Resultado derrota() {
        UI.log(UI.pintar("\n  La compania cae derrotada. La oscuridad reclama sus nombres...", UI.ROJO));
        UI.pausa();
        return Resultado.DERROTA;
    }

    private void render(Personaje actor) {
        UI.limpiar();
        UI.seccion("RONDA " + ronda + "   TURNO DE " + actor.getNombre().toUpperCase());
        System.out.println(UI.pintar("  \u2500\u2500\u2500 ENEMIGOS \u2500\u2500\u2500", UI.ROJO));
        for (int i = 0; i < enemigos.size(); i++) {
            Enemigo e = enemigos.get(i);
            System.out.printf("  [%d] %-24s %s%s%n", i + 1, e.getNombre(),
                    UI.barra("", e.getVida(), e.getVidaMax(), UI.ROJO), e.efectosTexto());
        }
        System.out.println(UI.pintar("\n  \u2500\u2500\u2500 COMPANIA \u2500\u2500\u2500", UI.CIAN));
        for (int i = 0; i < heroes.size(); i++) {
            Personaje h = heroes.get(i);
            System.out.printf("  [%d] %-16s %s  %s%s%n", i + 1, h.getNombre(),
                    UI.barra("Vida", h.getVida(), h.getVidaMax(), h.estaVivo() ? UI.VERDE : UI.ROJO),
                    UI.barra("Cordura", h.getCordura(), 100, UI.MAGENTA), h.efectosTexto());
        }
    }
}
