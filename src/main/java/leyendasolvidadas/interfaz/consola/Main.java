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

/**
 * Punto de entrada de Leyendas Olvidadas: La Compania.
 * Uso: java Main [--sin-color] [--sin-limpiar]
 */
public class Main {
    public static void main(String[] args) {
        leyendasolvidadas.dominio.eventos.BusEventos.conectar(UI::mostrarEvento);
        for (String a : args)
            if (a.equals("--sin-color")) {
                UI.color = false;
            } else if (a.equals("--sin-limpiar")) UI.limpiarPantalla = false;
        new Juego(new GuardarCargar()).iniciarJuego();
    }
}
