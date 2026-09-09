package leyendasolvidadas.dominio.combate;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;

import java.util.List;

/** Héroe de control que debilita, aturde y marca enemigos. */
public class Zahori extends Personaje {
    public Zahori(String nombre) {
        super(nombre, 1, 82, 2, 9, 8, 5, 100, 14,
                new ProgresionClase(10, 12, 18, 0, "Los presagios se revelan mas nitidos. (+Presagio)"));
        setArma(new Arma("Pendulo de Azabache", 3, Rareza.COMUN));
        asignarHabilidades(List.of(
            Habilidad.ataque("Mal Presagio", "Vaticinio hiriente a cualquier fila", 5, 0.85, new int[]{1, 2, 3}),
            new Habilidad("Sino Aciago", "Sella su destino: golpeara mas flojo", 18, 1, 0.7,
                    new int[]{1, 2, 3}, false, TipoEfecto.DEBILITADO, 90, 2, 0, false, 0, 0, 0),
            new Habilidad("Sombra del Cuelebre", "Vision aterradora que paraliza", 22, 2, 0.9,
                    new int[]{1, 2}, false, TipoEfecto.ATURDIDO, 45, 1, 0, false, 0, 0, 0),
            new Habilidad("Aojar", "Echa el mal de ojo a toda la formacion", 30, 3, 0.8,
                    new int[]{1, 2, 3}, true, TipoEfecto.MARCADO, 100, 2, 0, false, 0, 0, 0)
        ));
    }
    @Override public String nombreRecurso() { return "Presagio"; }
}
