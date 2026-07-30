package leyendasolvidadas.interfaz.consola;

import leyendasolvidadas.aplicacion.*;

/** Escenas de apertura y cierre del capítulo II. */
public final class CapituloDosConsola {
    private final RepositorioPartidas repositorio;
    private final ServicioCapituloDos servicio = new ServicioCapituloDos();

    public CapituloDosConsola(RepositorioPartidas repositorio) { this.repositorio = repositorio; }

    public void presentarSiPendiente(EstadoJuego estado) {
        if (!servicio.requierePresentacion(estado)) return;
        UI.limpiar(); UI.titulo("CAPITULO II — LOS CAMINOS DE ÁNIMAS");
        UI.log("Inés dibuja dos rutas: una se hunde en las Brañas; la otra sigue las campanas del Camino Viejo.");
        UI.log("Esa noche, Aldara de Moura espera junto al pozo. Parece recordar vuestro nombre desde antes de conoceros.");
        if (estado.getProgresoCampana().haDecidido("cap1.ines.confiar_en_tome"))
            UI.log(UI.pintar("Padre Tomé intentó impedir que Inés acudiera a la cita.", UI.MAGENTA));
        else if (estado.getProgresoCampana().haDecidido("cap1.ines.proteger"))
            UI.log(UI.pintar("Inés permanece a tu lado y traduce el murmullo de los Sin Rostro.", UI.CIAN));
        else if (estado.getProgresoCampana().haDecidido("cap1.ines.interrogar"))
            UI.log(UI.pintar("Inés recuerda cada pregunta que le hiciste, aunque no recuerde su propio nombre.", UI.MAGENTA));
        System.out.println("  1. Escuchar las reglas antiguas.\n  2. Desafiar sus amenazas.\n  3. Ofrecer un pacto a cambio del Libro.");
        servicio.conocerAldara(estado, ServicioCapituloDos.ActitudAldara.values()[UI.leerOpcion(1, 3) - 1]);
        repositorio.guardar(estado); UI.pausa();
    }

    public void cerrar(EstadoJuego estado) {
        UI.seccion("EL LIBRO DE LOS NOMBRES");
        UI.log("Las páginas recuperadas encajan. Ciento trece peregrinos fueron encerrados y quemados por los fundadores.");
        UI.log("Padre Tomé reconoce que su familia arrancó páginas para proteger a los culpables.");
        System.out.println("  1. Revelar la matanza a toda Valdesombra.\n  2. Ocultarla hasta completar el Libro.\n  3. Usar la verdad para forzar la cooperación del concejo.");
        servicio.completar(estado, ServicioCapituloDos.Verdad.values()[UI.leerOpcion(1, 3) - 1]);
        UI.log(UI.pintar("CAPÍTULO II COMPLETADO — Minas de San Lourenzo y Pazo de Soutomaior desbloqueados.", UI.AMARILLO));
        repositorio.guardar(estado);
    }
}
