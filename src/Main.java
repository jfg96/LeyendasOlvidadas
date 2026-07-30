/**
 * Punto de entrada de Leyendas Olvidadas: La Compania.
 * Uso: java Main [--sin-color]
 */
public class Main {
    public static void main(String[] args) {
        for (String a : args)
            if (a.equals("--sin-color")) {
                UI.color = false;
                break;
            }
        Juego.getInstancia().iniciarJuego();
    }
}
