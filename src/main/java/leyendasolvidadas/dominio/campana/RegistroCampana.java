package leyendasolvidadas.dominio.campana;

import java.util.*;

/** Memoria persistente y acotada de una partida: crónica y criaturas vistas. */
public final class RegistroCampana {
    private static final int MAX_ENTRADAS = 200;
    private final List<String> diario = new ArrayList<>();
    private final Set<String> criaturas = new LinkedHashSet<>();

    public List<String> getDiario() { return List.copyOf(diario); }
    public Set<String> getCriaturas() { return Collections.unmodifiableSet(criaturas); }
    public void anotar(String entrada) {
        if (entrada == null || entrada.isBlank()) return;
        if (diario.size() == MAX_ENTRADAS) diario.remove(0);
        diario.add(entrada.length() > 240 ? entrada.substring(0, 240) : entrada);
    }
    public void descubrir(String criatura) {
        if (criatura != null && !criatura.isBlank() && criatura.length() <= 120) criaturas.add(criatura);
    }
    public static RegistroCampana restaurar(List<String> diario, Set<String> criaturas) {
        RegistroCampana r = new RegistroCampana();
        diario.stream().skip(Math.max(0, diario.size() - MAX_ENTRADAS)).forEach(r::anotar);
        criaturas.forEach(r::descubrir); return r;
    }
}
