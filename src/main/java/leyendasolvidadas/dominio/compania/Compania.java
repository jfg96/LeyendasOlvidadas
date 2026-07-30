package leyendasolvidadas.dominio.compania;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.objetos.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Plantilla persistente del jugador. El protagonista es su miembro fundador,
 * no puede ser despedido y debe participar en toda formacion activa.
 */
public class Compania {

    public static final int MAX_PLANTILLA = 6;
    public static final int MAX_FORMACION = 3;

    private final Personaje protagonista;
    private final List<Personaje> plantilla = new ArrayList<>();
    private final List<Personaje> formacionActiva = new ArrayList<>();
    private Inventario inventarioCompartido;
    private final Map<Personaje, Map<Personaje, Integer>> relaciones = new IdentityHashMap<>();

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

    public int afinidad(Personaje a, Personaje b) {
        if (a == b) return 100;
        return relaciones.getOrDefault(a, Map.of()).getOrDefault(b, 0);
    }

    public void modificarAfinidad(Personaje a, Personaje b, int cambio) {
        if (a == null || b == null || a == b || !plantilla.contains(a) || !plantilla.contains(b)) return;
        if (cambio > 0 && (a.getDefectoMecanico() == DefectoMecanico.DESCONFIANZA
                || b.getDefectoMecanico() == DefectoMecanico.DESCONFIANZA)) cambio = Math.max(1, cambio / 2);
        int valor = Math.max(-100, Math.min(100, afinidad(a, b) + cambio));
        relaciones.computeIfAbsent(a, x -> new IdentityHashMap<>()).put(b, valor);
        relaciones.computeIfAbsent(b, x -> new IdentityHashMap<>()).put(a, valor);
    }

    public void restaurarAfinidad(Personaje a, Personaje b, int valor) {
        if (a == null || b == null || a == b) return;
        relaciones.computeIfAbsent(a, x -> new IdentityHashMap<>()).put(b, Math.max(-100, Math.min(100, valor)));
        relaciones.computeIfAbsent(b, x -> new IdentityHashMap<>()).put(a, Math.max(-100, Math.min(100, valor)));
    }
}
