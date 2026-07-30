package leyendasolvidadas.dominio.azar;


import java.util.Random;

/** Generador central de azar. Permite partidas reproducibles con semilla. */
public final class Rng {
    private static Random rnd = new Random();
    private Rng() {}
    public static void semilla(long s) { rnd = new Random(s); }
    /** Entero entre a y b, ambos incluidos. */
    public static int entre(int a, int b) { return a + rnd.nextInt(b - a + 1); }
    /** true con probabilidad p (0-100). */
    public static boolean prob(int p) { return rnd.nextInt(100) < p; }
    public static double variacion() { return 0.9 + rnd.nextDouble() * 0.2; }
    public static <T> T elegir(java.util.List<T> lista) { return lista.get(rnd.nextInt(lista.size())); }
}
