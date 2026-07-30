package leyendasolvidadas.aplicacion;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.dominio.eventos.*;

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
    private final ContextoCombate exp;
    private final GestorMisiones gestor;
    private final Inventario inventario;
    private final VistaCombate vista;
    private int ronda = 1;

    /** Constructor de compatibilidad para encuentros de un solo heroe. */
    public Combate(Personaje heroe, List<Enemigo> enemigos, ContextoCombate exp,
                   GestorMisiones gestor, VistaCombate vista) {
        this(List.of(heroe), enemigos, exp, gestor, heroe.getInventario(), vista);
    }

    public Combate(List<Personaje> heroes, List<Enemigo> enemigos, ContextoCombate exp,
                   GestorMisiones gestor, Inventario inventario, VistaCombate vista) {
        if (heroes == null || heroes.isEmpty())
            throw new IllegalArgumentException("El combate necesita al menos un heroe");
        this.heroes = new ArrayList<>(heroes);
        this.enemigos = new ArrayList<>(enemigos);
        this.exp = exp;
        this.gestor = gestor;
        this.inventario = inventario;
        this.vista = vista;
    }

    private int luz() { return exp != null ? exp.getLuz() : 60; }
    private double multDanioEnemigo() { return luz() < 15 ? 1.2 : 1.0; }
    private int estresExtra() { return luz() < 15 ? 3 : luz() < 40 ? 1 : 0; }
    private List<Personaje> heroesVivos() { return heroes.stream().filter(Personaje::estaVivo).toList(); }

    public Resultado ejecutar(boolean emboscada) {
        vista.mostrarInicio(emboscada, List.copyOf(enemigos));

        if (emboscada) {
            BusEventos.publicar("\u00a1La oscuridad les da el primer golpe!", TipoMensaje.PELIGRO);
            for (Personaje h : heroesVivos()) h.sufrirEstres(6);
            for (Enemigo e : new ArrayList<>(enemigos)) if (e.estaVivo()) turnoEnemigo(e);
            if (heroesVivos().isEmpty()) return derrota();
        }
        vista.pausa();

        while (!heroesVivos().isEmpty() && !enemigos.isEmpty()) {
            for (int i = 0; i < enemigos.size(); i++) enemigos.get(i).prepararIntencion(i + 1);
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
                    BusEventos.publicar(actor.getNombre() + " esta aturdido y pierde el turno.", TipoMensaje.PROGRESO);
                    continue;
                }

                if (actor instanceof Enemigo enemigo) {
                    if (enemigo instanceof Jefe jefe) jefe.comprobarFase();
                    turnoEnemigo(enemigo);
                    if (enemigo instanceof Jefe jefe && jefe.enFaseDos()
                            && enemigo.estaVivo() && !heroesVivos().isEmpty()) {
                        BusEventos.publicar(jefe.getNombre() + " encadena otra accion en su frenesí.", TipoMensaje.HORROR);
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
        for (Habilidad h : heroe.getHabilidades()) if (h.getCooldownActual() > 0) h.reducirCooldown();

        if ("PARANOIA".equals(heroe.getAflixion()) && Rng.prob(20)) {
            BusEventos.publicar(heroe.getNombre() + " se paraliza por la paranoia.", TipoMensaje.HORROR);
            vista.pausa();
            return null;
        }
        if ("DESESPERACION".equals(heroe.getAflixion()) && Rng.prob(15)) {
            heroe.recibirDanio(4, true);
            heroe.sufrirEstres(4);
            BusEventos.publicar(heroe.getNombre() + " se hace dano presa de la desesperacion.", TipoMensaje.HORROR);
            vista.pausa();
            return null;
        }

        while (true) {
            vista.mostrarEstado(ronda, luz(), List.copyOf(heroes), List.copyOf(enemigos), heroe);
            List<Habilidad> habilidades = heroe.getHabilidades();
            int op = vista.elegirAccion(heroe, habilidades);

            if (op <= 4) {
                Habilidad h = habilidades.get(op - 1);
                if (!h.disponible(heroe)) {
                    BusEventos.publicar("Aun no puedes usar esa tecnica.", TipoMensaje.PELIGRO);
                    continue;
                }
                if (usarHabilidad(heroe, h)) { vista.pausa(); return null; }
            } else if (op == 5) {
                if (vista.usarInventario(inventario, heroe, exp)) { vista.pausa(); return null; }
            } else if (op == 6) {
                heroe.setRecurso(heroe.getRecurso() + heroe.getRecursoMax() * 0.4);
                heroe.curar(heroe.getVidaMax() * 0.10);
                heroe.aliviarEstres(4);
                BusEventos.publicar(heroe.getNombre() + " recupera el aliento.", TipoMensaje.EXITO);
                vista.pausa();
                return null;
            } else {
                int prob = Math.min(90, 35 + heroesVivos().stream()
                        .mapToInt(Personaje::getVelocidad).sum() * 3);
                if (Rng.prob(prob)) {
                    for (Personaje miembro : heroesVivos()) miembro.sufrirEstres(8);
                    BusEventos.publicar("La compania escapa entre las sombras.", TipoMensaje.HORROR);
                    vista.pausa();
                    return Resultado.HUIDA;
                }
                BusEventos.publicar("\u00a1Les cortan la retirada!", TipoMensaje.PELIGRO);
                vista.pausa();
                return null;
            }
        }
    }

    private boolean usarHabilidad(Personaje heroe, Habilidad h) {
        heroe.setRecurso(heroe.getRecurso() - h.getCoste());
        h.activarCooldown();
        if (h.getEstresPropio() < 0) heroe.aliviarEstres(-h.getEstresPropio());
        else if (h.getEstresPropio() > 0) heroe.sufrirEstres(h.getEstresPropio());

        if (h.esSobreSi()) {
            Personaje objetivo = h.esSobreAliado() ? vista.elegirAliado(heroesVivos()) : heroe;
            if (objetivo == null) { devolverCoste(heroe, h); return false; }
            objetivo.aplicarEfecto(h.getEfecto(), h.getDuracionEfecto(), h.getPotenciaEfecto());
            BusEventos.publicar(heroe.getNombre() + " usa " + h.getNombre() + " sobre "
                    + objetivo.getNombre() + ".", TipoMensaje.PROGRESO);
            return true;
        }

        List<Enemigo> objetivos = objetivosValidos(h);
        if (objetivos.isEmpty()) {
            BusEventos.publicar("Ningun enemigo esta al alcance.", TipoMensaje.PELIGRO);
            devolverCoste(heroe, h);
            return false;
        }
        List<Enemigo> golpeados = h.esArea() ? objetivos
                : List.of(vista.elegirEnemigo(objetivos, List.copyOf(enemigos)));
        BusEventos.publicar(heroe.getNombre() + " usa " + h.getNombre() + ".", TipoMensaje.PROGRESO);
        for (Enemigo e : new ArrayList<>(golpeados)) golpear(heroe, h, e);
        return true;
    }

    private void devolverCoste(Personaje heroe, Habilidad h) {
        heroe.setRecurso(heroe.getRecurso() + h.getCoste());
        h.setCooldownActual(0);
    }

    private List<Enemigo> objetivosValidos(Habilidad h) {
        List<Enemigo> lista = new ArrayList<>();
        for (int i = 0; i < enemigos.size(); i++)
            for (int fila : h.getFilas()) if (fila == i + 1) { lista.add(enemigos.get(i)); break; }
        return lista;
    }

    private void golpear(Personaje heroe, Habilidad h, Enemigo enemigo) {
        if (Rng.prob(enemigo.esquivaActual())) {
            BusEventos.publicar(enemigo.getNombre() + " esquiva el golpe.");
            return;
        }
        boolean critico = Rng.prob(heroe.criticoActual() + h.getBonusCritico());
        double danio = heroe.ataqueBase() * h.getMultiplicador() * heroe.modDanioSaliente() * Rng.variacion();
        if (critico) danio *= 1.6;
        if (h.getMultiplicador() > 0) {
            double real = enemigo.recibirDanio(danio, false);
            BusEventos.publicar((critico ? "\u00a1CRITICO! " : "") + enemigo.getNombre()
                    + " sufre " + (int) real + " de dano.", critico ? TipoMensaje.RECOMPENSA : TipoMensaje.PELIGRO);
            if (critico) heroe.aliviarEstres(3);
            if (h.getRoboVida() > 0) heroe.curar(real * h.getRoboVida());
        }
        if (h.getEfecto() != null && enemigo.estaVivo() && Rng.prob(h.getProbabilidadEfecto())) {
            double potencia = h.getEfecto() == TipoEfecto.QUEMADURA || h.getEfecto() == TipoEfecto.SANGRADO
                    ? 3 + heroe.getNivel() : h.getPotenciaEfecto();
            enemigo.aplicarEfecto(h.getEfecto(), h.getDuracionEfecto(), potencia);
        }
        if (!enemigo.estaVivo()) procesarMuerte(enemigo);
    }

    private void turnoEnemigo(Enemigo enemigo) {
        List<Personaje> vivos = heroesVivos();
        if (vivos.isEmpty()) return;
        int fila = enemigos.indexOf(enemigo) + 1;
        MovimientoEnemigo movimiento = enemigo.consumirIntencion(fila);
        Personaje objetivo = Rng.elegir(vivos);

        if (movimiento.seCura()) {
            enemigo.curar(enemigo.getVidaMax() * 0.15);
            BusEventos.publicar(enemigo.getNombre() + " usa " + movimiento.getNombre() + " y se cura.", TipoMensaje.EXITO);
            return;
        }
        if (movimiento.esSobreSi()) {
            enemigo.aplicarEfecto(movimiento.getEfecto(), movimiento.getDuracionEfecto(), movimiento.getPotenciaEfecto());
            BusEventos.publicar(enemigo.getNombre() + " usa " + movimiento.getNombre() + ".", TipoMensaje.PELIGRO);
            aplicarTerror(movimiento, objetivo);
            return;
        }
        BusEventos.publicar(enemigo.getNombre() + " usa " + movimiento.getNombre()
                + " contra " + objetivo.getNombre() + ".", TipoMensaje.PELIGRO);
        if (movimiento.getMultiplicador() > 0 && !Rng.prob(objetivo.esquivaActual())) {
            boolean critico = Rng.prob(8 + (luz() < 15 ? 7 : 0));
            double danio = enemigo.getDanioBase() * movimiento.getMultiplicador() * enemigo.multFase()
                    * multDanioEnemigo() * enemigo.modDanioSaliente() * Rng.variacion();
            if (critico) danio *= 1.6;
            double real = objetivo.recibirDanio(danio, false);
            BusEventos.publicar(objetivo.getNombre() + " sufre " + (int) real + " de dano.", TipoMensaje.PELIGRO);
            if (critico) objetivo.sufrirEstres(8);
            if (movimiento.getEfecto() != null && Rng.prob(movimiento.getProbabilidadEfecto()))
                objetivo.aplicarEfecto(movimiento.getEfecto(), movimiento.getDuracionEfecto(), movimiento.getPotenciaEfecto());
        }
        aplicarTerror(movimiento, objetivo);
    }

    private void aplicarTerror(MovimientoEnemigo movimiento, Personaje objetivo) {
        if (movimiento.getEstres() <= 0) return;
        int total = movimiento.getEstres() + estresExtra();
        objetivo.sufrirEstres(total);
        BusEventos.publicar(objetivo.getNombre() + " sufre +" + total + " estres.", TipoMensaje.HORROR);
    }

    private void procesarMuerte(Enemigo enemigo) {
        if (!enemigos.remove(enemigo)) return;
        BusEventos.publicar("\u2620 " + enemigo.getNombre() + " cae abatido.", TipoMensaje.EXITO);
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
        BusEventos.publicar("VICTORIA", TipoMensaje.RECOMPENSA);
        for (Personaje heroe : heroesVivos()) heroe.aliviarEstres(5);
        vista.pausa();
        return Resultado.VICTORIA;
    }

    private Resultado derrota() {
        BusEventos.publicar("La compania cae derrotada. La oscuridad reclama sus nombres...", TipoMensaje.PELIGRO);
        vista.pausa();
        return Resultado.DERROTA;
    }

}
