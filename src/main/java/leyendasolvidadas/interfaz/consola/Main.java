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
        var eventos = new leyendasolvidadas.dominio.eventos.BusEventos(UI::mostrarEvento);
        var azar = new leyendasolvidadas.dominio.azar.AzarJava();
        for (String a : args)
            if (a.equals("--sin-color")) {
                UI.color = false;
            } else if (a.equals("--sin-limpiar")) UI.limpiarPantalla = false;
        new Juego(new GuardarCargar(eventos), eventos, azar).iniciarJuego();
    }
}
