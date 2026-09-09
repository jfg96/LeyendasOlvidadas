package leyendasolvidadas.dominio.combate;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;

import java.util.List;

/** Héroe resistente con habilidades de protección y control. */
public class Fraile extends Personaje {
    public Fraile(String nombre) {
        super(nombre, 1, 100, 5, 6, 7, 3, 100, 15, 11, 18, 12, 2,
                "Tu fe pesa mas que el acero. (+Vida, +Defensa, +Fervor)");
        setArma(new Arma("Cruz de Hierro", 5, Rareza.COMUN));
        asignarHabilidades(List.of(
            Habilidad.ataque("Golpe de Fe", "Mazazo sagrado a la vanguardia", 5, 1.0, new int[]{1, 2}),
            new Habilidad("Verbo Sagrado", "Palabra santa que aturde a las animas", 20, 2, 1.1,
                    new int[]{1, 2, 3}, false, TipoEfecto.ATURDIDO, 40, 1, 0, false, 0, 0, -3),
            new Habilidad("Fuego Purificador", "Llama bendita que consume la retaguardia", 22, 2, 0.9,
                    new int[]{2, 3}, false, TipoEfecto.QUEMADURA, 85, 3, 0, false, 0, 0, 0),
            new Habilidad("Amparo Divino", "Se cubre con la fe y aquieta el alma", 20, 3, 0,
                    new int[]{}, false, TipoEfecto.PROTEGIDO, 100, 2, 0, true, 0, 0, -8)
        ));
    }
    @Override public String nombreRecurso() { return "Fervor"; }
}
