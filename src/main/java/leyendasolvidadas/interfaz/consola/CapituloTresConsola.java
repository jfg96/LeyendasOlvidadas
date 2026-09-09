package leyendasolvidadas.interfaz.consola;

import leyendasolvidadas.aplicacion.*;

public final class CapituloTresConsola {
    private final RepositorioPartidas repo;
    private final ServicioCapituloTres servicio = new ServicioCapituloTres();
    public CapituloTresConsola(RepositorioPartidas repo) { this.repo = repo; }
    public void presentarSiPendiente(EstadoJuego e) {
        if (!servicio.requierePresentacion(e)) return;
        UI.limpiar(); UI.titulo("CAPITULO III — LA DEUDA DE LOS VIVOS");
        UI.log("La verdad divide Valdesombra. Don Gonzalo de Soutomaior os recibe mientras nuevas quemaduras aparecen bajo sus guantes.");
        if (e.getProgresoCampana().haDecidido("cap2.verdad.revelar")) UI.log(UI.pintar("Las familias expuestas exigen vuestra expulsión.", UI.ROJO));
        else if (e.getProgresoCampana().haDecidido("cap2.verdad.negociar")) UI.log(UI.pintar("El concejo sabe que ahora también le debéis un favor.", UI.AMARILLO));
        System.out.println("  1. Aceptar sus cien reales.\n  2. Rechazar cualquier ayuda.\n  3. Fingir lealtad para entrar en el Pazo.");
        servicio.conocerGonzalo(e, UI.elegirEnum(ServicioCapituloTres.ActitudGonzalo.class, 1, 3));
        repo.guardar(e); UI.pausa();
    }
    public void cerrar(EstadoJuego e) {
        UI.seccion("LA COMPAÑA ENTRA EN VALDESOMBRA");
        UI.log("Don Gonzalo alza la Falange del Guía. Entre los muertos camina el anterior capitán de los Desenterrados.");
        UI.log("Solo podéis defender un lugar antes de que la procesión cruce la plaza.");
        System.out.println("  1. Archivo parroquial   2. Ermita   3. Herrería");
        ServicioCapituloTres.Defensa defensa = UI.elegirEnum(ServicioCapituloTres.Defensa.class, 1, 3);
        System.out.println("  ¿A quién apoyaréis para recuperar la Falange?\n  1. Padre Tomé   2. Aldara   3. Don Gonzalo");
        ServicioCapituloTres.Alianza alianza = UI.elegirEnum(ServicioCapituloTres.Alianza.class, 1, 3);
        servicio.completar(e, defensa, alianza);
        UI.log(UI.pintar("CAPÍTULO III COMPLETADO — dos edificios han quedado dañados.", UI.AMARILLO));
        repo.guardar(e);
    }
}
