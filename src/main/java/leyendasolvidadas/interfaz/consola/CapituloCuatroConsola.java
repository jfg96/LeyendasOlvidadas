package leyendasolvidadas.interfaz.consola;

import leyendasolvidadas.aplicacion.EstadoJuego;
import leyendasolvidadas.aplicacion.RepositorioPartidas;
import leyendasolvidadas.aplicacion.ServicioCapituloCuatro;

/** Escenas y decisiones interactivas de El libro de los nombres. */
public final class CapituloCuatroConsola {
    private final RepositorioPartidas repo;
    private final ServicioCapituloCuatro servicio = new ServicioCapituloCuatro();

    public CapituloCuatroConsola(RepositorioPartidas repo) { this.repo = repo; }

    public void presentarSiPendiente(EstadoJuego estado) {
        if (!servicio.requierePresentacion(estado)) return;
        UI.limpiar();
        UI.titulo("CAPITULO IV — EL LIBRO DE LOS NOMBRES");
        UI.log("Las páginas recuperadas contienen ciento trece huecos. Cada nombre fue borrado tres veces:");
        UI.log("de la parroquia, de la memoria de los peregrinos y de las cuentas de quienes ordenaron la matanza.");
        UI.log("Para reconstruirlos tendréis que regresar a lugares que ya creíais vencidos.");
        System.out.println("  ¿Quién custodiará el Libro durante la búsqueda?\n  1. Padre Tomé   2. Aldara   3. La compañía");
        servicio.abrirLibro(estado, UI.elegirEnum(ServicioCapituloCuatro.CustodiaLibro.class));
        repo.guardar(estado); UI.pausa();
    }

    public void cerrar(EstadoJuego estado) {
        UI.seccion("LA VIGILIA DE LOS CIENTO DOCE");
        UI.log("Las voces responden una a una. Los descendientes de los culpables esperan vuestra sentencia.");
        System.out.println("  1. Revelar públicamente los linajes culpables.");
        System.out.println("  2. Proteger a los descendientes de los crímenes de sus mayores.");
        System.out.println("  3. Exigir reparación sin convertir los apellidos en condenas.");
        var justicia = UI.elegirEnum(ServicioCapituloCuatro.JusticiaFamilias.class);

        UI.log("Queda un último hueco. La tinta cambia ante dos nombres posibles.");
        System.out.println("  1. El antepasado del héroe, guía que abandonó a los peregrinos.");
        System.out.println("  2. Inés, la peregrina cuyo nombre fue arrancado para mantener abierta la maldición.");
        var nombre = UI.elegirEnum(ServicioCapituloCuatro.NombreCientoTrece.class);

        System.out.println("  ¿Cómo prepararéis el rito en el Hospital?\n  1. Sal y fuego   2. Reliquias recuperadas   3. Campanas de Valdesombra");
        var preparacion = UI.elegirEnum(ServicioCapituloCuatro.PreparacionRitual.class);
        servicio.completar(estado, justicia, nombre, preparacion);
        UI.log(UI.pintar("CAPÍTULO IV COMPLETADO — el Hospital del Camino Viejo queda abierto.", UI.AMARILLO));
        UI.log(UI.pintar("La Santa Compaña ya conoce vuestro nombre.", UI.MAGENTA));
        repo.guardar(estado);
    }
}
