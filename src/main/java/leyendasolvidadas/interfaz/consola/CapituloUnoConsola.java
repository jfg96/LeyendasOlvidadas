package leyendasolvidadas.interfaz.consola;

import leyendasolvidadas.aplicacion.*;

/** Presentación en terminal del primer capítulo y de Padre Tomé. */
public final class CapituloUnoConsola {
    private final RepositorioPartidas repositorio;
    private final ServicioCapituloUno servicio = new ServicioCapituloUno();

    public CapituloUnoConsola(RepositorioPartidas repositorio) { this.repositorio = repositorio; }

    public void presentarSiPendiente(EstadoJuego estado) {
        if (!servicio.requierePresentacion(estado)) return;
        UI.limpiar();
        UI.titulo("CAPITULO I — LAS CAMPANAS DE VALDESOMBRA");
        UI.log("Padre Tomé te espera en la ermita, con la sotana húmeda y los ojos sin sueño.");
        UI.log("Admite que otras personas han sido olvidadas, pero se niega a decir cuántas.");
        UI.log("Sobre su mesa descansa un libro al que le han arrancado varias páginas.");
        System.out.println("\n  1. Confiar en él y ofrecerle ayuda.");
        System.out.println("  2. Dejar claro que sabes que oculta algo.");
        System.out.println("  3. Exigir respuestas antes de aceptar ningún encargo.");
        ServicioCapituloUno.Actitud actitud = ServicioCapituloUno.Actitud.values()[UI.leerOpcion(1, 3) - 1];
        servicio.conocerPadreTome(estado, actitud);
        UI.log(UI.pintar("\"Formad una compañía. El Bosque de los Ahorcados será el primer camino.\"", UI.MAGENTA));
        repositorio.guardar(estado);
        UI.pausa();
    }
}
