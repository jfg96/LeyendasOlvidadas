package leyendasolvidadas.dominio.objetos;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.mundo.*;

/** Reliquia equipable con un efecto pasivo. */
public class Amuleto extends Item {
    /** Tipos de bendicion pasiva. */
    public enum Don { CRITICO, ESQUIVA, TEMPLE, CODICIA, FUROR }

    private final Don don;
    private final int potencia;

    public Amuleto(String nombre, Don don, int potencia, Rareza rareza) {
        super(nombre, rareza, 25 + potencia * 5);
        this.don = don;
        this.potencia = (int) Math.round(potencia * rareza.getMult());
    }
    private Amuleto(String nombre, Don don, int potencia, Rareza rareza, boolean restaurado) {
        super(nombre, rareza, 25 + potencia * 5);
        this.don = don;
        this.potencia = potencia;
    }
    public static Amuleto restaurar(String nombre, Don don, int potencia, Rareza rareza) {
        return new Amuleto(nombre, don, potencia, rareza, true);
    }
    public Don getDon() { return don; }
    public int getPotencia() { return potencia; }
    @Override public String descripcion() {
        switch (don) {
            case CRITICO: return "Reliquia | Critico +" + potencia + "%";
            case ESQUIVA: return "Reliquia | Esquiva +" + potencia + "%";
            case TEMPLE:  return "Reliquia | Estres recibido -" + potencia + "%";
            case CODICIA: return "Reliquia | Oro obtenido +" + potencia + "%";
            default:      return "Reliquia | Danio +" + potencia + "%";
        }
    }
    public static Amuleto aleatorio(int bonusRareza) {
        return aleatorio(bonusRareza, FuenteAzar.global());
    }
    public static Amuleto aleatorio(int bonusRareza, FuenteAzar azar) {
        Don d = Don.values()[azar.entre(0, Don.values().length - 1)];
        String[] nombres = {"Higa de Azabache", "Escapulario Raido", "Campanilla de Ermita",
                "Diente de Lobisome", "Cruz de Caravaca", "Medalla del Romero"};
        int pot;
        switch (d) {
            case TEMPLE: case CODICIA: pot = azar.entre(10, 25); break;
            case FUROR: pot = azar.entre(6, 14); break;
            default: pot = azar.entre(5, 12);
        }
        return new Amuleto(azar.elegir(java.util.List.of(nombres)), d, pot, Rareza.sortear(bonusRareza, azar));
    }
}
