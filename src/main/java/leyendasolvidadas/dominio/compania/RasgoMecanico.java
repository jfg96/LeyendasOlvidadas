package leyendasolvidadas.dominio.compania;

/** Virtudes de carácter con efecto real sobre las reglas del juego. */
public enum RasgoMecanico {
    TEMPLE_DE_HIERRO("-15 % estrés recibido"),
    OJO_PARA_EL_PELIGRO("+5 % esquiva"),
    MANOS_FIRMES("+5 % crítico"),
    LEALTAD_OBSTINADA("Gana el doble de lealtad"),
    INSTINTO_DE_SUPERVIVENCIA("+10 % vida máxima");

    private final String efecto;
    RasgoMecanico(String efecto) { this.efecto = efecto; }
    public String getEfecto() { return efecto; }
}
