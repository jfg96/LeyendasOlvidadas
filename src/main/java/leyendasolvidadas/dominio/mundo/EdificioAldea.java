package leyendasolvidadas.dominio.mundo;

public enum EdificioAldea {
    ERMITA("Ermita de Santa Mariña"), HERRERIA("Herrería de Maese Roldán"),
    TABERNA("Taberna de la Última Luz"), ARCHIVO("Archivo parroquial"),
    CUARTEL("Cuartel de la compañía"), CAMPANARIO("Campanario");

    private final String nombre;
    EdificioAldea(String nombre) { this.nombre = nombre; }
    public String getNombre() { return nombre; }
}
