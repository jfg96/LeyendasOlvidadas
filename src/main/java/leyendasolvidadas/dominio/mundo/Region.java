package leyendasolvidadas.dominio.mundo;

import leyendasolvidadas.dominio.campana.CapituloCampana;

/** Regiones estables de campaña sobre las que se construirá el contenido. */
public enum Region {
    BOSQUE_DE_LOS_AHORCADOS("Bosque de los Ahorcados", CapituloCampana.CAMPANAS_DE_VALDESOMBRA,
            "Poca visibilidad, caminos cambiantes y emboscadas."),
    BRANAS_HUNDIDAS("Brañas Hundidas", CapituloCampana.CAMINOS_DE_ANIMAS,
            "Enfermedad, barro y apariciones reflejadas en el agua."),
    CAMINO_DE_LOS_DIFUNTOS("Camino de los Difuntos", CapituloCampana.CAMINOS_DE_ANIMAS,
            "Estrés, campanas y muertos que han perdido su nombre."),
    MINAS_DE_SAN_LOURENZO("Minas de San Lourenzo", CapituloCampana.DEUDA_DE_LOS_VIVOS,
            "Oscuridad absoluta, derrumbes y materiales valiosos."),
    PAZO_DE_SOUTOMAIOR("Pazo de Soutomaior", CapituloCampana.DEUDA_DE_LOS_VIVOS,
            "Intriga, documentos, guardias y secretos familiares."),
    HOSPITAL_DEL_CAMINO_VIEJO("Hospital del Camino Viejo", CapituloCampana.ULTIMA_PROCESION,
            "El incendio se repite entre recuerdos del pasado y el presente.");

    private final String nombre;
    private final CapituloCampana capituloDesbloqueo;
    private final String amenaza;

    Region(String nombre, CapituloCampana capituloDesbloqueo, String amenaza) {
        this.nombre = nombre;
        this.capituloDesbloqueo = capituloDesbloqueo;
        this.amenaza = amenaza;
    }

    public String getNombre() { return nombre; }
    public CapituloCampana getCapituloDesbloqueo() { return capituloDesbloqueo; }
    public String getAmenaza() { return amenaza; }
}
