package leyendasolvidadas.dominio.mundo;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/** Estado persistente y mutable de los servicios de Valdesombra. */
public final class EstadoAldea {
    private final EnumMap<EdificioAldea, Integer> niveles = new EnumMap<>(EdificioAldea.class);
    private final EnumMap<EdificioAldea, Boolean> danados = new EnumMap<>(EdificioAldea.class);

    public EstadoAldea() {
        for (EdificioAldea edificio : EdificioAldea.values()) {
            niveles.put(edificio, edificio == EdificioAldea.CAMPANARIO ? 0 : 1);
            danados.put(edificio, false);
        }
    }
    public int nivel(EdificioAldea edificio) { return niveles.get(edificio); }
    public boolean estaDanado(EdificioAldea edificio) { return danados.get(edificio); }
    public void danar(EdificioAldea edificio) { danados.put(edificio, true); }
    public void reparar(EdificioAldea edificio) { danados.put(edificio, false); }
    public void mejorar(EdificioAldea edificio) { niveles.put(edificio, Math.min(3, nivel(edificio) + 1)); }
    public Map<EdificioAldea, Integer> getNiveles() { return Collections.unmodifiableMap(niveles); }
    public void restaurar(EdificioAldea edificio, int nivel, boolean danado) {
        niveles.put(edificio, Math.max(0, Math.min(3, nivel))); danados.put(edificio, danado);
    }
}
