package leyendasolvidadas.dominio.azar;

import java.util.List;

/** Fuente inyectable de decisiones aleatorias. */
public interface FuenteAzar {
    int entre(int minimo, int maximo);
    boolean probabilidad(int porcentaje);
    default double variacion() { return 0.9 + entre(0, 10_000) / 50_000.0; }

    default <T> T elegir(List<T> opciones) {
        return opciones.get(entre(0, opciones.size() - 1));
    }

    /** Adaptador temporal para código que todavía usa el generador global. */
    static FuenteAzar global() {
        return new FuenteAzar() {
            @Override public int entre(int minimo, int maximo) { return Rng.entre(minimo, maximo); }
            @Override public boolean probabilidad(int porcentaje) { return Rng.prob(porcentaje); }
        };
    }
}
