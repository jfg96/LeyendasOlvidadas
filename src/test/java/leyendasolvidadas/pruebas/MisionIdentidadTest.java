package leyendasolvidadas.pruebas;

import leyendasolvidadas.dominio.azar.AzarJava;
import leyendasolvidadas.dominio.azar.FuenteAzar;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.Dificultad;
import leyendasolvidadas.dominio.mundo.Region;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** Verifica que la identidad de los encargos no dependa de su texto visible. */
class MisionIdentidadTest {
    @Test
    void conservaLaIdentidadAunqueCambieElTitulo() {
        Mision mision = new MisionJefe(MisionId.REY_SOGAS, "Un título revisado",
                "La misma misión con otro texto.", Dificultad.MEDIA, 200, 300, null, false);

        assertEquals(MisionId.REY_SOGAS, mision.getId());
        assertEquals("Un título revisado", mision.getNombre());
    }

    @Test
    void lasFabricasRegionalesAsignanIdentificadoresEspecificos() {
        comprobarRegion(Region.BRANAS_HUNDIDAS,
                EnumSet.of(MisionId.RESPIRAN_BARRO, MisionId.TUMBA_ALDARA));
        comprobarRegion(Region.CAMINO_DE_LOS_DIFUNTOS,
                EnumSet.of(MisionId.CAMPANAS_SIN_CAMPANERO, MisionId.PAGINA_SIN_NOMBRES));
        comprobarRegion(Region.MINAS_DE_SAN_LOURENZO,
                EnumSet.of(MisionId.HIERRO_PARA_MUERTOS, MisionId.GALERIAS_BAJO_PAZO));
        comprobarRegion(Region.PAZO_DE_SOUTOMAIOR,
                EnumSet.of(MisionId.CRIADOS_SIN_NOMBRE, MisionId.INVENTARIO_CULPABLES));

        assertEquals(MisionId.TABLILLAS_PATIBULO, GestorMisiones.generarLibroNombres(
                Region.BOSQUE_DE_LOS_AHORCADOS, 2, Dificultad.FACIL).getId());
        assertEquals(MisionId.LETANIA_AUSENTES, GestorMisiones.generarLibroNombres(
                Region.CAMINO_DE_LOS_DIFUNTOS, 2, Dificultad.FACIL).getId());
        assertEquals(MisionId.ARBOL_CULPA, GestorMisiones.generarLibroNombres(
                Region.PAZO_DE_SOUTOMAIOR, 2, Dificultad.FACIL).getId());
    }

    private static void comprobarRegion(Region region, Set<MisionId> esperados) {
        FuenteAzar azar = new AzarJava(region.ordinal());
        for (int i = 0; i < 20; i++)
            assertTrue(esperados.contains(GestorMisiones.generarRegional(
                    region, 2, Dificultad.FACIL, azar).getId()));
    }
}
