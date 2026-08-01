package leyendasolvidadas.pruebas;

import leyendasolvidadas.dominio.azar.Rng;
import leyendasolvidadas.dominio.mundo.Habitacion;
import leyendasolvidadas.dominio.mundo.MapaExpedicion;
import leyendasolvidadas.dominio.mundo.TipoHabitacion;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/** Verifica las invariantes de los mapas procedurales de expedición. */
class MapaExpedicionTest {
    @Test
    void generaElTamanoSolicitadoConCoordenadasUnicas() {
        for (int semilla = 0; semilla < 50; semilla++) {
            Rng.semilla(semilla);
            int tamano = 7 + semilla % 3 * 2;
            MapaExpedicion mapa = new MapaExpedicion(tamano, false);

            assertEquals(tamano, mapa.getHabitaciones().size());
            assertEquals(tamano, mapa.getHabitaciones().stream()
                    .map(h -> h.getX() + ":" + h.getY()).distinct().count());
            assertEquals(TipoHabitacion.ENTRADA, mapa.getEntrada().getTipo());
            assertEquals(0, mapa.getEntrada().getX());
            assertEquals(0, mapa.getEntrada().getY());
        }
    }

    @Test
    void todasLasSalasSonAccesiblesYLasConexionesSonReciprocas() {
        Rng.semilla(731);
        MapaExpedicion mapa = new MapaExpedicion(11, false);
        Set<Habitacion> accesibles = Collections.newSetFromMap(new IdentityHashMap<>());
        Deque<Habitacion> pendientes = new ArrayDeque<>();
        accesibles.add(mapa.getEntrada());
        pendientes.add(mapa.getEntrada());

        while (!pendientes.isEmpty()) {
            Habitacion actual = pendientes.remove();
            for (Map.Entry<Character, Habitacion> conexion : actual.getConexiones().entrySet()) {
                Habitacion vecina = conexion.getValue();
                assertSame(actual, vecina.getConexiones().get(opuesta(conexion.getKey())));
                if (accesibles.add(vecina)) pendientes.add(vecina);
            }
        }

        assertEquals(mapa.getHabitaciones().size(), accesibles.size());
    }

    @Test
    void situaUnUnicoObjetivoALaMaximaDistancia() {
        for (int semilla = 0; semilla < 30; semilla++) {
            Rng.semilla(semilla);
            MapaExpedicion mapa = new MapaExpedicion(9, true);
            List<Habitacion> objetivos = mapa.getHabitaciones().stream()
                    .filter(h -> h.getTipo() == TipoHabitacion.OBJETIVO).toList();
            Map<Habitacion, Integer> distancias = distanciasDesde(mapa.getEntrada());

            assertEquals(1, objetivos.size());
            assertNotSame(mapa.getEntrada(), objetivos.get(0));
            assertEquals(Collections.max(distancias.values()), distancias.get(objetivos.get(0)));
        }
    }

    private static Map<Habitacion, Integer> distanciasDesde(Habitacion entrada) {
        Map<Habitacion, Integer> distancias = new IdentityHashMap<>();
        Deque<Habitacion> pendientes = new ArrayDeque<>();
        distancias.put(entrada, 0);
        pendientes.add(entrada);
        while (!pendientes.isEmpty()) {
            Habitacion actual = pendientes.remove();
            for (Habitacion vecina : actual.getConexiones().values()) {
                if (!distancias.containsKey(vecina)) {
                    distancias.put(vecina, distancias.get(actual) + 1);
                    pendientes.add(vecina);
                }
            }
        }
        return distancias;
    }

    private static char opuesta(char direccion) {
        return switch (direccion) {
            case 'N' -> 'S';
            case 'S' -> 'N';
            case 'E' -> 'O';
            case 'O' -> 'E';
            default -> throw new IllegalArgumentException("Dirección desconocida: " + direccion);
        };
    }
}
