package leyendasolvidadas.dominio.campana;

import leyendasolvidadas.dominio.mundo.Region;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

/** Progreso narrativo persistente, independiente de cualquier interfaz. */
public final class ProgresoCampana {
    private static final int MAX_DECISIONES = 512;
    private static final int MAX_LONGITUD_ID = 80;

    private CapituloCampana capitulo;
    private final Set<String> decisiones;
    private final EnumSet<Region> regionesDesbloqueadas;

    public ProgresoCampana() {
        this(CapituloCampana.PROLOGO, Set.of(), Set.of());
    }

    private ProgresoCampana(CapituloCampana capitulo, Set<String> decisiones, Set<Region> regiones) {
        this.capitulo = capitulo;
        this.decisiones = new LinkedHashSet<>(decisiones);
        this.regionesDesbloqueadas = regiones.isEmpty()
                ? EnumSet.noneOf(Region.class) : EnumSet.copyOf(regiones);
        desbloquearRegionesDelCapitulo();
    }

    public CapituloCampana getCapitulo() { return capitulo; }
    public Set<String> getDecisiones() { return Collections.unmodifiableSet(decisiones); }
    public Set<Region> getRegionesDesbloqueadas() {
        return Collections.unmodifiableSet(regionesDesbloqueadas);
    }

    public boolean haDecidido(String id) {
        return decisiones.contains(validarId(id));
    }

    public boolean registrarDecision(String id) {
        String valido = validarId(id);
        if (!decisiones.contains(valido) && decisiones.size() >= MAX_DECISIONES)
            throw new IllegalStateException("Se ha alcanzado el limite de decisiones narrativas");
        return decisiones.add(valido);
    }

    public boolean estaDesbloqueada(Region region) {
        return regionesDesbloqueadas.contains(region);
    }

    public void avanzarA(CapituloCampana destino) {
        if (!capitulo.puedeAvanzarA(destino))
            throw new IllegalArgumentException("Transicion de capitulo no valida: " + capitulo + " -> " + destino);
        capitulo = destino;
        desbloquearRegionesDelCapitulo();
    }

    public static ProgresoCampana restaurar(CapituloCampana capitulo, Set<String> decisiones,
                                             Set<Region> regiones) {
        if (capitulo == null) throw new IllegalArgumentException("El capitulo es obligatorio");
        if (decisiones.size() > MAX_DECISIONES) throw new IllegalArgumentException("Demasiadas decisiones");
        for (String id : decisiones) validarId(id);
        return new ProgresoCampana(capitulo, decisiones, regiones);
    }

    private void desbloquearRegionesDelCapitulo() {
        for (Region region : Region.values())
            if (region.getCapituloDesbloqueo().ordinal() <= capitulo.ordinal())
                regionesDesbloqueadas.add(region);
    }

    private static String validarId(String id) {
        if (id == null || id.isBlank() || id.length() > MAX_LONGITUD_ID
                || !id.matches("[a-z0-9]+(?:[._-][a-z0-9]+)*"))
            throw new IllegalArgumentException("Identificador narrativo no valido: " + id);
        return id;
    }
}
