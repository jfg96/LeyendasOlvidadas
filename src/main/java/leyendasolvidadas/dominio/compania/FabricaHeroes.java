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
    private static final List<String> ORIGENES = List.of(
            "Las brañas de Cernadas", "Un caserío del Camino Viejo", "Las minas de San Lourenzo",
            "Una romería sin regreso", "Los montes al norte de Valdesombra");
    private static final List<String> DESCRIPCIONES = List.of(
            "Habla poco y duerme siempre mirando hacia la puerta.",
            "Conoce oraciones que ningún párroco admitiría haber enseñado.",
            "Lleva barro seco en las botas aunque no haya llovido.",
            "Busca trabajo desde que su aldea desapareció de los mapas.",
            "Asegura haber oído su nombre dentro de una campana rota.");
    private static final List<String> RASGOS = List.of("Temple de hierro", "Ojo para el peligro",
            "Manos firmes", "Lealtad obstinada", "Instinto de supervivencia");
    private static final List<String> DEFECTOS = List.of("Miedo al agua estancada", "Aversión a las campanas",
            "Codicia", "Sueño intranquilo", "Desconfianza hacia la Iglesia");
    private static final List<String> MOTIVACIONES = List.of("Pagar una deuda familiar", "Encontrar a un desaparecido",
            "Comprar tierras lejos de la niebla", "Demostrar que los muertos mienten", "Recuperar su buen nombre");
    private static final List<String> FRASES = List.of("Cobro por adelantado. Los muertos nunca pagan.",
            "Si el bosque dice mi nombre, no respondáis.", "No soy valiente; simplemente ya no tengo adónde volver.",
            "Una vela basta, si quien la lleva no tiembla.", "Todo monstruo deja huellas. Incluso los que rezan.");

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
        candidato.setTrasfondo(new TrasfondoMercenario(Rng.elegir(ORIGENES), Rng.elegir(DESCRIPCIONES),
                Rng.elegir(RASGOS), Rng.elegir(DEFECTOS), Rng.elegir(MOTIVACIONES), Rng.elegir(FRASES)));
        return candidato;
    }
}
