package leyendasolvidadas.pruebas;

import org.junit.jupiter.api.Test;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.campana.*;
import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.infraestructura.*;
import leyendasolvidadas.interfaz.consola.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

/** Verifica una ida y vuelta completa del formato de guardado versionado. */
public class CodecPartidaTest {
    @Test
    void conservaUnaPartidaCompleta() throws Exception {
        Personaje protagonista = FabricaHeroes.crear(1, "Aldan");
        protagonista.prepararNivelInicial(3);
        protagonista.getArma().mejorar();
        Personaje meiga = FabricaHeroes.crear(4, "Iria");
        meiga.setTrasfondo(new TrasfondoMercenario("Brañas Hundidas", "No mira su reflejo.",
                "Temple", "Miedo al agua", "Encontrar a su hermana", "El barro también recuerda."));
        Personaje gaitero = FabricaHeroes.crear(6, "Xoan");
        Compania compania = new Compania(protagonista);
        compania.contratar(meiga);
        compania.contratar(gaitero);
        compania.prepararFormacion(List.of(protagonista, meiga, gaitero));
        compania.getInventario().ganarOro(237);
        compania.getInventario().anadir(new Armadura("Coraza", 7, 22, Rareza.EPICA));
        compania.getInventario().anadir(new Amuleto("Higa", Amuleto.Don.TEMPLE, 13, Rareza.RARA));

        EstadoJuego original = new EstadoJuego();
        original.restaurarProgreso(8, 5, true, compania,
                List.of(Pocion.antorcha()), List.of(FabricaHeroes.crear(8, "Sabela")));
        original.getProgresoCampana().avanzarA(CapituloCampana.CAMPANAS_DE_VALDESOMBRA);
        original.getProgresoCampana().avanzarA(CapituloCampana.CAMINOS_DE_ANIMAS);
        original.getProgresoCampana().registrarDecision("peregrino.liberado");
        original.getEstadoAldea().danar(EdificioAldea.ARCHIVO);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CodecPartida.escribir(new DataOutputStream(bytes), original);
        EstadoJuego copia = CodecPartida.leer(new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())));

        comprobar(copia.getSemana() == 8, "Debe conservar la semana");
        comprobar(copia.getExpedicionesGanadas() == 5, "Debe conservar las victorias");
        comprobar(copia.isCampanaGanada(), "Debe conservar el final de campana");
        comprobar(copia.getProgresoCampana().getCapitulo() == CapituloCampana.CAMINOS_DE_ANIMAS,
                "Debe conservar el capitulo narrativo");
        comprobar(copia.getProgresoCampana().haDecidido("peregrino.liberado"),
                "Debe conservar las decisiones narrativas");
        comprobar(copia.getProgresoCampana().estaDesbloqueada(Region.BRANAS_HUNDIDAS),
                "Debe conservar las regiones desbloqueadas");
        comprobar(copia.getEstadoAldea().estaDanado(EdificioAldea.ARCHIVO),
                "Debe conservar los daños de Valdesombra");
        comprobar(copia.getCompania().getPlantilla().size() == 3, "Debe conservar la plantilla");
        comprobar("Brañas Hundidas".equals(copia.getCompania().getPlantilla().get(1).getTrasfondo().origen()),
                "Debe conservar la identidad narrativa de los mercenarios");
        comprobar(copia.getCompania().getFormacionActiva().size() == 3, "Debe conservar la formacion");
        comprobar(copia.getCompania().getInventario().getOro() == 237, "Debe conservar el oro");
        comprobar(copia.getCompania().getInventario().getItems().size() == 2, "Debe conservar objetos");
        Armadura armadura = (Armadura) copia.getCompania().getInventario().getItems().get(0);
        comprobar(armadura.getRareza() == Rareza.EPICA && armadura.getDefensa() == 12,
                "Debe conservar rareza y estadisticas sin multiplicarlas dos veces");
        comprobar(copia.getJugador().getNivel() == 3 && copia.getJugador().getArma().getMejoras() == 1,
                "Debe conservar nivel y forja");

        byte[] guardadoV1 = bytes.toByteArray().clone();
        ByteBuffer.wrap(guardadoV1).putInt(4, 1);
        EstadoJuego legado = CodecPartida.leer(new DataInputStream(new ByteArrayInputStream(guardadoV1)));
        comprobar(legado.getProgresoCampana().getCapitulo() == CapituloCampana.EPILOGO,
                "Una campana v1 completada debe migrar al epilogo");
        comprobar(legado.getProgresoCampana().haDecidido("legacy.campana_completada"),
                "La migracion v1 debe dejar una marca narrativa explicita");
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }
}
