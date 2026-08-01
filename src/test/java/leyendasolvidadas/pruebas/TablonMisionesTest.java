package leyendasolvidadas.pruebas;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.compania.FabricaHeroes;
import leyendasolvidadas.dominio.misiones.MisionId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TablonMisionesTest {
    @Test
    void siempreGeneraTresOfertasOrdinarias() {
        EstadoJuego estado = estadoInicial();
        ServicioTablonMisiones.Tablon tablon = new ServicioTablonMisiones().generar(estado);

        assertEquals(3, tablon.ordinarias().size());
        assertTrue(tablon.especiales().isEmpty());
    }

    @Test
    void ofreceElJefeNarrativoCuandoSeCumplenSusRequisitos() {
        EstadoJuego estado = estadoInicial();
        estado.getProgresoCampana().registrarDecision("cap1.simbolo_peregrinos_descubierto");

        ServicioTablonMisiones.Tablon tablon = new ServicioTablonMisiones().generar(estado);

        assertTrue(tablon.especiales().stream().anyMatch(m -> m.getId() == MisionId.REY_SOGAS));
    }

    private static EstadoJuego estadoInicial() {
        EstadoJuego estado = new EstadoJuego();
        estado.setJugador(FabricaHeroes.crear(1, "Aldán"));
        return estado;
    }
}
