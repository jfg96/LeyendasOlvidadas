package leyendasolvidadas.interfaz.consola;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.infraestructura.*;
import leyendasolvidadas.interfaz.consola.*;
import leyendasolvidadas.dominio.campana.RegistroCampana;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Gestiona el mapa, la luz y la exploración de una expedición. */
public class Expedicion implements ContextoCombate {

    /** Posibles resultados de una expedición. */
    public enum Resultado { EXITO, ABANDONO, MUERTE }

    private final List<Personaje> heroes;
    private final Personaje protagonista;
    private final Inventario inventario;
    private final GestorMisiones gestor = new GestorMisiones();
    private final Dificultad dificultad;
    private final int nivelZona;
    private final int victoriasPrevias;
    private final Region region;
    private final List<Habitacion> habitaciones;
    private Habitacion actual;
    private final Habitacion entrada;
    private final CondicionesExpedicion condiciones;
    private final RegistroCampana registro;

    public Expedicion(Compania compania, Mision mision, Dificultad dificultad, int victoriasPrevias) {
        this(compania, mision, dificultad, victoriasPrevias, null);
    }

    public Expedicion(Compania compania, Mision mision, Dificultad dificultad, int victoriasPrevias,
                      RegistroCampana registro) {
        this(compania, mision, dificultad, victoriasPrevias, registro, FuenteAzar.global());
    }

    public Expedicion(Compania compania, Mision mision, Dificultad dificultad, int victoriasPrevias,
                      RegistroCampana registro, FuenteAzar azar) {
        this.heroes = new ArrayList<>(compania.getFormacionActiva());
        this.protagonista = compania.getProtagonista();
        this.inventario = compania.getInventario();
        this.dificultad = dificultad;
        this.victoriasPrevias = victoriasPrevias;
        this.region = mision.getRegion();
        this.registro = registro;
        this.nivelZona = (int) Math.round(heroes.stream().mapToInt(Personaje::getNivel)
                .average().orElse(1)) + dificultad.getNivelExtra();
        this.condiciones = new CondicionesExpedicion(region, nivelZona, azar);
        gestor.asignar(mision);
        MapaExpedicion mapa = new MapaExpedicion(7 + dificultad.ordinal() * 2, mision.requiereObjetivo());
        this.habitaciones = mapa.getHabitaciones();
        this.entrada = mapa.getEntrada();
        this.actual = entrada;
        entrar(entrada, true);
    }

    private List<Personaje> heroesVivos() { return heroes.stream().filter(Personaje::estaVivo).toList(); }
    private boolean companiaDerrotada() { return heroesVivos().isEmpty(); }

    public int getLuz() { return condiciones.getLuz(); }
    public void subirLuz(int n) { condiciones.subirLuz(n); }
    public String luzTexto() {
        int luz = getLuz();
        String estado = luz >= 75 ? UI.pintar("Radiante", UI.AMARILLO)
                : luz >= 40 ? "Tenue"
                : luz >= 15 ? UI.pintar("Penumbra", UI.MAGENTA)
                : UI.pintar("TINIEBLAS", UI.ROJO);
        return luz + " (" + estado + ")";
    }
    /** Devuelve el multiplicador de botín correspondiente a la luz actual. */
    public double getMultBotin() { return condiciones.getMultBotin(); }
    /** Devuelve la mejora de rareza correspondiente a la luz actual. */
    public int getBonusRareza() { return condiciones.getBonusRareza(); }

    private long clave(int x, int y) { return ((long) x << 32) ^ (y & 0xffffffffL); }

    private void dibujarMapa() {
        int minX = 0, maxX = 0, minY = 0, maxY = 0;
        for (Habitacion h : habitaciones) {
            minX = Math.min(minX, h.getX()); maxX = Math.max(maxX, h.getX());
            minY = Math.min(minY, h.getY()); maxY = Math.max(maxY, h.getY());
        }
        Map<Long, Habitacion> porPos = new HashMap<>();
        for (Habitacion h : habitaciones) porPos.put(clave(h.getX(), h.getY()), h);
        UI.seccion("MAPA DEL PARAJE   @ tu  E entrada  ♦ objetivo  ^ campamento  ? sin explorar");
        for (int fy = minY; fy <= maxY; fy++) {
            StringBuilder fila = new StringBuilder("   ");
            StringBuilder bajo = new StringBuilder("   ");
            for (int fx = minX; fx <= maxX; fx++) {
                Habitacion h = porPos.get(clave(fx, fy));
                if (h == null) { fila.append("    "); bajo.append("    "); continue; }
                boolean vis = h.esConocida() || h == actual;
                fila.append("[").append(h.simbolo(actual)).append("]");
                Habitacion este = h.getConexiones().get('E');
                fila.append(vis && este != null && este.esConocida() ? UI.pintar("─", UI.TENUE) : " ");
                Habitacion sur = h.getConexiones().get('S');
                bajo.append(vis && sur != null && sur.esConocida() ? UI.pintar(" │  ", UI.TENUE) : "    ");
            }
            System.out.println(fila);
            if (fy < maxY) System.out.println(bajo);
        }
    }

    /** Ejecuta la exploración hasta regresar o perder el grupo. */
    public Resultado explorar() {
        while (true) {
            UI.limpiar();
            UI.titulo("EXPEDICION: " + gestor.getMision().getNombre().toUpperCase()
                    + "  [" + dificultad.getTitulo() + "]");
            if (region != null) UI.log(UI.pintar(region.getNombre() + " — " + region.getAmenaza(), UI.MAGENTA));
            dibujarMapa();
            System.out.println();
            for (Personaje heroe : heroes)
                System.out.println("  " + UI.barra(heroe.getNombre(), heroe.getVida(), heroe.getVidaMax(),
                        heroe.estaVivo() ? UI.VERDE : UI.ROJO) + "   "
                        + UI.barra("Cordura", heroe.getCordura(), 100, UI.MAGENTA));
            System.out.println("  Antorcha: " + luzTexto());
            UI.log(UI.pintar("Encargo: " + gestor.getMision().progreso(), UI.CIAN));
            System.out.println();
            List<Character> salidas = new ArrayList<>(actual.getConexiones().keySet());
            StringBuilder sb = new StringBuilder("  1. Avanzar (");
            for (char c : salidas) sb.append(c).append(" ");
            System.out.println(sb.append(")").toString().replace("( ", "("));
            UI.opcion(2, "Mochila y equipo", "usar objetos y revisar la formación");
            UI.opcion(3, "Detalle del encargo", "objetivo, progreso y recompensa");
            boolean puedeAcampar = actual.getTipo() == TipoHabitacion.CAMPAMENTO && !actual.estaResuelta();
            if (puedeAcampar) UI.opcion(4, "Acampar junto al fuego", "recupera al grupo y la antorcha");
            else UI.opcionDeshabilitada(4, "Acampar", "necesitas un campamento sin usar");
            boolean puedeVolver = gestor.hayMisionCompletada();
            if (puedeVolver) UI.opcion(5, "Volver a Valdesombra", "misión completada · cobrar recompensa");
            else UI.opcionDeshabilitada(5, "Volver con victoria", "el objetivo sigue pendiente");
            UI.opcion(6, "Abandonar la expedición", "sin recompensa · +15 estrés");

            int op = UI.leerOpcion(1, 6);
            switch (op) {
                case 1: {
                    Resultado r = moverse(salidas);
                    if (r != null) return r;
                    break;
                }
                case 2:
                    Personaje usuario = elegirHeroeVivo("\u00bfQuien usa un objeto?");
                    if (usuario != null) ControladorInventario.menuUsar(inventario, usuario, this);
                    UI.pausa();
                    break;
                case 3:
                    for (String linea : gestor.resumen().split("\\n")) UI.log(linea);
                    UI.pausa();
                    break;
                case 4:
                    if (puedeAcampar) acampar();
                    else { UI.aviso("Aquí no se puede acampar."); UI.pausa(); }
                    break;
                case 5:
                    if (puedeVolver) return Resultado.EXITO;
                    else { UI.aviso("Completa primero el objetivo del encargo."); UI.pausa(); }
                    break;
                case 6:
                    if (UI.confirmar("¿Abandonar? Perderas la recompensa y el animo (+15 estres)")) {
                        for (Personaje heroe : heroesVivos()) heroe.sufrirEstresAmbiental(15, region);
                        return Resultado.ABANDONO;
                    }
                    break;
            }
            if (companiaDerrotada()) return Resultado.MUERTE;
        }
    }

    private Resultado moverse(List<Character> salidas) {
        System.out.println("  ¿Hacia donde? (0 para quedarte)");
        for (int i = 0; i < salidas.size(); i++) {
            char c = salidas.get(i);
            Habitacion destino = actual.getConexiones().get(c);
            String pista = destino.estaVisitada() ? "(visitada)" : destino.esConocida() ? "(descubierta)" : "(sin explorar)";
            System.out.println("  " + (i + 1) + ". " + nombreDir(c) + " " + UI.pintar(pista, UI.TENUE));
        }
        int op = UI.leerOpcion(0, salidas.size());
        if (op == 0) return null;
        Habitacion destino = actual.getConexiones().get(salidas.get(op - 1));

        int segmentos = condiciones.numeroSegmentos();
        for (int s = 1; s <= segmentos; s++) {
            CondicionesExpedicion.Paso paso = condiciones.avanzarSegmento(heroesVivos());
            if (paso.infectado() != null)
                UI.log(UI.pintar("El barro infecta las heridas de " + paso.infectado().getNombre() + ".", UI.ROJO));
            if (paso.derrumbe())
                UI.log(UI.pintar("Un derrumbe sacude la galería.", UI.ROJO));
            System.out.println();
            UI.log(UI.pintar("Avanzas por el corredor (" + s + "/" + segmentos + ")... Luz " + getLuz() + ".", UI.TENUE));
            int r = condiciones.tirarEventoPasillo();
            if (r <= 22) {
                Combate.Resultado res = nuevoCombate(crearGrupoRegional())
                        .ejecutar(condiciones.hayEmboscada());
                if (res == Combate.Resultado.DERROTA) return Resultado.MUERTE;
                if (res == Combate.Resultado.HUIDA) return null;
            } else if (r <= 32) {
                Evento.trampa(Rng.elegir(heroesVivos()));
                if (companiaDerrotada()) return Resultado.MUERTE;
                UI.pausa();
            } else if (r <= 40) {
                List<Enemigo> mimico = Evento.cofre(protagonista, this, false);
                if (mimico != null) {
                    Combate.Resultado res = nuevoCombate(mimico).ejecutar(true);
                    if (res == Combate.Resultado.DERROTA) return Resultado.MUERTE;
                }
                UI.pausa();
            }
        }
        actual = destino;
        return entrar(destino, false);
    }
    private String nombreDir(char c) {
        return switch (c) {
            case 'N' -> "Norte";
            case 'S' -> "Sur";
            case 'E' -> "Este";
            default -> "Oeste";
        };
    }

    /** Resuelve el contenido de una habitación al entrar. */
    private Resultado entrar(Habitacion h, boolean esInicio) {
        boolean primeraVez = !h.estaVisitada();
        h.visitar();
        for (Habitacion vecina : h.getConexiones().values()) vecina.descubrir();
        int visitadas = 0;
        for (Habitacion hab : habitaciones) if (hab.estaVisitada()) visitadas++;
        gestor.notificarVisita(visitadas, habitaciones.size());
        if (h.getTipo() == TipoHabitacion.ENTRADA) gestor.notificarEntrada();
        if (esInicio) return null;

        System.out.println();
        if (primeraVez) for (Personaje heroe : heroesVivos()) h.aplicarAmbiente(heroe);
        if (companiaDerrotada()) return Resultado.MUERTE;
        if (h.estaResuelta()) return null;

        switch (h.getTipo()) {
            case COMBATE: {
                h.resolver();
            Combate.Resultado res = nuevoCombate(crearGrupoRegional())
                        .ejecutar(condiciones.hayEmboscada());
                if (res == Combate.Resultado.DERROTA) return Resultado.MUERTE;
                break;
            }
            case TESORO: {
                h.resolver();
                List<Enemigo> mimico = Evento.cofre(protagonista, this, true);
                if (mimico != null) {
                    Combate.Resultado res = nuevoCombate(mimico).ejecutar(true);
                    if (res == Combate.Resultado.DERROTA) return Resultado.MUERTE;
                }
                UI.pausa();
                break;
            }
            case CURIO: {
                h.resolver();
                if (region == Region.BOSQUE_DE_LOS_AHORCADOS) Evento.curioBosque(protagonista, this);
                else if (region == Region.BRANAS_HUNDIDAS) Evento.curioBranas(protagonista, this);
                else if (region == Region.CAMINO_DE_LOS_DIFUNTOS) Evento.curioCamino(protagonista, this);
                else if (region == Region.MINAS_DE_SAN_LOURENZO) Evento.curioMinas(protagonista, this);
                else if (region == Region.PAZO_DE_SOUTOMAIOR) Evento.curioPazo(protagonista, this);
                else Evento.curioAleatorio(protagonista, this);
                if (companiaDerrotada()) return Resultado.MUERTE;
                UI.pausa();
                break;
            }
            case OBJETIVO: {
                Resultado r = resolverObjetivo(h);
                if (r != null) return r;
                break;
            }
            default:
        }
        return null;
    }

    private Resultado resolverObjetivo(Habitacion h) {
        Mision m = gestor.getMision();
        if (m instanceof MisionJefe) {
            h.resolver();
            Jefe jefe = switch (m.getId()) {
                case ULTIMA_PROCESION -> Bestiario.crearJefeFinal(nivelZona);
                case REY_SOGAS -> Bestiario.crearReiAforcados(nivelZona);
                case SUDARIOS_ALDARA -> Bestiario.crearLavandeiraMaior(nivelZona);
                case PUERTAS_HOSPITAL -> Bestiario.crearHospitalario(nivelZona);
                case CAMPANA_CAPATAZ -> Bestiario.crearCapataz(nivelZona);
                case CRIPTA_SOUTOMAIOR -> Bestiario.crearCustodioCripta(nivelZona);
                default -> Bestiario.crearJefe(nivelZona, victoriasPrevias);
            };
            System.out.println(UI.pintar("\n  Has llegado a la guarida. Algo enorme respira en la oscuridad...", UI.MAGENTA));
            UI.pausa();
            Combate.Resultado res = nuevoCombate(List.of(jefe)).ejecutar(false);
            if (res == Combate.Resultado.DERROTA) return Resultado.MUERTE;
        } else if (m instanceof MisionReliquia) {
            if (!h.estaResuelta()) {
                h.resolver();
                System.out.println(UI.pintar("\n  En un pedestal de piedra descansa la reliquia robada.", UI.AMARILLO));
                gestor.notificarObjetivo();
                UI.pausa();
            }
        } else {
            h.resolver();
            Evento.cofre(protagonista, this, true);
            UI.pausa();
        }
        return null;
    }

    private void acampar() {
        actual.resolver();
        UI.limpiar();
        UI.seccion("CAMPAMENTO");
        UI.log("Enciendes una fogata al abrigo de las piedras. El mundo, por un rato, calla.");
        for (Personaje heroe : heroes) {
            if (!heroe.estaVivo()) heroe.setVida(heroe.getVidaMax() * 0.15);
            heroe.curar(heroe.getVidaMax() * 0.35);
            heroe.setRecurso(heroe.getRecursoMax());
            heroe.aliviarEstres(25);
            heroe.limpiarEfectosNegativos();
        }
        subirLuz(30);
        UI.log(UI.pintar("+35% vida, recurso al maximo, -25 estres, males purgados, +30 luz.", UI.VERDE));
        if (Rng.prob(20)) {
            UI.log(UI.pintar("...pero unos pasos te despiertan de madrugada.", UI.ROJO));
            UI.pausa();
            nuevoCombate(crearGrupoRegional()).ejecutar(true);
        } else {
            UI.pausa();
        }
    }

    public GestorMisiones getGestor() { return gestor; }

    private Combate nuevoCombate(List<Enemigo> enemigos) {
        if (registro != null) for (Enemigo enemigo : enemigos) registro.descubrir(enemigo.getNombre());
        return new Combate(heroes, enemigos, this, gestor, inventario, new VistaCombateConsola());
    }

    private List<Enemigo> crearGrupoRegional() {
        return region == null ? Bestiario.crearGrupo(nivelZona, dificultad)
                : Bestiario.crearGrupo(region, nivelZona, dificultad);
    }

    private Personaje elegirHeroeVivo(String titulo) {
        List<Personaje> vivos = heroesVivos();
        if (vivos.isEmpty()) return null;
        UI.seccion(titulo);
        for (int i = 0; i < vivos.size(); i++)
            UI.log((i + 1) + ". " + vivos.get(i).getNombre());
        return vivos.get(UI.leerOpcion(1, vivos.size()) - 1);
    }
}
