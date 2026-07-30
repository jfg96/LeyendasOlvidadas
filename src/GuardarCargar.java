import java.io.*;

/** Guardado y carga de la partida mediante serializacion de Java. */
public final class GuardarCargar {
    private static final String FICHERO = "partida.sav";
    private GuardarCargar() {}

    public static boolean existePartida() { return new File(FICHERO).exists(); }

    public static void guardar(EstadoJuego estado) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FICHERO))) {
            out.writeObject(estado);
            UI.log(UI.pintar("Partida guardada en '" + FICHERO + "'.", UI.VERDE));
        } catch (IOException e) {
            UI.log(UI.pintar("No se pudo guardar: " + e.getMessage(), UI.ROJO));
        }
    }

    public static EstadoJuego cargar() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FICHERO))) {
            EstadoJuego estado = (EstadoJuego) in.readObject();
            estado.prepararTrasCarga();
            return estado;
        } catch (IOException | ClassNotFoundException e) {
            UI.log(UI.pintar("No se pudo cargar la partida: " + e.getMessage(), UI.ROJO));
            return null;
        }
    }
}
