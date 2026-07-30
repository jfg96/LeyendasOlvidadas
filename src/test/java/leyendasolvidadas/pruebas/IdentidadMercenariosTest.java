package leyendasolvidadas.pruebas;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.combate.Personaje;
import leyendasolvidadas.dominio.compania.FabricaHeroes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdentidadMercenariosTest {

    @Test
    void generaCandidatosConIdentidadCompleta() {
        Personaje candidato = FabricaHeroes.candidatoAleatorio(2);
        assertNotNull(candidato.getTrasfondo());
        assertFalse(candidato.getTrasfondo().origen().isBlank());
        assertFalse(candidato.getTrasfondo().rasgo().isBlank());
        assertFalse(candidato.getTrasfondo().defecto().isBlank());
        assertFalse(candidato.getTrasfondo().motivacion().isBlank());
        assertFalse(candidato.getTrasfondo().frase().isBlank());
    }

    @Test
    void padreTomeSoloSePresentaUnaVezYRecuerdaLaActitud() {
        EstadoJuego estado = new EstadoJuego();
        estado.setJugador(FabricaHeroes.crear(1, "Aldán"));
        estado.getProgresoCampana().avanzarA(CapituloCampana.CAMPANAS_DE_VALDESOMBRA);
        ServicioCapituloUno servicio = new ServicioCapituloUno();

        assertTrue(servicio.requierePresentacion(estado));
        servicio.conocerPadreTome(estado, ServicioCapituloUno.Actitud.DESCONFIAR);
        assertFalse(servicio.requierePresentacion(estado));
        assertTrue(estado.getProgresoCampana().haDecidido("cap1.padre_tome.desconfiar"));
        assertThrows(IllegalStateException.class,
                () -> servicio.conocerPadreTome(estado, ServicioCapituloUno.Actitud.CONFIAR));
    }
}
