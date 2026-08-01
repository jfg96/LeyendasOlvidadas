package leyendasolvidadas.pruebas;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.Personaje;
import leyendasolvidadas.dominio.compania.FabricaHeroes;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.Dificultad;
import leyendasolvidadas.dominio.mundo.Region;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Comprueba las consecuencias de una expedición sin arrancar la consola. */
class ResolucionExpedicionTest {
    private final ServicioResolucionExpedicion servicio = new ServicioResolucionExpedicion();

    @Test
    void aplicaRecompensasYProgresoTrasUnaVictoria() {
        EstadoJuego estado = estadoInicial();
        Personaje protagonista = estado.getJugador();
        Mision mision = new MisionCaza(Dificultad.FACIL, 1, 80, 40, null)
                .enRegion(Region.BOSQUE_DE_LOS_AHORCADOS);

        ServicioResolucionExpedicion.Resolucion resolucion =
                servicio.resolver(estado, mision, ResultadoExpedicion.VICTORIA);

        assertEquals(2, estado.getSemana());
        assertEquals(1, estado.getExpedicionesGanadas());
        assertEquals(80, protagonista.getInventario().getOro());
        assertEquals(42, protagonista.getExperiencia());
        assertEquals(80, resolucion.oroRecibido());
        assertEquals(42, resolucion.experienciaRecibida());
        assertEquals(1, estado.getRegistroCampana().getDiario().size());
    }

    @Test
    void aplicaLaPenalizacionYRecuperacionTrasUnaDerrota() {
        EstadoJuego estado = estadoInicial();
        Personaje protagonista = estado.getJugador();
        protagonista.getInventario().ganarOro(101);
        protagonista.setVida(1);
        Mision mision = new MisionExploracion(Dificultad.MEDIA, 50, 50, null);

        ServicioResolucionExpedicion.Resolucion resolucion =
                servicio.resolver(estado, mision, ResultadoExpedicion.DERROTA);

        assertEquals(50, resolucion.oroPerdido());
        assertEquals(51, protagonista.getInventario().getOro());
        assertEquals(protagonista.getVidaMax() * 0.5, protagonista.getVida());
        assertEquals(2, estado.getSemana());
    }

    @Test
    void comunicaLosCierresNarrativosSinInvocarUnaInterfaz() {
        EstadoJuego estado = estadoInicial();
        Mision jefe = new MisionJefe(MisionId.SUDARIOS_ALDARA, "Título modificable", "Descripción",
                Dificultad.MEDIA, 100, 100, null, false).enRegion(Region.BRANAS_HUNDIDAS);

        ServicioResolucionExpedicion.Resolucion resolucion =
                servicio.resolver(estado, jefe, ResultadoExpedicion.VICTORIA);

        assertTrue(estado.getProgresoCampana().haDecidido("cap2.lavandeira_derrotada"));
        assertFalse(resolucion.requiereCierreCapituloDos());
        assertFalse(resolucion.requiereFinal());
    }

    private static EstadoJuego estadoInicial() {
        EstadoJuego estado = new EstadoJuego();
        estado.setJugador(FabricaHeroes.crear(1, "Aldán"));
        return estado;
    }
}
