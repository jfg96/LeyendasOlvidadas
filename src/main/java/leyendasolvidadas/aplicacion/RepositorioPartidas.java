package leyendasolvidadas.aplicacion;

/** Puerto de persistencia utilizado por los casos de uso. */
public interface RepositorioPartidas {
    boolean existePartida();
    boolean guardar(EstadoJuego estado);
    EstadoJuego cargar();
}
