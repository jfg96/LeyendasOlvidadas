package leyendasolvidadas.aplicacion;

import java.util.List;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.Inventario;
import leyendasolvidadas.dominio.mundo.FuenteLuz;

/** Puerto de interacción del combate, implementable por consola o JavaFX. */
public interface VistaCombate {
    void mostrarInicio(boolean emboscada, List<Enemigo> enemigos);
    void mostrarEstado(int ronda, int luz, List<Personaje> heroes,
                       List<Enemigo> enemigos, Personaje actor);
    int elegirAccion(Personaje heroe, List<Habilidad> habilidades);
    Personaje elegirAliado(List<Personaje> aliados);
    Enemigo elegirEnemigo(List<Enemigo> alcanzables, List<Enemigo> formacion);
    boolean usarInventario(Inventario inventario, Personaje personaje, FuenteLuz luz);
    void pausa();
}
