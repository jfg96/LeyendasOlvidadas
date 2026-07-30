package leyendasolvidadas.dominio.misiones;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.mundo.*;


/**
 * Coordina la mision activa de la expedicion y reenvia los eventos del mundo.
 */
public class GestorMisiones {
    private Mision misionActual;

    public void asignar(Mision m) { misionActual = m; }
    public Mision getMision() { return misionActual; }
    public boolean hayMisionCompletada() {
        return misionActual != null && misionActual.estaCompletada();
    }
    public void notificarMuerte(Enemigo e) { if (misionActual != null) misionActual.notificarMuerte(e); }
    public void notificarVisita(int v, int t) { if (misionActual != null) misionActual.notificarVisita(v, t); }
    public void notificarObjetivo() { if (misionActual != null) misionActual.notificarObjetivo(); }
    public void notificarEntrada() {
        if (misionActual instanceof MisionReliquia) ((MisionReliquia) misionActual).notificarEntrada();
    }
    public String resumen() {
        if (misionActual == null) return "Sin encargo activo.";
        return "ENCARGO: " + misionActual.getNombre() + " [" + misionActual.getDificultad().getTitulo() + "]\n"
                + misionActual.getDescripcion() + "\nProgreso: " + misionActual.progreso()
                + "\nRecompensa: " + misionActual.getOroRecompensa() + " reales, "
                + misionActual.getXpRecompensa() + " XP"
                + (misionActual.getItemRecompensa() != null
                    ? ", " + misionActual.getItemRecompensa().getNombre() : "");
    }

    /** Genera una mision aleatoria acorde al nivel del heroe. */
    public static Mision generar(int nivelHeroe, Dificultad dif) {
        int niv = nivelHeroe + dif.getNivelExtra();
        int oro = (30 + niv * 12) * (dif.ordinal() + 1);
        int xp = (40 + niv * 20) * (dif.ordinal() + 1);
        Item premio = Rng.prob(50) ? null
                : Rng.prob(50) ? Amuleto.aleatorio(10 + dif.ordinal() * 8)
                : Arma.aleatoria(niv, 10 + dif.ordinal() * 8);
        switch (Rng.entre(0, 3)) {
            case 0: return new MisionCaza(dif, 3 + dif.ordinal() * 2, oro, xp, premio);
            case 1: return new MisionExploracion(dif, oro, xp, premio);
            case 2: return new MisionReliquia(dif, oro, xp, premio);
            default: return new MisionJefe(dif, (int) (oro * 1.4), (int) (xp * 1.4),
                    premio != null ? premio : Amuleto.aleatorio(15), false);
        }
    }

    public static Mision generarBosque(int nivelHeroe, Dificultad dif) {
        int niv = nivelHeroe + dif.getNivelExtra();
        int oro = (35 + niv * 12) * (dif.ordinal() + 1);
        int xp = (45 + niv * 20) * (dif.ordinal() + 1);
        Item premio = Rng.prob(55) ? null : Amuleto.aleatorio(10 + dif.ordinal() * 8);
        Mision mision = switch (Rng.entre(0, 2)) {
            case 0 -> new MisionCaza("Las sogas vacías",
                    "Abatir a las criaturas que anidan bajo los antiguos patíbulos.", dif,
                    3 + dif.ordinal(), oro, xp, premio);
            case 1 -> new MisionExploracion("El sendero que regresa",
                    "Cartografiar los caminos que cambian cuando nadie los mira.", dif, oro, xp, premio);
            default -> new MisionReliquia("La medalla del ahorcado",
                    "Recuperar una medalla entre las raíces y devolverla a la entrada.", dif, oro, xp, premio);
        };
        return mision.enRegion(Region.BOSQUE_DE_LOS_AHORCADOS);
    }

    public static Mision generarRegional(Region region, int nivel, Dificultad dif) {
        if (region == Region.BOSQUE_DE_LOS_AHORCADOS) return generarBosque(nivel, dif);
        int n = nivel + dif.getNivelExtra();
        int oro = (40 + n * 13) * (dif.ordinal() + 1), xp = (50 + n * 22) * (dif.ordinal() + 1);
        Mision m = switch (region) {
            case BRANAS_HUNDIDAS -> Rng.prob(50)
                    ? new MisionCaza("Los que respiran barro", "Abatir a las criaturas surgidas de la ciénaga.", dif, 3 + dif.ordinal(), oro, xp, null)
                    : new MisionExploracion("La tumba vacía de Aldara", "Cartografiar las islas que aparecen bajo la niebla amarilla.", dif, oro, xp, null);
            case CAMINO_DE_LOS_DIFUNTOS -> Rng.prob(50)
                    ? new MisionCaza("Campanas sin campanero", "Silenciar a los muertos que recorren el Camino Viejo.", dif, 3 + dif.ordinal(), oro, xp, null)
                    : new MisionReliquia("Una página sin nombres", "Recuperar una hoja arrancada del registro parroquial.", dif, oro, xp, null);
            case MINAS_DE_SAN_LOURENZO -> Rng.prob(50)
                    ? new MisionCaza("Hierro para los muertos", "Eliminar a los mineros que siguen picando tras su muerte.", dif, 3 + dif.ordinal(), oro, xp, null)
                    : new MisionExploracion("Galerías bajo el Pazo", "Encontrar el santuario enterrado bajo la explotación.", dif, oro, xp, null);
            case PAZO_DE_SOUTOMAIOR -> Rng.prob(50)
                    ? new MisionCaza("Los criados sin nombre", "Abrirse paso entre los guardianes de la casa Soutomaior.", dif, 3 + dif.ordinal(), oro, xp, null)
                    : new MisionReliquia("El inventario de los culpables", "Robar el registro del saqueo de los peregrinos.", dif, oro, xp, null);
            default -> generar(nivel, dif);
        };
        return m.enRegion(region);
    }

    /** Encargos de investigación que reconstruyen los tres testimonios del Libro. */
    public static Mision generarLibroNombres(Region region, int nivel, Dificultad dif) {
        int n = nivel + dif.getNivelExtra();
        int oro = (45 + n * 14) * (dif.ordinal() + 1);
        int xp = (60 + n * 24) * (dif.ordinal() + 1);
        Mision mision = switch (region) {
            case BOSQUE_DE_LOS_AHORCADOS -> new MisionReliquia("Las tablillas del patíbulo",
                    "Recuperar las marcas con las que el verdugo contó a los peregrinos.", dif, oro, xp, null);
            case CAMINO_DE_LOS_DIFUNTOS -> new MisionExploracion("La letanía de los ausentes",
                    "Seguir las voces y transcribir los nombres que aún recuerda el Camino.", dif, oro, xp, null);
            case PAZO_DE_SOUTOMAIOR -> new MisionReliquia("El árbol de la culpa",
                    "Robar la genealogía que vincula a las familias con la matanza.", dif, oro, xp, null);
            default -> throw new IllegalArgumentException("Región ajena al Libro de los Nombres: " + region);
        };
        return mision.enRegion(region);
    }
}
