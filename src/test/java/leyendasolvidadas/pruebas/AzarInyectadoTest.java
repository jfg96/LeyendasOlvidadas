package leyendasolvidadas.pruebas;

import leyendasolvidadas.dominio.azar.AzarJava;
import leyendasolvidadas.dominio.azar.FuenteAzar;
import leyendasolvidadas.dominio.combate.Enemigo;
import leyendasolvidadas.dominio.combate.Meiga;
import leyendasolvidadas.dominio.objetos.Item;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AzarInyectadoTest {
    @Test
    void unEnemigoUsaLaFuenteAsignadaEnLugarDeUnGeneradorGlobal() {
        Enemigo enemigo = new Enemigo("Prueba", 3, false);
        enemigo.configurarAzar(new Minimo());

        assertEquals(9, enemigo.getOro());
        assertEquals(9, enemigo.getOro());
    }

    @Test
    void probabilidadContinuaAceptaFraccionesSinRedondear() {
        assertEquals(false, new Umbral(0).probabilidad(0.0));
        assertEquals(false, new Umbral(0).probabilidad(-0.5));
        assertEquals(true, new Umbral(0).probabilidad(0.3));
        assertEquals(true, new Umbral(49999).probabilidad(0.5));
        assertEquals(false, new Umbral(50000).probabilidad(0.5));
        assertEquals(true, new Umbral(0).probabilidad(1.0));
        assertEquals(true, new Umbral(0).probabilidad(2.0));
    }

    @Test
    void soltarBotinEsReproducibleConLaMismaSemilla() {
        assertEquals(secuenciaBotin(11L), secuenciaBotin(11L));
    }

    private static List<String> secuenciaBotin(long semilla) {
        Enemigo enemigo = new Enemigo("B", 4, false);
        enemigo.configurarAzar(new AzarJava(semilla));
        List<String> resultados = new ArrayList<>();
        for (double prob : new double[]{0.30, 0.8775, 1.2, 0.56, 0.77}) {
            Item soltado = enemigo.soltarBotin(new Meiga("H"), prob, 5);
            resultados.add(soltado == null ? "NADA" : soltado.getNombre());
        }
        return resultados;
    }

    private static final class Minimo implements FuenteAzar {
        @Override public int entre(int minimo, int maximo) { return minimo; }
        @Override public boolean probabilidad(int porcentaje) { return false; }
    }

    private static final class Umbral implements FuenteAzar {
        private final int tirada;
        Umbral(int tirada) { this.tirada = tirada; }
        @Override public int entre(int minimo, int maximo) { return tirada; }
        @Override public boolean probabilidad(int porcentaje) { return porcentaje > tirada; }
    }
}
