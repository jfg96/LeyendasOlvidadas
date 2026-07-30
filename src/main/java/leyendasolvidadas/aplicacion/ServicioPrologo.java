package leyendasolvidadas.aplicacion;

import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.campana.ProgresoCampana;
import leyendasolvidadas.dominio.combate.Enemigo;
import leyendasolvidadas.dominio.combate.MovimientoEnemigo;
import leyendasolvidadas.dominio.combate.Personaje;

/** Reglas y transiciones del prólogo, sin texto ni dependencias de consola. */
public final class ServicioPrologo {
    public enum Paso { CARTA, MOTIVACION, FUNERAL, RASTRO, CEMENTERIO, DESENLACE, COMPLETADO }
    public enum Motivacion { DINERO, DEBER, CULPA, CURIOSIDAD }
    public enum RespuestaNina { ANOTAR_NOMBRE, PROMETER_VOLVER, EXAMINAR_CENIZA }
    public enum ResultadoCementerio { VICTORIA, HUIDA, DERROTA }

    private static final String CARTA = "prologo.carta_leida";
    private static final String FUNERAL = "prologo.funeral_interrumpido";
    private static final String CEMENTERIO = "prologo.cementerio_resuelto";

    public Paso pasoActual(EstadoJuego estado) {
        ProgresoCampana progreso = estado.getProgresoCampana();
        if (progreso.getCapitulo() != CapituloCampana.PROLOGO) return Paso.COMPLETADO;
        if (!progreso.haDecidido(CARTA)) return Paso.CARTA;
        if (!tienePrefijo(progreso, "prologo.motivacion.")) return Paso.MOTIVACION;
        if (!progreso.haDecidido(FUNERAL)) return Paso.FUNERAL;
        if (!tienePrefijo(progreso, "prologo.nina.")) return Paso.RASTRO;
        if (!progreso.haDecidido(CEMENTERIO)) return Paso.CEMENTERIO;
        return Paso.DESENLACE;
    }

    public void leerCarta(EstadoJuego estado) {
        exigirPaso(estado, Paso.CARTA);
        estado.getProgresoCampana().registrarDecision(CARTA);
    }

    public void elegirMotivacion(EstadoJuego estado, Motivacion motivacion) {
        exigirPaso(estado, Paso.MOTIVACION);
        estado.getProgresoCampana().registrarDecision("prologo.motivacion."
                + motivacion.name().toLowerCase());
    }

    public void registrarDesaparicion(EstadoJuego estado) {
        exigirPaso(estado, Paso.FUNERAL);
        estado.getProgresoCampana().registrarDecision(FUNERAL);
    }

    public void responderALaNina(EstadoJuego estado, RespuestaNina respuesta) {
        exigirPaso(estado, Paso.RASTRO);
        estado.getProgresoCampana().registrarDecision("prologo.nina."
                + respuesta.name().toLowerCase());
    }

    public Enemigo crearSinRostro(int nivelProtagonista) {
        Enemigo enemigo = new Enemigo("Peregrino Sin Rostro", Math.max(1, nivelProtagonista), false);
        enemigo.setVidaMaxBase(20 + Math.max(1, nivelProtagonista) * 4);
        enemigo.setVida(enemigo.getVidaMax());
        enemigo.anadirMovimiento(MovimientoEnemigo.golpe("Mano de Ceniza", 0.55, 3));
        return enemigo;
    }

    public void resolverCementerio(EstadoJuego estado, ResultadoCementerio resultado) {
        exigirPaso(estado, Paso.CEMENTERIO);
        ProgresoCampana progreso = estado.getProgresoCampana();
        progreso.registrarDecision("prologo.cementerio." + resultado.name().toLowerCase());
        progreso.registrarDecision(CEMENTERIO);
        if (resultado == ResultadoCementerio.DERROTA) recuperarTrasDerrota(estado.getJugador());
    }

    public void finalizar(EstadoJuego estado) {
        exigirPaso(estado, Paso.DESENLACE);
        estado.getProgresoCampana().registrarDecision("prologo.nina_olvidada");
        estado.getProgresoCampana().avanzarA(CapituloCampana.CAMPANAS_DE_VALDESOMBRA);
    }

    private static void recuperarTrasDerrota(Personaje protagonista) {
        protagonista.setVida(protagonista.getVidaMax() * 0.5);
        protagonista.limpiarEfectos();
        protagonista.aliviarEstres(20);
    }

    private static boolean tienePrefijo(ProgresoCampana progreso, String prefijo) {
        return progreso.getDecisiones().stream().anyMatch(id -> id.startsWith(prefijo));
    }

    private void exigirPaso(EstadoJuego estado, Paso esperado) {
        Paso actual = pasoActual(estado);
        if (actual != esperado)
            throw new IllegalStateException("Paso de prologo esperado " + esperado + ", actual " + actual);
    }
}
