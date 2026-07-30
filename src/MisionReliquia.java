/** Mision de rescate: recuperar una reliquia del fondo del paraje y volver a la entrada. */
public class MisionReliquia extends Mision {
    private boolean recogida = false;
    private boolean entregada = false;

    public MisionReliquia(Dificultad dif, int oro, int xp, Item item) {
        super("La Reliquia Perdida", "Recuperar la reliquia robada y regresar a la entrada.", dif, oro, xp, item);
    }
    @Override public boolean requiereObjetivo() { return true; }
    @Override public void notificarObjetivo() {
        if (!recogida) {
            recogida = true;
            System.out.println(UI.pintar("\n  ✦ Tomas la reliquia sagrada. ¡Vuelve a la ENTRADA (E) para consagrarla! ✦", UI.AMARILLO));
        }
    }
    /** Llamado al pisar la entrada. */
    public void notificarEntrada() {
        if (recogida && !entregada) { entregada = true; completar(); }
    }
    @Override public String progreso() {
        return recogida ? "Reliquia en mano: vuelve a la entrada" : "Busca la camara marcada (♦)";
    }
}
