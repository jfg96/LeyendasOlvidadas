package leyendasolvidadas.infraestructura;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.dominio.eventos.*;

import java.io.*;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Repositorio de partidas en archivo mediante el formato versionado LOSV. */
public final class GuardarCargar implements RepositorioPartidas {
    private static final Path FICHERO_PREDETERMINADO = Path.of("partida.sav");
    private final Path fichero;
    private final Path temporal;
    private final Path respaldo;

    public GuardarCargar() { this(FICHERO_PREDETERMINADO); }

    public GuardarCargar(Path fichero) {
        if (fichero == null) throw new IllegalArgumentException("La ruta de guardado es obligatoria");
        this.fichero = fichero.toAbsolutePath().normalize();
        this.temporal = rutaAuxiliar(this.fichero, ".tmp");
        this.respaldo = rutaAuxiliar(this.fichero, ".bak");
    }

    @Override public boolean existePartida() {
        return Files.isRegularFile(fichero) || Files.isRegularFile(respaldo);
    }

    @Override public boolean guardar(EstadoJuego estado) {
        try {
            Path directorio = fichero.getParent();
            if (directorio != null) Files.createDirectories(directorio);
            escribirTemporal(estado);
            if (Files.isRegularFile(fichero))
                Files.copy(fichero, respaldo, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.COPY_ATTRIBUTES);
            reemplazarPrincipal();
            BusEventos.publicar("Partida guardada en '" + fichero.getFileName() + "'.", TipoMensaje.EXITO);
            return true;
        } catch (IOException e) {
            eliminarTemporal();
            BusEventos.publicar("No se pudo guardar: " + e.getMessage(), TipoMensaje.PELIGRO);
            return false;
        }
    }

    @Override public EstadoJuego cargar() {
        IOException falloPrincipal = null;
        if (Files.isRegularFile(fichero)) {
            try {
                return leer(fichero);
            } catch (IOException e) {
                falloPrincipal = e;
            }
        }
        if (Files.isRegularFile(respaldo)) {
            try {
                EstadoJuego recuperado = leer(respaldo);
                BusEventos.publicar("La partida principal estaba dañada; se cargó la copia de seguridad.",
                        TipoMensaje.PROGRESO);
                return recuperado;
            } catch (IOException e) {
                if (falloPrincipal != null) e.addSuppressed(falloPrincipal);
                falloPrincipal = e;
            }
        }
        String detalle = falloPrincipal == null ? "no existe ningún archivo de guardado" : falloPrincipal.getMessage();
        BusEventos.publicar("No se pudo cargar la partida LOSV: " + detalle, TipoMensaje.PELIGRO);
        return null;
    }

    private void escribirTemporal(EstadoJuego estado) throws IOException {
        try (FileOutputStream archivo = new FileOutputStream(temporal.toFile());
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(archivo))) {
            CodecPartida.escribir(out, estado);
            out.flush();
            archivo.getFD().sync();
        }
    }

    private void reemplazarPrincipal() throws IOException {
        try {
            Files.move(temporal, fichero, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporal, fichero, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static EstadoJuego leer(Path ruta) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(ruta)))) {
            return CodecPartida.leer(in);
        }
    }

    private static Path rutaAuxiliar(Path principal, String sufijo) {
        return principal.resolveSibling(principal.getFileName() + sufijo);
    }

    private void eliminarTemporal() {
        try {
            Files.deleteIfExists(temporal);
        } catch (IOException ignorada) {
            // El error original de guardado es el que debe comunicarse al jugador.
        }
    }
}
