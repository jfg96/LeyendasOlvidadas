/** Efectos de estado del combate. */
public enum TipoEfecto {
    SANGRADO("Sangrado", true), VENENO("Veneno", true), QUEMADURA("Quemadura", true),
    ATURDIDO("Aturdido", true), MARCADO("Marcado", true), DEBILITADO("Debilitado", true),
    PROTEGIDO("Protegido", false), FORTALECIDO("Fortalecido", false), SOMBRA("Sombra", false),
    REGENERACION("Regeneracion", false);

    private final String nombre;
    private final boolean negativo;
    TipoEfecto(String nombre, boolean negativo) { this.nombre = nombre; this.negativo = negativo; }
    public String getNombre() { return nombre; }
    public boolean esNegativo() { return negativo; }
}
