/** Armadura equipable: aporta defensa y vida maxima. */
public class Armadura extends Item {
    private final int defensa;
    private final int vidaExtra;

    public Armadura(String nombre, int defensa, int vidaExtra, Rareza rareza) {
        super(nombre, rareza, defensa * 8 + vidaExtra);
        this.defensa = (int) Math.round(defensa * rareza.getMult());
        this.vidaExtra = (int) Math.round(vidaExtra * rareza.getMult());
    }
    private Armadura(String nombre, int defensa, int vidaExtra, Rareza rareza, boolean restaurada) {
        super(nombre, rareza, defensa * 8 + vidaExtra);
        this.defensa = defensa;
        this.vidaExtra = vidaExtra;
    }
    public static Armadura restaurar(String nombre, int defensa, int vidaExtra, Rareza rareza) {
        return new Armadura(nombre, defensa, vidaExtra, rareza, true);
    }
    public int getDefensa() { return defensa; }
    public int getVidaExtra() { return vidaExtra; }
    @Override public String descripcion() { return "Armadura | Def +" + defensa + ", Vida +" + vidaExtra; }

    public static Armadura aleatoria(int nivel, int bonusRareza) {
        String[] nombres = {"Cota del Peregrino", "Jubon Encerado", "Coraza del Alguacil",
                "Sayo de Penitente", "Peto del Miliciano"};
        return new Armadura(Rng.elegir(java.util.List.of(nombres)),
                2 + nivel, 10 + nivel * 4, Rareza.sortear(bonusRareza));
    }
}
