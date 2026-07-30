package leyendasolvidadas.aplicacion;

import leyendasolvidadas.dominio.combate.Personaje;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.misiones.MisionPersonal;
import leyendasolvidadas.dominio.mundo.*;

import java.util.ArrayList;
import java.util.List;

/** Generación y consecuencias de las historias de los cinco mercenarios únicos. */
public final class ServicioMisionesPersonales {
    public enum Desenlace { LEALTAD_PERMANENTE, CARGAR_CON_LA_CICATRIZ, ABANDONAR_COMPANIA, SACRIFICIO }

    public List<MisionPersonal> disponibles(EstadoJuego estado) {
        List<MisionPersonal> misiones = new ArrayList<>();
        for (Personaje p : estado.getCompania().getFormacionActiva()) {
            MercenarioUnico u = p.getIdentidadUnica();
            if (u == null || estado.getProgresoCampana().haDecidido(id(u, "resuelta"))) continue;
            if (estado.getProgresoCampana().haDecidido(id(u, "etapa2"))) continue;
            int etapa = estado.getProgresoCampana().haDecidido(id(u, "etapa1")) ? 2 : 1;
            MisionPersonal mision = crear(u, etapa, estado.getCompania().nivelMedio());
            if (estado.getProgresoCampana().estaDesbloqueada(mision.getRegion())) misiones.add(mision);
        }
        return misiones;
    }

    public boolean registrarVictoria(EstadoJuego estado, MisionPersonal mision) {
        Personaje mercenario = buscar(estado, mision.getMercenario());
        if (mercenario == null || !estado.getCompania().getFormacionActiva().contains(mercenario))
            throw new IllegalStateException("El protagonista de la misión no participó");
        estado.getProgresoCampana().registrarDecision(id(mision.getMercenario(), "etapa" + mision.getEtapa()));
        return mision.getEtapa() == 2;
    }

    public ResultadoAccion resolver(EstadoJuego estado, MercenarioUnico unico, Desenlace desenlace) {
        if (!estado.getProgresoCampana().haDecidido(id(unico, "etapa2")))
            return ResultadoAccion.error("La historia personal todavía no ha llegado a su desenlace.");
        Personaje mercenario = buscar(estado, unico);
        if (mercenario == null) return ResultadoAccion.error("El mercenario ya no está en Valdesombra.");
        Personaje protagonista = estado.getCompania().getProtagonista();
        switch (desenlace) {
            case LEALTAD_PERMANENTE -> {
                mercenario.modificarLealtad(100 - mercenario.getLealtad());
                mercenario.setPersonalidadMecanica(RasgoMecanico.LEALTAD_OBSTINADA, mercenario.getDefectoMecanico());
                estado.getCompania().modificarAfinidad(protagonista, mercenario, 40);
            }
            case CARGAR_CON_LA_CICATRIZ -> {
                mercenario.modificarLealtad(80 - mercenario.getLealtad());
                mercenario.sufrirHerida(heridaDe(unico));
                estado.getCompania().modificarAfinidad(protagonista, mercenario, 20);
            }
            case ABANDONAR_COMPANIA -> new ServicioCompania().despedir(estado, mercenario);
            case SACRIFICIO -> {
                new ServicioCompania().despedir(estado, mercenario);
                estado.getProgresoCampana().registrarDecision(id(unico, "muerto"));
            }
        }
        estado.getProgresoCampana().registrarDecision(id(unico, "desenlace." + desenlace.name().toLowerCase()));
        estado.getProgresoCampana().registrarDecision(id(unico, "resuelta"));
        estado.getRegistroCampana().anotar("Historia personal de " + unico.getNombre() + ": "
                + desenlace.name().toLowerCase().replace('_', ' ') + ".");
        return ResultadoAccion.exito(unico.getNombre() + " cierra su historia: "
                + desenlace.name().toLowerCase().replace('_', ' ') + ".");
    }

    private MisionPersonal crear(MercenarioUnico u, int etapa, int nivel) {
        String nombre, descripcion; Region region;
        switch (u) {
            case EL_RETORNADO -> { nombre = etapa == 1 ? "La tumba del Retornado" : "El cadáver que sueña";
                descripcion = etapa == 1 ? "Abrir la fosa de la que escapó El Retornado." : "Encontrar al muerto que camina con su rostro.";
                region = etapa == 1 ? Region.BOSQUE_DE_LOS_AHORCADOS : Region.CAMINO_DE_LOS_DIFUNTOS; }
            case SOR_EREA -> { nombre = etapa == 1 ? "El badajo de Santa Comba" : "Una campana para un santo";
                descripcion = etapa == 1 ? "Recuperar el badajo enterrado por las monjas." : "Hacer sonar la campana donde ningún santo escucha.";
                region = etapa == 1 ? Region.CAMINO_DE_LOS_DIFUNTOS : Region.MINAS_DE_SAN_LOURENZO; }
            case XOAN_DAS_NAVALLAS -> { nombre = etapa == 1 ? "La deuda de las navajas" : "La última traición de Xoán";
                descripcion = etapa == 1 ? "Robar el pagaré que condena a Xoán." : "Entrar en la casa de la familia que lo persigue.";
                region = Region.PAZO_DE_SOUTOMAIOR; }
            case A_FILLA_DO_LOBO -> { nombre = etapa == 1 ? "El olor de la camada" : "Sangre bajo la luna";
                descripcion = etapa == 1 ? "Seguir a los lobos que reconocen su sangre." : "Recuperar el collar de su madre entre los ahogados.";
                region = etapa == 1 ? Region.BOSQUE_DE_LOS_AHORCADOS : Region.BRANAS_HUNDIDAS; }
            case MARTINO_EL_TUERTO -> { nombre = etapa == 1 ? "El ojo en el frasco" : "La mirada de la moura";
                descripcion = etapa == 1 ? "Llevar el ojo de Martiño hasta la fuente sellada." : "Devolver a la moura la visión que le fue robada.";
                region = etapa == 1 ? Region.PAZO_DE_SOUTOMAIOR : Region.BRANAS_HUNDIDAS; }
            default -> throw new IllegalStateException();
        }
        MisionPersonal m = new MisionPersonal(u, etapa, nombre, descripcion,
                etapa == 1 ? Dificultad.MEDIA : Dificultad.DIFICIL, 100 + nivel * 25, 160 + nivel * 35);
        m.enRegion(region); return m;
    }

    private Personaje buscar(EstadoJuego estado, MercenarioUnico unico) {
        return estado.getCompania().getPlantilla().stream().filter(p -> p.getIdentidadUnica() == unico).findFirst().orElse(null);
    }
    private String id(MercenarioUnico unico, String sufijo) { return "personal." + unico.id() + "." + sufijo; }
    private HeridaPersistente heridaDe(MercenarioUnico unico) {
        return switch (unico) { case EL_RETORNADO -> HeridaPersistente.PULMON_QUEMADO;
            case SOR_EREA -> HeridaPersistente.MANO_LESIONADA; case XOAN_DAS_NAVALLAS -> HeridaPersistente.CICATRIZ_PROFUNDA;
            case A_FILLA_DO_LOBO -> HeridaPersistente.RODILLA_DANADA; case MARTINO_EL_TUERTO -> HeridaPersistente.MANO_LESIONADA; };
    }
}
