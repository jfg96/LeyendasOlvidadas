package leyendasolvidadas.dominio.eventos;

import java.util.function.Consumer;

/** Puerto ligero para publicar hechos sin conocer la interfaz que los presenta. */
public final class BusEventos {
    private static Consumer<EventoDominio> receptor = evento -> {};

    private BusEventos() {}

    public static void conectar(Consumer<EventoDominio> nuevoReceptor) {
        receptor = nuevoReceptor != null ? nuevoReceptor : evento -> {};
    }

    public static void publicar(String mensaje) { publicar(mensaje, TipoMensaje.NEUTRO); }
    public static void publicar(String mensaje, TipoMensaje tipo) {
        receptor.accept(new EventoDominio(mensaje, tipo));
    }
}
