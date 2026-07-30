package leyendasolvidadas.pruebas;

import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.campana.ProgresoCampana;
import leyendasolvidadas.dominio.mundo.Region;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Comprueba las invariantes del nuevo armazon narrativo. */
class ProgresoCampanaTest {

    @Test
    void avanzaEnOrdenYDesbloqueaRegiones() {
        ProgresoCampana progreso = new ProgresoCampana();

        assertEquals(CapituloCampana.PROLOGO, progreso.getCapitulo());
        assertTrue(progreso.getRegionesDesbloqueadas().isEmpty());

        progreso.avanzarA(CapituloCampana.CAMPANAS_DE_VALDESOMBRA);
        assertTrue(progreso.estaDesbloqueada(Region.BOSQUE_DE_LOS_AHORCADOS));
        assertFalse(progreso.estaDesbloqueada(Region.BRANAS_HUNDIDAS));

        progreso.avanzarA(CapituloCampana.CAMINOS_DE_ANIMAS);
        assertTrue(progreso.estaDesbloqueada(Region.BRANAS_HUNDIDAS));
        assertTrue(progreso.estaDesbloqueada(Region.CAMINO_DE_LOS_DIFUNTOS));
        assertThrows(IllegalArgumentException.class,
                () -> progreso.avanzarA(CapituloCampana.ULTIMA_PROCESION));
    }

    @Test
    void conservaDecisionesMedianteIdentificadoresEstables() {
        ProgresoCampana progreso = new ProgresoCampana();

        assertTrue(progreso.registrarDecision("prologo.nina_recordada"));
        assertFalse(progreso.registrarDecision("prologo.nina_recordada"));
        assertTrue(progreso.haDecidido("prologo.nina_recordada"));
        assertThrows(IllegalArgumentException.class,
                () -> progreso.registrarDecision("Texto libre no estable"));
        assertThrows(UnsupportedOperationException.class,
                () -> progreso.getDecisiones().add("decision.inyectada"));
    }
}
