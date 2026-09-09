package leyendasolvidadas.dominio.combate;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;

import java.util.List;

/** Heroína de apoyo especializada en curación y veneno. */
public class Meiga extends Personaje {
    public Meiga(String nombre) {
        super(nombre, 1, 85, 2, 8, 6, 4, 100, 14,
                new ProgresionClase(10, 14, 18, 0, "Los viejos ensalmos brotan mas hondos. (+Vida, +Fe)"));
        setArma(new Arma("Vara de Serbal", 3, Rareza.COMUN));
        asignarHabilidades(List.of(
            Habilidad.ataque("Mal de Ojo", "Maldicion a cualquier fila", 5, 0.85, new int[]{1, 2, 3}),
            new Habilidad("Ensalmo", "Rezo que regenera la vida y serena el animo", 20, 3, 0,
                    new int[]{}, false, TipoEfecto.REGENERACION, 100, 3, 12, true, 0, 0, -6).aAliado(),
            new Habilidad("Conxuro", "Ponzona que pudre a la retaguardia", 15, 1, 0.7,
                    new int[]{2, 3}, false, TipoEfecto.VENENO, 80, 3, 6, false, 0, 0, 0),
            new Habilidad("Bico da Meiga", "Beso que arranca la esencia y la vuelve vida", 25, 2, 1.0,
                    new int[]{1, 2}, false, null, 0, 0, 0, false, 0, 0.6, -3)
        ));
    }
    @Override public String nombreRecurso() { return "Fe"; }
}
