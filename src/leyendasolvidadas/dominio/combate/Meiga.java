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
 * Meiga: la Curandera de la aldea. Fragil en ataque pero se sostiene sola
 * con ensalmos que regeneran la vida y calman la mente; drena la esencia
 * de sus enemigos. Canaliza la Fe. En grupo, la columna vertebral del soporte.
 */
public class Meiga extends Personaje {
    private final List<Habilidad> habilidades;

    public Meiga(String nombre) {
        super(nombre, 1, 85, 2, 8, 6, 4, 100, 14);
        setArma(new Arma("Vara de Serbal", 3, Rareza.COMUN));
        habilidades = List.of(
            Habilidad.ataque("Mal de Ojo", "Maldicion a cualquier fila", 5, 0.85, new int[]{1, 2, 3}),
            new Habilidad("Ensalmo", "Rezo que regenera la vida y serena el animo", 20, 3, 0,
                    new int[]{}, false, TipoEfecto.REGENERACION, 100, 3, 12, true, 0, 0, -6).aAliado(),
            new Habilidad("Conxuro", "Ponzona que pudre a la retaguardia", 15, 1, 0.7,
                    new int[]{2, 3}, false, TipoEfecto.VENENO, 80, 3, 6, false, 0, 0, 0),
            new Habilidad("Bico da Meiga", "Beso que arranca la esencia y la vuelve vida", 25, 2, 1.0,
                    new int[]{1, 2}, false, null, 0, 0, 0, false, 0, 0.6, -3)
        );
    }
    @Override public double ataqueBase() {
        return 10 + 3 * getNivel() + (getArma() != null ? getArma().getDanio() : 0);
    }
    @Override public String nombreRecurso() { return "Fe"; }
    @Override public List<Habilidad> getHabilidades() { return habilidades; }
    @Override public void subirNivel() {
        super.subirNivel();
        setVidaMaxBase(getVidaMaxBase() + 14);
        setVida(getVidaMax());
        setRecursoMax(getRecursoMax() + 18);
        setRecurso(getRecursoMax());
        logProgresion("Los viejos ensalmos brotan mas hondos. (+Vida, +Fe)");
    }
}
