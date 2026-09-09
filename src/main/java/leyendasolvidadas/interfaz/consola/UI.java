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
import leyendasolvidadas.dominio.eventos.*;

import java.util.Scanner;

/** Utilidades de presentación y entrada para la terminal. */
public final class UI {
    public static boolean color = true;
    public static boolean limpiarPantalla = true;
    public static final int ANCHO = 78;
    public static final Scanner SC = new Scanner(System.in);
    private static TerminalJuego terminal;

    private UI() {}

    static void configurarTerminal(TerminalJuego nuevaTerminal) { terminal = nuevaTerminal; }

    public static int ancho() {
        int disponible = terminal == null ? 80 : terminal.columnas();
        return Math.max(60, Math.min(100, disponible - 2));
    }

    public static String c(String codigo) { return color ? codigo : ""; }
    public static final String RESET = "\u001B[0m", NEGRITA = "\u001B[1m", TENUE = "\u001B[2m";
    public static final String ROJO = "\u001B[31m", VERDE = "\u001B[32m", AMARILLO = "\u001B[33m";
    public static final String AZUL = "\u001B[34m", MAGENTA = "\u001B[35m", CIAN = "\u001B[36m", GRIS = "\u001B[90m";

    public static String pintar(String texto, String col) { return c(col) + texto + c(RESET); }
    public static String item(Item item) {
        String colorRareza = switch (item.getRareza()) {
            case COMUN -> GRIS;
            case RARA -> CIAN;
            case EPICA -> MAGENTA;
            case LEGENDARIA -> AMARILLO;
        };
        return pintar(item.getNombre(), colorRareza);
    }

    public static void limpiar() {
        if (limpiarPantalla && terminal != null && terminal.pantallaCompleta()) terminal.limpiar();
        else if (limpiarPantalla) System.out.print("\u001B[2J\u001B[H");
        else System.out.println();
    }

    public static void titulo(String t) {
        int ancho = ancho();
        t = t.length() > ancho - 8 ? t.substring(0, ancho - 9) + "…" : t;
        String marca = " LEYENDAS OLVIDADAS ";
        int resto = Math.max(1, ancho - marca.length() - 2);
        System.out.println(pintar("┌" + marca + "─".repeat(resto) + "┐", AMARILLO));
        System.out.println(pintar("│ ", AMARILLO) + pintar(t.toUpperCase(), NEGRITA)
                + " ".repeat(Math.max(0, ancho - t.length() - 3)) + pintar("│", AMARILLO));
        System.out.println(pintar("└" + "─".repeat(ancho) + "┘", AMARILLO));
    }

    public static void seccion(String t) {
        System.out.println();
        System.out.println(pintar("  " + t.toUpperCase(), GRIS));
        System.out.println(pintar("  " + "─".repeat(Math.max(2, ancho() - 4)), GRIS));
    }

    public static void turno(String nombre, String detalle) {
        System.out.println();
        System.out.println(pintar("  ▶ AHORA ACTÚA: " + nombre.toUpperCase(), CIAN + NEGRITA));
        if (detalle != null && !detalle.isBlank()) log(pintar(detalle, TENUE));
        System.out.println();
    }

    public static void opcion(int numero, String nombre, String detalle) {
        System.out.printf("  %s %-28s %s%n", pintar("[" + numero + "]", AMARILLO), nombre,
                pintar(detalle == null ? "" : "· " + detalle, TENUE));
    }

    /** Lee un ordinal dentro de un rango y lo traduce a la constante de esa posicion. */
    public static <E extends Enum<E>> E elegirEnum(Class<E> tipo, int min, int max) {
        return tipo.getEnumConstants()[leerOpcion(min, max) - 1];
    }

    /** Pinta el mensaje de un resultado de caso de uso segun su exito. */
    public static void pintarResultado(ResultadoAccion resultado) {
        log(pintar(resultado.mensaje(), resultado.exito() ? VERDE : ROJO));
    }
    public static void opcionDeshabilitada(int numero, String nombre, String motivo) {
        System.out.printf("  %s %-28s %s%n", pintar("[" + numero + "]", GRIS), pintar(nombre, GRIS),
                pintar("· " + motivo, TENUE));
    }
    public static void aviso(String texto) { log(pintar("! " + texto, AMARILLO)); }

    /** Dibuja una barra de progreso con su valor actual. */
    public static String barra(String etiqueta, double valor, double max, String col) {
        int ancho = 14;
        int llenos = (max <= 0) ? 0 : (int) Math.round(ancho * Math.max(0, valor) / max);
        llenos = Math.min(ancho, llenos);
        return String.format("%-9s %s%s%s%s %d/%d", etiqueta,
                c(col), "█".repeat(llenos), c(GRIS), "░".repeat(ancho - llenos) + c(RESET),
                (int) valor, (int) max);
    }

    public static void log(String msg) { System.out.println("  " + msg); }

    public static void mostrarEvento(EventoDominio evento) {
        String colorEvento = switch (evento.tipo()) {
            case EXITO -> VERDE;
            case PELIGRO -> ROJO;
            case HORROR -> MAGENTA;
            case RECOMPENSA -> AMARILLO;
            case PROGRESO -> CIAN;
            default -> RESET;
        };
        log(pintar(evento.mensaje(), colorEvento));
    }

    public static void pausa() {
        pie("ENTER continuar");
        SC.nextLine();
    }

    public static void pie(String acciones) {
        System.out.println();
        System.out.println(pintar("  " + "─".repeat(Math.max(2, ancho() - 4)), GRIS));
        System.out.print(pintar("  " + acciones + "  ", TENUE));
    }

    /**
     * Lee una opción dentro del intervalo indicado.
     *
     * @param min valor mínimo aceptado
     * @param max valor máximo aceptado
     * @return la opción introducida
     */
    public static int leerOpcion(int min, int max) {
        while (true) {
            pie("Escribe una opción y pulsa ENTER");
            System.out.print(pintar("› ", AMARILLO + NEGRITA));
            String linea = SC.nextLine().trim();
            try {
                int n = Integer.parseInt(linea);
                if (n >= min && n <= max) return n;
            } catch (NumberFormatException ignorada) { }
            System.out.println(pintar("  (!) Elige un numero entre " + min + " y " + max + ".", ROJO));
        }
    }

    public static String leerTexto(String indicacion) {
        System.out.print(pintar("  " + indicacion + ": ", AMARILLO));
        String t = SC.nextLine().trim();
        return t.isEmpty() ? "Anonimo" : t;
    }

    public static boolean confirmar(String pregunta) {
        while (true) {
            System.out.print(pintar("  " + pregunta + " [s/n]: ", AMARILLO));
            String respuesta = SC.nextLine().trim().toLowerCase();
            if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) return true;
            if (respuesta.equals("n") || respuesta.equals("no")) return false;
            System.out.println(pintar("  (!) Responde s o n.", ROJO));
        }
    }
}
