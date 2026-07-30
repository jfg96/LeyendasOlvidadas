package leyendasolvidadas.dominio.compania;

/** Debilidades de carácter que introducen costes y riesgos concretos. */
public enum DefectoMecanico {
    MIEDO_AL_AGUA("+25 % estrés en las Brañas"),
    AVERSION_A_LAS_CAMPANAS("+25 % estrés en el Camino y el Hospital"),
    CODICIA("Contratar cuesta un 20 % más"),
    SUENO_INTRANQUILO("La recuperación de vida es un 20 % menor"),
    DESCONFIANZA("Las relaciones positivas crecen más despacio");

    private final String efecto;
    DefectoMecanico(String efecto) { this.efecto = efecto; }
    public String getEfecto() { return efecto; }
}
