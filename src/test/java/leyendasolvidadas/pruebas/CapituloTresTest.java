package leyendasolvidadas.pruebas;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.compania.FabricaHeroes;
import leyendasolvidadas.dominio.mundo.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CapituloTresTest {
    @Test
    void descubreLaFalangeDañaLaAldeaYAvanzaElCapitulo() {
        EstadoJuego e = new EstadoJuego(); e.setJugador(FabricaHeroes.crear(1,"Aldán"));
        e.getProgresoCampana().avanzarA(CapituloCampana.CAMPANAS_DE_VALDESOMBRA);
        e.getProgresoCampana().avanzarA(CapituloCampana.CAMINOS_DE_ANIMAS);
        e.getProgresoCampana().avanzarA(CapituloCampana.DEUDA_DE_LOS_VIVOS);
        ServicioCapituloTres s = new ServicioCapituloTres();
        s.conocerGonzalo(e, ServicioCapituloTres.ActitudGonzalo.FINGIR_LEALTAD);
        for(int i=0;i<2;i++) s.registrarVictoria(e, Region.MINAS_DE_SAN_LOURENZO);
        for(int i=0;i<2;i++) s.registrarVictoria(e, Region.PAZO_DE_SOUTOMAIOR);
        s.registrarJefe(e, Region.MINAS_DE_SAN_LOURENZO); s.registrarJefe(e, Region.PAZO_DE_SOUTOMAIOR);
        s.completar(e, ServicioCapituloTres.Defensa.ARCHIVO, ServicioCapituloTres.Alianza.ALDARA);
        assertEquals(CapituloCampana.LIBRO_DE_LOS_NOMBRES, e.getProgresoCampana().getCapitulo());
        assertFalse(e.getEstadoAldea().estaDanado(EdificioAldea.ARCHIVO));
        assertTrue(e.getEstadoAldea().estaDanado(EdificioAldea.ERMITA));
        assertTrue(e.getProgresoCampana().haDecidido("cap3.capitan_en_compana"));
        e.getCompania().getInventario().ganarOro(200);
        assertTrue(new ServicioAldea().reparar(e, EdificioAldea.ERMITA).exito());
        assertFalse(e.getEstadoAldea().estaDanado(EdificioAldea.ERMITA));
    }
}
