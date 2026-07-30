package leyendasolvidadas.pruebas;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.combate.Personaje;
import leyendasolvidadas.dominio.compania.FabricaHeroes;
import leyendasolvidadas.dominio.misiones.GestorMisiones;
import leyendasolvidadas.dominio.mundo.*;
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

        assertEquals(1, servicio.registrarVictoria(estado, Region.BOSQUE_DE_LOS_AHORCADOS));
        assertEquals(2, servicio.registrarVictoria(estado, Region.BOSQUE_DE_LOS_AHORCADOS));
        assertEquals(3, servicio.registrarVictoria(estado, Region.BOSQUE_DE_LOS_AHORCADOS));
        assertTrue(estado.getProgresoCampana().haDecidido("cap1.simbolo_peregrinos_descubierto"));
        servicio.completarBosque(estado, ServicioCapituloUno.ActitudInes.PROTEGER);
        assertEquals(CapituloCampana.CAMINOS_DE_ANIMAS, estado.getProgresoCampana().getCapitulo());
        assertTrue(estado.getProgresoCampana().estaDesbloqueada(Region.BRANAS_HUNDIDAS));
        assertTrue(estado.getProgresoCampana().estaDesbloqueada(Region.CAMINO_DE_LOS_DIFUNTOS));
        assertTrue(estado.getProgresoCampana().haDecidido("cap1.ines.proteger"));
    }

    @Test
    void generaEncargosYEnemigosPropiosDelBosque() {
        assertEquals(Region.BOSQUE_DE_LOS_AHORCADOS,
                GestorMisiones.generarBosque(1, Dificultad.FACIL).getRegion());
        assertTrue(Bestiario.crearGrupo(Region.BOSQUE_DE_LOS_AHORCADOS, 1, Dificultad.FACIL)
                .stream().allMatch(e -> e.getNombre().equals("Lobo de la Sierra")
                        || e.getNombre().equals("Ahorcado Verde") || e.getNombre().equals("Corvo de Carne")));
    }
}
