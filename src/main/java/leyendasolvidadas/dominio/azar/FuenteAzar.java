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

    /** Fuente independiente para constructores de compatibilidad. */
    static FuenteAzar global() {
        return new AzarJava();
    }
}
