package leyendasolvidadas.pruebas;

import leyendasolvidadas.dominio.combate.Personaje;
import leyendasolvidadas.dominio.compania.FabricaHeroes;
import leyendasolvidadas.dominio.eventos.EventoDominio;
import leyendasolvidadas.dominio.objetos.Pocion;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventosInyectadosTest {
    @Test
    void dosPartidasNoCompartenSusReceptores() {
        List<EventoDominio> primera = new ArrayList<>();
        List<EventoDominio> segunda = new ArrayList<>();
        Personaje uno = FabricaHeroes.crear(1, "Uno");
        Personaje dos = FabricaHeroes.crear(1, "Dos");
        uno.configurarEventos(primera::add);
        dos.configurarEventos(segunda::add);
        uno.setVida(1);
        dos.setVida(1);

        Pocion.vida().usar(uno, null);

        assertFalse(primera.isEmpty());
        assertTrue(segunda.isEmpty());
    }
}
