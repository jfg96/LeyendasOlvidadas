package leyendasolvidadas.aplicacion;

import java.util.List;
import leyendasolvidadas.dominio.combate.Personaje;
import leyendasolvidadas.dominio.compania.Compania;
import leyendasolvidadas.dominio.compania.Inventario;

/** Casos de uso de plantilla y formacion, reutilizables por cualquier interfaz. */
public class ServicioCompania {
    public ResultadoAccion contratar(EstadoJuego estado, Personaje candidato) {
        Compania compania = estado.getCompania();
        if (candidato == null || !estado.getCandidatos().contains(candidato))
            return ResultadoAccion.error("El candidato ya no esta disponible.");
        if (compania.plantillaLlena()) return ResultadoAccion.error("La compania ya tiene seis miembros.");
        int coste = EstadoJuego.costeContratacion(candidato);
        if (!compania.getInventario().gastarOro(coste))
            return ResultadoAccion.error("No hay suficientes reales en la tesoreria.");
        if (!compania.contratar(candidato)) {
            compania.getInventario().ganarOro(coste);
            return ResultadoAccion.error("No se pudo incorporar al candidato.");
        }
        estado.getCandidatos().remove(candidato);
        return ResultadoAccion.exito(candidato.getNombre() + " se une a la compania.");
    }

    public ResultadoAccion prepararFormacion(EstadoJuego estado, List<Personaje> miembros) {
        try {
            estado.getCompania().prepararFormacion(miembros);
            return ResultadoAccion.exito("Formacion preparada con " + miembros.size() + " integrante(s).");
        } catch (IllegalArgumentException e) {
            return ResultadoAccion.error(e.getMessage());
        }
    }

    public ResultadoAccion despedir(EstadoJuego estado, Personaje personaje) {
        Compania compania = estado.getCompania();
        if (personaje == null || compania.esProtagonista(personaje))
            return ResultadoAccion.error("El protagonista no puede abandonar su propia leyenda.");
        devolverEquipo(compania.getInventario(), personaje);
        if (!compania.despedir(personaje)) return ResultadoAccion.error("Ese aventurero no pertenece a la compania.");
        return ResultadoAccion.exito(personaje.getNombre() + " abandona Valdesombra.");
    }

    private void devolverEquipo(Inventario almacen, Personaje personaje) {
        if (personaje.getArma() != null && almacen.anadir(personaje.getArma())) personaje.setArma(null);
        if (personaje.getArmadura() != null && almacen.anadir(personaje.getArmadura())) personaje.setArmadura(null);
        if (personaje.getAmuleto() != null && almacen.anadir(personaje.getAmuleto())) personaje.setAmuleto(null);
    }
}
