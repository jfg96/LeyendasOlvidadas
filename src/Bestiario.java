import java.util.ArrayList;
import java.util.List;

/**
 * Fabrica de criaturas del folclore iberico. Construye grupos de combate
 * y jefes segun el nivel de la zona.
 */
public final class Bestiario {
    private Bestiario() {}

    private static int[] F(int... f) { return f; }

    // ---------- Criaturas comunes ----------
    private static Enemigo duende(int niv) {
        Enemigo e = new Enemigo("Duende Burlon", niv, false);
        e.anadirMovimiento(MovimientoEnemigo.golpe("Pedrada", 1.0, 3));
        e.anadirMovimiento(new MovimientoEnemigo("Burla Cruel", 0, null, 0, 0, 0, 8, 2, F(1,2,3), false, false));
        e.setFilaPreferida(2);
        return e;
    }
    private static Enemigo anima(int niv) {
        Enemigo e = new Enemigo("Anima en Pena", niv, false);
        e.anadirMovimiento(new MovimientoEnemigo("Lamento Fúnebre", 0.6, null, 0, 0, 0, 7, 2, F(1,2,3), false, false));
        e.anadirMovimiento(MovimientoEnemigo.golpe("Toque Gelido", 1.0, 3));
        e.setFilaPreferida(3);
        return e;
    }
    private static Enemigo lobo(int niv) {
        Enemigo e = new Enemigo("Lobo de la Sierra", niv, false);
        e.anadirMovimiento(new MovimientoEnemigo("Mordisco", 1.0, TipoEfecto.SANGRADO, 40, 2, 2 + niv, 0, 3, F(1,2), false, false));
        e.anadirMovimiento(MovimientoEnemigo.golpe("Zarpazo", 0.9, 2));
        return e;
    }
    private static Enemigo trasgo(int niv) {
        Enemigo e = new Enemigo("Trasgo de Alacena", niv, false);
        e.anadirMovimiento(new MovimientoEnemigo("Trastada", 0.7, null, 0, 0, 0, 5, 2, F(1,2,3), false, false));
        e.anadirMovimiento(MovimientoEnemigo.golpe("Garrotazo", 1.0, 3));
        return e;
    }
    private static Enemigo espectro(int niv) {
        Enemigo e = new Enemigo("Espectro del Camposanto", niv, false);
        e.anadirMovimiento(MovimientoEnemigo.golpe("Guadana Umbria", 1.2, 3));
        e.anadirMovimiento(new MovimientoEnemigo("Susurro Funebre", 0, null, 0, 0, 0, 9, 2, F(1,2,3), false, false));
        return e;
    }
    private static Enemigo meiga(int niv) {
        Enemigo e = new Enemigo("Meiga Oscura", niv, false);
        e.anadirMovimiento(new MovimientoEnemigo("Fuego Fatuo", 0.8, TipoEfecto.QUEMADURA, 60, 3, 2 + niv, 0, 3, F(2,3), false, false));
        e.anadirMovimiento(new MovimientoEnemigo("Maleficio", 0, TipoEfecto.DEBILITADO, 100, 2, 0, 4, 2, F(1,2,3), false, false));
        e.setFilaPreferida(3);
        return e;
    }
    // ---------- Elites ----------
    private static Enemigo lobisome(int niv) {
        Enemigo e = new Enemigo("Lobisome", niv, true);
        e.anadirMovimiento(new MovimientoEnemigo("Desgarro Salvaje", 1.1, TipoEfecto.SANGRADO, 70, 3, 3 + niv, 0, 3, F(1,2), false, false));
        e.anadirMovimiento(new MovimientoEnemigo("Aullido Ancestral", 0, null, 0, 0, 0, 12, 2, F(1,2,3), false, false));
        return e;
    }
    private static Enemigo caballero(int niv) {
        Enemigo e = new Enemigo("Caballero de la Compania", niv, true);
        e.anadirMovimiento(MovimientoEnemigo.golpe("Tajo Espectral", 1.25, 3));
        e.anadirMovimiento(new MovimientoEnemigo("Estandarte del Miedo", 0, TipoEfecto.FORTALECIDO, 100, 2, 0, 10, 2, F(1,2,3), false, true));
        return e;
    }
    private static Enemigo cuelebre(int niv) {
        Enemigo e = new Enemigo("Cuelebre Joven", niv, true);
        e.anadirMovimiento(new MovimientoEnemigo("Aliento de Fuego", 0.9, TipoEfecto.QUEMADURA, 70, 3, 3 + niv, 4, 3, F(1,2,3), false, false));
        e.anadirMovimiento(new MovimientoEnemigo("Coletazo", 1.1, TipoEfecto.ATURDIDO, 25, 1, 0, 0, 2, F(1,2), false, false));
        return e;
    }

    /** Genera un grupo de 1-3 enemigos para un combate normal. */
    public static List<Enemigo> crearGrupo(int nivelZona, Dificultad dif) {
        int niv = Math.max(1, nivelZona + Rng.entre(-1, 1));
        int cuantos = 1 + (Rng.prob(70) ? 1 : 0) + (dif != Dificultad.FACIL && Rng.prob(45) ? 1 : 0);
        List<Enemigo> grupo = new ArrayList<>();
        int probElite = dif == Dificultad.FACIL ? 8 : dif == Dificultad.MEDIA ? 16 : 26;
        for (int i = 0; i < cuantos; i++) {
            if (Rng.prob(probElite) && !hayElite(grupo)) grupo.add(eliteAleatorio(niv + 1));
            else grupo.add(comunAleatorio(niv));
        }
        // La retaguardia prefiere ir detras: ordenar por fila preferida.
        grupo.sort((a, b) -> Integer.compare(a.getFilaPreferida(), b.getFilaPreferida()));
        return grupo;
    }
    private static boolean hayElite(List<Enemigo> g) {
        for (Enemigo e : g) if (e.esElite()) return true;
        return false;
    }
    private static Enemigo comunAleatorio(int niv) {
        switch (Rng.entre(0, 5)) {
            case 0: return duende(niv);
            case 1: return anima(niv);
            case 2: return lobo(niv);
            case 3: return trasgo(niv);
            case 4: return espectro(niv);
            default: return meiga(niv);
        }
    }
    private static Enemigo eliteAleatorio(int niv) {
        switch (Rng.entre(0, 2)) {
            case 0: return lobisome(niv);
            case 1: return caballero(niv);
            default: return cuelebre(niv);
        }
    }

    /** Jefes de expedicion, rotan segun las victorias acumuladas. */
    public static Jefe crearJefe(int nivelZona, int victorias) {
        int niv = nivelZona + 2;
        switch (victorias % 3) {
            case 0: {
                Jefe j = new Jefe("El Ahorcado del Roble", niv, "¡La soga nunca perdona!");
                j.anadirMovimiento(new MovimientoEnemigo("Latigo de Esparto", 1.1, TipoEfecto.SANGRADO, 60, 3, 3 + niv, 0, 3, F(1,2,3), false, false));
                j.anadirMovimiento(new MovimientoEnemigo("Mirada Vacia", 0, null, 0, 0, 0, 11, 2, F(1,2,3), false, false));
                j.anadirMovimientoFase2(new MovimientoEnemigo("Danza del Colgado", 1.5, null, 0, 0, 0, 8, 4, F(1,2,3), false, false));
                return j;
            }
            case 1: {
                Jefe j = new Jefe("La Meiga Suprema", niv, "¡Haberlas, haylas!");
                j.anadirMovimiento(new MovimientoEnemigo("Llamas Fatuas", 0.9, TipoEfecto.QUEMADURA, 80, 3, 3 + niv, 3, 3, F(1,2,3), false, false));
                j.anadirMovimiento(new MovimientoEnemigo("Mal de Ojo", 0, TipoEfecto.DEBILITADO, 100, 2, 0, 8, 2, F(1,2,3), false, false));
                j.anadirMovimientoFase2(new MovimientoEnemigo("Aquelarre", 1.3, TipoEfecto.QUEMADURA, 100, 2, 4 + niv, 6, 4, F(1,2,3), false, false));
                return j;
            }
            default: {
                Jefe j = new Jefe("El Cuelebre de la Cueva", niv, "El tesoro es MIO.");
                j.anadirMovimiento(new MovimientoEnemigo("Aliento Igneo", 1.0, TipoEfecto.QUEMADURA, 70, 3, 3 + niv, 4, 3, F(1,2,3), false, false));
                j.anadirMovimiento(new MovimientoEnemigo("Coletazo Brutal", 1.2, TipoEfecto.ATURDIDO, 35, 1, 0, 0, 2, F(1,2,3), false, false));
                j.anadirMovimientoFase2(new MovimientoEnemigo("Vendaval de Escamas", 1.6, null, 0, 0, 0, 5, 4, F(1,2,3), false, false));
                return j;
            }
        }
    }

    /** El jefe final de la campana: La Santa Compania. */
    public static Jefe crearJefeFinal(int nivelHeroe) {
        Jefe j = new Jefe("La Santa Compania", nivelHeroe + 4,
                "La procesion de las animas reclama tu vela...");
        j.anadirMovimiento(new MovimientoEnemigo("Cirio Apagado", 1.1, null, 0, 0, 0, 8, 3, F(1,2,3), false, false));
        j.anadirMovimiento(new MovimientoEnemigo("Letania Sepulcral", 0, null, 0, 0, 0, 14, 2, F(1,2,3), false, false));
        j.anadirMovimiento(new MovimientoEnemigo("Toque de Difuntos", 0.9, TipoEfecto.DEBILITADO, 80, 2, 0, 6, 2, F(1,2,3), false, false));
        j.anadirMovimientoFase2(new MovimientoEnemigo("Procesion de las Animas", 1.7, null, 0, 0, 0, 10, 4, F(1,2,3), false, false));
        j.anadirMovimientoFase2(new MovimientoEnemigo("Ultima Vela", 0.8, TipoEfecto.QUEMADURA, 100, 3, 5 + nivelHeroe, 8, 2, F(1,2,3), false, false));
        return j;
    }
}
