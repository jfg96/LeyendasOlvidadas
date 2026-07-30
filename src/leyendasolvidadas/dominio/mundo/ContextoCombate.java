package leyendasolvidadas.dominio.mundo;

/** Datos de expedicion que afectan a un combate y sus recompensas. */
public interface ContextoCombate extends FuenteLuz {
    int getLuz();
    double getMultBotin();
    int getBonusRareza();
}
