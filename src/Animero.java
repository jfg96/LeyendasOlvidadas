import java.util.List;

/**
 * Animero: el que llama a las animas en la noche con su campanilla. Fragil
 * pero devastador; canaliza Mana y puede robar la esencia de los muertos.
 */
public class Animero extends Personaje {
    private final List<Habilidad> habilidades;

    public Animero(String nombre) {
        super(nombre, 1, 80, 1, 8, 8, 4, 100, 12);
        setArma(new Arma("Baston de Endrino", 3, Rareza.COMUN));
        habilidades = List.of(
            Habilidad.ataque("Chispa Errante", "Descarga que alcanza cualquier fila", 5, 0.9, new int[]{1, 2, 3}),
            new Habilidad("Fuego Fatuo", "Llama espectral que prende la retaguardia", 15, 0, 0.8,
                    new int[]{2, 3}, false, TipoEfecto.QUEMADURA, 80, 3, 0, false, 0, 0, 0),
            new Habilidad("Aliento de Escarcha", "Frio que muerde y aturde", 25, 1, 1.2,
                    new int[]{1, 2}, false, TipoEfecto.ATURDIDO, 35, 1, 0, false, 0, 0, 0),
            new Habilidad("Sifon de Animas", "Roba la vida del enemigo", 35, 3, 1.1,
                    new int[]{1, 2, 3}, false, null, 0, 0, 0, false, 0, 0.5, -3)
        );
    }
    @Override public double ataqueBase() {
        return 11 + 3 * getNivel() + (getArma() != null ? getArma().getDanio() : 0);
    }
    @Override public String nombreRecurso() { return "Mana"; }
    @Override public List<Habilidad> getHabilidades() { return habilidades; }
    @Override public void subirNivel() {
        super.subirNivel();
        setVidaMaxBase(getVidaMaxBase() + 12);
        setVida(getVidaMax());
        setRecursoMax(getRecursoMax() + 20);
        setRecurso(getRecursoMax());
        logProgresion("Las animas susurran nuevos secretos. (+Mana)");
    }
}
