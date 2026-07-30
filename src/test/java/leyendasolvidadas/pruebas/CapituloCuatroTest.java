package leyendasolvidadas.pruebas;

import leyendasolvidadas.aplicacion.EstadoJuego;
import leyendasolvidadas.aplicacion.ServicioCapituloCuatro;
import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.compania.FabricaHeroes;
import leyendasolvidadas.dominio.mundo.Region;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CapituloCuatroTest {
    @Test
    void reconstruyeLosNombresPreparaElRitualYAbreLaUltimaProcesion() {
        EstadoJuego estado = enCapituloCuatro();
        ServicioCapituloCuatro servicio = new ServicioCapituloCuatro();
        servicio.abrirLibro(estado, ServicioCapituloCuatro.CustodiaLibro.COMPANIA);

        assertEquals(1, servicio.registrarHallazgo(estado, Region.BOSQUE_DE_LOS_AHORCADOS));
        assertEquals(1, servicio.registrarHallazgo(estado, Region.BOSQUE_DE_LOS_AHORCADOS));
        assertEquals(2, servicio.registrarHallazgo(estado, Region.CAMINO_DE_LOS_DIFUNTOS));
        assertEquals(3, servicio.registrarHallazgo(estado, Region.PAZO_DE_SOUTOMAIOR));
        assertTrue(servicio.puedeCelebrarRitual(estado));

        servicio.completar(estado, ServicioCapituloCuatro.JusticiaFamilias.EXIGIR_REPARACION,
                ServicioCapituloCuatro.NombreCientoTrece.INES_LA_DESMEMORIADA,
                ServicioCapituloCuatro.PreparacionRitual.CAMPANAS);

        assertEquals(CapituloCampana.ULTIMA_PROCESION, estado.getProgresoCampana().getCapitulo());
        assertTrue(estado.getProgresoCampana().haDecidido("cap4.nombre113.ines_la_desmemoriada"));
        assertTrue(estado.getProgresoCampana().estaDesbloqueada(Region.HOSPITAL_DEL_CAMINO_VIEJO));
    }

    @Test
    void noPermiteCerrarConElLibroIncompleto() {
        EstadoJuego estado = enCapituloCuatro();
        ServicioCapituloCuatro servicio = new ServicioCapituloCuatro();
        servicio.abrirLibro(estado, ServicioCapituloCuatro.CustodiaLibro.ALDARA);
        servicio.registrarHallazgo(estado, Region.BOSQUE_DE_LOS_AHORCADOS);
        assertThrows(IllegalStateException.class, () -> servicio.completar(estado,
                ServicioCapituloCuatro.JusticiaFamilias.PROTEGER_DESCENDIENTES,
                ServicioCapituloCuatro.NombreCientoTrece.ANTEPASADO_DEL_HEROE,
                ServicioCapituloCuatro.PreparacionRitual.RELIQUIAS));
    }

    private EstadoJuego enCapituloCuatro() {
        EstadoJuego estado = new EstadoJuego();
        estado.setJugador(FabricaHeroes.crear(1, "Aldán"));
        estado.getProgresoCampana().avanzarA(CapituloCampana.CAMPANAS_DE_VALDESOMBRA);
        estado.getProgresoCampana().avanzarA(CapituloCampana.CAMINOS_DE_ANIMAS);
        estado.getProgresoCampana().avanzarA(CapituloCampana.DEUDA_DE_LOS_VIVOS);
        estado.getProgresoCampana().avanzarA(CapituloCampana.LIBRO_DE_LOS_NOMBRES);
        return estado;
    }
}
