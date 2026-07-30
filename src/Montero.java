import java.util.List;

/**
 * Montero: el Ballestero de las brañas. Artilleria de retaguardia; castiga
 * las filas lejanas, remata con tiros certeros y traba a la vanguardia con
 * lazos. Fragil de cerca: vive de mantener la distancia y el Pulso firme.
 */
public class Montero extends Personaje {
    private final List<Habilidad> habilidades;

    public Montero(String nombre) {
        super(nombre, 1, 90, 3, 10, 14, 5, 100, 18);
        setArma(new Arma("Ballesta de Cuerno", 5, Rareza.COMUN));
        habilidades = List.of(
            Habilidad.ataque("Virote", "Saeta que busca la retaguardia", 8, 1.0, new int[]{2, 3}),
            new Habilidad("Tiro Certero", "Apunta al ojo: golpe demoledor a cualquier fila", 15, 1, 1.3,
                    new int[]{1, 2, 3}, false, null, 0, 0, 0, false, 25, 0, 0),
            new Habilidad("Trampa de Lazo", "Enreda a la vanguardia y la deja inmovil", 20, 2, 0.6,
                    new int[]{1}, false, TipoEfecto.ATURDIDO, 45, 1, 0, false, 0, 0, 0),
            new Habilidad("Marcar la Pieza", "Senala la presa para toda la caceria", 18, 2, 0.8,
                    new int[]{1, 2, 3}, false, TipoEfecto.MARCADO, 100, 2, 0, false, 0, 0, 0)
        );
    }
    @Override public double ataqueBase() {
        return 12 + 3 * getNivel() + (getArma() != null ? getArma().getDanio() : 0);
    }
    @Override public String nombreRecurso() { return "Pulso"; }
    @Override public List<Habilidad> getHabilidades() { return habilidades; }
    @Override public void subirNivel() {
        super.subirNivel();
        setVidaMaxBase(getVidaMaxBase() + 15);
        setVida(getVidaMax());
        setRecursoMax(getRecursoMax() + 15);
        setRecurso(getRecursoMax());
        UI.log("Tu pulso no tiembla ni en la niebla. (+Vida, +Pulso)");
    }
}
