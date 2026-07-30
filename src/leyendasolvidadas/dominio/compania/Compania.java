package leyendasolvidadas.dominio.compania;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.infraestructura.*;
import leyendasolvidadas.interfaz.consola.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Plantilla persistente del jugador. El protagonista es su miembro fundador,
 * no puede ser despedido y debe participar en toda formacion activa.
 */
public class Compania implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int MAX_PLANTILLA = 6;
    public static final int MAX_FORMACION = 3;

    private final Personaje protagonista;
    private final List<Personaje> plantilla = new ArrayList<>();
    private final List<Personaje> formacionActiva = new ArrayList<>();
    private Inventario inventarioCompartido;

    public Compania(Personaje protagonista) {
        if (protagonista == null) throw new IllegalArgumentException("El protagonista es obligatorio");
        this.protagonista = protagonista;
        this.inventarioCompartido = protagonista.getInventario();
        plantilla.add(protagonista);
        formacionActiva.add(protagonista);
    }

    public Personaje getProtagonista() { return protagonista; }
    public List<Personaje> getPlantilla() { return Collections.unmodifiableList(plantilla); }
    public List<Personaje> getFormacionActiva() { return Collections.unmodifiableList(formacionActiva); }
    public Inventario getInventario() {
        if (inventarioCompartido == null) inventarioCompartido = protagonista.getInventario();
        return inventarioCompartido;
    }
    public boolean esProtagonista(Personaje personaje) { return protagonista == personaje; }
    public boolean estaCompleta() { return formacionActiva.size() == MAX_FORMACION; }
    public boolean plantillaLlena() { return plantilla.size() == MAX_PLANTILLA; }

    /** Incorpora un miembro a la reserva. No modifica la formacion activa. */
    public boolean contratar(Personaje personaje) {
        if (personaje == null || plantillaLlena() || plantilla.contains(personaje)) return false;
        plantilla.add(personaje);
        return true;
    }

    /** Despide a un miembro que no sea el protagonista. */
    public boolean despedir(Personaje personaje) {
        if (personaje == null || esProtagonista(personaje) || !plantilla.remove(personaje)) return false;
        formacionActiva.remove(personaje);
        return true;
    }

    /**
     * Sustituye la formacion que saldra de expedicion. Debe incluir al
     * protagonista, contener entre uno y tres miembros y no repetirlos.
     */
    public void prepararFormacion(List<Personaje> miembros) {
        if (miembros == null || miembros.isEmpty() || miembros.size() > MAX_FORMACION)
            throw new IllegalArgumentException("La formacion debe tener entre 1 y 3 miembros");
        if (!miembros.contains(protagonista))
            throw new IllegalArgumentException("El protagonista debe formar parte del grupo activo");
        if (miembros.stream().distinct().count() != miembros.size())
            throw new IllegalArgumentException("No puede haber miembros repetidos");
        if (!plantilla.containsAll(miembros))
            throw new IllegalArgumentException("Todos los miembros deben pertenecer a la compania");

        formacionActiva.clear();
        formacionActiva.addAll(miembros);
    }

    public int nivelMedio() {
        return (int) Math.round(plantilla.stream().mapToInt(Personaje::getNivel).average().orElse(1));
    }
}
