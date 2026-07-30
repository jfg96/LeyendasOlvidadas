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

/** Repositorio de partidas en archivo mediante el formato versionado LOSV. */
public final class GuardarCargar implements RepositorioPartidas {
    private static final String FICHERO = "partida.sav";
    public GuardarCargar() {}

    @Override public boolean existePartida() { return new File(FICHERO).exists(); }

    @Override public boolean guardar(EstadoJuego estado) {
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(FICHERO)))) {
            CodecPartida.escribir(out, estado);
            BusEventos.publicar("Partida guardada en '" + FICHERO + "'.", TipoMensaje.EXITO);
            return true;
        } catch (IOException e) {
            BusEventos.publicar("No se pudo guardar: " + e.getMessage(), TipoMensaje.PELIGRO);
            return false;
        }
    }

    @Override public EstadoJuego cargar() {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(FICHERO)))) {
            return CodecPartida.leer(in);
        } catch (IOException e) {
            BusEventos.publicar("No se pudo cargar la partida LOSV: " + e.getMessage(), TipoMensaje.PELIGRO);
            return null;
        }
    }
}
