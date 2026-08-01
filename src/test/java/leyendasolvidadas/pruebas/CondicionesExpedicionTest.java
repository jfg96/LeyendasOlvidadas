package leyendasolvidadas.pruebas;

import leyendasolvidadas.dominio.azar.FuenteAzar;
import leyendasolvidadas.dominio.combate.Personaje;
import leyendasolvidadas.dominio.combate.TipoEfecto;
import leyendasolvidadas.dominio.compania.FabricaHeroes;
import leyendasolvidadas.dominio.mundo.CondicionesExpedicion;
import leyendasolvidadas.dominio.mundo.Region;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CondicionesExpedicionTest {
    @Test
    void aplicaLosUmbralesDeLuzABotinRarezaYEmboscada() {
        AzarFijo azar = new AzarFijo(false);
        CondicionesExpedicion condiciones = new CondicionesExpedicion(null, 1, azar);

        assertEquals(1.0, condiciones.getMultBotin());
        assertEquals(0, condiciones.getBonusRareza());
        for (int i = 0; i < 6; i++) condiciones.avanzarSegmento(List.of());
        assertEquals(70, condiciones.getLuz());
        assertEquals(1.1, condiciones.getMultBotin());
        assertEquals(4, condiciones.getBonusRareza());
        for (int i = 0; i < 7; i++) condiciones.avanzarSegmento(List.of());
        assertEquals(35, condiciones.getLuz());
        assertEquals(1.3, condiciones.getMultBotin());
        assertEquals(10, condiciones.getBonusRareza());
        for (int i = 0; i < 5; i++) condiciones.avanzarSegmento(List.of());
        assertEquals(10, condiciones.getLuz());
        assertEquals(1.6, condiciones.getMultBotin());
        assertEquals(18, condiciones.getBonusRareza());
    }

    @Test
    void diferenciaElConsumoYLosRiesgosDeCadaRegion() {
        Personaje heroe = FabricaHeroes.crear(1, "Aldán");
        CondicionesExpedicion bosque = new CondicionesExpedicion(
                Region.BOSQUE_DE_LOS_AHORCADOS, 2, new AzarFijo(false));
        bosque.avanzarSegmento(List.of(heroe));
        assertEquals(93, bosque.getLuz());

        CondicionesExpedicion minas = new CondicionesExpedicion(
                Region.MINAS_DE_SAN_LOURENZO, 3, new AzarFijo(true));
        double vida = heroe.getVida();
        CondicionesExpedicion.Paso derrumbe = minas.avanzarSegmento(List.of(heroe));
        assertEquals(90, minas.getLuz());
        assertTrue(derrumbe.derrumbe());
        assertEquals(8, derrumbe.danoDerrumbe());
        assertTrue(heroe.getVida() < vida);

        CondicionesExpedicion branas = new CondicionesExpedicion(
                Region.BRANAS_HUNDIDAS, 2, new AzarFijo(true));
        CondicionesExpedicion.Paso infeccion = branas.avanzarSegmento(List.of(heroe));
        assertSame(heroe, infeccion.infectado());
        assertTrue(heroe.getEfectos().stream().anyMatch(e -> e.getTipo() == TipoEfecto.VENENO));
    }

    private record AzarFijo(boolean ocurre) implements FuenteAzar {
        @Override public int entre(int minimo, int maximo) { return minimo; }
        @Override public boolean probabilidad(int porcentaje) { return ocurre; }
    }
}
