package leyendasolvidadas.dominio.mundo;

import leyendasolvidadas.dominio.azar.FuenteAzar;
import leyendasolvidadas.dominio.combate.Personaje;

import java.util.List;

/** Recuperación y riesgo nocturno de un campamento de expedición. */
public final class Campamento {
    private Campamento() {}

    public static boolean descansar(List<Personaje> heroes, FuenteLuz luz, FuenteAzar azar) {
        for (Personaje heroe : heroes) {
            if (!heroe.estaVivo()) heroe.setVida(heroe.getVidaMax() * 0.15);
            heroe.curar(heroe.getVidaMax() * 0.35);
            heroe.setRecurso(heroe.getRecursoMax());
            heroe.aliviarEstres(25);
            heroe.limpiarEfectosNegativos();
        }
        luz.subirLuz(30);
        return azar.probabilidad(20);
    }
}
