package leyendasolvidadas.dominio.combate;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;

import java.util.List;

/** Héroe de vanguardia especializado en defensa. */
public class Alabardero extends Personaje {
    public Alabardero(String nombre, Arma armaInicial) {
        super(nombre, 1, 110, 5, 5, 6, 3, 100, 25,
                new ProgresionClase(14, 22, 10, 2, "El acero pesa menos en tus manos. (+Vida, +Defensa, +Aguante)"));
        setArma(armaInicial);
        asignarHabilidades(List.of(
            Habilidad.ataque("Tajo Firme", "Golpe fiable a la vanguardia", 0, 1.0, new int[]{1, 2}),
            new Habilidad("Embestida", "Carga que puede aturdir", 25, 2, 1.3, new int[]{1},
                    false, TipoEfecto.ATURDIDO, 50, 1, 0, false, 0, 0, 0),
            new Habilidad("Muro de Acero", "Se protege y templa el animo", 20, 2, 0, new int[]{},
                    false, TipoEfecto.PROTEGIDO, 100, 2, 0, true, 0, 0, -6),
            new Habilidad("Juicio de Hierro", "Barrido contra toda la formacion", 40, 3, 0.7,
                    new int[]{1, 2, 3}, true, null, 0, 0, 0, false, 0, 0, 0)
        ));
    }
    @Override public String nombreRecurso() { return "Aguante"; }
}
