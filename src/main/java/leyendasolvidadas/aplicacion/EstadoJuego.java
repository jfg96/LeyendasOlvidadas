package leyendasolvidadas.aplicacion;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.campana.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;

import java.util.ArrayList;
import java.util.List;

/** Todo el estado persistente de la partida (lo que se guarda en disco). */
public class EstadoJuego {

    /** Campo conservado para poder recuperar guardados anteriores al sistema de compania. */
    private Personaje jugador;
    private Compania compania;
    private int semana = 1;
    private int expedicionesGanadas = 0;
    private boolean campanaGanada = false;
    private List<Item> ofertasHerreria = new ArrayList<>();
    private List<Personaje> candidatos = new ArrayList<>();
    private ProgresoCampana progresoCampana = new ProgresoCampana();
    private EstadoAldea estadoAldea = new EstadoAldea();
    private RegistroCampana registroCampana = new RegistroCampana();

    public Compania getCompania() {
        if (compania == null && jugador != null) {
            compania = new Compania(jugador);
            int capitalMinimo = 130;
            if (compania.getInventario().getOro() < capitalMinimo)
                compania.getInventario().ganarOro(capitalMinimo - compania.getInventario().getOro());
        }
        return compania;
    }
    public Personaje getJugador() { return getCompania().getProtagonista(); }
    public void setJugador(Personaje j) {
        jugador = j;
        compania = new Compania(j);
    }
    public int getSemana() { return semana; }
    public void avanzarSemana() { semana++; }
    public int getExpedicionesGanadas() { return expedicionesGanadas; }
    public void registrarVictoria() { expedicionesGanadas++; }
    public boolean isCampanaGanada() { return campanaGanada; }
    public void setCampanaGanada(boolean v) { campanaGanada = v; }
    public List<Item> getOfertasHerreria() { return ofertasHerreria; }
    public List<Personaje> getCandidatos() {
        if (candidatos == null) candidatos = new ArrayList<>();
        return candidatos;
    }
    public ProgresoCampana getProgresoCampana() {
        if (progresoCampana == null) progresoCampana = new ProgresoCampana();
        return progresoCampana;
    }
    public EstadoAldea getEstadoAldea() {
        if (estadoAldea == null) estadoAldea = new EstadoAldea();
        return estadoAldea;
    }
    public void restaurarEstadoAldea(EstadoAldea estadoAldea) { this.estadoAldea = estadoAldea; }
    public RegistroCampana getRegistroCampana() {
        if (registroCampana == null) registroCampana = new RegistroCampana();
        return registroCampana;
    }
    public void restaurarRegistroCampana(RegistroCampana registro) { this.registroCampana = registro; }

    /** Renueva el género de la herreria (se llama cada semana). */
    public void renovarHerreria() {
        ofertasHerreria.clear();
        int niv = getCompania().nivelMedio();
        int calidad = 5 + getEstadoAldea().nivel(EdificioAldea.HERRERIA) * 5;
        ofertasHerreria.add(Arma.aleatoria(niv, calidad));
        ofertasHerreria.add(Armadura.aleatoria(niv, calidad));
        ofertasHerreria.add(Rng.prob(50) ? Amuleto.aleatorio(5)
                : (Rng.prob(50) ? Pocion.vida() : Pocion.antorcha()));
    }

    /** Renueva los tres aventureros disponibles para contratar esta semana. */
    public void renovarContratacion() {
        getCandidatos().clear();
        int nivelBase = Math.max(1, getCompania().nivelMedio() - 1);
        List<MercenarioUnico> disponibles = java.util.Arrays.stream(MercenarioUnico.values())
                .filter(u -> u.getCapitulo().ordinal() <= getProgresoCampana().getCapitulo().ordinal())
                .filter(u -> !getProgresoCampana().haDecidido("mercenario." + u.id() + ".contratado"))
                .filter(u -> getCompania().getPlantilla().stream().noneMatch(p -> p.getIdentidadUnica() == u))
                .toList();
        if (!disponibles.isEmpty()) {
            MercenarioUnico unico = disponibles.get((semana - 1) % disponibles.size());
            Personaje candidato = FabricaHeroes.crearUnico(unico); candidato.prepararNivelInicial(nivelBase);
            getCandidatos().add(candidato);
        }
        while (getCandidatos().size() < 3)
            getCandidatos().add(FabricaHeroes.candidatoAleatorio(nivelBase + (Rng.prob(25) ? 1 : 0)));
    }

    public static int costeContratacion(Personaje candidato) {
        int base = 35 + candidato.getNivel() * 30;
        return candidato.getDefectoMecanico() == DefectoMecanico.CODICIA ? (int)Math.ceil(base * 1.20) : base;
    }

    public void restaurarProgreso(int semana, int victorias, boolean campanaGanada,
                                  Compania compania, List<Item> ofertas, List<Personaje> candidatos) {
        restaurarProgreso(semana, victorias, campanaGanada, compania, ofertas, candidatos,
                new ProgresoCampana());
    }

    public void restaurarProgreso(int semana, int victorias, boolean campanaGanada,
                                  Compania compania, List<Item> ofertas, List<Personaje> candidatos,
                                  ProgresoCampana progresoCampana) {
        this.semana = Math.max(1, semana);
        this.expedicionesGanadas = Math.max(0, victorias);
        this.campanaGanada = campanaGanada;
        this.jugador = compania.getProtagonista();
        this.compania = compania;
        this.ofertasHerreria = new ArrayList<>(ofertas);
        this.candidatos = new ArrayList<>(candidatos);
        this.progresoCampana = progresoCampana;
    }

    /** Completa campos incorporados en versiones posteriores al cargar. */
    public void prepararTrasCarga() {
        Compania actual = getCompania();
        if (actual.getPlantilla().size() < Compania.MAX_FORMACION && getCandidatos().isEmpty())
            renovarContratacion();
    }
}
