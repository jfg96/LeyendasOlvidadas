package leyendasolvidadas.aplicacion;

import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.combate.Personaje;

import java.util.ArrayList;
import java.util.List;

/** Reglas de La última procesión y selección de un epílogo alcanzable. */
public final class ServicioCapituloCinco {
    public enum Ruta { PLAZA_DEFORMADA, PASADIZOS_DEL_ARCHIVO, CAMPANARIO }
    public enum FinalCampana {
        NOMBRES_DEVUELTOS, NUEVO_GUIA, EJERCITO_DE_LOS_MUERTOS, QUE_ARDA_VALDESOMBRA, DEUDA_PERDONADA
    }
    public enum NuevoGuia { PROTAGONISTA, INES, PADRE_TOME, DON_GONZALO, MERCENARIO_LEAL }

    public boolean requierePresentacion(EstadoJuego estado) {
        return estado.getProgresoCampana().getCapitulo() == CapituloCampana.ULTIMA_PROCESION
                && !estado.getProgresoCampana().haDecidido("cap5.procesion_iniciada");
    }

    public void iniciarProcesion(EstadoJuego estado, Ruta ruta) {
        if (!requierePresentacion(estado)) throw new IllegalStateException("La procesión ya ha comenzado");
        estado.getProgresoCampana().registrarDecision("cap5.ruta." + ruta.name().toLowerCase());
        estado.getProgresoCampana().registrarDecision("cap5.procesion_iniciada");
        registrarAliado(estado);
        aplicarPreparacion(estado);
    }

    public List<FinalCampana> finalesDisponibles(EstadoJuego estado) {
        List<FinalCampana> finales = new ArrayList<>();
        if (estado.getProgresoCampana().haDecidido("cap2.verdad.revelar")
                || estado.getProgresoCampana().haDecidido("cap4.justicia.exigir_reparacion"))
            finales.add(FinalCampana.NOMBRES_DEVUELTOS);
        finales.add(FinalCampana.NUEVO_GUIA);
        if (estado.getProgresoCampana().haDecidido("cap3.alianza.don_gonzalo")
                || estado.getProgresoCampana().haDecidido("cap3.gonzalo.aceptar_ayuda")
                || estado.getProgresoCampana().haDecidido("cap3.gonzalo.fingir_lealtad"))
            finales.add(FinalCampana.EJERCITO_DE_LOS_MUERTOS);
        finales.add(FinalCampana.QUE_ARDA_VALDESOMBRA);
        if (puedePerdonarDeuda(estado)) finales.add(FinalCampana.DEUDA_PERDONADA);
        return List.copyOf(finales);
    }

    public void completar(EstadoJuego estado, FinalCampana finalCampana, NuevoGuia nuevoGuia) {
        if (estado.getProgresoCampana().getCapitulo() != CapituloCampana.ULTIMA_PROCESION)
            throw new IllegalStateException("La última procesión no está activa");
        if (!finalesDisponibles(estado).contains(finalCampana))
            throw new IllegalArgumentException("Ese final no está disponible en esta campaña");
        if ((finalCampana == FinalCampana.NUEVO_GUIA) != (nuevoGuia != null))
            throw new IllegalArgumentException("El sacrificio solo corresponde al final del nuevo guía");
        estado.getProgresoCampana().registrarDecision("cap5.final." + finalCampana.name().toLowerCase());
        if (nuevoGuia != null)
            estado.getProgresoCampana().registrarDecision("cap5.nuevo_guia." + nuevoGuia.name().toLowerCase());
        estado.setCampanaGanada(true);
        estado.getProgresoCampana().avanzarA(CapituloCampana.EPILOGO);
    }

    private boolean puedePerdonarDeuda(EstadoJuego estado) {
        return estado.getProgresoCampana().haDecidido("cap1.ines.proteger")
                && estado.getProgresoCampana().haDecidido("cap2.verdad.revelar")
                && !estado.getProgresoCampana().haDecidido("cap3.alianza.don_gonzalo")
                && estado.getProgresoCampana().haDecidido("cap4.justicia.exigir_reparacion")
                && estado.getProgresoCampana().haDecidido("cap4.nombre113.ines_la_desmemoriada")
                && estado.getProgresoCampana().haDecidido("cap4.ciento_doce_nombres_devuelto");
    }

    private void registrarAliado(EstadoJuego estado) {
        String aliado = estado.getProgresoCampana().haDecidido("cap3.alianza.padre_tome") ? "padre_tome"
                : estado.getProgresoCampana().haDecidido("cap3.alianza.aldara") ? "aldara" : "don_gonzalo";
        estado.getProgresoCampana().registrarDecision("cap5.aliado." + aliado);
    }

    private void aplicarPreparacion(EstadoJuego estado) {
        for (Personaje heroe : estado.getCompania().getFormacionActiva()) {
            if (estado.getProgresoCampana().haDecidido("cap4.preparacion.sal_y_fuego")) {
                heroe.curar(heroe.getVidaMax() * 0.35);
                heroe.limpiarEfectosNegativos();
            } else if (estado.getProgresoCampana().haDecidido("cap4.preparacion.reliquias")) {
                heroe.aliviarEstres(30);
            } else {
                heroe.setRecurso(heroe.getRecursoMax());
                heroe.aliviarEstres(15);
            }
        }
    }
}
