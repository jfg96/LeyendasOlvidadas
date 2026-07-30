package leyendasolvidadas.aplicacion;

import java.util.List;
import leyendasolvidadas.dominio.combate.Personaje;
import leyendasolvidadas.dominio.compania.Compania;
import leyendasolvidadas.dominio.compania.Inventario;
import leyendasolvidadas.dominio.compania.HeridaPersistente;

/** Casos de uso de plantilla y formacion, reutilizables por cualquier interfaz. */
public class ServicioCompania {
    public enum ResultadoExpedicion { VICTORIA, ABANDONO, DERROTA }
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
        Personaje insumiso = miembros == null ? null : miembros.stream()
                .filter(p -> !estado.getCompania().esProtagonista(p) && p.getLealtad() < 10).findFirst().orElse(null);
        if (insumiso != null) return ResultadoAccion.error(insumiso.getNombre() + " se niega a partir: su lealtad está rota.");
        try {
            estado.getCompania().prepararFormacion(miembros);
            aplicarCohesion(estado.getCompania(), miembros);
            return ResultadoAccion.exito("Formacion preparada con " + miembros.size() + " integrante(s).");
        } catch (IllegalArgumentException e) {
            return ResultadoAccion.error(e.getMessage());
        }
    }

    private void aplicarCohesion(Compania compania, List<Personaje> miembros) {
        for (int i = 0; i < miembros.size(); i++) for (int j = i + 1; j < miembros.size(); j++) {
            int afinidad = compania.afinidad(miembros.get(i), miembros.get(j));
            if (afinidad >= 50) { miembros.get(i).aliviarEstres(3); miembros.get(j).aliviarEstres(3); }
            else if (afinidad <= -50) { miembros.get(i).sufrirEstres(3); miembros.get(j).sufrirEstres(3); }
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

    public void registrarConvivencia(Compania compania, ResultadoExpedicion resultado) {
        List<Personaje> grupo = compania.getFormacionActiva();
        int lealtad = switch (resultado) { case VICTORIA -> 2; case ABANDONO -> -4; case DERROTA -> -10; };
        int afinidad = switch (resultado) { case VICTORIA -> 3; case ABANDONO -> -2; case DERROTA -> 1; };
        for (Personaje heroe : grupo) {
            if (!compania.esProtagonista(heroe)) heroe.modificarLealtad(lealtad);
            if (resultado == ResultadoExpedicion.DERROTA)
                heroe.sufrirHerida(leyendasolvidadas.dominio.azar.Rng.elegir(
                        List.of(HeridaPersistente.values())));
        }
        for (int i = 0; i < grupo.size(); i++) for (int j = i + 1; j < grupo.size(); j++)
            compania.modificarAfinidad(grupo.get(i), grupo.get(j), afinidad);
    }
}
