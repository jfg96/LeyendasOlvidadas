import java.util.List;

/**
 * Gaitero: el Juglar de las romerias. Sostiene la moral con coplas y
 * aturuxos: se envalentona, ahuyenta el horror y debilita a la horda con
 * la muneira. Su Aliento es el recurso. En grupo, el maestro del temple.
 */
public class Gaitero extends Personaje {
    private final List<Habilidad> habilidades;

    public Gaitero(String nombre) {
        super(nombre, 1, 95, 4, 8, 8, 5, 100, 16);
        setArma(new Arma("Punal del Juglar", 4, Rareza.COMUN));
        habilidades = List.of(
            new Habilidad("Copla Hiriente", "Verso mordaz que hiere y anima", 6, 0, 0.9,
                    new int[]{1, 2, 3}, false, null, 0, 0, 0, false, 0, 0, -3),
            new Habilidad("Aturuxo", "Grito de guerra: se crece ante el peligro", 20, 3, 0,
                    new int[]{}, false, TipoEfecto.FORTALECIDO, 100, 3, 0, true, 0, 0, -6),
            new Habilidad("Alborada", "Melodia serena que regenera y calma la mente", 22, 4, 0,
                    new int[]{}, false, TipoEfecto.REGENERACION, 100, 3, 8, true, 0, 0, -12),
            new Habilidad("Muneira Marcial", "Compas atronador que quiebra a toda la horda", 30, 3, 0.75,
                    new int[]{1, 2, 3}, true, TipoEfecto.DEBILITADO, 60, 2, 0, false, 0, 0, 0)
        );
    }
    @Override public double ataqueBase() {
        return 11 + 3 * getNivel() + (getArma() != null ? getArma().getDanio() : 0);
    }
    @Override public String nombreRecurso() { return "Aliento"; }
    @Override public List<Habilidad> getHabilidades() { return habilidades; }
    @Override public void subirNivel() {
        super.subirNivel();
        setVidaMaxBase(getVidaMaxBase() + 16);
        setVida(getVidaMax());
        setRecursoMax(getRecursoMax() + 14);
        setRecurso(getRecursoMax());
        UI.log("Tu gaita llega mas lejos que el miedo. (+Vida, +Aliento)");
    }
}
