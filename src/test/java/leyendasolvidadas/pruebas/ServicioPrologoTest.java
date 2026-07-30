package leyendasolvidadas.pruebas;

import leyendasolvidadas.aplicacion.EstadoJuego;
import leyendasolvidadas.aplicacion.ServicioPrologo;
import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.compania.FabricaHeroes;
import leyendasolvidadas.dominio.mundo.Region;
import leyendasolvidadas.infraestructura.CodecPartida;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import static org.junit.jupiter.api.Assertions.*;

/** Verifica el flujo del prólogo y su reanudación desde un guardado intermedio. */
class ServicioPrologoTest {

    @Test
    void reanudaElPrologoYConservaSusConsecuencias() throws Exception {
        ServicioPrologo servicio = new ServicioPrologo();
        EstadoJuego estado = nuevoEstado();

        assertEquals(ServicioPrologo.Paso.CARTA, servicio.pasoActual(estado));
        assertThrows(IllegalStateException.class, () -> servicio.finalizar(estado));
        servicio.leerCarta(estado);
        servicio.elegirMotivacion(estado, ServicioPrologo.Motivacion.CULPA);

        EstadoJuego reanudado = guardarYCargar(estado);
        assertEquals(ServicioPrologo.Paso.FUNERAL, servicio.pasoActual(reanudado));
        assertTrue(reanudado.getProgresoCampana().haDecidido("prologo.motivacion.culpa"));

        servicio.registrarDesaparicion(reanudado);
        servicio.responderALaNina(reanudado, ServicioPrologo.RespuestaNina.ANOTAR_NOMBRE);
        reanudado.getJugador().setVida(0);
        servicio.resolverCementerio(reanudado, ServicioPrologo.ResultadoCementerio.DERROTA);

        assertTrue(reanudado.getJugador().getVida() > 0, "La derrota tutorial no debe bloquear la campaña");
        assertEquals(ServicioPrologo.Paso.DESENLACE, servicio.pasoActual(reanudado));
        servicio.finalizar(reanudado);

        assertEquals(CapituloCampana.CAMPANAS_DE_VALDESOMBRA,
                reanudado.getProgresoCampana().getCapitulo());
        assertTrue(reanudado.getProgresoCampana().haDecidido("prologo.nina_olvidada"));
        assertTrue(reanudado.getProgresoCampana().estaDesbloqueada(Region.BOSQUE_DE_LOS_AHORCADOS));
        assertEquals(ServicioPrologo.Paso.COMPLETADO, servicio.pasoActual(reanudado));
    }

    private static EstadoJuego nuevoEstado() {
        EstadoJuego estado = new EstadoJuego();
        estado.setJugador(FabricaHeroes.crear(1, "Aldán"));
        return estado;
    }

    private static EstadoJuego guardarYCargar(EstadoJuego estado) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CodecPartida.escribir(new DataOutputStream(bytes), estado);
        return CodecPartida.leer(new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())));
    }
}
