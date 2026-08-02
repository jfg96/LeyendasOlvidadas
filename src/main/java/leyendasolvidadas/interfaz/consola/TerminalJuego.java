package leyendasolvidadas.interfaz.consola;

import java.io.IOException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

/**
 * Controla el ciclo de vida de la terminal sin permitir que esos detalles se
 * filtren al resto de la interfaz. En una terminal real usa el buffer alterno;
 * al ejecutarse desde un IDE o una tubería conserva una salida lineal legible.
 */
public final class TerminalJuego implements AutoCloseable {
    private final Terminal terminal;
    private final boolean pantallaCompleta;
    private boolean cerrada;

    private TerminalJuego(Terminal terminal, boolean pantallaCompleta) {
        this.terminal = terminal;
        this.pantallaCompleta = pantallaCompleta;
    }

    public static TerminalJuego abrir(boolean solicitarPantallaCompleta) {
        try {
            Terminal terminal = TerminalBuilder.builder().system(true).dumb(true).build();
            boolean disponible = solicitarPantallaCompleta && !Terminal.TYPE_DUMB.equals(terminal.getType());
            TerminalJuego sesion = new TerminalJuego(terminal, disponible);
            if (disponible) {
                sesion.capacidad(InfoCmp.Capability.enter_ca_mode);
                sesion.capacidad(InfoCmp.Capability.cursor_invisible);
                sesion.capacidad(InfoCmp.Capability.clear_screen);
            }
            UI.configurarTerminal(sesion);
            return sesion;
        } catch (IOException e) {
            TerminalJuego sesion = new TerminalJuego(null, false);
            UI.configurarTerminal(sesion);
            return sesion;
        }
    }

    public boolean pantallaCompleta() { return pantallaCompleta; }

    public int columnas() {
        return terminal == null || terminal.getWidth() <= 0 ? 80 : terminal.getWidth();
    }

    public int filas() {
        return terminal == null || terminal.getHeight() <= 0 ? 24 : terminal.getHeight();
    }

    public void limpiar() {
        if (!pantallaCompleta) return;
        capacidad(InfoCmp.Capability.clear_screen);
    }

    private void capacidad(InfoCmp.Capability capacidad) {
        if (terminal != null) {
            terminal.puts(capacidad);
            terminal.flush();
        }
    }

    @Override public void close() {
        if (cerrada) return;
        cerrada = true;
        if (pantallaCompleta) {
            capacidad(InfoCmp.Capability.cursor_visible);
            capacidad(InfoCmp.Capability.exit_ca_mode);
        }
        if (terminal != null) {
            try { terminal.close(); } catch (IOException ignorada) { }
        }
        UI.configurarTerminal(null);
    }
}
