package leyendasolvidadas.dominio.combate;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;

import java.util.List;

/** Héroe de apoyo que recupera y refuerza al grupo. */
public class Gaitero extends Personaje {
    public Gaitero(String nombre) {
        super(nombre, 1, 95, 4, 8, 8, 5, 100, 16, 11, 16, 14, 0,
                "Tu gaita llega mas lejos que el miedo. (+Vida, +Aliento)");
        setArma(new Arma("Punal del Juglar", 4, Rareza.COMUN));
        asignarHabilidades(List.of(
            new Habilidad("Copla Hiriente", "Verso mordaz que hiere y anima", 6, 0, 0.9,
                    new int[]{1, 2, 3}, false, null, 0, 0, 0, false, 0, 0, -3),
            new Habilidad("Aturuxo", "Grito de guerra: se crece ante el peligro", 20, 3, 0,
                    new int[]{}, false, TipoEfecto.FORTALECIDO, 100, 3, 0, true, 0, 0, -6),
            new Habilidad("Alborada", "Melodia serena que regenera y calma la mente", 22, 3, 0,
                    new int[]{}, false, TipoEfecto.REGENERACION, 100, 3, 12, true, 0, 0, -12).aAliado(),
            new Habilidad("Muneira Marcial", "Compas atronador que quiebra a toda la horda", 30, 3, 0.75,
                    new int[]{1, 2, 3}, true, TipoEfecto.DEBILITADO, 60, 2, 0, false, 0, 0, 0)
        ));
    }
    @Override public String nombreRecurso() { return "Aliento"; }
}
