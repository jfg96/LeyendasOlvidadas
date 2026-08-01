package leyendasolvidadas.dominio.azar;

import java.util.Random;

/** Fuente de azar aislada basada en {@link Random}. */
public final class AzarJava implements FuenteAzar {
    private final Random random;

    public AzarJava() { this(new Random()); }
    public AzarJava(long semilla) { this(new Random(semilla)); }
    private AzarJava(Random random) { this.random = random; }

    @Override public int entre(int minimo, int maximo) {
        if (maximo < minimo) throw new IllegalArgumentException("Rango de azar inválido");
        return minimo + random.nextInt(maximo - minimo + 1);
    }

    @Override public boolean probabilidad(int porcentaje) {
        return random.nextInt(100) < Math.max(0, Math.min(100, porcentaje));
    }
}
