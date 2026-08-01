package leyendasolvidadas.dominio.mundo;

import leyendasolvidadas.dominio.azar.FuenteAzar;
import leyendasolvidadas.dominio.eventos.PublicadorEventos;

import java.util.*;

/** Mapa conexo y reproducible de una expedición. */
public final class MapaExpedicion {
    private final List<Habitacion> habitaciones = new ArrayList<>();
    private final Habitacion entrada;
    private final FuenteAzar azar;
    private final PublicadorEventos eventos;

    public MapaExpedicion(int numeroHabitaciones, boolean requiereObjetivo) {
        this(numeroHabitaciones, requiereObjetivo, FuenteAzar.global(), PublicadorEventos.silencioso());
    }

    public MapaExpedicion(int numeroHabitaciones, boolean requiereObjetivo, FuenteAzar azar) {
        this(numeroHabitaciones, requiereObjetivo, azar, PublicadorEventos.silencioso());
    }

    public MapaExpedicion(int numeroHabitaciones, boolean requiereObjetivo, FuenteAzar azar,
                          PublicadorEventos eventos) {
        if (numeroHabitaciones < 2)
            throw new IllegalArgumentException("Una expedición necesita al menos dos habitaciones");

        this.azar = Objects.requireNonNull(azar, "La fuente de azar es obligatoria");
        this.eventos = eventos == null ? PublicadorEventos.silencioso() : eventos;
        Map<Long, Habitacion> porPosicion = new HashMap<>();
        entrada = nuevaHabitacion(0, 0, TipoHabitacion.ENTRADA);
        habitaciones.add(entrada);
        porPosicion.put(clave(0, 0), entrada);
        generar(numeroHabitaciones, porPosicion);

        if (requiereObjetivo) {
            Habitacion lejana = masLejana();
            reemplazar(lejana, nuevaHabitacion(lejana.getX(), lejana.getY(), TipoHabitacion.OBJETIVO));
        }
    }

    public List<Habitacion> getHabitaciones() { return Collections.unmodifiableList(habitaciones); }
    public Habitacion getEntrada() { return entrada; }

    private void generar(int numeroHabitaciones, Map<Long, Habitacion> porPosicion) {
        int x = 0, y = 0;
        int[][] direcciones = {{0, -1}, {0, 1}, {1, 0}, {-1, 0}};
        char[] letras = {'N', 'S', 'E', 'O'};
        int intentos = 0;
        boolean hayCampamento = false;
        while (habitaciones.size() < numeroHabitaciones && intentos < 500) {
            intentos++;
            int direccion = azar.entre(0, 3);
            int nuevoX = x + direcciones[direccion][0];
            int nuevoY = y + direcciones[direccion][1];
            Habitacion origen = porPosicion.get(clave(x, y));
            Habitacion destino = porPosicion.get(clave(nuevoX, nuevoY));
            if (destino == null) {
                TipoHabitacion tipo = sortearTipo(!hayCampamento && habitaciones.size() >= 3);
                if (tipo == TipoHabitacion.CAMPAMENTO) hayCampamento = true;
                destino = nuevaHabitacion(nuevoX, nuevoY, tipo);
                habitaciones.add(destino);
                porPosicion.put(clave(nuevoX, nuevoY), destino);
            }
            origen.conectar(letras[direccion], destino);
            destino.conectar(letras[opuesta(direccion)], origen);
            x = nuevoX;
            y = nuevoY;
            if (azar.probabilidad(30)) {
                x = 0;
                y = 0;
            }
        }
        if (habitaciones.size() != numeroHabitaciones)
            throw new IllegalStateException("No se pudo generar un mapa con el tamaño solicitado");
    }

    private TipoHabitacion sortearTipo(boolean forzarCampamento) {
        if (forzarCampamento && azar.probabilidad(35)) return TipoHabitacion.CAMPAMENTO;
        int valor = azar.entre(1, 100);
        if (valor <= 45) return TipoHabitacion.COMBATE;
        if (valor <= 63) return TipoHabitacion.CURIO;
        if (valor <= 78) return TipoHabitacion.TESORO;
        if (valor <= 88) return TipoHabitacion.CAMPAMENTO;
        return TipoHabitacion.VACIA;
    }

    private Habitacion masLejana() {
        Map<Habitacion, Integer> distancias = new HashMap<>();
        Deque<Habitacion> pendientes = new ArrayDeque<>();
        distancias.put(entrada, 0);
        pendientes.add(entrada);
        Habitacion lejana = entrada;
        while (!pendientes.isEmpty()) {
            Habitacion habitacion = pendientes.remove();
            for (Habitacion vecina : habitacion.getConexiones().values()) {
                if (!distancias.containsKey(vecina)) {
                    distancias.put(vecina, distancias.get(habitacion) + 1);
                    if (distancias.get(vecina) > distancias.get(lejana)) lejana = vecina;
                    pendientes.add(vecina);
                }
            }
        }
        return lejana;
    }

    private void reemplazar(Habitacion anterior, Habitacion nueva) {
        for (Map.Entry<Character, Habitacion> conexion : anterior.getConexiones().entrySet()) {
            nueva.conectar(conexion.getKey(), conexion.getValue());
            Habitacion vecina = conexion.getValue();
            for (Map.Entry<Character, Habitacion> inversa : vecina.getConexiones().entrySet())
                if (inversa.getValue() == anterior) vecina.conectar(inversa.getKey(), nueva);
        }
        habitaciones.set(habitaciones.indexOf(anterior), nueva);
    }

    private static int opuesta(int direccion) {
        return direccion == 0 ? 1 : direccion == 1 ? 0 : direccion == 2 ? 3 : 2;
    }

    private static long clave(int x, int y) {
        return ((long) x << 32) ^ (y & 0xffffffffL);
    }

    private Habitacion nuevaHabitacion(int x, int y, TipoHabitacion tipo) {
        return new Habitacion(x, y, tipo, azar).configurarEventos(eventos);
    }
}
