import java.util.List;

/**
 * Zahori: el Vidente que lee malos presagios. Controlador de campo, no
 * pega fuerte pero debilita, aturde y marca a la horda entera para que
 * caiga ante los demas. Fragil como el Animero; su recurso es el Presagio.
 */
public class Zahori extends Personaje {
    private final List<Habilidad> habilidades;

    public Zahori(String nombre) {
        super(nombre, 1, 82, 2, 9, 8, 5, 100, 14);
        setArma(new Arma("Pendulo de Azabache", 3, Rareza.COMUN));
        habilidades = List.of(
            Habilidad.ataque("Mal Presagio", "Vaticinio hiriente a cualquier fila", 5, 0.85, new int[]{1, 2, 3}),
            new Habilidad("Sino Aciago", "Sella su destino: golpeara mas flojo", 18, 1, 0.7,
                    new int[]{1, 2, 3}, false, TipoEfecto.DEBILITADO, 90, 2, 0, false, 0, 0, 0),
            new Habilidad("Sombra del Cuelebre", "Vision aterradora que paraliza", 22, 2, 0.9,
                    new int[]{1, 2}, false, TipoEfecto.ATURDIDO, 45, 1, 0, false, 0, 0, 0),
            new Habilidad("Aojar", "Echa el mal de ojo a toda la formacion", 30, 3, 0.8,
                    new int[]{1, 2, 3}, true, TipoEfecto.MARCADO, 100, 2, 0, false, 0, 0, 0)
        );
    }
    @Override public double ataqueBase() {
        return 10 + 3 * getNivel() + (getArma() != null ? getArma().getDanio() : 0);
    }
    @Override public String nombreRecurso() { return "Presagio"; }
    @Override public List<Habilidad> getHabilidades() { return habilidades; }
    @Override public void subirNivel() {
        super.subirNivel();
        setVidaMaxBase(getVidaMaxBase() + 12);
        setVida(getVidaMax());
        setRecursoMax(getRecursoMax() + 18);
        setRecurso(getRecursoMax());
        UI.log("Los presagios se revelan mas nitidos. (+Presagio)");
    }
}
