package leyendasolvidadas.dominio.objetos;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.mundo.*;

/** Arma equipable. Su dano escala con la rareza y las mejoras de la herreria. */
public class Arma extends Item {
    private final double danioBase;
    private int mejoras; // niveles de forja

    public Arma(String nombre, double danioBase, Rareza rareza) {
        super(nombre, rareza, (int) (danioBase * 6));
        this.danioBase = danioBase;
    }
    public double getDanio() { return danioBase * getRareza().getMult() + mejoras * 3; }
    public double getDanioBase() { return danioBase; }
    public int getMejoras() { return mejoras; }
    public void mejorar() { mejoras++; }
    @Override public String descripcion() {
        String forja = mejoras > 0 ? " +" + mejoras : "";
        return "Arma" + forja + " | Danio +" + (int) getDanio();
    }
    /** Genera un arma aleatoria acorde al nivel. */
    public static Arma aleatoria(int nivel, int bonusRareza) {
        String[] nombres = {"Espada de Romero", "Hacha del Carbonero", "Estoque Toledano",
                "Maza del Sepulturero", "Hoz Segadora", "Alfanje Herrumbroso"};
        return new Arma(Rng.elegir(java.util.List.of(nombres)),
                6 + nivel * 2 + Rng.entre(0, 3), Rareza.sortear(bonusRareza));
    }
}
