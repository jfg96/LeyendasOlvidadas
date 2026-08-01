package leyendasolvidadas.dominio.mundo;

import leyendasolvidadas.dominio.combate.Enemigo;
import leyendasolvidadas.dominio.combate.Jefe;
import leyendasolvidadas.dominio.misiones.Mision;

import java.util.List;

/** Selecciona enemigos y jefes a partir del contexto estable de la misión. */
public final class PreparadorEncuentros {
    public List<Enemigo> crearGrupo(Region region, int nivelZona, Dificultad dificultad) {
        return region == null ? Bestiario.crearGrupo(nivelZona, dificultad)
                : Bestiario.crearGrupo(region, nivelZona, dificultad);
    }

    public Jefe crearJefe(Mision mision, int nivelZona, int victoriasPrevias) {
        return switch (mision.getId()) {
            case ULTIMA_PROCESION -> Bestiario.crearJefeFinal(nivelZona);
            case REY_SOGAS -> Bestiario.crearReiAforcados(nivelZona);
            case SUDARIOS_ALDARA -> Bestiario.crearLavandeiraMaior(nivelZona);
            case PUERTAS_HOSPITAL -> Bestiario.crearHospitalario(nivelZona);
            case CAMPANA_CAPATAZ -> Bestiario.crearCapataz(nivelZona);
            case CRIPTA_SOUTOMAIOR -> Bestiario.crearCustodioCripta(nivelZona);
            default -> Bestiario.crearJefe(nivelZona, victoriasPrevias);
        };
    }
}
