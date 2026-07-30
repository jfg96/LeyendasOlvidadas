package leyendasolvidadas.dominio.objetos;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.mundo.*;

/** Rarezas de los objetos: multiplican su potencia y su valor. */
public enum Rareza {
    COMUN("Comun", 1.00), RARA("Rara", 1.35),
    EPICA("Epica", 1.75), LEGENDARIA("Legendaria", 2.30);

    private final String nombre; private final double mult;
    Rareza(String n, double m) { nombre = n; mult = m; }
    public String getNombre() { return nombre; }
    public double getMult() { return mult; }

    /** Sortea una rareza; bonus (0..30) mejora las probabilidades (oscuridad, jefes...). */
    public static Rareza sortear(int bonus) {
        int r = Rng.entre(1, 100) - bonus;
        if (r <= 4) return LEGENDARIA;
        if (r <= 16) return EPICA;
        if (r <= 42) return RARA;
        return COMUN;
    }
}
