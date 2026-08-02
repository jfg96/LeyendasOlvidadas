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

/** Punto de entrada de la aplicación. */
public class Main {
    public static void main(String[] args) {
        for (String argumento : args) if (argumento.equals("--ayuda") || argumento.equals("-h")) {
            mostrarAyuda();
            return;
        }
        boolean pantallaCompleta = true;
        for (String a : args)
            if (a.equals("--sin-color")) {
                UI.color = false;
            } else if (a.equals("--sin-limpiar") || a.equals("--modo-lineal")) {
                UI.limpiarPantalla = false;
                pantallaCompleta = false;
            }
        try (TerminalJuego terminal = TerminalJuego.abrir(pantallaCompleta)) {
            if (!terminal.dimensionesAdecuadas()) {
                UI.limpiar();
                UI.titulo("TERMINAL DEMASIADO PEQUEÑA");
                UI.aviso("Se necesitan al menos 80 columnas y 24 filas.");
                UI.log("Tamaño detectado: " + terminal.columnas() + " × " + terminal.filas() + ".");
                UI.pausa();
                return;
            }
            var eventos = new leyendasolvidadas.dominio.eventos.BusEventos(UI::mostrarEvento);
            var azar = new leyendasolvidadas.dominio.azar.AzarJava();
            new Juego(new GuardarCargar(eventos), eventos, azar).iniciarJuego();
        }
    }

    private static void mostrarAyuda() {
        System.out.println("Leyendas Olvidadas: La Compañía");
        System.out.println("Uso: java -jar leyendas-olvidadas.jar [opciones]");
        System.out.println("  --sin-color     Desactiva exclusivamente el color");
        System.out.println("  --modo-lineal   No usa la pantalla completa y conserva el historial");
        System.out.println("  --sin-limpiar   Alias compatible de --modo-lineal");
        System.out.println("  --ayuda, -h     Muestra esta ayuda");
    }
}
