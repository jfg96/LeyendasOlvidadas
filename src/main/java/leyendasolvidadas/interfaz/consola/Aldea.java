package leyendasolvidadas.interfaz.consola;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.campana.*;
import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.infraestructura.*;
import leyendasolvidadas.interfaz.consola.*;

import java.util.ArrayList;
import java.util.List;

/** Gestiona las acciones disponibles en Valdesombra. */
public class Aldea {
    private EstadoJuego estado;
    private final RepositorioPartidas repositorioPartidas;
    private final ServicioCompania servicioCompania = new ServicioCompania();
    private final ServicioAldea servicioAldea = new ServicioAldea();

    public Aldea(EstadoJuego estado, RepositorioPartidas repositorioPartidas) {
        this.estado = estado;
        this.repositorioPartidas = repositorioPartidas;
    }
    public EstadoJuego getEstado() { return estado; }

    /**
     * Muestra la aldea hasta que el jugador parte o sale.
     *
     * @return la expedición elegida, o {@code null} si termina la partida
     */
    public Expedicion bucle() {
        while (true) {
            Personaje h = estado.getJugador();
            UI.limpiar();
            UI.titulo("ALDEA DE VALDESOMBRA — Semana " + estado.getSemana());
            System.out.println("  " + UI.barra("Vida", h.getVida(), h.getVidaMax(), UI.VERDE)
                    + "   " + UI.barra("Cordura", h.getCordura(), 100, UI.MAGENTA));
            UI.log(h.getNombre() + ", nivel " + h.getNivel() + " (" + h.getExperiencia() + "/"
                    + h.xpNecesaria() + " XP)   Oro: " + UI.pintar(h.getInventario().getOro() + " reales", UI.AMARILLO)
                    + "   Parajes limpiados: " + estado.getExpedicionesGanadas());
            UI.log("Formacion: " + estado.getCompania().getFormacionActiva().stream()
                    .map(Personaje::getNombre).reduce((a, b) -> a + " / " + b).orElse("-") );
            UI.log("Campaña: " + UI.pintar(estado.getProgresoCampana().getCapitulo().getTitulo(), UI.MAGENTA));
            String danados = estado.getEstadoAldea().getNiveles().keySet().stream()
                    .filter(estado.getEstadoAldea()::estaDanado).map(EdificioAldea::getNombre)
                    .reduce((a, b) -> a + ", " + b).orElse("");
            if (!danados.isEmpty()) UI.log(UI.pintar("Edificios dañados: " + danados, UI.ROJO));
            UI.seccion("AVENTURA");
            UI.opcion(1, "Tablón de encargos", "preparar una expedición");
            UI.seccion("SERVICIOS DE VALDESOMBRA");
            UI.opcion(2, "Ermita", "curar cuerpo y alma");
            UI.opcion(3, "Taberna", "rumores, vino y dados");
            UI.opcion(4, "Herrería", "comprar, vender y forjar");
            UI.opcion(5, "Mochila y equipo", "inventario compartido");
            UI.opcion(6, "Compañía", "contratar y preparar formación");
            UI.opcion(7, "Valdesombra", "edificios, daños y mejoras");
            UI.seccion("ARCHIVO");
            UI.opcion(8, "Diario de campaña", "historia de esta partida");
            UI.opcion(9, "Bestiario", "criaturas descubiertas");
            UI.seccion("SISTEMA");
            UI.opcion(10, "Guardar partida", "conservar el progreso");
            UI.opcion(11, "Cargar partida", "recuperar el último guardado");
            UI.opcion(12, "Ayuda", "controles y reglas esenciales");
            UI.opcion(13, "Guardar y salir", "volver al sistema");
            switch (UI.leerOpcion(1, 13)) {
                case 1: {
                    Expedicion e = tablon();
                    if (e != null) return e;
                    break;
                }
                case 2: {
                    Personaje paciente = elegirMiembro("\u00bfQuien necesita la ermita?");
                    if (paciente != null) ermita(paciente);
                    break;
                }
                case 3: taberna(h); break;
                case 4: herreria(h); break;
                case 5: gestionarEquipo(); break;
                case 6: gestionarCompania(); break;
                case 7: repararAldea(); break;
                case 8: mostrarDiario(); break;
                case 9: mostrarBestiario(); break;
                case 10: repositorioPartidas.guardar(estado); UI.pausa(); break;
                case 11: cargarPartida(); break;
                case 12: mostrarAyuda(); break;
                case 13: repositorioPartidas.guardar(estado); return null;
            }
        }
    }

    private void mostrarAyuda() {
        UI.limpiar(); UI.titulo("AYUDA DE CAMPO");
        UI.seccion("COMBATE POR INICIATIVA");
        UI.log("No eliges qué héroe actúa. Al comenzar cada ronda, velocidad y azar fijan el orden.");
        UI.log("Busca el rótulo «AHORA ACTÚA»; las habilidades mostradas pertenecen únicamente a ese héroe.");
        UI.log("Después de elegir habilidad, el juego pide objetivo solo cuando hay varias opciones válidas.");
        UI.seccion("FILAS E INTENCIONES");
        UI.log("F1 es vanguardia y F3 retaguardia. Cada habilidad indica las filas que puede alcanzar.");
        UI.log("⚔ ataque · ‼ golpe fuerte · ☣ daño persistente · ◉ estrés · ◇ control · ◆ apoyo.");
        UI.seccion("EXPEDICIONES");
        UI.log("Cumple el objetivo antes de volver. Abandonar evita riesgos mayores, pero pierde la recompensa.");
        UI.log("La oscuridad aumenta peligro y botín. Las heridas de una derrota se tratan en la ermita.");
        UI.pausa();
    }

    private void repararAldea() {
        UI.seccion("VALDESOMBRA");
        EdificioAldea[] edificios = EdificioAldea.values();
        for (int i = 0; i < edificios.length; i++) {
            EdificioAldea e = edificios[i];
            String accion = estado.getEstadoAldea().estaDanado(e) ? "REPARAR"
                    : estado.getEstadoAldea().nivel(e) < 3 ? "mejorar por " + servicioAldea.costeMejora(estado, e) : "máximo";
            UI.log((i + 1) + ". " + e.getNombre() + " · nivel " + estado.getEstadoAldea().nivel(e) + " · " + accion);
            UI.log(UI.pintar("   " + efectoEdificio(e), UI.TENUE));
        }
        System.out.println("  0. Volver"); int op = UI.leerOpcion(0, edificios.length);
        if (op > 0) {
            EdificioAldea e = edificios[op - 1];
            mostrarResultado(estado.getEstadoAldea().estaDanado(e) ? servicioAldea.reparar(estado, e)
                    : servicioAldea.mejorar(estado, e));
        }
        UI.pausa();
    }

    private String efectoEdificio(EdificioAldea e) {
        return switch (e) {
            case ERMITA -> "Reduce el coste de curar cuerpo y secuelas.";
            case HERRERIA -> "Mejora la calidad semanal y abarata la forja.";
            case TABERNA -> "El vino cuesta menos y alivia más estrés.";
            case ARCHIVO -> "Aumenta la experiencia obtenida en expediciones.";
            case CUARTEL -> "Reduce el coste de contratar mercenarios.";
            case CAMPANARIO -> "La formación parte con mayor serenidad.";
        };
    }

    private void mostrarDiario() {
        UI.seccion("DIARIO DE CAMPAÑA");
        List<String> entradas = estado.getRegistroCampana().getDiario();
        if (entradas.isEmpty()) UI.log("La crónica aún espera su primera línea.");
        else entradas.forEach(e -> UI.log("• " + e));
        UI.pausa();
    }

    private void mostrarBestiario() {
        UI.seccion("BESTIARIO DE VALDESOMBRA");
        if (estado.getRegistroCampana().getCriaturas().isEmpty()) UI.log("Todavía no habéis estudiado criatura alguna.");
        else for (String criatura : estado.getRegistroCampana().getCriaturas()) {
            UI.log(UI.pintar(criatura, UI.AMARILLO)); UI.log("   " + Bestiario.descripcion(criatura));
        }
        UI.pausa();
    }

    private void gestionarEquipo() {
        Personaje elegido = elegirMiembro("¿Quien revisa el equipo?");
        if (elegido != null) ControladorInventario.menuUsar(estado.getCompania().getInventario(), elegido, null);
        UI.pausa();
    }

    private void gestionarCompania() {
        while (true) {
            UI.limpiar();
            mostrarCompania();
            System.out.println("  1. Contratar aventurero");
            System.out.println("  2. Preparar formacion activa");
            System.out.println("  3. Despedir aventurero");
            System.out.println("  0. Volver a la plaza");
            switch (UI.leerOpcion(0, 3)) {
                case 0: return;
                case 1: contratar(); break;
                case 2: prepararFormacion(); break;
                case 3: despedir(); break;
            }
        }
    }

    private void mostrarCompania() {
        Compania compania = estado.getCompania();
        UI.seccion("LA COMPANIA  Plantilla " + compania.getPlantilla().size() + "/" + Compania.MAX_PLANTILLA);
        for (int i = 0; i < compania.getPlantilla().size(); i++) {
            Personaje p = compania.getPlantilla().get(i);
            String marcas = compania.esProtagonista(p) ? " [PROTAGONISTA]" : "";
            if (compania.getFormacionActiva().contains(p)) marcas += " [ACTIVO]";
            UI.log((i + 1) + ". " + p.getNombre() + " — " + p.getClass().getSimpleName()
                    + " niv " + p.getNivel() + " · lealtad " + p.getLealtad()
                    + (p.getHeridas().isEmpty() ? "" : " · " + p.getHeridas().size() + " secuela(s)")
                    + UI.pintar(marcas, UI.CIAN));
        }
        List<Personaje> activos = compania.getFormacionActiva();
        if (activos.size() > 1) {
            UI.log(UI.pintar("Relaciones de la formación:", UI.TENUE));
            for (int i = 0; i < activos.size(); i++) for (int j = i + 1; j < activos.size(); j++) {
                int afinidad = compania.afinidad(activos.get(i), activos.get(j));
                String vinculo = afinidad >= 30 ? "confianza" : afinidad <= -30 ? "rivalidad" : "distancia";
                UI.log("   " + activos.get(i).getNombre() + " / " + activos.get(j).getNombre()
                        + ": " + vinculo + " (" + (afinidad >= 0 ? "+" : "") + afinidad + ")");
            }
        }
        UI.log("Tesoreria: " + UI.pintar(compania.getInventario().getOro() + " reales", UI.AMARILLO));
    }

    private void contratar() {
        Compania compania = estado.getCompania();
        if (compania.plantillaLlena()) {
            UI.log(UI.pintar("La compania ya tiene seis miembros.", UI.ROJO));
            UI.pausa();
            return;
        }
        List<Personaje> candidatos = estado.getCandidatos();
        if (candidatos.isEmpty()) {
            UI.log(UI.pintar("No quedan aventureros disponibles esta semana.", UI.ROJO));
            UI.pausa();
            return;
        }
        UI.seccion("AVENTUREROS EN BUSCA DE COMPANIA");
        for (int i = 0; i < candidatos.size(); i++) {
            Personaje p = candidatos.get(i);
            UI.log((i + 1) + ". " + p.getNombre() + " — " + p.getClass().getSimpleName()
                    + " niv " + p.getNivel() + "  "
                    + UI.pintar(servicioCompania.costeContratacion(estado, p) + " reales", UI.AMARILLO));
            if (p.getTrasfondo() != null)
                UI.log(UI.pintar("   " + p.getTrasfondo().origen() + " · " + p.getTrasfondo().rasgo(), UI.TENUE));
        }
        System.out.println("  0. Volver");
        int op = UI.leerOpcion(0, candidatos.size());
        if (op == 0) return;
        Personaje candidato = candidatos.get(op - 1);
        mostrarTrasfondo(candidato);
        if (!UI.confirmar("¿Contratar a " + candidato.getNombre() + " por "
                + servicioCompania.costeContratacion(estado, candidato) + " reales?")) return;
        mostrarResultado(servicioCompania.contratar(estado, candidato));
        UI.pausa();
    }

    private void mostrarTrasfondo(Personaje candidato) {
        TrasfondoMercenario t = candidato.getTrasfondo();
        if (t == null) return;
        UI.seccion(candidato.getNombre().toUpperCase());
        UI.log(t.descripcion());
        UI.log("Origen: " + t.origen());
        UI.log(UI.pintar("Rasgo: " + t.rasgo(), UI.VERDE));
        if (candidato.getRasgoMecanico() != null) UI.log(UI.pintar("   Efecto: " + candidato.getRasgoMecanico().getEfecto(), UI.VERDE));
        UI.log(UI.pintar("Defecto: " + t.defecto(), UI.ROJO));
        if (candidato.getDefectoMecanico() != null) UI.log(UI.pintar("   Efecto: " + candidato.getDefectoMecanico().getEfecto(), UI.ROJO));
        UI.log("Motivación: " + t.motivacion());
        UI.log(UI.pintar("\"" + t.frase() + "\"", UI.CIAN));
    }

    private void prepararFormacion() {
        Compania compania = estado.getCompania();
        List<Personaje> disponibles = new ArrayList<>(compania.getPlantilla());
        disponibles.remove(compania.getProtagonista());
        List<Personaje> formacion = new ArrayList<>();
        formacion.add(compania.getProtagonista());

        while (formacion.size() < Compania.MAX_FORMACION && !disponibles.isEmpty()) {
            UI.seccion("ELIGE ACOMPANANTE " + formacion.size() + " DE " + (Compania.MAX_FORMACION - 1));
            for (int i = 0; i < disponibles.size(); i++) {
                Personaje p = disponibles.get(i);
                UI.log((i + 1) + ". " + p.getNombre() + " — " + p.getClass().getSimpleName()
                        + " niv " + p.getNivel());
            }
            System.out.println("  0. Terminar formacion");
            int op = UI.leerOpcion(0, disponibles.size());
            if (op == 0) break;
            formacion.add(disponibles.remove(op - 1));
        }
        mostrarResultado(servicioCompania.prepararFormacion(estado, formacion));
        UI.pausa();
    }

    private void despedir() {
        Personaje elegido = elegirMiembro("¿A quien quieres despedir?");
        if (elegido == null) return;
        if (estado.getCompania().esProtagonista(elegido)) {
            UI.log(UI.pintar("El protagonista no puede abandonar su propia leyenda.", UI.ROJO));
        } else if (UI.confirmar("¿Despedir a " + elegido.getNombre() + "?")) {
            mostrarResultado(servicioCompania.despedir(estado, elegido));
        }
        UI.pausa();
    }

    private void mostrarResultado(ResultadoAccion resultado) {
        UI.pintarResultado(resultado);
    }

    private Personaje elegirMiembro(String titulo) {
        List<Personaje> miembros = estado.getCompania().getPlantilla();
        UI.seccion(titulo);
        for (int i = 0; i < miembros.size(); i++) {
            Personaje p = miembros.get(i);
            UI.log((i + 1) + ". " + p.getNombre() + " — " + p.getClass().getSimpleName());
        }
        System.out.println("  0. Cancelar");
        int op = UI.leerOpcion(0, miembros.size());
        return op == 0 ? null : miembros.get(op - 1);
    }

    private void cargarPartida() {
        if (!repositorioPartidas.existePartida()) {
            UI.log(UI.pintar("No hay ninguna partida guardada que cargar.", UI.ROJO));
            UI.pausa();
            return;
        }
        if (!UI.confirmar("¿Cargar la partida guardada? Perderas el progreso no guardado")) return;

        EstadoJuego cargado = repositorioPartidas.cargar();
        if (cargado != null) {
            estado = cargado;
            UI.log(UI.pintar("Partida cargada. Regresas a Valdesombra.", UI.VERDE));
        }
        UI.pausa();
    }

    private Expedicion tablon() {
        Mision elegida = new TablonMisionesConsola().elegir(estado);
        if (elegida == null) return null;
        return new Expedicion(estado.getCompania(), elegida, elegida.getDificultad(),
                estado.getExpedicionesGanadas(), estado.getRegistroCampana(), estado.getAzar());
    }

    private void ermita(Personaje h) {
        UI.limpiar();
        UI.seccion("LA ERMITA DEL SANTO OLVIDADO");
        UI.log("La ermitaña te recibe con un gesto sereno.");
        int costeCura = servicioAldea.costeSanar(estado, h);
        int costeCalma = 25;
        System.out.println("  1. Sanar las heridas por completo (" + costeCura + " reales)");
        System.out.println("  2. Confesion y rezo: -35 estres (" + costeCalma + " reales)");
        if (!h.getHeridas().isEmpty()) System.out.println("  3. Tratar una secuela (" + servicioAldea.costeTratar(estado, h) + " reales)");
        System.out.println("  0. Salir");
        switch (UI.leerOpcion(0, h.getHeridas().isEmpty() ? 2 : 3)) {
            case 1:
                mostrarResultado(servicioAldea.sanar(estado, h));
                break;
            case 2:
                mostrarResultado(servicioAldea.calmar(estado, h, costeCalma, 35));
                break;
            case 3:
                for (int i = 0; i < h.getHeridas().size(); i++)
                    UI.log((i + 1) + ". " + h.getHeridas().get(i).name().replace('_', ' ').toLowerCase()
                            + " — " + h.getHeridas().get(i).getEfecto());
                int herida = UI.leerOpcion(1, h.getHeridas().size()) - 1;
                mostrarResultado(servicioAldea.tratarHerida(estado, h, h.getHeridas().get(herida)));
                break;
        }
        UI.pausa();
    }

    private void taberna(Personaje h) {
        UI.limpiar();
        UI.seccion("TABERNA \"EL CANDIL TORCIDO\"");
        int nivelTaberna = estado.getEstadoAldea().nivel(EdificioAldea.TABERNA);
        int alivioTaberna = 12 + nivelTaberna * 5;
        int costeTaberna = Math.max(4, 12 - nivelTaberna * 2);
        System.out.println("  1. Un vaso de vino: -" + alivioTaberna + " estres (" + costeTaberna + " reales)");
        System.out.println("  2. Escuchar rumores (gratis)");
        System.out.println("  3. Jugar a los dados (apuesta lo que quieras)");
        System.out.println("  0. Salir");
        switch (UI.leerOpcion(0, 3)) {
            case 1:
                mostrarResultado(servicioAldea.calmar(estado, h, costeTaberna, alivioTaberna));
                break;
            case 2: {
                String[] rumores = {
                    "\"Dicen que cuanto mas negra la noche, mas rico el botin... y mas larga la procesion.\"",
                    "\"La Meiga cobra en recuerdos. No la mires a los ojos.\"",
                    "\"El Cuelebre duerme sobre oro. Lo dificil no es entrar, es salir.\"",
                    "\"Si oyes campanillas de madrugada, reza: la Compania busca a quien lleve su vela.\"",
                    "\"Un buen amuleto vale mas que cien espadas. Un mal amuleto... tambien cobra.\"",
                    "\"Acampa cuando puedas, forastero. La cabeza se quiebra antes que el espinazo.\""};
                UI.log(UI.pintar(estado.getAzar().elegir(List.of(rumores)), UI.CIAN));
                break;
            }
            case 3: {
                System.out.println("  ¿Cuanto apuestas? (tienes " + h.getInventario().getOro() + " reales)");
                int apuesta = UI.leerOpcion(0, Math.max(0, h.getInventario().getOro()));
                if (apuesta == 0) { UI.log("Hoy no es dia de tentar la suerte."); break; }
                h.getInventario().gastarOro(apuesta);
                if (estado.getAzar().probabilidad(45)) {
                    h.getInventario().ganarOro(apuesta * 2);
                    UI.log(UI.pintar("¡Seis y seis! Doblas la apuesta: +" + apuesta * 2 + " reales.", UI.AMARILLO));
                } else {
                    UI.log(UI.pintar("Los dados te dan la espalda. Pierdes " + apuesta + " reales.", UI.ROJO));
                    h.sufrirEstres(3);
                }
                break;
            }
        }
        UI.pausa();
    }

    private void herreria(Personaje h) {
        while (true) {
            UI.limpiar();
            UI.seccion("HERRERIA DE LA VIUDA FERREIRO   Oro: " + h.getInventario().getOro());
            List<Item> ofertas = estado.getOfertasHerreria();
            System.out.println("  — Genero de la semana —");
            for (int i = 0; i < ofertas.size(); i++) {
                Item it = ofertas.get(i);
                System.out.printf("  %d. %-34s %-32s %s%n", i + 1, UI.item(it),
                        UI.pintar(it.descripcion(), UI.TENUE),
                        UI.pintar(it.getValorOro() + " reales", UI.AMARILLO));
            }
            int costeForja = servicioAldea.costeForjar(estado, h);
            System.out.println("  4. Vender objetos de la mochila (mitad de su valor)");
            if (h.getArma() != null)
                System.out.println("  5. Forjar tu arma: +3 de danio (" + costeForja + " reales)");
            System.out.println("  0. Salir");
            int op = UI.leerOpcion(0, 5);
            if (op == 0) return;
            if (op <= 3 && op <= ofertas.size()) {
                Item it = ofertas.get(op - 1);
                mostrarResultado(servicioAldea.comprar(estado, it));
                UI.pausa();
            } else if (op == 4) {
                vender(h);
            } else if (op == 5 && h.getArma() != null) {
                mostrarResultado(servicioAldea.forjar(estado, h));
                UI.pausa();
            }
        }
    }
    private void vender(Personaje h) {
        List<Item> items = h.getInventario().getItems();
        if (items.isEmpty()) { UI.log("La mochila esta vacia."); UI.pausa(); return; }
        ControladorInventario.mostrar(h.getInventario());
        System.out.println("  ¿Que vendes? (0 para nada)");
        int op = UI.leerOpcion(0, items.size());
        if (op == 0) return;
        Item it = items.get(op - 1);
        mostrarResultado(servicioAldea.vender(estado, it));
        UI.pausa();
    }
}
