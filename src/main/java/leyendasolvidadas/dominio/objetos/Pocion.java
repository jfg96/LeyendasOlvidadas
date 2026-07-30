package leyendasolvidadas.dominio.objetos;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.dominio.eventos.*;

/** Consumibles: pociones, antorchas, laudano... */
public class Pocion extends Item {
    private final TipoPocion tipo;
    private final double potencia;

    public Pocion(String nombre, TipoPocion tipo, double potencia) {
        super(nombre, Rareza.COMUN, (int) (potencia / 2) + 8);
        this.tipo = tipo; this.potencia = potencia;
    }
    public TipoPocion getTipo() { return tipo; }
    public double getPotencia() { return potencia; }

    /**
     * Consume la pocion sobre el personaje. La expedicion puede ser null (en la aldea).
     * @return true si se ha podido usar.
     */
    public boolean usar(Personaje p, FuenteLuz exp) {
        switch (tipo) {
            case VIDA:
                p.curar(potencia);
                BusEventos.publicar(p.getNombre() + " bebe " + getNombre() + " y recupera " + (int) potencia + " PV.", TipoMensaje.EXITO);
                return true;
            case RECURSO:
                p.setRecurso(p.getRecurso() + potencia);
                BusEventos.publicar("El tonico restaura " + (int) potencia + " de " + p.nombreRecurso() + ".", TipoMensaje.EXITO);
                return true;
            case CALMA:
                p.aliviarEstres((int) potencia);
                BusEventos.publicar("El laudano calma los nervios (-" + (int) potencia + " estres).", TipoMensaje.EXITO);
                return true;
            case PURGA:
                p.limpiarEfectosNegativos();
                BusEventos.publicar("El antidoto purga los males del cuerpo.", TipoMensaje.EXITO);
                return true;
            case ANTORCHA:
                if (exp == null) { BusEventos.publicar("Aqui no hace falta luz."); return false; }
                exp.subirLuz((int) potencia);
                BusEventos.publicar("Prendes una antorcha nueva (+" + (int) potencia + " de luz).", TipoMensaje.RECOMPENSA);
                return true;
        }
        return false;
    }
    @Override public String descripcion() {
        switch (tipo) {
            case VIDA: return "Consumible | Cura " + (int) potencia + " PV";
            case RECURSO: return "Consumible | +" + (int) potencia + " recurso";
            case CALMA: return "Consumible | -" + (int) potencia + " estres";
            case PURGA: return "Consumible | Limpia efectos negativos";
            default: return "Consumible | +" + (int) potencia + " de luz";
        }
    }
    public static Pocion vida() { return new Pocion("Pocion de Salud", TipoPocion.VIDA, 50); }
    public static Pocion tonico() { return new Pocion("Tonico Revitalizante", TipoPocion.RECURSO, 60); }
    public static Pocion laudano() { return new Pocion("Frasco de Laudano", TipoPocion.CALMA, 25); }
    public static Pocion antorcha() { return new Pocion("Antorcha de Brea", TipoPocion.ANTORCHA, 40); }
    public static Pocion antidoto() { return new Pocion("Antidoto de Ruda", TipoPocion.PURGA, 0); }
}
