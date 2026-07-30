package leyendasolvidadas.aplicacion;

/** Resultado presentable de un caso de uso, sin colores ni dependencias visuales. */
public record ResultadoAccion(boolean exito, String mensaje) {
    public static ResultadoAccion exito(String mensaje) { return new ResultadoAccion(true, mensaje); }
    public static ResultadoAccion error(String mensaje) { return new ResultadoAccion(false, mensaje); }
}
