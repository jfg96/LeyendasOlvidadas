package leyendasolvidadas.pruebas;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.Personaje;
import leyendasolvidadas.dominio.compania.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DesarrolloCompaniaTest {
    @Test
    void rasgosHeridasLealtadYRelacionesTienenEfectosMecanicos() {
        Personaje a = FabricaHeroes.crear(1, "A");
        Personaje b = FabricaHeroes.crear(2, "B");
        Personaje c = FabricaHeroes.crear(3, "C");
        a.setPersonalidadMecanica(RasgoMecanico.INSTINTO_DE_SUPERVIVENCIA, DefectoMecanico.DESCONFIANZA);
        b.setPersonalidadMecanica(RasgoMecanico.LEALTAD_OBSTINADA, DefectoMecanico.CODICIA);
        Compania compania = new Compania(a); compania.contratar(b); compania.contratar(c);
        compania.prepararFormacion(List.of(a, b, c));
        assertTrue(a.getVidaMax() > a.getVidaMaxBase());
        assertTrue(EstadoJuego.costeContratacion(b) > 35 + b.getNivel() * 30);

        new ServicioCompania().registrarConvivencia(compania, ServicioCompania.ResultadoExpedicion.VICTORIA);
        assertEquals(54, b.getLealtad());
        assertTrue(compania.afinidad(a, b) > 0);
        double vidaAntes = a.getVidaMax();
        assertTrue(a.sufrirHerida(HeridaPersistente.CICATRIZ_PROFUNDA));
        assertTrue(a.getVidaMax() < vidaAntes);
    }

    @Test
    void laErmitaTrataSecuelasConUnCostePersistente() {
        EstadoJuego estado = new EstadoJuego(); Personaje heroe = FabricaHeroes.crear(1, "Aldán");
        estado.setJugador(heroe); estado.getCompania().getInventario().ganarOro(100);
        heroe.sufrirHerida(HeridaPersistente.MANO_LESIONADA);
        int oro = estado.getCompania().getInventario().getOro();
        assertTrue(new ServicioAldea().tratarHerida(estado, heroe, HeridaPersistente.MANO_LESIONADA).exito());
        assertTrue(heroe.getHeridas().isEmpty());
        assertTrue(estado.getCompania().getInventario().getOro() < oro);
    }
}
