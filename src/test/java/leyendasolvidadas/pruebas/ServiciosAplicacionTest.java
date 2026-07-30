package leyendasolvidadas.pruebas;

import org.junit.jupiter.api.Test;

import java.util.List;
import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.Personaje;
import leyendasolvidadas.dominio.compania.FabricaHeroes;
import leyendasolvidadas.dominio.objetos.Pocion;

/** Comprueba casos de uso sin arrancar ninguna interfaz. */
public class ServiciosAplicacionTest {
    @Test
    void ejecutaCasosDeUsoSinInterfaz() {
        EstadoJuego estado = new EstadoJuego();
        Personaje protagonista = FabricaHeroes.crear(1, "Aldan");
        protagonista.getInventario().ganarOro(500);
        estado.setJugador(protagonista);
        estado.renovarContratacion();

        ServicioCompania companias = new ServicioCompania();
        Personaje primero = estado.getCandidatos().get(0);
        Personaje segundo = estado.getCandidatos().get(1);
        comprobar(companias.contratar(estado, primero).exito(), "Debe contratar sin UI");
        comprobar(companias.contratar(estado, segundo).exito(), "Debe contratar al segundo miembro");
        comprobar(companias.prepararFormacion(estado, List.of(protagonista, primero, segundo)).exito(),
                "Debe preparar la formacion sin UI");

        ServicioAldea aldea = new ServicioAldea();
        protagonista.setVida(1);
        comprobar(aldea.sanar(estado, protagonista).exito(), "Debe sanar desde aplicacion");
        comprobar(protagonista.getVida() == protagonista.getVidaMax(), "Debe restaurar toda la vida");
        estado.getOfertasHerreria().add(Pocion.vida());
        comprobar(aldea.comprar(estado, estado.getOfertasHerreria().get(0)).exito(),
                "Debe comprar sin depender de consola");
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }
}
