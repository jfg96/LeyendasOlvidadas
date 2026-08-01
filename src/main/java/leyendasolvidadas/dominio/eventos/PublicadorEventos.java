package leyendasolvidadas.dominio.eventos;

/** Destino inyectable de los eventos producidos por las reglas del juego. */
@FunctionalInterface
public interface PublicadorEventos {
    void publicar(EventoDominio evento);

    default void publicar(String mensaje) { publicar(mensaje, TipoMensaje.NEUTRO); }
    default void publicar(String mensaje, TipoMensaje tipo) {
        publicar(new EventoDominio(mensaje, tipo));
    }

    static PublicadorEventos silencioso() { return evento -> {}; }
}
