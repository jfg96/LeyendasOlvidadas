package leyendasolvidadas.dominio.misiones;

import leyendasolvidadas.dominio.compania.MercenarioUnico;
import leyendasolvidadas.dominio.mundo.Dificultad;

/** Encargo de reliquia asociado de forma inequívoca a un mercenario único. */
public final class MisionPersonal extends MisionReliquia {
    private final MercenarioUnico mercenario;
    private final int etapa;

    public MisionPersonal(MercenarioUnico mercenario, int etapa, String nombre, String descripcion,
                          Dificultad dificultad, int oro, int xp) {
        super(MisionId.MISION_PERSONAL, nombre, descripcion, dificultad, oro, xp, null);
        this.mercenario = mercenario; this.etapa = etapa;
    }
    public MercenarioUnico getMercenario() { return mercenario; }
    public int getEtapa() { return etapa; }
}
