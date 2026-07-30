package leyendasolvidadas.dominio.combate;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.infraestructura.*;
import leyendasolvidadas.interfaz.consola.*;

import java.util.List;

/**
 * Fraile: el exorcista errante, azote de animas y de la Santa Compania.
 * Sacerdote resistente que aturde con el Verbo, quema con fuego sagrado y
 * se ampara tras la fe, sereno donde otros enloquecen. Su recurso: el Fervor.
 */
public class Fraile extends Personaje {
    private final List<Habilidad> habilidades;

    public Fraile(String nombre) {
        super(nombre, 1, 100, 5, 6, 7, 3, 100, 15);
        setArma(new Arma("Cruz de Hierro", 5, Rareza.COMUN));
        habilidades = List.of(
            Habilidad.ataque("Golpe de Fe", "Mazazo sagrado a la vanguardia", 5, 1.0, new int[]{1, 2}),
            new Habilidad("Verbo Sagrado", "Palabra santa que aturde a las animas", 20, 2, 1.1,
                    new int[]{1, 2, 3}, false, TipoEfecto.ATURDIDO, 40, 1, 0, false, 0, 0, -3),
            new Habilidad("Fuego Purificador", "Llama bendita que consume la retaguardia", 22, 2, 0.9,
                    new int[]{2, 3}, false, TipoEfecto.QUEMADURA, 85, 3, 0, false, 0, 0, 0),
            new Habilidad("Amparo Divino", "Se cubre con la fe y aquieta el alma", 20, 3, 0,
                    new int[]{}, false, TipoEfecto.PROTEGIDO, 100, 2, 0, true, 0, 0, -8)
        );
    }
    @Override public double ataqueBase() {
        return 11 + 3 * getNivel() + (getArma() != null ? getArma().getDanio() : 0);
    }
    @Override public String nombreRecurso() { return "Fervor"; }
    @Override public List<Habilidad> getHabilidades() { return habilidades; }
    @Override public void subirNivel() {
        super.subirNivel();
        setVidaMaxBase(getVidaMaxBase() + 18);
        setVida(getVidaMax());
        setDefensaBase(getDefensaBase() + 2);
        setRecursoMax(getRecursoMax() + 12);
        setRecurso(getRecursoMax());
        logProgresion("Tu fe pesa mas que el acero. (+Vida, +Defensa, +Fervor)");
    }
}
