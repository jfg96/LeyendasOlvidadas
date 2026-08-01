package leyendasolvidadas.pruebas;

import leyendasolvidadas.aplicacion.EstadoJuego;
import leyendasolvidadas.dominio.azar.AzarJava;
import leyendasolvidadas.dominio.compania.FabricaHeroes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AzarPartidaTest {
    @Test
    void dosPartidasConLaMismaSemillaGeneranLasMismasOfertas() {
        EstadoJuego primera = partida(947);
        EstadoJuego segunda = partida(947);

        assertEquals(primera.getOfertasHerreria().stream().map(i -> i.getNombre()).toList(),
                segunda.getOfertasHerreria().stream().map(i -> i.getNombre()).toList());
        assertEquals(primera.getCandidatos().stream().map(p -> p.getNombre() + p.getClass().getSimpleName()).toList(),
                segunda.getCandidatos().stream().map(p -> p.getNombre() + p.getClass().getSimpleName()).toList());
    }

    private static EstadoJuego partida(long semilla) {
        EstadoJuego estado = new EstadoJuego();
        estado.configurarAzar(new AzarJava(semilla));
        estado.setJugador(FabricaHeroes.crear(1, "Aldán"));
        estado.renovarHerreria();
        estado.renovarContratacion();
        return estado;
    }
}
