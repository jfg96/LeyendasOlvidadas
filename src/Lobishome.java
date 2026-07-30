import java.util.List;

/**
 * Lobishome: el maldito que se torna bestia en las noches de niebla.
 * Bruiser de vanguardia: desangra a sus presas, drena su vida al morder y
 * se enfurece con la Furia acumulada. Mucha vida, poca guardia: mata o cae.
 */
public class Lobishome extends Personaje {
    private final List<Habilidad> habilidades;

    public Lobishome(String nombre) {
        super(nombre, 1, 110, 3, 6, 12, 4, 100, 22);
        setArma(new Arma("Zarpa Lobuna", 6, Rareza.COMUN));
        habilidades = List.of(
            new Habilidad("Zarpazo Sangrante", "Garra que abre la carne", 10, 0, 1.0,
                    new int[]{1, 2}, false, TipoEfecto.SANGRADO, 80, 3, 0, false, 0, 0, 0),
            new Habilidad("Mordisco Feroz", "Dentellada que desangra y devora", 20, 1, 1.4,
                    new int[]{1}, false, TipoEfecto.SANGRADO, 100, 2, 0, false, 0, 0.3, 0),
            new Habilidad("Aullido", "Aulla a la luna y se envalentona", 20, 3, 0,
                    new int[]{}, false, TipoEfecto.FORTALECIDO, 100, 3, 0, true, 0, 0, -4),
            new Habilidad("Frenesi Lunar", "Descuartiza a cuanto tiene delante", 35, 3, 0.9,
                    new int[]{1, 2}, true, TipoEfecto.SANGRADO, 70, 3, 0, false, 0, 0.2, 0)
        );
    }
    @Override public double ataqueBase() {
        return 13 + 3 * getNivel() + (getArma() != null ? getArma().getDanio() : 0);
    }
    @Override public String nombreRecurso() { return "Furia"; }
    @Override public List<Habilidad> getHabilidades() { return habilidades; }
    @Override public void subirNivel() {
        super.subirNivel();
        setVidaMaxBase(getVidaMaxBase() + 18);
        setVida(getVidaMax());
        setRecursoMax(getRecursoMax() + 12);
        setRecurso(getRecursoMax());
        UI.log("La bestia gana terreno bajo tu piel. (+Vida, +Furia)");
    }
}
