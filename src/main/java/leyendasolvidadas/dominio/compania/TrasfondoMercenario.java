package leyendasolvidadas.dominio.compania;

/** Identidad narrativa estable de un candidato o miembro de la compañía. */
public record TrasfondoMercenario(String origen, String descripcion, String rasgo,
                                  String defecto, String motivacion, String frase) {
    public TrasfondoMercenario {
        origen = validar(origen, "origen");
        descripcion = validar(descripcion, "descripcion");
        rasgo = validar(rasgo, "rasgo");
        defecto = validar(defecto, "defecto");
        motivacion = validar(motivacion, "motivacion");
        frase = validar(frase, "frase");
    }

    public static TrasfondoMercenario legado() {
        return new TrasfondoMercenario("Los caminos de Valdesombra",
                "Llegó antes de que comenzaran a escribirse las crónicas de la compañía.",
                "Superviviente", "Pasado silenciado", "Salir con vida de la comarca",
                "Hay historias que pesan menos si nadie las cuenta.");
    }

    private static String validar(String valor, String campo) {
        if (valor == null || valor.isBlank() || valor.length() > 240)
            throw new IllegalArgumentException("Trasfondo sin " + campo + " valido");
        return valor;
    }
}
