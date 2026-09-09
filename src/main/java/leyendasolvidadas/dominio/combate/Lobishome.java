package leyendasolvidadas.dominio.combate;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;

import java.util.List;

/** Héroe de vanguardia centrado en sangrado y robo de vida. */
public class Lobishome extends Personaje {
    public Lobishome(String nombre) {
        super(nombre, 1, 110, 3, 6, 12, 4, 100, 22, 13, 18, 12, 0,
                "La bestia gana terreno bajo tu piel. (+Vida, +Furia)");
        setArma(new Arma("Zarpa Lobuna", 6, Rareza.COMUN));
        asignarHabilidades(List.of(
            new Habilidad("Zarpazo Sangrante", "Garra que abre la carne", 10, 0, 1.0,
                    new int[]{1, 2}, false, TipoEfecto.SANGRADO, 80, 3, 0, false, 0, 0, 0),
            new Habilidad("Mordisco Feroz", "Dentellada que desangra y devora", 20, 1, 1.4,
                    new int[]{1}, false, TipoEfecto.SANGRADO, 100, 2, 0, false, 0, 0.3, 0),
            new Habilidad("Aullido", "Aulla a la luna y se envalentona", 20, 3, 0,
                    new int[]{}, false, TipoEfecto.FORTALECIDO, 100, 3, 0, true, 0, 0, -4),
            new Habilidad("Frenesi Lunar", "Descuartiza a cuanto tiene delante", 35, 3, 0.9,
                    new int[]{1, 2}, true, TipoEfecto.SANGRADO, 70, 3, 0, false, 0, 0.2, 0)
        ));
    }
    @Override public String nombreRecurso() { return "Furia"; }
}
