import java.util.List;

/**
 * Bandolero: forajido de la Sierra. Rapido, letal y escurridizo;
 * gasta Energia y castiga a los enemigos marcados.
 */
public class Bandolero extends Personaje {
    private final List<Habilidad> habilidades;

    public Bandolero(String nombre, Arma armaInicial) {
        super(nombre, 1, 90, 3, 15, 18, 6, 100, 20);
        setArma(armaInicial);
        habilidades = List.of(
            new Habilidad("Punalada Traicionera", "Busca el hueco entre las costillas", 10, 0, 1.0,
                    new int[]{1}, false, null, 0, 0, 0, false, 20, 0, 0),
            new Habilidad("Cuchilla Arrojadiza", "Hoja que desangra a distancia", 15, 0, 0.85,
                    new int[]{2, 3}, false, TipoEfecto.SANGRADO, 75, 3, 0, false, 0, 0, 0),
            new Habilidad("Paso Sombrio", "Se funde con la penumbra (+esquiva)", 20, 2, 0, new int[]{},
                    false, TipoEfecto.SOMBRA, 100, 2, 0, true, 0, 0, -4),
            new Habilidad("Marca del Cazador", "Senala a la presa: recibira mas danio", 30, 3, 1.15,
                    new int[]{1, 2, 3}, false, TipoEfecto.MARCADO, 100, 2, 0, false, 0, 0, 0)
        );
    }
    @Override public double ataqueBase() {
        return 11 + 3 * getNivel() + (getArma() != null ? getArma().getDanio() : 0);
    }
    @Override public String nombreRecurso() { return "Energia"; }
    @Override public List<Habilidad> getHabilidades() { return habilidades; }
    @Override public void subirNivel() {
        super.subirNivel();
        setVidaMaxBase(getVidaMaxBase() + 16);
        setVida(getVidaMax());
        setRecursoMax(getRecursoMax() + 15);
        setRecurso(getRecursoMax());
        UI.log("Tus pies apenas rozan el suelo. (+Vida, +Energia)");
    }
}
