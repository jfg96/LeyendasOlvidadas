package leyendasolvidadas.interfaz.consola;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.compania.MercenarioUnico;

/** Decisiones finales de las historias personales. */
public final class MisionesPersonalesConsola {
    private final RepositorioPartidas repo;
    private final ServicioMisionesPersonales servicio = new ServicioMisionesPersonales();
    public MisionesPersonalesConsola(RepositorioPartidas repo) { this.repo = repo; }

    public void resolver(EstadoJuego estado, MercenarioUnico unico) {
        UI.seccion("HISTORIA PERSONAL — " + unico.getNombre().toUpperCase());
        UI.log(texto(unico));
        System.out.println("  1. Permanecer y jurar lealtad a la compañía.");
        System.out.println("  2. Aceptar la verdad y cargar con su cicatriz.");
        System.out.println("  3. Abandonar Valdesombra para siempre.");
        System.out.println("  4. Aceptar un sacrificio que no permite regreso.");
        ResultadoAccion resultado = servicio.resolver(estado, unico,
                UI.elegirEnum(ServicioMisionesPersonales.Desenlace.class, 1, 4));
        UI.pintarResultado(resultado);
        repo.guardar(estado); UI.pausa();
    }

    private String texto(MercenarioUnico u) {
        return switch (u) {
            case EL_RETORNADO -> "El cuerpo que ocupaba su tumba abre los ojos. Solo uno de los dos conservará esa vida prestada.";
            case SOR_EREA -> "La campana obliga a responder, pero el santo pide a Erea que elija entre obediencia y voz propia.";
            case XOAN_DAS_NAVALLAS -> "La familia ofrece perdón a cambio de otra traición. Xoán deja la decisión en vuestras manos.";
            case A_FILLA_DO_LOBO -> "La camada la reconoce como hermana. La sangre humana y la del monte reclaman nombres distintos.";
            case MARTINO_EL_TUERTO -> "La moura devuelve la mirada de Martiño y con ella todos los futuros que había preferido no ver.";
        };
    }
}
