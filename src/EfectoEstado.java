import java.io.Serializable;

/** Instancia activa de un efecto de estado sobre un personaje. */
public class EfectoEstado implements Serializable {
    private final TipoEfecto tipo;
    private int duracion;      // turnos restantes
    private final double potencia; // dano por turno en los DoT

    public EfectoEstado(TipoEfecto tipo, int duracion, double potencia) {
        this.tipo = tipo; this.duracion = duracion; this.potencia = potencia;
    }
    public TipoEfecto getTipo() { return tipo; }
    public int getDuracion() { return duracion; }
    public double getPotencia() { return potencia; }
    public void refrescar(int d) { if (d > duracion) duracion = d; }
    /** Consume un turno. Devuelve true si el efecto expira. */
    public boolean avanzarTurno() { return --duracion <= 0; }
    public String toString() { return tipo.getNombre() + "(" + duracion + ")"; }
}
