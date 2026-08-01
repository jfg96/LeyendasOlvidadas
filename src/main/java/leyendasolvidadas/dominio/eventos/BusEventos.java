package leyendasolvidadas.dominio.eventos;

import java.util.function.Consumer;

/** Adaptador de eventos hacia un receptor externo. */
public final class BusEventos implements PublicadorEventos {
    private final Consumer<EventoDominio> receptor;

    public BusEventos(Consumer<EventoDominio> receptor) {
        this.receptor = receptor != null ? receptor : evento -> {};
    }

    @Override public void publicar(EventoDominio evento) { receptor.accept(evento); }
}
