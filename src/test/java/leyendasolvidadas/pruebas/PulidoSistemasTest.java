package leyendasolvidadas.pruebas;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.Enemigo;
import leyendasolvidadas.dominio.compania.FabricaHeroes;
import leyendasolvidadas.dominio.mundo.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PulidoSistemasTest {
    @Test
    void lasMejorasDeAldeaConsumenEconomiaYTienenLimites() {
        EstadoJuego e = new EstadoJuego(); e.setJugador(FabricaHeroes.crear(1, "Aldán"));
        e.getCompania().getInventario().ganarOro(2000); ServicioAldea aldea = new ServicioAldea();
        int oro = e.getCompania().getInventario().getOro();
        assertTrue(aldea.mejorar(e, EdificioAldea.HERRERIA).exito());
        assertEquals(2, e.getEstadoAldea().nivel(EdificioAldea.HERRERIA));
        assertTrue(e.getCompania().getInventario().getOro() < oro);
        assertTrue(aldea.mejorar(e, EdificioAldea.HERRERIA).exito());
        assertFalse(aldea.mejorar(e, EdificioAldea.HERRERIA).exito());
    }

    @Test
    void losEnemigosAnuncianUnaIntencionAntesDeActuar() {
        Enemigo enemigo = Bestiario.crearGrupo(Region.BOSQUE_DE_LOS_AHORCADOS, 2, Dificultad.MEDIA).get(0);
        enemigo.prepararIntencion(1);
        assertNotNull(enemigo.getIntencion());
        assertSame(enemigo.getIntencion(), enemigo.consumirIntencion(1));
        assertNull(enemigo.getIntencion());
    }
}
