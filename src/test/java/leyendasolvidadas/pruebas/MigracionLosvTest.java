package leyendasolvidadas.pruebas;

import leyendasolvidadas.aplicacion.EstadoJuego;
import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.combate.Personaje;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.mundo.EdificioAldea;
import leyendasolvidadas.infraestructura.CodecPartida;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Ejercita especímenes completos de cada versión histórica LOSV. */
class MigracionLosvTest {
    @Test
    void migraEspecimenesRealesDesdeV1HastaV6() throws Exception {
        for (int version = 1; version <= 6; version++) {
            EstadoJuego original = crearPartida();
            byte[] especimen = escribir(original, version);
            EstadoJuego migrada = CodecPartida.leer(new DataInputStream(new ByteArrayInputStream(especimen)));

            assertEquals(version, java.nio.ByteBuffer.wrap(especimen).getInt(4));
            assertEquals(6, migrada.getSemana());
            assertEquals("Aldán", migrada.getJugador().getNombre());
            assertEquals(2, migrada.getCompania().getPlantilla().size());
            assertNotNull(migrada.getProgresoCampana());
            assertNotNull(migrada.getEstadoAldea());
            assertNotNull(migrada.getRegistroCampana());
            if (version >= 2) assertEquals(CapituloCampana.CAMPANAS_DE_VALDESOMBRA,
                    migrada.getProgresoCampana().getCapitulo());
            if (version >= 3) assertNotNull(migrada.getCompania().getPlantilla().get(1).getTrasfondo());
            if (version >= 4) assertTrue(migrada.getEstadoAldea().estaDanado(EdificioAldea.ERMITA));
            if (version >= 5) assertEquals(77, migrada.getCompania().getPlantilla().get(1).getLealtad());
            if (version >= 6) assertEquals(MercenarioUnico.SOR_EREA,
                    migrada.getCompania().getPlantilla().get(1).getIdentidadUnica());
        }
    }

    private static EstadoJuego crearPartida() {
        Personaje protagonista = FabricaHeroes.crear(1, "Aldán");
        Personaje mercenaria = FabricaHeroes.crearUnico(MercenarioUnico.SOR_EREA);
        mercenaria.modificarLealtad(27);
        Compania compania = new Compania(protagonista);
        compania.contratar(mercenaria);
        compania.prepararFormacion(List.of(protagonista, mercenaria));
        compania.restaurarAfinidad(protagonista, mercenaria, 35);
        EstadoJuego estado = new EstadoJuego();
        estado.restaurarProgreso(6, 2, false, compania, List.of(), List.of(), estado.getProgresoCampana());
        estado.getProgresoCampana().avanzarA(CapituloCampana.CAMPANAS_DE_VALDESOMBRA);
        estado.getEstadoAldea().danar(EdificioAldea.ERMITA);
        estado.getRegistroCampana().anotar("Entrada que solo conserva LOSV v7.");
        return estado;
    }

    private static byte[] escribir(EstadoJuego estado, int version) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CodecPartida.escribirVersion(new DataOutputStream(bytes), estado, version);
        return bytes.toByteArray();
    }
}
