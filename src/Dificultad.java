/** Dificultad de una expedicion. */
public enum Dificultad {
    FACIL("Novicio", 0), MEDIA("Veterano", 2), DIFICIL("Pesadilla", 4);
    private final String titulo; private final int nivelExtra;
    Dificultad(String t, int n) { titulo = t; nivelExtra = n; }
    public String getTitulo() { return titulo; }
    public int getNivelExtra() { return nivelExtra; }
}
