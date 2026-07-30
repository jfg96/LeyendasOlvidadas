package leyendasolvidadas.dominio.mundo;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.infraestructura.*;
import leyendasolvidadas.interfaz.consola.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Una expedicion a un paraje maldito: mapa procedural de habitaciones,
 * gestion de la antorcha (luz) y bucle de exploracion.
 */
public class Expedicion {

    /** Resultado de la expedicion. */
    public enum Resultado { EXITO, ABANDONO, MUERTE }

    private final List<Personaje> heroes;
    private final Personaje protagonista;
    private final Inventario inventario;
    private final GestorMisiones gestor = new GestorMisiones();
    private final Dificultad dificultad;
    private final int nivelZona;
    private final List<Habitacion> habitaciones = new ArrayList<>();
    private Habitacion actual;
    private Habitacion entrada;
    private int luz = 100;

    public Expedicion(Compania compania, Mision mision, Dificultad dificultad) {
        this.heroes = new ArrayList<>(compania.getFormacionActiva());
        this.protagonista = compania.getProtagonista();
        this.inventario = compania.getInventario();
        this.dificultad = dificultad;
        this.nivelZona = (int) Math.round(heroes.stream().mapToInt(Personaje::getNivel)
                .average().orElse(1)) + dificultad.getNivelExtra();
        gestor.asignar(mision);
        generarMapa(7 + dificultad.ordinal() * 2);
    }

    private List<Personaje> heroesVivos() { return heroes.stream().filter(Personaje::estaVivo).toList(); }
    private boolean companiaDerrotada() { return heroesVivos().isEmpty(); }

    // ------------------------------------------------------------------- luz
    public int getLuz() { return luz; }
    public void subirLuz(int n) { luz = Math.min(100, luz + n); }
    private void bajarLuz(int n) { luz = Math.max(0, luz - n); }
    public String luzTexto() {
        String estado = luz >= 75 ? UI.pintar("Radiante", UI.AMARILLO)
                : luz >= 40 ? "Tenue"
                : luz >= 15 ? UI.pintar("Penumbra", UI.MAGENTA)
                : UI.pintar("TINIEBLAS", UI.ROJO);
        return luz + " (" + estado + ")";
    }
    /** La oscuridad multiplica el botin. */
    public double getMultBotin() { return luz >= 75 ? 1.0 : luz >= 40 ? 1.1 : luz >= 15 ? 1.3 : 1.6; }
    /** La oscuridad mejora la rareza del botin. */
    public int getBonusRareza() { return luz >= 75 ? 0 : luz >= 40 ? 4 : luz >= 15 ? 10 : 18; }
    private int probEmboscada() { return luz >= 75 ? 4 : luz >= 40 ? 10 : luz >= 15 ? 18 : 30; }
    private int estresPorPaso() { return luz >= 75 ? 0 : luz >= 40 ? 1 : luz >= 15 ? 2 : 4; }

    // ------------------------------------------------------------------ mapa
    /** Genera el mapa como un paseo aleatorio conexo sobre una cuadricula. */
    private void generarMapa(int numHabitaciones) {
        Map<Long, Habitacion> porPos = new HashMap<>();
        entrada = new Habitacion(0, 0, TipoHabitacion.ENTRADA);
        habitaciones.add(entrada);
        porPos.put(clave(0, 0), entrada);
        int x = 0, y = 0;
        int[][] dirs = {{0, -1}, {0, 1}, {1, 0}, {-1, 0}};
        char[] letras = {'N', 'S', 'E', 'O'};
        int intentos = 0;
        boolean hayCampamento = false;
        while (habitaciones.size() < numHabitaciones && intentos < 500) {
            intentos++;
            int d = Rng.entre(0, 3);
            int nx = x + dirs[d][0], ny = y + dirs[d][1];
            Habitacion origen = porPos.get(clave(x, y));
            Habitacion destino = porPos.get(clave(nx, ny));
            if (destino == null) {
                TipoHabitacion tipo = sortearTipo(!hayCampamento && habitaciones.size() >= 3);
                if (tipo == TipoHabitacion.CAMPAMENTO) hayCampamento = true;
                destino = new Habitacion(nx, ny, tipo);
                habitaciones.add(destino);
                porPos.put(clave(nx, ny), destino);
            }
            origen.conectar(letras[d], destino);
            destino.conectar(letras[d == 0 ? 1 : d == 1 ? 0 : d == 2 ? 3 : 2], origen);
            x = nx; y = ny;
            if (Rng.prob(30)) { x = 0; y = 0; } // volver a ramificar desde la entrada
        }
        // La habitacion mas lejana (BFS) pasa a ser el OBJETIVO si la mision lo requiere.
        Habitacion lejana = masLejana();
        if (gestor.getMision().requiereObjetivo()) {
            reemplazar(lejana, new Habitacion(lejana.getX(), lejana.getY(), TipoHabitacion.OBJETIVO));
        }
        actual = entrada;
        entrar(entrada, true);
    }
    private long clave(int x, int y) { return ((long) x << 32) ^ (y & 0xffffffffL); }
    private TipoHabitacion sortearTipo(boolean forzarCampamento) {
        if (forzarCampamento && Rng.prob(35)) return TipoHabitacion.CAMPAMENTO;
        int r = Rng.entre(1, 100);
        if (r <= 45) return TipoHabitacion.COMBATE;
        if (r <= 63) return TipoHabitacion.CURIO;
        if (r <= 78) return TipoHabitacion.TESORO;
        if (r <= 88) return TipoHabitacion.CAMPAMENTO;
        return TipoHabitacion.VACIA;
    }
    private Habitacion masLejana() {
        Map<Habitacion, Integer> dist = new HashMap<>();
        Deque<Habitacion> cola = new ArrayDeque<>();
        dist.put(entrada, 0); cola.add(entrada);
        Habitacion lejos = entrada;
        while (!cola.isEmpty()) {
            Habitacion h = cola.poll();
            for (Habitacion v : h.getConexiones().values()) {
                if (!dist.containsKey(v)) {
                    dist.put(v, dist.get(h) + 1);
                    if (dist.get(v) > dist.get(lejos)) lejos = v;
                    cola.add(v);
                }
            }
        }
        return lejos;
    }
    private void reemplazar(Habitacion vieja, Habitacion nueva) {
        for (Map.Entry<Character, Habitacion> en : vieja.getConexiones().entrySet()) {
            nueva.conectar(en.getKey(), en.getValue());
            Habitacion vecina = en.getValue();
            for (Map.Entry<Character, Habitacion> e2 : vecina.getConexiones().entrySet())
                if (e2.getValue() == vieja) vecina.conectar(e2.getKey(), nueva);
        }
        habitaciones.set(habitaciones.indexOf(vieja), nueva);
    }

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

    // ------------------------------------------------------------ exploracion
    /** Bucle principal de la expedicion. */
    public Resultado explorar() {
        while (true) {
            UI.limpiar();
            UI.titulo("EXPEDICION: " + gestor.getMision().getNombre().toUpperCase()
                    + "  [" + dificultad.getTitulo() + "]");
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
            System.out.println("  2. Mochila y equipo");
            System.out.println("  3. Detalle del encargo");
            boolean puedeAcampar = actual.getTipo() == TipoHabitacion.CAMPAMENTO && !actual.estaResuelta();
            if (puedeAcampar) System.out.println("  4. " + UI.pintar("Acampar junto al fuego", UI.AMARILLO));
            boolean puedeVolver = gestor.hayMisionCompletada();
            if (puedeVolver) System.out.println("  5. " + UI.pintar("Volver a la aldea (VICTORIA)", UI.VERDE));
            System.out.println("  6. Abandonar la expedicion");

            int op = UI.leerOpcion(1, 6);
            switch (op) {
                case 1: {
                    Resultado r = moverse(salidas);
                    if (r != null) return r;
                    break;
                }
                case 2:
                    Personaje usuario = elegirHeroeVivo("\u00bfQuien usa un objeto?");
                    if (usuario != null) inventario.menuUsar(usuario, this);
                    UI.pausa();
                    break;
                case 3:
                    gestor.mostrarResumen();
                    UI.pausa();
                    break;
                case 4:
                    if (puedeAcampar) acampar();
                    break;
                case 5:
                    if (puedeVolver) return Resultado.EXITO;
                    break;
                case 6:
                    if (UI.confirmar("¿Abandonar? Perderas la recompensa y el animo (+15 estres)")) {
                        for (Personaje heroe : heroesVivos()) heroe.sufrirEstres(15);
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
            String pista = destino.estaVisitada() ? "(ya explorada)" : destino.esConocida() ? "(?)" : "(?)";
            System.out.println("  " + (i + 1) + ". " + nombreDir(c) + " " + UI.pintar(pista, UI.TENUE));
        }
        int op = UI.leerOpcion(0, salidas.size());
        if (op == 0) return null;
        Habitacion destino = actual.getConexiones().get(salidas.get(op - 1));

        // ---- travesia del pasillo ----
        int segmentos = Rng.entre(2, 3);
        for (int s = 1; s <= segmentos; s++) {
            bajarLuz(5);
            int estres = estresPorPaso();
            if (estres > 0) for (Personaje heroe : heroesVivos()) heroe.sufrirEstres(estres);
            System.out.println();
            UI.log(UI.pintar("Avanzas por el corredor (" + s + "/" + segmentos + ")... Luz " + luz + ".", UI.TENUE));
            int r = Rng.entre(1, 100);
            if (r <= 22) {
                Combate.Resultado res = nuevoCombate(Bestiario.crearGrupo(nivelZona, dificultad))
                        .ejecutar(Rng.prob(probEmboscada()));
                if (res == Combate.Resultado.DERROTA) return Resultado.MUERTE;
                if (res == Combate.Resultado.HUIDA) return null; // vuelve a la sala anterior
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

    /** Resuelve la llegada a una habitacion. */
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
                Combate.Resultado res = nuevoCombate(Bestiario.crearGrupo(nivelZona, dificultad))
                        .ejecutar(Rng.prob(probEmboscada()));
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
                Evento.curioAleatorio(protagonista, this);
                if (companiaDerrotada()) return Resultado.MUERTE;
                UI.pausa();
                break;
            }
            case OBJETIVO: {
                Resultado r = resolverObjetivo(h);
                if (r != null) return r;
                break;
            }
            default: // VACIA, CAMPAMENTO, ENTRADA: nada automatico
        }
        return null;
    }

    private Resultado resolverObjetivo(Habitacion h) {
        Mision m = gestor.getMision();
        if (m instanceof MisionJefe) {
            h.resolver();
            Jefe jefe = ((MisionJefe) m).esFinal()
                    ? Bestiario.crearJefeFinal(nivelZona)
                    : Bestiario.crearJefe(nivelZona, Juego.getInstancia().getEstado().getExpedicionesGanadas());
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
            nuevoCombate(Bestiario.crearGrupo(nivelZona, dificultad)).ejecutar(true);
        } else {
            UI.pausa();
        }
    }

    public GestorMisiones getGestor() { return gestor; }

    private Combate nuevoCombate(List<Enemigo> enemigos) {
        return new Combate(heroes, enemigos, this, gestor, inventario);
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
