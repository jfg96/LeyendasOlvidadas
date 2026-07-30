package leyendasolvidadas.dominio.compania;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.objetos.*;

import java.util.List;

/** Construye heroes jugables para la creacion inicial y la contratacion. */
public final class FabricaHeroes {
    private static final List<String> NOMBRES = List.of(
            "Aldara", "Bieito", "Catuxa", "Dinis", "Elvira", "Froilan",
            "Iria", "Lope", "Mencia", "Nuno", "Sabela", "Xoan");

    private FabricaHeroes() {}

    public static Personaje crear(int clase, String nombre) {
        return switch (clase) {
            case 1 -> new Alabardero(nombre, new Arma("Alabarda Mellada", 6, Rareza.COMUN));
            case 2 -> new Animero(nombre);
            case 3 -> new Bandolero(nombre, new Arma("Daga Cachicuerna", 5, Rareza.COMUN));
            case 4 -> new Meiga(nombre);
            case 5 -> new Montero(nombre);
            case 6 -> new Gaitero(nombre);
            case 7 -> new Lobishome(nombre);
            case 8 -> new Zahori(nombre);
            case 9 -> new Fraile(nombre);
            default -> throw new IllegalArgumentException("Clase de heroe desconocida: " + clase);
        };
    }

    public static Personaje candidatoAleatorio(int nivelObjetivo) {
        String nombre = Rng.elegir(NOMBRES);
        Personaje candidato = crear(Rng.entre(1, 9), nombre);
        candidato.prepararNivelInicial(Math.max(1, nivelObjetivo));
        return candidato;
    }
}
