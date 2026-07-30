package leyendasolvidadas.pruebas;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.compania.FabricaHeroes;
import leyendasolvidadas.dominio.misiones.GestorMisiones;
import leyendasolvidadas.dominio.mundo.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CapituloDosTest {
    @Test
    void completaLasDosRutasYDesbloqueaLaDeudaDeLosVivos() {
        EstadoJuego estado = new EstadoJuego();
        estado.setJugador(FabricaHeroes.crear(1, "Aldán"));
        estado.getProgresoCampana().avanzarA(CapituloCampana.CAMPANAS_DE_VALDESOMBRA);
        estado.getProgresoCampana().avanzarA(CapituloCampana.CAMINOS_DE_ANIMAS);
        ServicioCapituloDos servicio = new ServicioCapituloDos();

        servicio.conocerAldara(estado, ServicioCapituloDos.ActitudAldara.ESCUCHAR);
        assertEquals(1, servicio.registrarVictoria(estado, Region.BRANAS_HUNDIDAS));
        assertEquals(2, servicio.registrarVictoria(estado, Region.BRANAS_HUNDIDAS));
        assertEquals(1, servicio.registrarVictoria(estado, Region.CAMINO_DE_LOS_DIFUNTOS));
        assertEquals(2, servicio.registrarVictoria(estado, Region.CAMINO_DE_LOS_DIFUNTOS));
        assertTrue(estado.getProgresoCampana().haDecidido("cap2.branas.jefe_disponible"));
        assertTrue(estado.getProgresoCampana().haDecidido("cap2.camino.jefe_disponible"));

        servicio.registrarJefe(estado, Region.BRANAS_HUNDIDAS);
        servicio.registrarJefe(estado, Region.CAMINO_DE_LOS_DIFUNTOS);
        assertTrue(servicio.puedeCerrar(estado));
        servicio.completar(estado, ServicioCapituloDos.Verdad.REVELAR);

        assertEquals(CapituloCampana.DEUDA_DE_LOS_VIVOS, estado.getProgresoCampana().getCapitulo());
        assertTrue(estado.getProgresoCampana().estaDesbloqueada(Region.MINAS_DE_SAN_LOURENZO));
        assertTrue(estado.getProgresoCampana().estaDesbloqueada(Region.PAZO_DE_SOUTOMAIOR));
        assertTrue(estado.getProgresoCampana().haDecidido("cap2.matanza_revelada"));
    }

    @Test
    void generaContenidoDiferenciadoParaAmbasRegiones() {
        assertEquals(Region.BRANAS_HUNDIDAS,
                GestorMisiones.generarRegional(Region.BRANAS_HUNDIDAS, 2, Dificultad.FACIL).getRegion());
        assertEquals(Region.CAMINO_DE_LOS_DIFUNTOS,
                GestorMisiones.generarRegional(Region.CAMINO_DE_LOS_DIFUNTOS, 2, Dificultad.MEDIA).getRegion());
        assertEquals("A Lavandeira Maior", Bestiario.crearLavandeiraMaior(2).getNombre());
        assertEquals("El Hospitalario", Bestiario.crearHospitalario(2).getNombre());
    }
}
