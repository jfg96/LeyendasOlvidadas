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

/**
 * La aldea: refugio entre expediciones. Tablon de misiones, ermita,
 * taberna, herreria y gestion del equipo.
 */
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
     * Bucle de la aldea. @return la expedicion elegida, o null si el jugador sale del juego.
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
            String danados = estado.getEstadoAldea().getNiveles().keySet().stream()
                    .filter(estado.getEstadoAldea()::estaDanado).map(EdificioAldea::getNombre)
                    .reduce((a, b) -> a + ", " + b).orElse("");
            if (!danados.isEmpty()) UI.log(UI.pintar("Edificios dañados: " + danados, UI.ROJO));
            System.out.println();
            System.out.println("  1. Tablon de encargos " + UI.pintar("(partir de expedicion)", UI.TENUE));
            System.out.println("  2. Ermita " + UI.pintar("(curar cuerpo y alma)", UI.TENUE));
            System.out.println("  3. Taberna " + UI.pintar("(rumores, vino y dados)", UI.TENUE));
            System.out.println("  4. Herreria " + UI.pintar("(comprar, vender, forjar)", UI.TENUE));
            System.out.println("  5. Mochila y equipo");
            System.out.println("  6. Gestionar compania " + UI.pintar("(contratar y formar grupo)", UI.TENUE));
            System.out.println("  7. Estado y reparaciones de Valdesombra");
            System.out.println("  8. Guardar partida");
            System.out.println("  9. Cargar partida");
            System.out.println("  10. Guardar y salir del juego");
            switch (UI.leerOpcion(1, 10)) {
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
                case 8: repositorioPartidas.guardar(estado); UI.pausa(); break;
                case 9: cargarPartida(); break;
                case 10: repositorioPartidas.guardar(estado); return null;
            }
        }
    }

    private void repararAldea() {
        List<EdificioAldea> danados = estado.getEstadoAldea().getNiveles().keySet().stream()
                .filter(estado.getEstadoAldea()::estaDanado).toList();
        UI.seccion("VALDESOMBRA");
        if (danados.isEmpty()) { UI.log("Todos los edificios permanecen en pie."); UI.pausa(); return; }
        for (int i=0;i<danados.size();i++) UI.log((i+1)+". "+danados.get(i).getNombre()+" — "
                +(80+estado.getEstadoAldea().nivel(danados.get(i))*20)+" reales");
        System.out.println("  0. Volver"); int op=UI.leerOpcion(0,danados.size());
        if(op>0) mostrarResultado(servicioAldea.reparar(estado,danados.get(op-1))); UI.pausa();
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
                    + UI.pintar(EstadoJuego.costeContratacion(p) + " reales", UI.AMARILLO));
            if (p.getTrasfondo() != null)
                UI.log(UI.pintar("   " + p.getTrasfondo().origen() + " · " + p.getTrasfondo().rasgo(), UI.TENUE));
        }
        System.out.println("  0. Volver");
        int op = UI.leerOpcion(0, candidatos.size());
        if (op == 0) return;
        Personaje candidato = candidatos.get(op - 1);
        mostrarTrasfondo(candidato);
        if (!UI.confirmar("¿Contratar a " + candidato.getNombre() + " por "
                + EstadoJuego.costeContratacion(candidato) + " reales?")) return;
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
        UI.log(UI.pintar(resultado.mensaje(), resultado.exito() ? UI.VERDE : UI.ROJO));
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

    // ---------------------------------------------------------------- tablon
    private Expedicion tablon() {
        Personaje h = estado.getJugador();
        if (!estado.getCompania().estaCompleta()) {
            UI.log(UI.pintar("Necesitas una formacion de tres antes de partir.", UI.ROJO));
            UI.log("Contrata acompanantes y prepara el grupo desde Gestionar compania.");
            UI.pausa();
            return null;
        }
        UI.limpiar();
        UI.seccion("TABLON DE ENCARGOS");
        boolean finalDisponible = estado.getProgresoCampana().getCapitulo() == CapituloCampana.ULTIMA_PROCESION
                && !estado.isCampanaGanada();
        boolean cierreBosque = estado.getProgresoCampana().haDecidido("cap1.simbolo_peregrinos_descubierto")
                && !estado.getProgresoCampana().haDecidido("cap1.rei_derrotado");
        Mision[] ofertas = new Mision[3];
        Region[] regiones = estado.getProgresoCampana().getCapitulo() == CapituloCampana.CAMINOS_DE_ANIMAS
                ? new Region[]{Region.BRANAS_HUNDIDAS, Region.CAMINO_DE_LOS_DIFUNTOS, Region.BRANAS_HUNDIDAS}
                : estado.getProgresoCampana().getCapitulo() == CapituloCampana.DEUDA_DE_LOS_VIVOS
                ? new Region[]{Region.MINAS_DE_SAN_LOURENZO, Region.PAZO_DE_SOUTOMAIOR, Region.MINAS_DE_SAN_LOURENZO}
                : estado.getProgresoCampana().getCapitulo() == CapituloCampana.LIBRO_DE_LOS_NOMBRES
                ? new Region[]{Region.BOSQUE_DE_LOS_AHORCADOS, Region.CAMINO_DE_LOS_DIFUNTOS, Region.PAZO_DE_SOUTOMAIOR}
                : new Region[]{Region.BOSQUE_DE_LOS_AHORCADOS, Region.BOSQUE_DE_LOS_AHORCADOS, Region.BOSQUE_DE_LOS_AHORCADOS};
        Dificultad[] difs = {Dificultad.FACIL, Dificultad.MEDIA, Dificultad.DIFICIL};
        for (int i = 0; i < 3; i++) {
            ofertas[i] = estado.getProgresoCampana().getCapitulo() == CapituloCampana.LIBRO_DE_LOS_NOMBRES
                    ? GestorMisiones.generarLibroNombres(regiones[i], estado.getCompania().nivelMedio(), difs[i])
                    : estado.getProgresoCampana().estaDesbloqueada(regiones[i])
                    ? GestorMisiones.generarRegional(regiones[i], estado.getCompania().nivelMedio(), difs[i])
                    : GestorMisiones.generar(estado.getCompania().nivelMedio(), difs[i]);
            System.out.printf("  %d. [%s] %-24s %s%n", i + 1,
                    UI.pintar(difs[i].getTitulo(), difs[i] == Dificultad.FACIL ? UI.VERDE
                            : difs[i] == Dificultad.MEDIA ? UI.AMARILLO : UI.ROJO),
                    ofertas[i].getNombre(),
                    UI.pintar(ofertas[i].getOroRecompensa() + " reales, " + ofertas[i].getXpRecompensa() + " XP", UI.TENUE));
            UI.log(UI.pintar("   " + ofertas[i].getDescripcion(), UI.TENUE));
        }
        List<Mision> especiales = new ArrayList<>();
        if (finalDisponible) especiales.add(new MisionJefe(Dificultad.DIFICIL, 500, 1000,
                Amuleto.aleatorio(30), true).enRegion(Region.HOSPITAL_DEL_CAMINO_VIEJO));
        else if (cierreBosque) especiales.add(new MisionJefe("El rey de las sogas",
                "Seguir a Inés y abatir a O Rei dos Aforcados.", Dificultad.MEDIA, 220, 300,
                Amuleto.aleatorio(20), false).enRegion(Region.BOSQUE_DE_LOS_AHORCADOS));
        boolean jefeBranas = estado.getProgresoCampana().haDecidido("cap2.branas.jefe_disponible")
                && !estado.getProgresoCampana().haDecidido("cap2.lavandeira_derrotada");
        boolean jefeCamino = estado.getProgresoCampana().haDecidido("cap2.camino.jefe_disponible")
                && !estado.getProgresoCampana().haDecidido("cap2.hospitalario_derrotado");
        if (jefeBranas) especiales.add(new MisionJefe("Los sudarios de Aldara",
                "Derrotar a A Lavandeira Maior y recuperar las páginas sumergidas.", Dificultad.MEDIA,
                260, 340, Amuleto.aleatorio(22), false).enRegion(Region.BRANAS_HUNDIDAS));
        if (jefeCamino) especiales.add(new MisionJefe("Las puertas del hospital",
                "Vencer al Hospitalario que cerró las puertas durante el incendio.", Dificultad.DIFICIL,
                300, 400, Amuleto.aleatorio(25), false).enRegion(Region.CAMINO_DE_LOS_DIFUNTOS));
        boolean jefeMinas = estado.getProgresoCampana().haDecidido("cap3.minas.jefe_disponible")
                && !estado.getProgresoCampana().haDecidido("cap3.capataz_derrotado");
        boolean jefePazo = estado.getProgresoCampana().haDecidido("cap3.pazo.jefe_disponible")
                && !estado.getProgresoCampana().haDecidido("cap3.cripta_soutomaior_abierta");
        if (jefeMinas) especiales.add(new MisionJefe("La campana del capataz", "Romper las cadenas de O Capataz.",
                Dificultad.MEDIA, 320, 440, Amuleto.aleatorio(25), false).enRegion(Region.MINAS_DE_SAN_LOURENZO));
        if (jefePazo) especiales.add(new MisionJefe("La cripta de los Soutomaior", "Entrar en la cripta donde se oculta la Falange.",
                Dificultad.DIFICIL, 360, 500, Amuleto.aleatorio(28), false).enRegion(Region.PAZO_DE_SOUTOMAIOR));
        boolean ritualNombres = new ServicioCapituloCuatro().puedeCelebrarRitual(estado);
        if (ritualNombres) especiales.add(new MisionReliquia("La vigilia de los ciento doce",
                "Llevar el Libro reconstruido hasta el osario y devolver los nombres a sus muertos.",
                Dificultad.DIFICIL, 420, 650, Amuleto.aleatorio(30)).enRegion(Region.CAMINO_DE_LOS_DIFUNTOS));
        especiales.addAll(new ServicioMisionesPersonales().disponibles(estado));
        for (int i = 0; i < especiales.size(); i++)
            System.out.println(UI.pintar("  " + (i + 4) + ". ☠ " + especiales.get(i).getNombre().toUpperCase()
                    + " — " + especiales.get(i).getDescripcion(), UI.MAGENTA));
        System.out.println("  0. Volver a la plaza");
        int max = 3 + especiales.size();
        int op = UI.leerOpcion(0, max);
        if (op == 0) return null;
        Mision elegida = op > 3 ? especiales.get(op - 4) : ofertas[op - 1];
        if (!UI.confirmar("¿Partir hacia '" + elegida.getNombre() + "'?")) return null;
        return new Expedicion(estado.getCompania(), elegida, elegida.getDificultad(),
                estado.getExpedicionesGanadas());
    }

    // ---------------------------------------------------------------- ermita
    private void ermita(Personaje h) {
        UI.limpiar();
        UI.seccion("LA ERMITA DEL SANTO OLVIDADO");
        UI.log("La ermitaña te recibe con un gesto sereno.");
        int costeCura = 15 + h.getNivel() * 5;
        int costeCalma = 25;
        System.out.println("  1. Sanar las heridas por completo (" + costeCura + " reales)");
        System.out.println("  2. Confesion y rezo: -35 estres (" + costeCalma + " reales)");
        if (!h.getHeridas().isEmpty()) System.out.println("  3. Tratar una secuela (" + (35 + h.getNivel() * 10) + " reales)");
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

    // --------------------------------------------------------------- taberna
    private void taberna(Personaje h) {
        UI.limpiar();
        UI.seccion("TABERNA \"EL CANDIL TORCIDO\"");
        System.out.println("  1. Un vaso de vino: -15 estres (10 reales)");
        System.out.println("  2. Escuchar rumores (gratis)");
        System.out.println("  3. Jugar a los dados (apuesta lo que quieras)");
        System.out.println("  0. Salir");
        switch (UI.leerOpcion(0, 3)) {
            case 1:
                mostrarResultado(servicioAldea.calmar(estado, h, 10, 15));
                break;
            case 2: {
                String[] rumores = {
                    "\"Dicen que cuanto mas negra la noche, mas rico el botin... y mas larga la procesion.\"",
                    "\"La Meiga cobra en recuerdos. No la mires a los ojos.\"",
                    "\"El Cuelebre duerme sobre oro. Lo dificil no es entrar, es salir.\"",
                    "\"Si oyes campanillas de madrugada, reza: la Compania busca a quien lleve su vela.\"",
                    "\"Un buen amuleto vale mas que cien espadas. Un mal amuleto... tambien cobra.\"",
                    "\"Acampa cuando puedas, forastero. La cabeza se quiebra antes que el espinazo.\""};
                UI.log(UI.pintar(Rng.elegir(List.of(rumores)), UI.CIAN));
                break;
            }
            case 3: {
                System.out.println("  ¿Cuanto apuestas? (tienes " + h.getInventario().getOro() + " reales)");
                int apuesta = UI.leerOpcion(0, Math.max(0, h.getInventario().getOro()));
                if (apuesta == 0) { UI.log("Hoy no es dia de tentar la suerte."); break; }
                h.getInventario().gastarOro(apuesta);
                if (Rng.prob(45)) {
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

    // -------------------------------------------------------------- herreria
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
            int costeForja = h.getArma() != null ? 50 * (h.getArma().getMejoras() + 1) : 0;
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
