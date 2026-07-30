package leyendasolvidadas.dominio.combate;

import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.infraestructura.*;
import leyendasolvidadas.interfaz.consola.*;

import java.io.Serializable;

/** Habilidad de combate de un heroe. Es un contenedor de datos: la resolucion la hace Combate. */
public class Habilidad implements Serializable {
    final String nombre, desc;
    final int coste;            // coste de recurso
    final int cooldown;         // turnos de enfriamiento
    int cdActual = 0;
    final double mult;          // multiplicador de dano (0 = no ofensiva)
    final int[] filas;          // filas enemigas alcanzables
    final boolean aoe;          // golpea todas las filas alcanzables
    final TipoEfecto efecto;    // efecto aplicado (o null)
    final int probEfecto, durEfecto;
    final double potEfecto;
    final boolean sobreSi;      // el efecto se aplica al propio heroe
    boolean sobreAliado;        // permite escoger un integrante de la formacion
    final int critBonus;        // bonus de critico de esta habilidad
    final double robo;          // fraccion del dano que se roba como vida
    final int estresPropio;     // estres que se inflige (+) o alivia (-) el heroe

    public Habilidad(String nombre, String desc, int coste, int cooldown, double mult, int[] filas,
                     boolean aoe, TipoEfecto efecto, int probEfecto, int durEfecto, double potEfecto,
                     boolean sobreSi, int critBonus, double robo, int estresPropio) {
        this.nombre = nombre; this.desc = desc; this.coste = coste; this.cooldown = cooldown;
        this.mult = mult; this.filas = filas; this.aoe = aoe; this.efecto = efecto;
        this.probEfecto = probEfecto; this.durEfecto = durEfecto; this.potEfecto = potEfecto;
        this.sobreSi = sobreSi; this.critBonus = critBonus; this.robo = robo; this.estresPropio = estresPropio;
    }
    /** Atajo para habilidades ofensivas simples. */
    public static Habilidad ataque(String n, String d, int coste, double mult, int[] filas) {
        return new Habilidad(n, d, coste, 0, mult, filas, false, null, 0, 0, 0, false, 0, 0, 0);
    }
    public boolean disponible(Personaje p) { return cdActual == 0 && p.getRecurso() >= coste; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return desc; }
    public int getCoste() { return coste; }
    public int getCooldown() { return cooldown; }
    public double getMultiplicador() { return mult; }
    public int[] getFilas() { return filas.clone(); }
    public boolean esArea() { return aoe; }
    public TipoEfecto getEfecto() { return efecto; }
    public int getProbabilidadEfecto() { return probEfecto; }
    public int getDuracionEfecto() { return durEfecto; }
    public double getPotenciaEfecto() { return potEfecto; }
    public boolean esSobreSi() { return sobreSi; }
    public boolean esSobreAliado() { return sobreAliado; }
    public int getBonusCritico() { return critBonus; }
    public double getRoboVida() { return robo; }
    public int getEstresPropio() { return estresPropio; }
    public int getCooldownActual() { return cdActual; }
    public void setCooldownActual(int valor) { cdActual = Math.max(0, Math.min(cooldown, valor)); }
    public void reducirCooldown() { if (cdActual > 0) cdActual--; }
    public void activarCooldown() { cdActual = cooldown; }
    public Habilidad aAliado() { sobreAliado = true; return this; }
    public String filasTexto() {
        StringBuilder sb = new StringBuilder();
        for (int f : filas) sb.append(f);
        return sb.toString();
    }
}
