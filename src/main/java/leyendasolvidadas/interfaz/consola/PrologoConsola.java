package leyendasolvidadas.interfaz.consola;

import leyendasolvidadas.aplicacion.Combate;
import leyendasolvidadas.aplicacion.EstadoJuego;
import leyendasolvidadas.aplicacion.RepositorioPartidas;
import leyendasolvidadas.aplicacion.ServicioPrologo;
import leyendasolvidadas.dominio.combate.Enemigo;

import java.util.List;

/** Adaptador de terminal para las escenas y decisiones del prólogo. */
public final class PrologoConsola {
    private final RepositorioPartidas repositorio;
    private final ServicioPrologo servicio = new ServicioPrologo();

    public PrologoConsola(RepositorioPartidas repositorio) {
        this.repositorio = repositorio;
    }

    public void jugar(EstadoJuego estado) {
        while (servicio.pasoActual(estado) != ServicioPrologo.Paso.COMPLETADO) {
            switch (servicio.pasoActual(estado)) {
                case CARTA -> carta(estado);
                case MOTIVACION -> motivacion(estado);
                case FUNERAL -> funeral(estado);
                case RASTRO -> rastro(estado);
                case CEMENTERIO -> cementerio(estado);
                case DESENLACE -> desenlace(estado);
                case COMPLETADO -> { return; }
            }
            repositorio.guardar(estado);
        }
    }

    private void carta(EstadoJuego estado) {
        UI.limpiar();
        UI.titulo("PROLOGO — LA NOVENA CAMPANADA");
        UI.log("El caballo del anterior capitán regresó solo al amanecer.");
        UI.log("Tenía espuma negra en el hocico y una campana cosida dentro del vientre.");
        UI.log("");
        UI.log("Tres días después llegó una carta del concejo de Valdesombra:");
        UI.log(UI.pintar("\"Venid antes de la novena campanada. Después, ya no quedará", UI.TENUE));
        UI.log(UI.pintar("nadie capaz de recordar por qué os llamamos.\"", UI.TENUE));
        servicio.leerCarta(estado);
        UI.pausa();
    }

    private void motivacion(EstadoJuego estado) {
        UI.limpiar();
        UI.seccion("¿POR QUE ACUDISTE A VALDESOMBRA?");
        System.out.println("  1. Por el dinero prometido por el concejo.");
        System.out.println("  2. Por deber hacia la antigua Hermandad del Camino.");
        System.out.println("  3. Por una culpa familiar que nunca te explicaron.");
        System.out.println("  4. Porque ningún camino prohibido permanece sin recorrer.");
        ServicioPrologo.Motivacion eleccion = UI.elegirEnum(ServicioPrologo.Motivacion.class);
        servicio.elegirMotivacion(estado, eleccion);
    }

    private void funeral(EstadoJuego estado) {
        UI.limpiar();
        UI.titulo("SANTA MARIÑA DE VALDESOMBRA");
        UI.log("Llegas mientras entierran a una mujer bajo una lluvia que no limpia nada.");
        UI.log("Su hija, Lúa, aprieta una muñeca de trapo junto a la fosa.");
        UI.log("La campana dobla por novena vez. Todos bajan la cabeza.");
        UI.log("");
        UI.log(UI.pintar("Cuando vuelven a levantarla, la niña ha desaparecido.", UI.MAGENTA));
        UI.log("Solo quedan huellas pequeñas que atraviesan la verja del cementerio.");
        servicio.registrarDesaparicion(estado);
        UI.pausa();
    }

    private void rastro(EstadoJuego estado) {
        UI.limpiar();
        UI.seccion("AL OTRO LADO DE LA VERJA");
        UI.log("Encuentras a Lúa frente a una tumba sin inscripción.");
        UI.log("Tiene ceniza alrededor del cuello y repite: \"Dice que han olvidado su nombre\".");
        System.out.println("\n  1. Preguntarle su nombre y anotarlo en tu carta.");
        System.out.println("  2. Prometerle que la devolverás con vida junto a su familia.");
        System.out.println("  3. Examinar la ceniza y las marcas alrededor de la tumba.");
        ServicioPrologo.RespuestaNina respuesta = UI.elegirEnum(ServicioPrologo.RespuestaNina.class);
        servicio.responderALaNina(estado, respuesta);
    }

    private void cementerio(EstadoJuego estado) {
        UI.limpiar();
        UI.log("La tierra se abre sin ruido. Un hábito carbonizado emerge de la fosa.");
        UI.log("Donde debería haber un rostro solo queda una superficie lisa cubierta de hollín.");
        UI.log(UI.pintar("La criatura extiende una mano hacia la niña.", UI.ROJO));
        UI.pausa();

        Enemigo sinRostro = servicio.crearSinRostro(estado.getJugador().getNivel());
        Combate.Resultado resultado = new Combate(estado.getJugador(), List.of(sinRostro),
                null, null, new VistaCombateConsola()).ejecutar(false);
        ServicioPrologo.ResultadoCementerio consecuencia = switch (resultado) {
            case VICTORIA -> ServicioPrologo.ResultadoCementerio.VICTORIA;
            case HUIDA -> ServicioPrologo.ResultadoCementerio.HUIDA;
            case DERROTA -> ServicioPrologo.ResultadoCementerio.DERROTA;
        };
        servicio.resolverCementerio(estado, consecuencia);
    }

    private void desenlace(EstadoJuego estado) {
        UI.limpiar();
        UI.titulo("LA NIÑA QUE NO EXISTÍA");
        UI.log("Regresas a la plaza con Lúa agarrada a tu mano.");
        UI.log("El párroco os mira, confuso. Su abuela pregunta por qué traes una muñeca vacía.");
        UI.log("Nadie recuerda a la niña. Su nombre ha desaparecido del registro del bautismo");
        UI.log("y de la piedra recién colocada sobre la tumba de su madre.");
        UI.log("");
        if (estado.getProgresoCampana().haDecidido("prologo.nina.anotar_nombre"))
            UI.log(UI.pintar("Pero en el margen de tu carta todavía puede leerse: LÚA.", UI.CIAN));
        else
            UI.log(UI.pintar("Solo tú recuerdas que hace un instante alguien caminaba a tu lado.", UI.CIAN));
        UI.log("");
        UI.log("Padre Tomé cierra las puertas de la ermita y susurra:");
        UI.log(UI.pintar("\"Los muertos no han venido por nuestras almas. Han venido por los nombres.\"", UI.MAGENTA));
        servicio.finalizar(estado);
        UI.pausa();
    }
}
