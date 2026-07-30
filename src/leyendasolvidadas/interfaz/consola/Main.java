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
 * Uso: java Main [--sin-color]
 */
public class Main {
    public static void main(String[] args) {
        leyendasolvidadas.dominio.eventos.BusEventos.conectar(UI::mostrarEvento);
        for (String a : args)
            if (a.equals("--sin-color")) {
                UI.color = false;
                break;
            }
        new Juego(new GuardarCargar()).iniciarJuego();
    }
}
