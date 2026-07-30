package leyendasolvidadas.dominio.mundo;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.infraestructura.*;
import leyendasolvidadas.interfaz.consola.*;

/** Dificultad de una expedicion. */
public enum Dificultad {
    FACIL("Novicio", 0), MEDIA("Veterano", 1), DIFICIL("Pesadilla", 2);
    private final String titulo; private final int nivelExtra;
    Dificultad(String t, int n) { titulo = t; nivelExtra = n; }
    public String getTitulo() { return titulo; }
    public int getNivelExtra() { return nivelExtra; }
}
