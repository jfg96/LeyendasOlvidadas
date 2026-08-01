package leyendasolvidadas.pruebas;

import leyendasolvidadas.dominio.azar.FuenteAzar;
import leyendasolvidadas.dominio.combate.Enemigo;
import leyendasolvidadas.dominio.combate.Personaje;
import leyendasolvidadas.dominio.compania.FabricaHeroes;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CicloExpedicionTest {
    @Test
    void completaLosTresObjetivosBasicos() {
        MisionCaza caza = new MisionCaza(Dificultad.FACIL, 2, 10, 10, null);
        caza.notificarMuerte(new Enemigo("Trasgo", 1, false));
        assertFalse(caza.estaCompletada());
        caza.notificarMuerte(new Enemigo("Lobo", 1, false));
        assertTrue(caza.estaCompletada());

        MisionExploracion exploracion = new MisionExploracion(Dificultad.FACIL, 10, 10, null);
        exploracion.notificarVisita(8, 10);
        assertFalse(exploracion.estaCompletada());
        exploracion.notificarVisita(9, 10);
        assertTrue(exploracion.estaCompletada());

        MisionReliquia reliquia = new MisionReliquia(Dificultad.FACIL, 10, 10, null);
        reliquia.notificarObjetivo();
        assertFalse(reliquia.estaCompletada());
        reliquia.notificarEntrada();
        assertTrue(reliquia.estaCompletada());
    }

    @Test
    void seleccionaCadaJefePorIdentificadorYNoPorTitulo() {
        PreparadorEncuentros preparador = new PreparadorEncuentros();
        Map<MisionId, String> esperados = Map.of(
                MisionId.REY_SOGAS, "O Rei dos Aforcados",
                MisionId.SUDARIOS_ALDARA, "A Lavandeira Maior",
                MisionId.PUERTAS_HOSPITAL, "El Hospitalario",
                MisionId.CAMPANA_CAPATAZ, "O Capataz",
                MisionId.CRIPTA_SOUTOMAIOR, "El Custodio de la Cripta",
                MisionId.ULTIMA_PROCESION, "La Santa Compania");

        esperados.forEach((id, nombre) -> {
            Mision mision = new MisionJefe(id, "Título irrelevante", "Descripción",
                    Dificultad.MEDIA, 10, 10, null, id == MisionId.ULTIMA_PROCESION);
            assertEquals(nombre, preparador.crearJefe(mision, 3, 0).getNombre());
        });
    }

    @Test
    void elCampamentoRecuperaAlGrupoYComunicaLaEmboscada() {
        Personaje heroe = FabricaHeroes.crear(1, "Aldán");
        heroe.setVida(1);
        heroe.setRecurso(0);
        CondicionesExpedicion condiciones = new CondicionesExpedicion(null, 1, new AzarFijo(true));
        for (int i = 0; i < 10; i++) condiciones.avanzarSegmento(List.of());

        boolean emboscada = Campamento.descansar(List.of(heroe), condiciones, new AzarFijo(true));

        assertTrue(emboscada);
        assertTrue(heroe.getVida() > 1);
        assertEquals(heroe.getRecursoMax(), heroe.getRecurso());
        assertEquals(80, condiciones.getLuz());
    }

    private record AzarFijo(boolean ocurre) implements FuenteAzar {
        @Override public int entre(int minimo, int maximo) { return minimo; }
        @Override public boolean probabilidad(int porcentaje) { return ocurre; }
    }
}
