package leyendasolvidadas.dominio.combate;

/** Datos propios del crecimiento común y del daño de clase de un héroe. */
public record ProgresionClase(
        double danioBase,
        int crecimientoVida,
        int crecimientoRecurso,
        int crecimientoDefensa,
        String mensajeCrecimiento) {

    /** Sin progresión de clase: lo usan los enemigos. */
    public static final ProgresionClase VACIA = new ProgresionClase(0, 0, 0, 0, null);
}