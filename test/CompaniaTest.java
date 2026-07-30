import java.util.List;

/** Pruebas sin dependencias de las invariantes del modelo de compania. */
public class CompaniaTest {
    public static void main(String[] args) {
        Personaje protagonista = new Animero("Fundador");
        Personaje meiga = new Meiga("Iria");
        Personaje fraile = new Fraile("Bieito");
        Compania compania = new Compania(protagonista);

        comprobar(compania.getProtagonista() == protagonista, "Debe conservar al protagonista");
        comprobar(compania.getInventario() == protagonista.getInventario(),
                "El inventario fundador debe convertirse en el inventario compartido");
        comprobar(!compania.despedir(protagonista), "No debe permitir despedir al protagonista");
        comprobar(compania.contratar(meiga), "Debe permitir contratar un miembro");
        comprobar(compania.contratar(fraile), "Debe permitir contratar un segundo miembro");
        comprobar(!compania.contratar(meiga), "No debe duplicar miembros");

        compania.prepararFormacion(List.of(protagonista, meiga, fraile));
        comprobar(compania.estaCompleta(), "La formacion de tres debe estar completa");
        esperarError(() -> compania.prepararFormacion(List.of(meiga, fraile)),
                "Debe exigir al protagonista en la formacion");
        esperarError(() -> compania.prepararFormacion(List.of(protagonista, meiga, meiga)),
                "Debe rechazar miembros repetidos");

        comprobar(compania.despedir(meiga), "Debe permitir despedir a un acompanante");
        comprobar(!compania.getFormacionActiva().contains(meiga),
                "Un despedido no puede seguir en la formacion");
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }

    private static void esperarError(Runnable accion, String mensaje) {
        try {
            accion.run();
            throw new AssertionError(mensaje);
        } catch (IllegalArgumentException esperada) {
            // Resultado esperado.
        }
    }
}
