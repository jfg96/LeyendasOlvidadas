package leyendasolvidadas.dominio.azar;

import java.util.List;

/** Fuente inyectable de decisiones aleatorias. */
public interface FuenteAzar {
    int entre(int minimo, int maximo);
    boolean probabilidad(int porcentaje);

    /**
     * Comprueba una probabilidad continua en [0,1] sin redondear a porcentaje entero.
     * Usa la misma fuente de azar para conservar la reproducibilidad.
     */
    default boolean probabilidad(double probabilidadUnitaria) {
        double p = Math.max(0, Math.min(1, probabilidadUnitaria));
        return entre(0, 99_999) < Math.round(p * 100_000);
    }
    default double variacion() { return 0.9 + entre(0, 10_000) / 50_000.0; }

    default <T> T elegir(List<T> opciones) {
        return opciones.get(entre(0, opciones.size() - 1));
    }

    /** Fuente independiente para constructores de compatibilidad. */
    static FuenteAzar global() {
        return new AzarJava();
    }
}
