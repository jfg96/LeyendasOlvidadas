package leyendasolvidadas.pruebas;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.combate.Personaje;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.misiones.MisionPersonal;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MisionesPersonalesTest {
    @Test
    void completaDosEtapasYConsolidaLaLealtadDelMercenario() {
        EstadoJuego estado = new EstadoJuego(); estado.setJugador(FabricaHeroes.crear(1, "Aldán"));
        avanzar(estado, CapituloCampana.DEUDA_DE_LOS_VIVOS);
        Personaje retornado = FabricaHeroes.crearUnico(MercenarioUnico.EL_RETORNADO);
        Personaje apoyo = FabricaHeroes.crear(4, "Iria");
        estado.getCompania().contratar(retornado); estado.getCompania().contratar(apoyo);
        estado.getCompania().prepararFormacion(List.of(estado.getJugador(), retornado, apoyo));
        ServicioMisionesPersonales servicio = new ServicioMisionesPersonales();

        MisionPersonal primera = servicio.disponibles(estado).get(0);
        assertEquals(1, primera.getEtapa()); assertFalse(servicio.registrarVictoria(estado, primera));
        MisionPersonal segunda = servicio.disponibles(estado).get(0);
        assertEquals(2, segunda.getEtapa()); assertTrue(servicio.registrarVictoria(estado, segunda));
        assertTrue(servicio.resolver(estado, MercenarioUnico.EL_RETORNADO,
                ServicioMisionesPersonales.Desenlace.LEALTAD_PERMANENTE).exito());
        assertEquals(100, retornado.getLealtad());
        assertTrue(estado.getProgresoCampana().haDecidido("personal.el_retornado.resuelta"));
        assertTrue(servicio.disponibles(estado).isEmpty());
    }

    @Test
    void losMercenariosUnicosAparecenUnaSolaVezTrasSerContratados() {
        EstadoJuego estado = new EstadoJuego(); estado.setJugador(FabricaHeroes.crear(1, "Aldán"));
        avanzar(estado, CapituloCampana.CAMPANAS_DE_VALDESOMBRA);
        estado.getCompania().getInventario().ganarOro(500); estado.renovarContratacion();
        Personaje unico = estado.getCandidatos().stream().filter(p -> p.getIdentidadUnica() != null).findFirst().orElseThrow();
        assertTrue(new ServicioCompania().contratar(estado, unico).exito());
        MercenarioUnico identidad = unico.getIdentidadUnica();
        estado.renovarContratacion();
        assertTrue(estado.getCandidatos().stream().noneMatch(p -> p.getIdentidadUnica() == identidad));
    }

    private void avanzar(EstadoJuego estado, CapituloCampana destino) {
        while (estado.getProgresoCampana().getCapitulo().ordinal() < destino.ordinal())
            estado.getProgresoCampana().avanzarA(CapituloCampana.values()[estado.getProgresoCampana().getCapitulo().ordinal() + 1]);
    }
}
