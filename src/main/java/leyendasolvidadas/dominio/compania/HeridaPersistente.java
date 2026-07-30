package leyendasolvidadas.dominio.compania;

/** Secuelas sufridas al caer en una expedición y tratables en la ermita. */
public enum HeridaPersistente {
    CICATRIZ_PROFUNDA("-10 % vida máxima"), MANO_LESIONADA("-10 % daño"),
    RODILLA_DANADA("-2 velocidad"), PULMON_QUEMADO("+15 % estrés recibido");

    private final String efecto;
    HeridaPersistente(String efecto) { this.efecto = efecto; }
    public String getEfecto() { return efecto; }
}
