package leyendasolvidadas.pruebas;

import leyendasolvidadas.aplicacion.EstadoJuego;
import leyendasolvidadas.aplicacion.ServicioCapituloCinco;
import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.compania.FabricaHeroes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CapituloCincoTest {
    @Test
    void lasDecisionesCoherentesDesbloqueanElPerdonYCierranLaCampana() {
        EstadoJuego estado = estadoFinal();
        var p = estado.getProgresoCampana();
        p.registrarDecision("cap1.ines.proteger"); p.registrarDecision("cap2.verdad.revelar");
        p.registrarDecision("cap3.alianza.aldara"); p.registrarDecision("cap4.justicia.exigir_reparacion");
        p.registrarDecision("cap4.nombre113.ines_la_desmemoriada");
        p.registrarDecision("cap4.ciento_doce_nombres_devuelto"); p.registrarDecision("cap4.preparacion.reliquias");
        p.registrarDecision("personal.el_retornado.desenlace.lealtad_permanente");
        p.registrarDecision("personal.sor_erea.desenlace.cargar_con_la_cicatriz");
        ServicioCapituloCinco servicio = new ServicioCapituloCinco();
        servicio.iniciarProcesion(estado, ServicioCapituloCinco.Ruta.PASADIZOS_DEL_ARCHIVO);
        assertTrue(servicio.finalesDisponibles(estado).contains(ServicioCapituloCinco.FinalCampana.DEUDA_PERDONADA));
        servicio.completar(estado, ServicioCapituloCinco.FinalCampana.DEUDA_PERDONADA, null);
        assertTrue(estado.isCampanaGanada()); assertEquals(CapituloCampana.EPILOGO, p.getCapitulo());
    }

    @Test
    void elPactoConGonzaloPermiteGobernarALosMuertos() {
        EstadoJuego estado = estadoFinal();
        estado.getProgresoCampana().registrarDecision("cap3.alianza.don_gonzalo");
        ServicioCapituloCinco servicio = new ServicioCapituloCinco();
        servicio.iniciarProcesion(estado, ServicioCapituloCinco.Ruta.PLAZA_DEFORMADA);
        assertTrue(servicio.finalesDisponibles(estado).contains(ServicioCapituloCinco.FinalCampana.EJERCITO_DE_LOS_MUERTOS));
        assertThrows(IllegalArgumentException.class, () -> servicio.completar(estado,
                ServicioCapituloCinco.FinalCampana.NUEVO_GUIA, null));
    }

    private EstadoJuego estadoFinal() {
        EstadoJuego estado = new EstadoJuego(); estado.setJugador(FabricaHeroes.crear(1, "Aldán"));
        for (CapituloCampana capitulo : new CapituloCampana[]{CapituloCampana.CAMPANAS_DE_VALDESOMBRA,
                CapituloCampana.CAMINOS_DE_ANIMAS, CapituloCampana.DEUDA_DE_LOS_VIVOS,
                CapituloCampana.LIBRO_DE_LOS_NOMBRES, CapituloCampana.ULTIMA_PROCESION})
            estado.getProgresoCampana().avanzarA(capitulo);
        return estado;
    }
}
