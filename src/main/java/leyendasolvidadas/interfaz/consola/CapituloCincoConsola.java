package leyendasolvidadas.interfaz.consola;

import leyendasolvidadas.aplicacion.EstadoJuego;
import leyendasolvidadas.aplicacion.RepositorioPartidas;
import leyendasolvidadas.aplicacion.ServicioCapituloCinco;

import java.util.List;

/** Escenas de apertura, desenlace y epílogo de La última procesión. */
public final class CapituloCincoConsola {
    private final RepositorioPartidas repo;
    private final ServicioCapituloCinco servicio = new ServicioCapituloCinco();

    public CapituloCincoConsola(RepositorioPartidas repo) { this.repo = repo; }

    public void presentarSiPendiente(EstadoJuego estado) {
        if (!servicio.requierePresentacion(estado)) return;
        UI.limpiar(); UI.titulo("CAPITULO V — LA ULTIMA PROCESION");
        UI.log("La novena campanada abre las puertas de Valdesombra. Las calles se doblan sobre sí mismas");
        UI.log("y la Santa Compaña entra llevando el rostro del anterior capitán bajo su capucha.");
        UI.log("Solo existe una salida: cruzar la aldea deformada y alcanzar el Hospital del Camino Viejo.");
        System.out.println("  Elegid la ruta de la compañía:");
        System.out.println("  1. La plaza, entre los vecinos y los muertos.");
        System.out.println("  2. Los pasadizos bajo el Archivo parroquial.");
        System.out.println("  3. El Campanario, por encima de la procesión.");
        servicio.iniciarProcesion(estado, UI.elegirEnum(ServicioCapituloCinco.Ruta.class));
        UI.log(UI.pintar("Los preparativos elegidos durante la vigilia fortalecen a la formación.", UI.VERDE));
        repo.guardar(estado); UI.pausa();
    }

    public void resolverFinal(EstadoJuego estado) {
        UI.seccion("EL UMBRAL DE LOS MUERTOS");
        UI.log("La Falange cae sobre las cenizas. Los ciento trece nombres responden, pero la deuda aún exige forma.");
        List<ServicioCapituloCinco.FinalCampana> finales = servicio.finalesDisponibles(estado);
        for (int i = 0; i < finales.size(); i++)
            System.out.println("  " + (i + 1) + ". " + titulo(finales.get(i)));
        var elegido = finales.get(UI.leerOpcion(1, finales.size()) - 1);
        ServicioCapituloCinco.NuevoGuia guia = null;
        if (elegido == ServicioCapituloCinco.FinalCampana.NUEVO_GUIA) {
            UI.log("La maldición puede contenerse durante una generación, pero alguien deberá portar la primera vela.");
            System.out.println("  1. El protagonista   2. Inés   3. Padre Tomé   4. Don Gonzalo   5. Un mercenario leal");
            guia = UI.elegirEnum(ServicioCapituloCinco.NuevoGuia.class);
        }
        servicio.completar(estado, elegido, guia);
        repo.guardar(estado);
    }

    public void mostrarEpilogo(EstadoJuego estado) {
        UI.limpiar();
        UI.titulo("EPILOGO");
        var progreso = estado.getProgresoCampana();
        if (progreso.haDecidido("cap5.final.nombres_devueltos")) {
            UI.log("El Libro queda completo y la verdad se lee en la plaza. La Compaña se deshace,");
            UI.log("pero los viejos apellidos pierden su poder y Valdesombra conserva la cicatriz.");
        } else if (progreso.haDecidido("cap5.final.nuevo_guia")) {
            String quien = progreso.getDecisiones().stream().filter(x -> x.startsWith("cap5.nuevo_guia."))
                    .findFirst().orElse("cap5.nuevo_guia.desconocido").substring("cap5.nuevo_guia.".length()).replace('_', ' ');
            UI.log("La primera vela cambia de manos. " + quien + " encabeza ahora la procesión.");
            UI.log("Valdesombra gana una generación de paz, comprada con una ausencia.");
        } else if (progreso.haDecidido("cap5.final.ejercito_de_los_muertos")) {
            UI.log("La Falange se alza. Los muertos obedecen y Valdesombra prospera detrás de su ejército,");
            UI.log("mientras en caminos lejanos otras aldeas empiezan a cerrar sus puertas.");
        } else if (progreso.haDecidido("cap5.final.que_arda_valdesombra")) {
            UI.log("El fuego termina lo que comenzó setenta años atrás. Los muertos ya no tienen a quién reclamar,");
            UI.log("y los supervivientes parten sin volver la vista hacia las campanas fundidas.");
        } else {
            UI.log("Los vivos aceptan recordar sin excusas y los muertos renuncian a cobrar otra vida.");
            UI.log("Inés pronuncia el último nombre. La procesión cruza el umbral y no deja guía tras ella.");
        }
        UI.log("");
        UI.log(UI.pintar("Al amanecer, por primera vez en setenta años, las campanas de Valdesombra permanecen en silencio.", UI.AMARILLO));
        UI.pausa();
    }

    private String titulo(ServicioCapituloCinco.FinalCampana finalCampana) {
        return switch (finalCampana) {
            case NOMBRES_DEVUELTOS -> "Devolver los nombres y revelar la verdad";
            case NUEVO_GUIA -> "Entregar una nueva guía a la procesión";
            case EJERCITO_DE_LOS_MUERTOS -> "Empuñar la Falange y gobernar a los muertos";
            case QUE_ARDA_VALDESOMBRA -> "Entregar Valdesombra al fuego";
            case DEUDA_PERDONADA -> "Pedir a vivos y muertos que perdonen la deuda";
        };
    }
}
