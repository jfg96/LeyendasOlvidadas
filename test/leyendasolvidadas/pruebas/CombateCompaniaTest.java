package leyendasolvidadas.pruebas;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.infraestructura.*;
import leyendasolvidadas.interfaz.consola.*;

import java.util.List;

/** Prueba de integracion minima del combate y sus recompensas compartidas. */
public class CombateCompaniaTest {
    public static void main(String[] args) {
        Personaje protagonista = FabricaHeroes.crear(1, "Aldan");
        Personaje meiga = FabricaHeroes.crear(4, "Iria");
        Personaje bandolero = FabricaHeroes.crear(3, "Lope");
        Compania compania = new Compania(protagonista);
        compania.contratar(meiga);
        compania.contratar(bandolero);
        compania.prepararFormacion(List.of(protagonista, meiga, bandolero));

        Enemigo enemigo = new Enemigo("Sombra de prueba", 1, false);
        enemigo.anadirMovimiento(MovimientoEnemigo.golpe("Roce", 0.1, 1));
        enemigo.setVida(1);
        Combate combate = new Combate(compania.getFormacionActiva(), List.of(enemigo),
                null, null, compania.getInventario(), new VistaAutomatica());

        comprobar(combate.ejecutar(false) == Combate.Resultado.VICTORIA,
                "La compania debe poder ganar el encuentro");
        for (Personaje heroe : compania.getFormacionActiva())
            comprobar(heroe.getExperiencia() > 0, "Todos los miembros deben recibir experiencia");
        comprobar(compania.getInventario().getOro() > 0, "El oro debe ingresar en la tesoreria");
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }

    private static class VistaAutomatica implements VistaCombate {
        public void mostrarInicio(boolean emboscada, List<Enemigo> enemigos) {}
        public void mostrarEstado(int ronda, int luz, List<Personaje> heroes,
                                  List<Enemigo> enemigos, Personaje actor) {}
        public int elegirAccion(Personaje heroe, List<Habilidad> habilidades) { return 1; }
        public Personaje elegirAliado(List<Personaje> aliados) { return aliados.get(0); }
        public Enemigo elegirEnemigo(List<Enemigo> alcanzables, List<Enemigo> formacion) {
            return alcanzables.get(0);
        }
        public boolean usarInventario(Inventario inventario, Personaje personaje, FuenteLuz luz) { return false; }
        public void pausa() {}
    }
}
