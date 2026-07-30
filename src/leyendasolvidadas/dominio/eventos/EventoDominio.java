package leyendasolvidadas.dominio.eventos;

/** Hecho narrativo emitido por las reglas del juego. */
public record EventoDominio(String mensaje, TipoMensaje tipo) {
    public EventoDominio {
        if (mensaje == null) throw new IllegalArgumentException("El mensaje es obligatorio");
        if (tipo == null) tipo = TipoMensaje.NEUTRO;
    }
}
