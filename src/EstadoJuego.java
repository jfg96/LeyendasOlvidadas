import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Todo el estado persistente de la partida (lo que se guarda en disco). */
public class EstadoJuego implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Campo conservado para poder recuperar guardados anteriores al sistema de compania. */
    private Personaje jugador;
    private Compania compania;
    private int semana = 1;
    private int expedicionesGanadas = 0;
    private boolean campanaGanada = false;
    private List<Item> ofertasHerreria = new ArrayList<>();
    private List<Personaje> candidatos = new ArrayList<>();

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

    /** Renueva el genero de la herreria (se llama cada semana). */
    public void renovarHerreria() {
        ofertasHerreria.clear();
        int niv = getCompania().nivelMedio();
        ofertasHerreria.add(Arma.aleatoria(niv, 5));
        ofertasHerreria.add(Armadura.aleatoria(niv, 5));
        ofertasHerreria.add(Rng.prob(50) ? Amuleto.aleatorio(5)
                : (Rng.prob(50) ? Pocion.vida() : Pocion.antorcha()));
    }

    /** Renueva los tres aventureros disponibles para contratar esta semana. */
    public void renovarContratacion() {
        getCandidatos().clear();
        int nivelBase = Math.max(1, getCompania().nivelMedio() - 1);
        for (int i = 0; i < 3; i++)
            getCandidatos().add(FabricaHeroes.candidatoAleatorio(nivelBase + (Rng.prob(25) ? 1 : 0)));
    }

    public static int costeContratacion(Personaje candidato) {
        return 35 + candidato.getNivel() * 30;
    }

    /** Completa campos incorporados en versiones posteriores al cargar. */
    public void prepararTrasCarga() {
        Compania actual = getCompania();
        if (actual.getPlantilla().size() < Compania.MAX_FORMACION && getCandidatos().isEmpty())
            renovarContratacion();
    }
}
