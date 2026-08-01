package leyendasolvidadas.pruebas;

import leyendasolvidadas.dominio.azar.FuenteAzar;
import leyendasolvidadas.dominio.combate.Enemigo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AzarInyectadoTest {
    @Test
    void unEnemigoUsaLaFuenteAsignadaEnLugarDeUnGeneradorGlobal() {
        Enemigo enemigo = new Enemigo("Prueba", 3, false);
        enemigo.configurarAzar(new Minimo());

        assertEquals(9, enemigo.getOro());
        assertEquals(9, enemigo.getOro());
    }

    private static final class Minimo implements FuenteAzar {
        @Override public int entre(int minimo, int maximo) { return minimo; }
        @Override public boolean probabilidad(int porcentaje) { return false; }
    }
}
