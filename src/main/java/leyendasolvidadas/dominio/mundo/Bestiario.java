package leyendasolvidadas.dominio.mundo;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.objetos.*;

import java.util.ArrayList;
import java.util.List;

/** Crea los enemigos y jefes de cada región. */
public final class Bestiario {
    private Bestiario() {}

    private static int[] F(int... f) { return f; }
    private static final int[] TODAS = F(1, 2, 3);

    public static String descripcion(String nombre) {
        if (nombre.contains("Ahorcado")) return "Un muerto sostenido por raíces y rencor; debilita antes de cerrar la soga.";
        if (nombre.contains("Lavandeira")) return "Lava sudarios de quienes todavía viven y contamina cuerpo y memoria.";
        if (nombre.contains("Campanero")) return "Una figura sin rostro cuyo toque aturde y descompone la voluntad.";
        if (nombre.contains("Lobisome") || nombre.contains("Lobo")) return "Cazador veloz que abre heridas y persigue a quien sangra.";
        if (nombre.contains("Compa") || nombre.contains("Cirio") || nombre.contains("Penitente"))
            return "Integrante de la procesión: mezcla daño, terror y marcas de condena.";
        if (nombre.contains("Meiga")) return "Bruja de retaguardia experta en fuego fatuo y debilitación.";
        if (nombre.contains("Cuelebre")) return "Bestia acorazada cuyo fuego castiga a toda formación descuidada.";
        return "Criatura de la niebla. Sus intenciones revelan la mejor forma de responder.";
    }

    private static Enemigo enemigo(String nombre, int nivel, boolean elite, int fila, MovimientoEnemigo... movimientos) {
        Enemigo e = new Enemigo(nombre, nivel, elite);
        for (MovimientoEnemigo m : movimientos) e.anadirMovimiento(m);
        e.setFilaPreferida(fila);
        return e;
    }
    private static Jefe jefe(String nombre, int nivel, String grito, MovimientoEnemigo... fase1) {
        Jefe j = new Jefe(nombre, nivel, grito);
        for (MovimientoEnemigo m : fase1) j.anadirMovimiento(m);
        return j;
    }
    private static Jefe segundaFase(Jefe j, MovimientoEnemigo... fase2) {
        for (MovimientoEnemigo m : fase2) j.anadirMovimientoFase2(m);
        return j;
    }

    private static Enemigo duende(int niv) {
        return enemigo("Duende Burlon", niv, false, 2,
                MovimientoEnemigo.golpe("Pedrada", 1.0, 3),
                new MovimientoEnemigo("Burla Cruel", 0, null, 0, 0, 0, 8, 2, TODAS, false, false));
    }
    private static Enemigo anima(int niv) {
        return enemigo("Anima en Pena", niv, false, 3,
                new MovimientoEnemigo("Lamento Fúnebre", 0.6, null, 0, 0, 0, 7, 2, TODAS, false, false),
                MovimientoEnemigo.golpe("Toque Gelido", 1.0, 3));
    }
    private static Enemigo lobo(int niv) {
        return enemigo("Lobo de la Sierra", niv, false, 1,
                new MovimientoEnemigo("Mordisco", 1.0, TipoEfecto.SANGRADO, 40, 2, 2 + niv, 0, 3, F(1,2), false, false),
                MovimientoEnemigo.golpe("Zarpazo", 0.9, 2));
    }
    private static Enemigo trasgo(int niv) {
        return enemigo("Trasgo de Alacena", niv, false, 1,
                new MovimientoEnemigo("Trastada", 0.7, null, 0, 0, 0, 5, 2, TODAS, false, false),
                MovimientoEnemigo.golpe("Garrotazo", 1.0, 3));
    }
    private static Enemigo espectro(int niv) {
        return enemigo("Espectro del Camposanto", niv, false, 1,
                MovimientoEnemigo.golpe("Guadana Umbria", 1.2, 3),
                new MovimientoEnemigo("Susurro Funebre", 0, null, 0, 0, 0, 9, 2, TODAS, false, false));
    }
    private static Enemigo meiga(int niv) {
        return enemigo("Meiga Oscura", niv, false, 3,
                new MovimientoEnemigo("Fuego Fatuo", 0.8, TipoEfecto.QUEMADURA, 60, 3, 2 + niv, 0, 3, F(2,3), false, false),
                new MovimientoEnemigo("Maleficio", 0, TipoEfecto.DEBILITADO, 100, 2, 0, 4, 2, TODAS, false, false));
    }
    private static Enemigo ahorcadoVerde(int niv) {
        return enemigo("Ahorcado Verde", niv, false, 1,
                new MovimientoEnemigo("Soga de Raíces", 0.9, TipoEfecto.DEBILITADO, 35, 2, 0, 3, 3, F(1,2), false, false),
                MovimientoEnemigo.golpe("Patada Pendular", 1.0, 2));
    }
    private static Enemigo corvoCarne(int niv) {
        return enemigo("Corvo de Carne", niv, false, 3,
                new MovimientoEnemigo("Picotazo", 0.8, TipoEfecto.SANGRADO, 35, 2, 2 + niv, 0, 3, TODAS, false, false));
    }
    private static Enemigo afogado(int niv) {
        return enemigo("Afogado", niv, false, 1,
                new MovimientoEnemigo("Abrazo de Ciénaga", 0.9, TipoEfecto.DEBILITADO, 45, 2, 0, 4, 3, F(1,2), false, false));
    }
    private static Enemigo lavandeira(int niv) {
        return enemigo("Lavandeira", niv, false, 3,
                new MovimientoEnemigo("Sudario Mojado", 0.7, TipoEfecto.VENENO, 55, 3, 2 + niv, 6, 3, TODAS, false, false));
    }
    private static Enemigo peregrinoQuemado(int niv) {
        return enemigo("Peregrino Quemado", niv, false, 1,
                new MovimientoEnemigo("Canto entre Llamas", 0.8, TipoEfecto.QUEMADURA, 45, 2, 2 + niv, 7, 3, TODAS, false, false));
    }
    private static Enemigo campanero(int niv) {
        return enemigo("Campanero Sin Rostro", niv, false, 3,
                new MovimientoEnemigo("Doblar de Difuntos", 0.5, TipoEfecto.ATURDIDO, 30, 1, 0, 10, 3, TODAS, false, false));
    }
    private static Enemigo mineiroMorto(int niv) {
        return enemigo("Mineiro Morto", niv, false, 1,
                MovimientoEnemigo.golpe("Pico Oxidado", 1.05, 3));
    }
    private static Enemigo trasnoHierro(int niv) {
        return enemigo("Trasno de Hierro", niv, false, 1,
                new MovimientoEnemigo("Polvo de Mina", 0.7, TipoEfecto.DEBILITADO, 55, 2, 0, 4, 3, TODAS, false, false));
    }
    private static Enemigo guardiaSoutomaior(int niv) {
        return enemigo("Guardia de Soutomaior", niv, false, 1,
                MovimientoEnemigo.golpe("Estocada", 1.1, 3));
    }
    private static Enemigo criadoSinNombre(int niv) {
        return enemigo("Criado Sin Nombre", niv, false, 1,
                new MovimientoEnemigo("Servicio Eterno", 0.7, TipoEfecto.MARCADO, 55, 2, 0, 6, 3, TODAS, false, false));
    }
    private static Enemigo portadorCirio(int niv) {
        return enemigo("Portador del Cirio", niv, false, 1,
                new MovimientoEnemigo("Cera de Mortaja", 0.8, TipoEfecto.DEBILITADO, 60, 2, 0, 6, 3, TODAS, false, false),
                MovimientoEnemigo.golpe("Vara Procesional", 1.05, 3));
    }
    private static Enemigo penitenteSinRostro(int niv) {
        return enemigo("Penitente Sin Rostro", niv, false, 3,
                new MovimientoEnemigo("Nombre Borrado", 0.7, TipoEfecto.MARCADO, 65, 2, 0, 9, 3, TODAS, false, false));
    }
    private static Enemigo lobisome(int niv) {
        return enemigo("Lobisome", niv, true, 1,
                new MovimientoEnemigo("Desgarro Salvaje", 1.1, TipoEfecto.SANGRADO, 70, 3, 3 + niv, 0, 3, F(1,2), false, false),
                new MovimientoEnemigo("Aullido Ancestral", 0, null, 0, 0, 0, 12, 2, TODAS, false, false));
    }
    private static Enemigo caballero(int niv) {
        return enemigo("Caballero de la Compania", niv, true, 1,
                MovimientoEnemigo.golpe("Tajo Espectral", 1.25, 3),
                new MovimientoEnemigo("Estandarte del Miedo", 0, TipoEfecto.FORTALECIDO, 100, 2, 0, 10, 2, TODAS, false, true));
    }
    private static Enemigo cuelebre(int niv) {
        return enemigo("Cuelebre Joven", niv, true, 1,
                new MovimientoEnemigo("Aliento de Fuego", 0.9, TipoEfecto.QUEMADURA, 70, 3, 3 + niv, 4, 3, TODAS, false, false),
                new MovimientoEnemigo("Coletazo", 1.1, TipoEfecto.ATURDIDO, 25, 1, 0, 0, 2, F(1,2), false, false));
    }

    /** Genera un grupo de 1-3 enemigos para un combate normal. */
    public static List<Enemigo> crearGrupo(int nivelZona, Dificultad dif) {
        return crearGrupo(nivelZona, dif, FuenteAzar.global());
    }
    public static List<Enemigo> crearGrupo(int nivelZona, Dificultad dif, FuenteAzar azar) {
        int niv = Math.max(1, nivelZona + azar.entre(-1, 1));
        int cuantos = dif == Dificultad.FACIL ? (azar.probabilidad(20) ? 3 : 2) : dif == Dificultad.MEDIA
                ? (azar.probabilidad(60) ? 3 : 2) : 3;
        List<Enemigo> grupo = new ArrayList<>();
        int probElite = dif == Dificultad.FACIL ? 8 : dif == Dificultad.MEDIA ? 16 : 26;
        for (int i = 0; i < cuantos; i++) {
            if (azar.probabilidad(probElite) && !hayElite(grupo)) grupo.add(eliteAleatorio(niv + 1, azar));
            else grupo.add(comunAleatorio(niv, azar));
        }
        // Ordenar evita que los enemigos de retaguardia ocupen la primera fila.
        grupo.sort((a, b) -> Integer.compare(a.getFilaPreferida(), b.getFilaPreferida()));
        return grupo;
    }
    public static List<Enemigo> crearGrupo(Region region, int nivelZona, Dificultad dif) {
        return crearGrupo(region, nivelZona, dif, FuenteAzar.global());
    }
    public static List<Enemigo> crearGrupo(Region region, int nivelZona, Dificultad dif, FuenteAzar azar) {
        if (region == Region.HOSPITAL_DEL_CAMINO_VIEJO) {
            int niv = Math.max(1, nivelZona + azar.entre(-1, 1));
            List<Enemigo> grupo = new ArrayList<>();
            grupo.add(portadorCirio(niv)); grupo.add(penitenteSinRostro(niv));
            if (dif != Dificultad.FACIL) grupo.add(azar.probabilidad(50) ? campanero(niv) : peregrinoQuemado(niv));
            grupo.sort((a, b) -> Integer.compare(a.getFilaPreferida(), b.getFilaPreferida()));
            return grupo;
        }
        if (region == Region.BRANAS_HUNDIDAS || region == Region.CAMINO_DE_LOS_DIFUNTOS) {
            int niv = Math.max(1, nivelZona + azar.entre(-1, 1));
            int cuantos = dif == Dificultad.FACIL ? 2 : 3;
            List<Enemigo> grupo = new ArrayList<>();
            for (int i = 0; i < cuantos; i++) grupo.add(region == Region.BRANAS_HUNDIDAS
                    ? (azar.probabilidad(55) ? afogado(niv) : lavandeira(niv))
                    : (azar.probabilidad(55) ? peregrinoQuemado(niv) : campanero(niv)));
            grupo.sort((a, b) -> Integer.compare(a.getFilaPreferida(), b.getFilaPreferida()));
            return grupo;
        }
        if (region == Region.MINAS_DE_SAN_LOURENZO || region == Region.PAZO_DE_SOUTOMAIOR) {
            int niv = Math.max(1, nivelZona + azar.entre(-1, 1)); int cuantos = dif == Dificultad.FACIL ? 2 : 3;
            List<Enemigo> grupo = new ArrayList<>();
            for (int i=0;i<cuantos;i++) grupo.add(region == Region.MINAS_DE_SAN_LOURENZO
                    ? (azar.probabilidad(60) ? mineiroMorto(niv) : trasnoHierro(niv))
                    : (azar.probabilidad(60) ? guardiaSoutomaior(niv) : criadoSinNombre(niv)));
            return grupo;
        }
        if (region != Region.BOSQUE_DE_LOS_AHORCADOS) return crearGrupo(nivelZona, dif, azar);
        int niv = Math.max(1, nivelZona + azar.entre(-1, 1));
        int cuantos = dif == Dificultad.FACIL ? 2 : 3;
        List<Enemigo> grupo = new ArrayList<>();
        for (int i = 0; i < cuantos; i++) {
            int tipo = azar.entre(0, 3);
            grupo.add(tipo == 0 ? lobo(niv) : tipo == 1 ? ahorcadoVerde(niv)
                    : tipo == 2 ? corvoCarne(niv) : (i == 0 && dif == Dificultad.DIFICIL ? lobisome(niv) : ahorcadoVerde(niv)));
        }
        grupo.sort((a, b) -> Integer.compare(a.getFilaPreferida(), b.getFilaPreferida()));
        return grupo;
    }
    private static boolean hayElite(List<Enemigo> g) {
        for (Enemigo e : g) if (e.esElite()) return true;
        return false;
    }
    private static Enemigo comunAleatorio(int niv, FuenteAzar azar) {
        switch (azar.entre(0, 5)) {
            case 0: return duende(niv);
            case 1: return anima(niv);
            case 2: return lobo(niv);
            case 3: return trasgo(niv);
            case 4: return espectro(niv);
            default: return meiga(niv);
        }
    }
    private static Enemigo eliteAleatorio(int niv, FuenteAzar azar) {
        switch (azar.entre(0, 2)) {
            case 0: return lobisome(niv);
            case 1: return caballero(niv);
            default: return cuelebre(niv);
        }
    }

    /** Jefes de expedicion, rotan segun las victorias acumuladas. */
    public static Jefe crearJefe(int nivelZona, int victorias) {
        return switch (victorias % 3) {
            case 0 -> segundaFase(jefe("El Ahorcado del Roble", nivelZona, "¡La soga nunca perdona!",
                    new MovimientoEnemigo("Latigo de Esparto", 1.1, TipoEfecto.SANGRADO, 60, 3, 3 + nivelZona, 0, 3, TODAS, false, false),
                    new MovimientoEnemigo("Mirada Vacia", 0, null, 0, 0, 0, 11, 2, TODAS, false, false)),
                    new MovimientoEnemigo("Danza del Colgado", 1.5, null, 0, 0, 0, 8, 4, TODAS, false, false));
            case 1 -> segundaFase(jefe("La Meiga Suprema", nivelZona, "¡Haberlas, haylas!",
                    new MovimientoEnemigo("Llamas Fatuas", 0.9, TipoEfecto.QUEMADURA, 80, 3, 3 + nivelZona, 3, 3, TODAS, false, false),
                    new MovimientoEnemigo("Mal de Ojo", 0, TipoEfecto.DEBILITADO, 100, 2, 0, 8, 2, TODAS, false, false)),
                    new MovimientoEnemigo("Aquelarre", 1.3, TipoEfecto.QUEMADURA, 100, 2, 4 + nivelZona, 6, 4, TODAS, false, false));
            default -> segundaFase(jefe("El Cuelebre de la Cueva", nivelZona, "El tesoro es MIO.",
                    new MovimientoEnemigo("Aliento Igneo", 1.0, TipoEfecto.QUEMADURA, 70, 3, 3 + nivelZona, 4, 3, TODAS, false, false),
                    new MovimientoEnemigo("Coletazo Brutal", 1.2, TipoEfecto.ATURDIDO, 35, 1, 0, 0, 2, TODAS, false, false)),
                    new MovimientoEnemigo("Vendaval de Escamas", 1.6, null, 0, 0, 0, 5, 4, TODAS, false, false));
        };
    }

    /** El jefe final de la campana: La Santa Compania. */
    public static Jefe crearJefeFinal(int nivelZona) {
        return segundaFase(jefe("La Santa Compania", nivelZona + 1,
                "La procesion de las animas reclama tu vela...",
                new MovimientoEnemigo("Cirio Apagado", 1.1, null, 0, 0, 0, 8, 3, TODAS, false, false),
                new MovimientoEnemigo("Letania Sepulcral", 0, null, 0, 0, 0, 14, 2, TODAS, false, false),
                new MovimientoEnemigo("Toque de Difuntos", 0.9, TipoEfecto.DEBILITADO, 80, 2, 0, 6, 2, TODAS, false, false)),
                new MovimientoEnemigo("Procesion de las Animas", 1.7, null, 0, 0, 0, 10, 4, TODAS, false, false),
                new MovimientoEnemigo("Ultima Vela", 0.8, TipoEfecto.QUEMADURA, 100, 3, 5 + nivelZona, 8, 2, TODAS, false, false));
    }
    public static Jefe crearReiAforcados(int nivelZona) {
        return segundaFase(jefe("O Rei dos Aforcados", nivelZona + 1,
                "Cada raíz de este bosque ha bebido de un inocente.",
                new MovimientoEnemigo("Soga del Verdugo", 1.1, TipoEfecto.MARCADO, 65, 2, 0, 5, 3, TODAS, false, false),
                new MovimientoEnemigo("Raíces Hambrientas", 0.8, TipoEfecto.SANGRADO, 70, 3, 3 + nivelZona, 3, 3, TODAS, false, false)),
                new MovimientoEnemigo("Todos Pendemos Juntos", 1.45, null, 0, 0, 0, 9, 4, TODAS, false, false));
    }
    public static Jefe crearLavandeiraMaior(int nivelZona) {
        return segundaFase(jefe("A Lavandeira Maior", nivelZona + 1, "Lavo hoy el sudario que vestirás mañana.",
                new MovimientoEnemigo("Sudario del Mañana", 0.9, TipoEfecto.VENENO, 75, 3, 3 + nivelZona, 7, 3, TODAS, false, false),
                new MovimientoEnemigo("Agua de Sepultura", 0.7, TipoEfecto.DEBILITADO, 80, 2, 0, 5, 3, TODAS, false, false)),
                new MovimientoEnemigo("Lavado de los Muertos", 1.45, null, 0, 0, 0, 8, 4, TODAS, false, false));
    }
    public static Jefe crearHospitalario(int nivelZona) {
        return segundaFase(jefe("El Hospitalario", nivelZona + 1, "Yo cerré las puertas. Vosotros alimentasteis el fuego.",
                new MovimientoEnemigo("Llave al Rojo", 1.1, TipoEfecto.QUEMADURA, 65, 3, 3 + nivelZona, 4, 3, F(1,2), false, false),
                new MovimientoEnemigo("Cerrar las Puertas", 0.6, TipoEfecto.ATURDIDO, 45, 1, 0, 9, 3, TODAS, false, false)),
                new MovimientoEnemigo("Ciento Trece Golpes", 1.55, null, 0, 0, 0, 10, 4, TODAS, false, false));
    }
    public static Jefe crearCapataz(int nivelZona) {
        return segundaFase(jefe("O Capataz", nivelZona + 1, "La campana marca el turno. La mina nunca duerme.",
                new MovimientoEnemigo("Cadena Minera", 1.1, TipoEfecto.ATURDIDO, 35, 1, 0, 4, 3, TODAS, false, false),
                new MovimientoEnemigo("Derrumbe", 0.9, TipoEfecto.DEBILITADO, 55, 2, 0, 7, 3, TODAS, false, false)),
                new MovimientoEnemigo("Último Turno", 1.55, null, 0, 0, 0, 8, 4, TODAS, false, false));
    }
    public static Jefe crearCustodioCripta(int nivelZona) {
        return segundaFase(jefe("El Custodio de la Cripta", nivelZona + 1, "Los Soutomaior no deben nada a los muertos.",
                new MovimientoEnemigo("Sello de Sal", 1.0, TipoEfecto.MARCADO, 65, 2, 0, 5, 3, TODAS, false, false),
                new MovimientoEnemigo("Sangre Noble", 0.8, TipoEfecto.SANGRADO, 65, 3, 3 + nivelZona, 4, 3, F(1,2), false, false)),
                new MovimientoEnemigo("Setenta Años de Silencio", 1.5, null, 0, 0, 0, 10, 4, TODAS, false, false));
    }
}