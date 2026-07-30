import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** Prueba de integracion minima del combate y sus recompensas compartidas. */
public class CombateCompaniaTest {
    public static void main(String[] args) {
        String entradas = "\n" + "1\n\n".repeat(12);
        System.setIn(new ByteArrayInputStream(entradas.getBytes(StandardCharsets.UTF_8)));
        UI.color = false;

        Personaje protagonista = FabricaHeroes.crear(1, "Aldan");
        Personaje meiga = FabricaHeroes.crear(4, "Iria");
        Personaje bandolero = FabricaHeroes.crear(3, "Lope");
        Compania compania = new Compania(protagonista);
        compania.contratar(meiga);
        compania.contratar(bandolero);
        compania.prepararFormacion(List.of(protagonista, meiga, bandolero));

        Enemigo enemigo = new Enemigo("Sombra de prueba", 1, false);
        enemigo.anadirMovimiento(MovimientoEnemigo.golpe("Roce", 0.1, 1));
        enemigo.setVida(1);
        Combate combate = new Combate(compania.getFormacionActiva(), List.of(enemigo),
                null, null, compania.getInventario());

        comprobar(combate.ejecutar(false) == Combate.Resultado.VICTORIA,
                "La compania debe poder ganar el encuentro");
        for (Personaje heroe : compania.getFormacionActiva())
            comprobar(heroe.getExperiencia() > 0, "Todos los miembros deben recibir experiencia");
        comprobar(compania.getInventario().getOro() > 0, "El oro debe ingresar en la tesoreria");
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }
}
