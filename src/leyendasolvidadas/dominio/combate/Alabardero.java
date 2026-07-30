package leyendasolvidadas.dominio.combate;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;

import java.util.List;

/**
 * Alabardero: veterano de la Vieja Guardia. Mucha vida y defensa;
 * usa Aguante para sus tecnicas y protege su cordura tras el escudo.
 */
public class Alabardero extends Personaje {
    private final List<Habilidad> habilidades;

    public Alabardero(String nombre, Arma armaInicial) {
        super(nombre, 1, 110, 5, 5, 6, 3, 100, 25);
        setArma(armaInicial);
        habilidades = List.of(
            Habilidad.ataque("Tajo Firme", "Golpe fiable a la vanguardia", 0, 1.0, new int[]{1, 2}),
            new Habilidad("Embestida", "Carga que puede aturdir", 25, 2, 1.3, new int[]{1},
                    false, TipoEfecto.ATURDIDO, 50, 1, 0, false, 0, 0, 0),
            new Habilidad("Muro de Acero", "Se protege y templa el animo", 20, 2, 0, new int[]{},
                    false, TipoEfecto.PROTEGIDO, 100, 2, 0, true, 0, 0, -6),
            new Habilidad("Juicio de Hierro", "Barrido contra toda la formacion", 40, 3, 0.7,
                    new int[]{1, 2, 3}, true, null, 0, 0, 0, false, 0, 0, 0)
        );
    }
    @Override public double ataqueBase() {
        return 14 + 3 * getNivel() + (getArma() != null ? getArma().getDanio() : 0);
    }
    @Override public String nombreRecurso() { return "Aguante"; }
    @Override public List<Habilidad> getHabilidades() { return habilidades; }
    @Override public void subirNivel() {
        super.subirNivel();
        setVidaMaxBase(getVidaMaxBase() + 22);
        setVida(getVidaMax());
        setDefensaBase(getDefensaBase() + 2);
        setRecursoMax(getRecursoMax() + 10);
        setRecurso(getRecursoMax());
        logProgresion("El acero pesa menos en tus manos. (+Vida, +Defensa, +Aguante)");
    }
}
