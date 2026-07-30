package leyendasolvidadas.dominio.combate;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.eventos.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Clase base abstracta de toda entidad viva: heroes y enemigos.
 * Gestiona vida, recurso, cordura, equipo, efectos de estado y experiencia.
 *
 * @author Javier Fernandez Gavino
 * @version 3.0 (Leyendas Olvidadas: La Compania)
 */
public abstract class Personaje {
    private String nombre;
    private int nivel;
    private double vida, vidaMax;
    private int defensa;
    private int esquiva;    // %
    private int critico;    // %
    private int velocidad;
    private double recurso, recursoMax;
    private int regenRecurso;
    private int cordura;    // 0-100, solo relevante en heroes
    private String aflixion; // null | "VIRTUD" | "PARANOIA" | "DESESPERACION"
    private int experiencia;
    private final Inventario inventario = new Inventario();
    private Arma arma; private Armadura armadura; private Amuleto amuleto;
    private final List<EfectoEstado> efectos = new ArrayList<>();
    private transient boolean progresionSilenciosa;

    public Personaje(String nombre, int nivel, double vidaMax, int defensa,
                     int esquiva, int critico, int velocidad, double recursoMax, int regenRecurso) {
        this.nombre = nombre;
        this.nivel = Math.max(1, Math.min(30, nivel));
        this.vidaMax = vidaMax; this.vida = vidaMax;
        this.defensa = defensa; this.esquiva = esquiva; this.critico = critico;
        this.velocidad = velocidad;
        this.recursoMax = recursoMax; this.recurso = recursoMax; this.regenRecurso = regenRecurso;
        this.cordura = 0; this.experiencia = 0;
    }

    // --- Getters / setters con validacion ---
    public String getNombre() { return nombre; }
    public void setNombre(String n) { nombre = n; }
    public int getNivel() { return nivel; }
    public void setNivel(int n) { nivel = Math.max(1, Math.min(30, n)); }
    public double getVida() { return vida; }
    public void setVida(double v) { vida = Math.max(0, Math.min(getVidaMax(), v)); }
    public double getVidaMax() { return vidaMax + (armadura != null ? armadura.getVidaExtra() : 0); }
    public void setVidaMaxBase(double v) { vidaMax = Math.max(1, Math.min(10000, v)); }
    public double getVidaMaxBase() { return vidaMax; }
    public int getDefensa() { return defensa + (armadura != null ? armadura.getDefensa() : 0); }
    public void setDefensaBase(int d) { defensa = Math.max(0, Math.min(1000, d)); }
    public int getDefensaBase() { return defensa; }
    public double getRecurso() { return recurso; }
    public void setRecurso(double r) { recurso = Math.max(0, Math.min(recursoMax, r)); }
    public double getRecursoMax() { return recursoMax; }
    public void setRecursoMax(double r) { recursoMax = Math.max(0, r); }
    public int getRegenRecurso() { return regenRecurso; }
    public int getVelocidad() { return velocidad; }
    public int getCordura() { return cordura; }
    public String getAflixion() { return aflixion; }
    public int getExperiencia() { return experiencia; }
    public Inventario getInventario() { return inventario; }
    public Arma getArma() { return arma; }
    public void setArma(Arma a) { arma = a; }
    public Armadura getArmadura() { return armadura; }
    public void setArmadura(Armadura a) { armadura = a; }
    public Amuleto getAmuleto() { return amuleto; }
    public void setAmuleto(Amuleto a) { amuleto = a; }
    public List<EfectoEstado> getEfectos() { return efectos; }

    // --- Estadisticas derivadas ---
    private int bonusAmuleto(Amuleto.Don don) {
        return (amuleto != null && amuleto.getDon() == don) ? amuleto.getPotencia() : 0;
    }
    public int esquivaActual() {
        int e = esquiva + bonusAmuleto(Amuleto.Don.ESQUIVA);
        if (tieneEfecto(TipoEfecto.SOMBRA)) e += 25;
        if ("VIRTUD".equals(aflixion)) e += 8;
        return Math.min(75, e);
    }
    public int criticoActual() {
        int c = critico + bonusAmuleto(Amuleto.Don.CRITICO);
        if ("VIRTUD".equals(aflixion)) c += 10;
        return Math.min(80, c);
    }
    /** Multiplicador de daño saliente segun efectos y reliquias. */
    public double modDanioSaliente() {
        double m = 1.0 + bonusAmuleto(Amuleto.Don.FUROR) / 100.0;
        if (tieneEfecto(TipoEfecto.FORTALECIDO)) m *= 1.25;
        if (tieneEfecto(TipoEfecto.DEBILITADO)) m *= 0.75;
        return m;
    }

    // --- Combate ---
    /** Dano de ataque base del personaje, incluyendo su arma. */
    public abstract double ataqueBase();
    /** Nombre del recurso de clase (Aguante, Mana, Energia...). */
    public abstract String nombreRecurso();
    /** Habilidades de combate del personaje (vacio en enemigos). */
    public List<Habilidad> getHabilidades() { return new ArrayList<>(); }

    /**
     * Aplica dano teniendo en cuenta defensa, MARCADO y PROTEGIDO.
     * @param bruto dano antes de mitigar.
     * @param ignoraDefensa true en danos por efectos (sangrado, veneno...).
     * @return dano real sufrido.
     */
    public double recibirDanio(double bruto, boolean ignoraDefensa) {
        double d = bruto;
        if (tieneEfecto(TipoEfecto.MARCADO)) d *= 1.5;
        if (tieneEfecto(TipoEfecto.PROTEGIDO)) d *= 0.6;
        if (!ignoraDefensa) d = Math.max(1, d - getDefensa());
        setVida(vida - d);
        return d;
    }
    public boolean estaVivo() { return vida > 0; }
    public void curar(double cantidad) { setVida(vida + cantidad); }

    // --- Efectos de estado ---
    public void aplicarEfecto(TipoEfecto tipo, int duracion, double potencia) {
        for (EfectoEstado e : efectos) {
            if (e.getTipo() == tipo) { e.refrescar(duracion); return; }
        }
        efectos.add(new EfectoEstado(tipo, duracion, potencia));
    }
    public boolean tieneEfecto(TipoEfecto tipo) {
        for (EfectoEstado e : efectos) if (e.getTipo() == tipo) return true;
        return false;
    }
    public void limpiarEfectosNegativos() { efectos.removeIf(e -> e.getTipo().esNegativo()); }
    public void limpiarEfectos() { efectos.clear(); }

    /** Avanza los efectos al inicio del turno: aplica DoTs y expira duraciones. */
    public void tickEfectos() {
        Iterator<EfectoEstado> it = efectos.iterator();
        while (it.hasNext()) {
            EfectoEstado e = it.next();
            TipoEfecto t = e.getTipo();
            if (t == TipoEfecto.SANGRADO || t == TipoEfecto.VENENO || t == TipoEfecto.QUEMADURA) {
                double d = recibirDanio(e.getPotencia(), true);
                BusEventos.publicar(nombre + " sufre " + (int) d + " por " + t.getNombre().toLowerCase() + ".", TipoMensaje.PELIGRO);
            }
            if (t == TipoEfecto.REGENERACION) {
                curar(e.getPotencia());
                BusEventos.publicar(nombre + " recupera " + (int) e.getPotencia() + " PV por regeneracion.", TipoMensaje.EXITO);
            }
            if (e.avanzarTurno()) it.remove();
        }
    }
    public String efectosTexto() {
        if (efectos.isEmpty()) return "";
        StringBuilder sb = new StringBuilder(" ");
        for (EfectoEstado e : efectos)
            sb.append("[").append(e).append("]");
        return sb.toString();
    }

    // --- Cordura (solo heroes) ---
    /** Aumenta el estres; al llegar a 100 se resuelve una Prueba de Determinacion. */
    public void sufrirEstres(int cantidad) {
        int reduccion = bonusAmuleto(Amuleto.Don.TEMPLE);
        cantidad = (int) Math.max(0, Math.round(cantidad * (1 - reduccion / 100.0)));
        if (cantidad <= 0) return;
        cordura = Math.min(100, cordura + cantidad);
        if (cordura >= 100 && aflixion == null) pruebaDeterminacion();
    }
    private void pruebaDeterminacion() {
        BusEventos.publicar("Tu mente se resquebraja... PRUEBA DE DETERMINACION", TipoMensaje.HORROR);
        if (Rng.prob(25)) {
            aflixion = "VIRTUD";
            cordura = 45;
            BusEventos.publicar("¡" + nombre.toUpperCase() + " SE CRECE ANTE EL HORROR! (Virtuoso: +critico, +esquiva)", TipoMensaje.RECOMPENSA);
        } else if (Rng.prob(50)) {
            aflixion = "PARANOIA";
            BusEventos.publicar(nombre + " sucumbe a la PARANOIA: a veces dudara y perdera el turno.", TipoMensaje.HORROR);
        } else {
            aflixion = "DESESPERACION";
            BusEventos.publicar(nombre + " cae en la DESESPERACION: su propia mente le atormenta.", TipoMensaje.HORROR);
        }
    }
    public void aliviarEstres(int cantidad) {
        cordura = Math.max(0, cordura - cantidad);
        if (aflixion != null && !"VIRTUD".equals(aflixion) && cordura < 30) {
            BusEventos.publicar(nombre + " recobra la compostura. La afliccion se disipa.", TipoMensaje.EXITO);
            aflixion = null;
        }
    }
    public void resetMental() { aflixion = null; }

    /** Restaura valores variables sin acoplar el dominio al formato de archivo. */
    public void restaurarEstado(double vida, double recurso, int cordura,
                                String aflixion, int experiencia, List<EfectoEstado> efectos,
                                List<Integer> cooldowns) {
        setVida(vida);
        setRecurso(recurso);
        this.cordura = Math.max(0, Math.min(100, cordura));
        this.aflixion = aflixion;
        this.experiencia = Math.max(0, experiencia);
        this.efectos.clear();
        this.efectos.addAll(efectos);
        List<Habilidad> habilidades = getHabilidades();
        for (int i = 0; i < habilidades.size() && i < cooldowns.size(); i++)
            habilidades.get(i).setCooldownActual(cooldowns.get(i));
    }

    // --- Experiencia ---
    public int xpNecesaria() { return nivel * 100; }
    public void ganarExperiencia(int cantidad) {
        experiencia += cantidad;
        BusEventos.publicar("+" + cantidad + " XP.", TipoMensaje.PROGRESO);
        while (experiencia >= xpNecesaria() && nivel < 30) {
            experiencia -= xpNecesaria();
            subirNivel();
        }
    }
    /** Sube de nivel. Las subclases mejoran aqui sus estadisticas. */
    public void subirNivel() {
        setNivel(nivel + 1);
        if (!progresionSilenciosa)
            BusEventos.publicar("¡" + nombre.toUpperCase() + " ALCANZA EL NIVEL " + nivel + "!", TipoMensaje.RECOMPENSA);
    }

    /** Escala un recluta sin mostrar mensajes de subida durante su generacion. */
    public void prepararNivelInicial(int objetivo) {
        progresionSilenciosa = true;
        while (nivel < Math.min(30, objetivo)) subirNivel();
        progresionSilenciosa = false;
        experiencia = 0;
    }

    protected void logProgresion(String mensaje) {
        if (!progresionSilenciosa) BusEventos.publicar(mensaje, TipoMensaje.PROGRESO);
    }
}
