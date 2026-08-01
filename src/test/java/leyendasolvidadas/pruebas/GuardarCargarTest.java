package leyendasolvidadas.pruebas;

import leyendasolvidadas.aplicacion.EstadoJuego;
import leyendasolvidadas.dominio.compania.FabricaHeroes;
import leyendasolvidadas.infraestructura.GuardarCargar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/** Verifica la escritura segura y la recuperación de partidas. */
class GuardarCargarTest {
    @TempDir Path directorio;

    @Test
    void guardaYCargaEnLaRutaConfigurada() {
        Path partida = directorio.resolve("perfil").resolve("partida.sav");
        GuardarCargar repositorio = new GuardarCargar(partida);
        EstadoJuego estado = partidaEnSemana(3);

        assertFalse(repositorio.existePartida());
        assertTrue(repositorio.guardar(estado));
        assertTrue(repositorio.existePartida());
        EstadoJuego cargado = repositorio.cargar();
        assertNotNull(cargado);
        assertEquals(3, cargado.getSemana());
        assertFalse(Files.exists(partida.resolveSibling("partida.sav.tmp")));
    }

    @Test
    void recuperaElRespaldoSiLaPartidaPrincipalEstaTruncada() throws Exception {
        Path partida = directorio.resolve("partida.sav");
        Path respaldo = directorio.resolve("partida.sav.bak");
        GuardarCargar repositorio = new GuardarCargar(partida);

        assertTrue(repositorio.guardar(partidaEnSemana(2)));
        assertTrue(repositorio.guardar(partidaEnSemana(5)));
        assertTrue(Files.isRegularFile(respaldo));

        Files.write(partida, new byte[]{0x4c, 0x4f});

        EstadoJuego recuperado = repositorio.cargar();
        assertNotNull(recuperado);
        assertEquals(2, recuperado.getSemana());
    }

    private static EstadoJuego partidaEnSemana(int semana) {
        EstadoJuego estado = new EstadoJuego();
        estado.setJugador(FabricaHeroes.crear(1, "Aldán"));
        while (estado.getSemana() < semana) estado.avanzarSemana();
        return estado;
    }
}
