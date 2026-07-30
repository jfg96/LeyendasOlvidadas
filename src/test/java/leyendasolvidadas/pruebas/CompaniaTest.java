package leyendasolvidadas.pruebas;

import org.junit.jupiter.api.Test;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.infraestructura.*;
import leyendasolvidadas.interfaz.consola.*;

import java.util.List;

/** Pruebas sin dependencias de las invariantes del modelo de compania. */
public class CompaniaTest {
    @Test
    void mantieneLasInvariantesDeLaCompania() throws Exception {
        Personaje protagonista = new Animero("Fundador");
        Personaje meiga = new Meiga("Iria");
        Personaje fraile = new Fraile("Bieito");
        Compania compania = new Compania(protagonista);

        comprobar(compania.getProtagonista() == protagonista, "Debe conservar al protagonista");
        comprobar(compania.getInventario() == protagonista.getInventario(),
                "El inventario fundador debe convertirse en el inventario compartido");
        comprobar(!compania.despedir(protagonista), "No debe permitir despedir al protagonista");
        comprobar(compania.contratar(meiga), "Debe permitir contratar un miembro");
        comprobar(compania.contratar(fraile), "Debe permitir contratar un segundo miembro");
        comprobar(!compania.contratar(meiga), "No debe duplicar miembros");

        compania.prepararFormacion(List.of(protagonista, meiga, fraile));
        comprobar(compania.estaCompleta(), "La formacion de tres debe estar completa");
        esperarError(() -> compania.prepararFormacion(List.of(meiga, fraile)),
                "Debe exigir al protagonista en la formacion");
        esperarError(() -> compania.prepararFormacion(List.of(protagonista, meiga, meiga)),
                "Debe rechazar miembros repetidos");

        comprobar(compania.despedir(meiga), "Debe permitir despedir a un acompanante");
        comprobar(!compania.getFormacionActiva().contains(meiga),
                "Un despedido no puede seguir en la formacion");

        Habilidad ensalmo = new Meiga("Leria").getHabilidades().get(1);
        comprobar(ensalmo.esSobreAliado(), "El ensalmo debe poder dirigirse a otro aliado");

        EstadoJuego antiguo = new EstadoJuego();
        Personaje legado = new Animero("Legado");
        legado.getInventario().ganarOro(40);
        antiguo.setJugador(legado);
        var campoCompania = EstadoJuego.class.getDeclaredField("compania");
        campoCompania.setAccessible(true);
        campoCompania.set(antiguo, null);
        comprobar(antiguo.getCompania().getInventario().getOro() == 130,
                "Un guardado antiguo debe recibir capital suficiente para fundar la compania");
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }

    private static void esperarError(Runnable accion, String mensaje) {
        try {
            accion.run();
            throw new AssertionError(mensaje);
        } catch (IllegalArgumentException esperada) {
            // Resultado esperado.
        }
    }
}
