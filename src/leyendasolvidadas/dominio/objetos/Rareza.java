package leyendasolvidadas.dominio.objetos;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.infraestructura.*;
import leyendasolvidadas.interfaz.consola.*;

/** Rarezas de los objetos: multiplican su potencia y su valor. */
public enum Rareza {
    COMUN("Comun", 1.00, UI.GRIS), RARA("Rara", 1.35, UI.CIAN),
    EPICA("Epica", 1.75, UI.MAGENTA), LEGENDARIA("Legendaria", 2.30, UI.AMARILLO);

    private final String nombre; private final double mult; private final String color;
    Rareza(String n, double m, String c) { nombre = n; mult = m; color = c; }
    public String getNombre() { return nombre; }
    public double getMult() { return mult; }
    public String getColor() { return color; }

    /** Sortea una rareza; bonus (0..30) mejora las probabilidades (oscuridad, jefes...). */
    public static Rareza sortear(int bonus) {
        int r = Rng.entre(1, 100) - bonus;
        if (r <= 4) return LEGENDARIA;
        if (r <= 16) return EPICA;
        if (r <= 42) return RARA;
        return COMUN;
    }
}
