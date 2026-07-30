package leyendasolvidadas.pruebas;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.compania.FabricaHeroes;
import leyendasolvidadas.dominio.mundo.Region;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CampanaCompletaTest {
    @Test
    void recorreTodosLosCapitulosSinEstadosImposibles() {
        EstadoJuego e = new EstadoJuego(); e.setJugador(FabricaHeroes.crear(1, "Aldán"));
        ServicioPrologo p = new ServicioPrologo(); p.leerCarta(e);
        p.elegirMotivacion(e, ServicioPrologo.Motivacion.DEBER); p.registrarDesaparicion(e);
        p.responderALaNina(e, ServicioPrologo.RespuestaNina.PROMETER_VOLVER);
        p.resolverCementerio(e, ServicioPrologo.ResultadoCementerio.VICTORIA); p.finalizar(e);

        ServicioCapituloUno c1 = new ServicioCapituloUno(); c1.conocerPadreTome(e, ServicioCapituloUno.Actitud.CONFIAR);
        for (int i = 0; i < 3; i++) c1.registrarVictoria(e, Region.BOSQUE_DE_LOS_AHORCADOS);
        c1.completarBosque(e, ServicioCapituloUno.ActitudInes.PROTEGER);

        ServicioCapituloDos c2 = new ServicioCapituloDos(); c2.conocerAldara(e, ServicioCapituloDos.ActitudAldara.ESCUCHAR);
        for (int i = 0; i < 2; i++) { c2.registrarVictoria(e, Region.BRANAS_HUNDIDAS); c2.registrarVictoria(e, Region.CAMINO_DE_LOS_DIFUNTOS); }
        c2.registrarJefe(e, Region.BRANAS_HUNDIDAS); c2.registrarJefe(e, Region.CAMINO_DE_LOS_DIFUNTOS);
        c2.completar(e, ServicioCapituloDos.Verdad.REVELAR);

        ServicioCapituloTres c3 = new ServicioCapituloTres(); c3.conocerGonzalo(e, ServicioCapituloTres.ActitudGonzalo.RECHAZAR);
        for (int i = 0; i < 2; i++) { c3.registrarVictoria(e, Region.MINAS_DE_SAN_LOURENZO); c3.registrarVictoria(e, Region.PAZO_DE_SOUTOMAIOR); }
        c3.registrarJefe(e, Region.MINAS_DE_SAN_LOURENZO); c3.registrarJefe(e, Region.PAZO_DE_SOUTOMAIOR);
        c3.completar(e, ServicioCapituloTres.Defensa.ARCHIVO, ServicioCapituloTres.Alianza.ALDARA);

        ServicioCapituloCuatro c4 = new ServicioCapituloCuatro(); c4.abrirLibro(e, ServicioCapituloCuatro.CustodiaLibro.COMPANIA);
        c4.registrarHallazgo(e, Region.BOSQUE_DE_LOS_AHORCADOS); c4.registrarHallazgo(e, Region.CAMINO_DE_LOS_DIFUNTOS);
        c4.registrarHallazgo(e, Region.PAZO_DE_SOUTOMAIOR);
        c4.completar(e, ServicioCapituloCuatro.JusticiaFamilias.EXIGIR_REPARACION,
                ServicioCapituloCuatro.NombreCientoTrece.INES_LA_DESMEMORIADA,
                ServicioCapituloCuatro.PreparacionRitual.CAMPANAS);

        ServicioCapituloCinco c5 = new ServicioCapituloCinco(); c5.iniciarProcesion(e, ServicioCapituloCinco.Ruta.CAMPANARIO);
        c5.completar(e, ServicioCapituloCinco.FinalCampana.NOMBRES_DEVUELTOS, null);
        assertEquals(CapituloCampana.EPILOGO, e.getProgresoCampana().getCapitulo());
        assertTrue(e.isCampanaGanada()); assertTrue(e.getProgresoCampana().estaDesbloqueada(Region.HOSPITAL_DEL_CAMINO_VIEJO));
    }
}
