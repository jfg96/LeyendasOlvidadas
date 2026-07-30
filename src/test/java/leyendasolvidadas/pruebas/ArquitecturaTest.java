package leyendasolvidadas.pruebas;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Impide reintroducir dependencias de consola dentro de las capas internas. */
public class ArquitecturaTest {
    private static final List<String> PROHIBIDOS = List.of(
            "leyendasolvidadas.interfaz.consola", "UI.", "System.out", "System.in", "Scanner");

    @Test
    void capasInternasNoDependenDeLaConsola() throws IOException {
        comprobarCapa(Path.of("src/main/java/leyendasolvidadas/dominio"));
        comprobarCapa(Path.of("src/main/java/leyendasolvidadas/aplicacion"));
        comprobarCapa(Path.of("src/main/java/leyendasolvidadas/infraestructura"));
    }

    private static void comprobarCapa(Path raiz) throws IOException {
        try (var archivos = Files.walk(raiz)) {
            for (Path archivo : archivos.filter(p -> p.toString().endsWith(".java")).toList()) {
                String codigo = Files.readString(archivo);
                for (String prohibido : PROHIBIDOS)
                    if (codigo.contains(prohibido))
                        throw new AssertionError(archivo + " depende de presentacion mediante '" + prohibido + "'");
            }
        }
    }
}
