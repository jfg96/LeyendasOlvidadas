package leyendasolvidadas.dominio.mundo;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;

import java.util.ArrayList;
import java.util.List;

/** Crea los enemigos y jefes de cada región. */
public final class Bestiario {
    private Bestiario() {}

    private static int[] F(int... f) { return f; }

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
    private static Enemigo ahorcadoVerde(int niv) {
        Enemigo e = new Enemigo("Ahorcado Verde", niv, false);
        e.anadirMovimiento(new MovimientoEnemigo("Soga de Raíces", 0.9, TipoEfecto.DEBILITADO, 35, 2, 0, 3, 3, F(1,2), false, false));
        e.anadirMovimiento(MovimientoEnemigo.golpe("Patada Pendular", 1.0, 2));
        return e;
    }
    private static Enemigo corvoCarne(int niv) {
        Enemigo e = new Enemigo("Corvo de Carne", niv, false);
        e.anadirMovimiento(new MovimientoEnemigo("Picotazo", 0.8, TipoEfecto.SANGRADO, 35, 2, 2 + niv, 0, 3, F(1,2,3), false, false));
        e.setFilaPreferida(3);
        return e;
    }
    private static Enemigo afogado(int niv) {
        Enemigo e = new Enemigo("Afogado", niv, false);
        e.anadirMovimiento(new MovimientoEnemigo("Abrazo de Ciénaga", 0.9, TipoEfecto.DEBILITADO, 45, 2, 0, 4, 3, F(1,2), false, false));
        return e;
    }
    private static Enemigo lavandeira(int niv) {
        Enemigo e = new Enemigo("Lavandeira", niv, false);
        e.anadirMovimiento(new MovimientoEnemigo("Sudario Mojado", 0.7, TipoEfecto.VENENO, 55, 3, 2 + niv, 6, 3, F(1,2,3), false, false));
        e.setFilaPreferida(3); return e;
    }
    private static Enemigo peregrinoQuemado(int niv) {
        Enemigo e = new Enemigo("Peregrino Quemado", niv, false);
        e.anadirMovimiento(new MovimientoEnemigo("Canto entre Llamas", 0.8, TipoEfecto.QUEMADURA, 45, 2, 2 + niv, 7, 3, F(1,2,3), false, false));
        return e;
    }
    private static Enemigo campanero(int niv) {
        Enemigo e = new Enemigo("Campanero Sin Rostro", niv, false);
        e.anadirMovimiento(new MovimientoEnemigo("Doblar de Difuntos", 0.5, TipoEfecto.ATURDIDO, 30, 1, 0, 10, 3, F(1,2,3), false, false));
        e.setFilaPreferida(3); return e;
    }
    private static Enemigo mineiroMorto(int niv) {
        Enemigo e = new Enemigo("Mineiro Morto", niv, false); e.anadirMovimiento(MovimientoEnemigo.golpe("Pico Oxidado", 1.05, 3)); return e;
    }
    private static Enemigo trasnoHierro(int niv) {
        Enemigo e = new Enemigo("Trasno de Hierro", niv, false); e.anadirMovimiento(new MovimientoEnemigo("Polvo de Mina", 0.7, TipoEfecto.DEBILITADO, 55, 2, 0, 4, 3, F(1,2,3), false, false)); return e;
    }
    private static Enemigo guardiaSoutomaior(int niv) {
        Enemigo e = new Enemigo("Guardia de Soutomaior", niv, false); e.anadirMovimiento(MovimientoEnemigo.golpe("Estocada", 1.1, 3)); return e;
    }
    private static Enemigo criadoSinNombre(int niv) {
        Enemigo e = new Enemigo("Criado Sin Nombre", niv, false); e.anadirMovimiento(new MovimientoEnemigo("Servicio Eterno", 0.7, TipoEfecto.MARCADO, 55, 2, 0, 6, 3, F(1,2,3), false, false)); return e;
    }
    private static Enemigo portadorCirio(int niv) {
        Enemigo e = new Enemigo("Portador del Cirio", niv, false);
        e.anadirMovimiento(new MovimientoEnemigo("Cera de Mortaja", 0.8, TipoEfecto.DEBILITADO, 60, 2, 0, 6, 3, F(1,2,3), false, false));
        e.anadirMovimiento(MovimientoEnemigo.golpe("Vara Procesional", 1.05, 3)); return e;
    }
    private static Enemigo penitenteSinRostro(int niv) {
        Enemigo e = new Enemigo("Penitente Sin Rostro", niv, false);
        e.anadirMovimiento(new MovimientoEnemigo("Nombre Borrado", 0.7, TipoEfecto.MARCADO, 65, 2, 0, 9, 3, F(1,2,3), false, false));
        e.setFilaPreferida(3); return e;
    }
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
        int cuantos = dif == Dificultad.FACIL ? (Rng.prob(20) ? 3 : 2) : dif == Dificultad.MEDIA
                ? (Rng.prob(60) ? 3 : 2) : 3;
        List<Enemigo> grupo = new ArrayList<>();
        int probElite = dif == Dificultad.FACIL ? 8 : dif == Dificultad.MEDIA ? 16 : 26;
        for (int i = 0; i < cuantos; i++) {
            if (Rng.prob(probElite) && !hayElite(grupo)) grupo.add(eliteAleatorio(niv + 1));
            else grupo.add(comunAleatorio(niv));
        }
        // Ordenar evita que los enemigos de retaguardia ocupen la primera fila.
        grupo.sort((a, b) -> Integer.compare(a.getFilaPreferida(), b.getFilaPreferida()));
        return grupo;
    }
    public static List<Enemigo> crearGrupo(Region region, int nivelZona, Dificultad dif) {
        if (region == Region.HOSPITAL_DEL_CAMINO_VIEJO) {
            int niv = Math.max(1, nivelZona + Rng.entre(-1, 1));
            List<Enemigo> grupo = new ArrayList<>();
            grupo.add(portadorCirio(niv)); grupo.add(penitenteSinRostro(niv));
            if (dif != Dificultad.FACIL) grupo.add(Rng.prob(50) ? campanero(niv) : peregrinoQuemado(niv));
            grupo.sort((a, b) -> Integer.compare(a.getFilaPreferida(), b.getFilaPreferida()));
            return grupo;
        }
        if (region == Region.BRANAS_HUNDIDAS || region == Region.CAMINO_DE_LOS_DIFUNTOS) {
            int niv = Math.max(1, nivelZona + Rng.entre(-1, 1));
            int cuantos = dif == Dificultad.FACIL ? 2 : 3;
            List<Enemigo> grupo = new ArrayList<>();
            for (int i = 0; i < cuantos; i++) grupo.add(region == Region.BRANAS_HUNDIDAS
                    ? (Rng.prob(55) ? afogado(niv) : lavandeira(niv))
                    : (Rng.prob(55) ? peregrinoQuemado(niv) : campanero(niv)));
            grupo.sort((a, b) -> Integer.compare(a.getFilaPreferida(), b.getFilaPreferida()));
            return grupo;
        }
        if (region == Region.MINAS_DE_SAN_LOURENZO || region == Region.PAZO_DE_SOUTOMAIOR) {
            int niv = Math.max(1, nivelZona + Rng.entre(-1, 1)); int cuantos = dif == Dificultad.FACIL ? 2 : 3;
            List<Enemigo> grupo = new ArrayList<>();
            for (int i=0;i<cuantos;i++) grupo.add(region == Region.MINAS_DE_SAN_LOURENZO
                    ? (Rng.prob(60) ? mineiroMorto(niv) : trasnoHierro(niv))
                    : (Rng.prob(60) ? guardiaSoutomaior(niv) : criadoSinNombre(niv)));
            return grupo;
        }
        if (region != Region.BOSQUE_DE_LOS_AHORCADOS) return crearGrupo(nivelZona, dif);
        int niv = Math.max(1, nivelZona + Rng.entre(-1, 1));
        int cuantos = dif == Dificultad.FACIL ? 2 : 3;
        List<Enemigo> grupo = new ArrayList<>();
        for (int i = 0; i < cuantos; i++) {
            int tipo = Rng.entre(0, 3);
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
        int niv = nivelZona;
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
    public static Jefe crearJefeFinal(int nivelZona) {
        Jefe j = new Jefe("La Santa Compania", nivelZona + 1,
                "La procesion de las animas reclama tu vela...");
        j.anadirMovimiento(new MovimientoEnemigo("Cirio Apagado", 1.1, null, 0, 0, 0, 8, 3, F(1,2,3), false, false));
        j.anadirMovimiento(new MovimientoEnemigo("Letania Sepulcral", 0, null, 0, 0, 0, 14, 2, F(1,2,3), false, false));
        j.anadirMovimiento(new MovimientoEnemigo("Toque de Difuntos", 0.9, TipoEfecto.DEBILITADO, 80, 2, 0, 6, 2, F(1,2,3), false, false));
        j.anadirMovimientoFase2(new MovimientoEnemigo("Procesion de las Animas", 1.7, null, 0, 0, 0, 10, 4, F(1,2,3), false, false));
        j.anadirMovimientoFase2(new MovimientoEnemigo("Ultima Vela", 0.8, TipoEfecto.QUEMADURA, 100, 3, 5 + nivelZona, 8, 2, F(1,2,3), false, false));
        return j;
    }
    public static Jefe crearReiAforcados(int nivelZona) {
        Jefe j = new Jefe("O Rei dos Aforcados", nivelZona + 1,
                "Cada raíz de este bosque ha bebido de un inocente.");
        j.anadirMovimiento(new MovimientoEnemigo("Soga del Verdugo", 1.1, TipoEfecto.MARCADO, 65, 2, 0, 5, 3, F(1,2,3), false, false));
        j.anadirMovimiento(new MovimientoEnemigo("Raíces Hambrientas", 0.8, TipoEfecto.SANGRADO, 70, 3, 3 + nivelZona, 3, 3, F(1,2,3), false, false));
        j.anadirMovimientoFase2(new MovimientoEnemigo("Todos Pendemos Juntos", 1.45, null, 0, 0, 0, 9, 4, F(1,2,3), false, false));
        return j;
    }
    public static Jefe crearLavandeiraMaior(int nivelZona) {
        Jefe j = new Jefe("A Lavandeira Maior", nivelZona + 1, "Lavo hoy el sudario que vestirás mañana.");
        j.anadirMovimiento(new MovimientoEnemigo("Sudario del Mañana", 0.9, TipoEfecto.VENENO, 75, 3, 3 + nivelZona, 7, 3, F(1,2,3), false, false));
        j.anadirMovimiento(new MovimientoEnemigo("Agua de Sepultura", 0.7, TipoEfecto.DEBILITADO, 80, 2, 0, 5, 3, F(1,2,3), false, false));
        j.anadirMovimientoFase2(new MovimientoEnemigo("Lavado de los Muertos", 1.45, null, 0, 0, 0, 8, 4, F(1,2,3), false, false));
        return j;
    }
    public static Jefe crearHospitalario(int nivelZona) {
        Jefe j = new Jefe("El Hospitalario", nivelZona + 1, "Yo cerré las puertas. Vosotros alimentasteis el fuego.");
        j.anadirMovimiento(new MovimientoEnemigo("Llave al Rojo", 1.1, TipoEfecto.QUEMADURA, 65, 3, 3 + nivelZona, 4, 3, F(1,2), false, false));
        j.anadirMovimiento(new MovimientoEnemigo("Cerrar las Puertas", 0.6, TipoEfecto.ATURDIDO, 45, 1, 0, 9, 3, F(1,2,3), false, false));
        j.anadirMovimientoFase2(new MovimientoEnemigo("Ciento Trece Golpes", 1.55, null, 0, 0, 0, 10, 4, F(1,2,3), false, false));
        return j;
    }
    public static Jefe crearCapataz(int nivelZona) {
        Jefe j = new Jefe("O Capataz", nivelZona + 1, "La campana marca el turno. La mina nunca duerme.");
        j.anadirMovimiento(new MovimientoEnemigo("Cadena Minera", 1.1, TipoEfecto.ATURDIDO, 35, 1, 0, 4, 3, F(1,2,3), false, false));
        j.anadirMovimiento(new MovimientoEnemigo("Derrumbe", 0.9, TipoEfecto.DEBILITADO, 55, 2, 0, 7, 3, F(1,2,3), false, false));
        j.anadirMovimientoFase2(new MovimientoEnemigo("Último Turno", 1.55, null, 0, 0, 0, 8, 4, F(1,2,3), false, false)); return j;
    }
    public static Jefe crearCustodioCripta(int nivelZona) {
        Jefe j = new Jefe("El Custodio de la Cripta", nivelZona + 1, "Los Soutomaior no deben nada a los muertos.");
        j.anadirMovimiento(new MovimientoEnemigo("Sello de Sal", 1.0, TipoEfecto.MARCADO, 65, 2, 0, 5, 3, F(1,2,3), false, false));
        j.anadirMovimiento(new MovimientoEnemigo("Sangre Noble", 0.8, TipoEfecto.SANGRADO, 65, 3, 3+nivelZona, 4, 3, F(1,2), false, false));
        j.anadirMovimientoFase2(new MovimientoEnemigo("Setenta Años de Silencio", 1.5, null, 0, 0, 0, 10, 4, F(1,2,3), false, false)); return j;
    }
}
