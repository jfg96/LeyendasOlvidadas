import java.io.*;

/** Guardado y carga de la partida mediante serializacion de Java. */
public final class GuardarCargar {
    private static final String FICHERO = "partida.sav";
    private GuardarCargar() {}

    public static boolean existePartida() { return new File(FICHERO).exists(); }

    public static void guardar(EstadoJuego estado) {
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(FICHERO)))) {
            CodecPartida.escribir(out, estado);
            UI.log(UI.pintar("Partida guardada en '" + FICHERO + "'.", UI.VERDE));
        } catch (IOException e) {
            UI.log(UI.pintar("No se pudo guardar: " + e.getMessage(), UI.ROJO));
        }
    }

    public static EstadoJuego cargar() {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(FICHERO)))) {
            return CodecPartida.leer(in);
        } catch (IOException e) {
            EstadoJuego legado = cargarLegado();
            if (legado != null) return legado;
            UI.log(UI.pintar("No se pudo cargar la partida: " + e.getMessage(), UI.ROJO));
            return null;
        }
    }

    private static EstadoJuego cargarLegado() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FICHERO))) {
            EstadoJuego estado = (EstadoJuego) in.readObject();
            estado.prepararTrasCarga();
            UI.log(UI.pintar("Partida antigua convertida al nuevo formato versionado.", UI.AMARILLO));
            return estado;
        } catch (IOException | ClassNotFoundException ignorada) {
            return null;
        }
    }
}
