import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Todo el estado persistente de la partida (lo que se guarda en disco). */
public class EstadoJuego implements Serializable {
    private static final long serialVersionUID = 1L;

    private Personaje jugador;
    private int semana = 1;
    private int expedicionesGanadas = 0;
    private boolean campanaGanada = false;
    private List<Item> ofertasHerreria = new ArrayList<>();

    public Personaje getJugador() { return jugador; }
    public void setJugador(Personaje j) { jugador = j; }
    public int getSemana() { return semana; }
    public void avanzarSemana() { semana++; }
    public int getExpedicionesGanadas() { return expedicionesGanadas; }
    public void registrarVictoria() { expedicionesGanadas++; }
    public boolean isCampanaGanada() { return campanaGanada; }
    public void setCampanaGanada(boolean v) { campanaGanada = v; }
    public List<Item> getOfertasHerreria() { return ofertasHerreria; }

    /** Renueva el genero de la herreria (se llama cada semana). */
    public void renovarHerreria() {
        ofertasHerreria.clear();
        int niv = jugador.getNivel();
        ofertasHerreria.add(Arma.aleatoria(niv, 5));
        ofertasHerreria.add(Armadura.aleatoria(niv, 5));
        ofertasHerreria.add(Rng.prob(50) ? Amuleto.aleatorio(5)
                : (Rng.prob(50) ? Pocion.vida() : Pocion.antorcha()));
    }
}
