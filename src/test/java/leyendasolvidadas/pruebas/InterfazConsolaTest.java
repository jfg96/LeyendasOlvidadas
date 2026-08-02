package leyendasolvidadas.pruebas;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import leyendasolvidadas.interfaz.consola.UI;
import org.junit.jupiter.api.Test;

class InterfazConsolaTest {
    @Test void desactivarColorNoDesactivaLaLimpieza() {
        PrintStream salidaOriginal = System.out;
        boolean colorOriginal = UI.color;
        boolean limpiezaOriginal = UI.limpiarPantalla;
        ByteArrayOutputStream captura = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(captura, true, StandardCharsets.UTF_8));
            UI.color = false;
            UI.limpiarPantalla = true;
            UI.limpiar();
            assertEquals("\u001B[2J\u001B[H", captura.toString(StandardCharsets.UTF_8));
        } finally {
            System.setOut(salidaOriginal);
            UI.color = colorOriginal;
            UI.limpiarPantalla = limpiezaOriginal;
        }
    }

    @Test void modoLinealNoEmiteSecuenciasDeControl() {
        PrintStream salidaOriginal = System.out;
        boolean limpiezaOriginal = UI.limpiarPantalla;
        ByteArrayOutputStream captura = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(captura, true, StandardCharsets.UTF_8));
            UI.limpiarPantalla = false;
            UI.limpiar();
            assertFalse(captura.toString(StandardCharsets.UTF_8).contains("\u001B["));
        } finally {
            System.setOut(salidaOriginal);
            UI.limpiarPantalla = limpiezaOriginal;
        }
    }
}
