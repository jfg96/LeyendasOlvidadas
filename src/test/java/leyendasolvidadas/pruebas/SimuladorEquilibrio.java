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

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Simulacion Monte Carlo reproducible para detectar extremos de equilibrio. */
public class SimuladorEquilibrio {
    private static final int ITERACIONES = 1000;
    private static FuenteAzar azar = new AzarJava();

    private record Resultado(boolean victoria, int rondas, int supervivientes, double vidaRestante) {}
    private record Resumen(double victorias, double rondas, double supervivientes, double vida) {}

    public static void main(String[] args) {
        PrintStream salida = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        Map<String, Resumen> resultados = new LinkedHashMap<>();
        int[][] composiciones = {
                {1, 4, 5}, // resistente, curacion y distancia
                {7, 3, 2}, // ofensiva
                {9, 8, 6}  // control y soporte
        };
        String[] nombres = {"equilibrada", "ofensiva", "control"};
        Dificultad[] dificultades = Dificultad.values();

        for (int nivel : new int[]{1, 3, 5}) {
            for (int c = 0; c < composiciones.length; c++) {
                for (Dificultad dificultad : dificultades) {
                    String clave = "niv " + nivel + " | " + nombres[c] + " | " + dificultad.getTitulo();
                    resultados.put(clave, simularSerie(composiciones[c], nivel, dificultad, c * 100_000L + nivel * 1_000L));
                }
                resultados.put("niv " + nivel + " | " + nombres[c] + " | Jefe",
                        simularJefes(composiciones[c], nivel, false, 700_000L + c * 10_000L + nivel));
                resultados.put("niv " + nivel + " | " + nombres[c] + " | Santa Compania",
                        simularJefes(composiciones[c], nivel, true, 900_000L + c * 10_000L + nivel));
            }
        }
        System.setOut(salida);
        System.out.println("ESCENARIO                                  VICTORIAS  RONDAS  SUPERV.  VIDA");
        resultados.forEach((escenario, r) -> System.out.printf("%-43s %7.1f%%  %6.2f  %7.2f  %5.1f%%%n",
                escenario, r.victorias * 100, r.rondas, r.supervivientes, r.vida * 100));
        resultados.forEach(SimuladorEquilibrio::validarFranja);
    }

    private static void validarFranja(String escenario, Resumen resumen) {
        if (escenario.contains("Novicio") && (resumen.victorias < 0.90 || resumen.rondas > 16))
            throw new AssertionError("Novicio ha dejado de ser accesible: " + escenario);
        if (escenario.contains("Veterano") && resumen.victorias < 0.75)
            throw new AssertionError("Veterano castiga demasiado: " + escenario);
        if (escenario.contains("Pesadilla") && (resumen.victorias < 0.15 || resumen.victorias > 0.75))
            throw new AssertionError("Pesadilla queda fuera de su franja: " + escenario);
        if (escenario.endsWith("| Jefe") && (resumen.victorias < 0.40 || resumen.victorias > 0.995))
            throw new AssertionError("Un jefe normal queda fuera de su franja: " + escenario);
        if (escenario.endsWith("Santa Compania") && (resumen.victorias < 0.25 || resumen.victorias > 0.95))
            throw new AssertionError("El jefe final queda fuera de su franja: " + escenario);
    }

    private static Resumen simularJefes(int[] clases, int nivel, boolean finalCampana, long semilla) {
        azar = new AzarJava(semilla);
        int victorias = 0, rondas = 0, supervivientes = 0;
        double vida = 0;
        for (int i = 0; i < ITERACIONES; i++) {
            List<Personaje> heroes = crearGrupo(clases, nivel);
            int nivelZona = nivel + Dificultad.DIFICIL.getNivelExtra();
            Jefe jefe = finalCampana ? Bestiario.crearJefeFinal(nivelZona)
                    : Bestiario.crearJefe(nivelZona, i % 3);
            jefe.configurarAzar(azar);
            Resultado r = simular(heroes, new ArrayList<>(List.of(jefe)), Dificultad.DIFICIL);
            if (r.victoria) victorias++;
            rondas += r.rondas;
            supervivientes += r.supervivientes;
            vida += r.vidaRestante;
        }
        return new Resumen(victorias / (double) ITERACIONES, rondas / (double) ITERACIONES,
                supervivientes / (double) ITERACIONES, vida / ITERACIONES);
    }

    private static Resumen simularSerie(int[] clases, int nivel, Dificultad dificultad, long semilla) {
        azar = new AzarJava(semilla);
        int victorias = 0, rondas = 0, supervivientes = 0;
        double vida = 0;
        for (int i = 0; i < ITERACIONES; i++) {
            List<Personaje> heroes = crearGrupo(clases, nivel);
            Resultado r = null;
            int rondasExpedicion = 0;
            boolean completada = true;
            for (int encuentro = 0; encuentro < 4; encuentro++) {
                List<Enemigo> enemigos = Bestiario.crearGrupo(
                        nivel + dificultad.getNivelExtra(), dificultad, azar);
                r = simular(heroes, enemigos, dificultad);
                rondasExpedicion += r.rondas;
                if (!r.victoria) { completada = false; break; }
                if (encuentro == 1) acampar(heroes);
            }
            if (completada) victorias++;
            rondas += rondasExpedicion;
            supervivientes += r.supervivientes;
            vida += r.vidaRestante;
        }
        return new Resumen(victorias / (double) ITERACIONES, rondas / (double) ITERACIONES,
                supervivientes / (double) ITERACIONES, vida / ITERACIONES);
    }

    private static void acampar(List<Personaje> heroes) {
        for (Personaje heroe : heroes) {
            if (!heroe.estaVivo()) heroe.setVida(heroe.getVidaMax() * 0.15);
            heroe.curar(heroe.getVidaMax() * 0.35);
            heroe.setRecurso(heroe.getRecursoMax());
            heroe.aliviarEstres(25);
            heroe.limpiarEfectosNegativos();
        }
    }

    private static List<Personaje> crearGrupo(int[] clases, int nivel) {
        List<Personaje> heroes = new ArrayList<>();
        for (int i = 0; i < clases.length; i++) {
            Personaje heroe = FabricaHeroes.crear(clases[i], "H" + i);
            heroe.configurarAzar(azar);
            heroe.prepararNivelInicial(nivel);
            heroes.add(heroe);
        }
        return heroes;
    }

    private static Resultado simular(List<Personaje> heroes, List<Enemigo> enemigos,
                                     Dificultad dificultad) {
        int ronda = 0;
        while (vivos(heroes) > 0 && vivos(enemigos) > 0 && ronda < 30) {
            ronda++;
            List<Personaje> orden = new ArrayList<>();
            orden.addAll(heroes.stream().filter(Personaje::estaVivo).toList());
            orden.addAll(enemigos.stream().filter(Personaje::estaVivo).toList());
            Map<Personaje, Integer> iniciativa = new java.util.HashMap<>();
            for (Personaje p : orden) iniciativa.put(p, p.getVelocidad() + azar.entre(0, 4));
            orden.sort(Comparator.comparingInt((Personaje p) -> iniciativa.get(p)).reversed());

            for (Personaje actor : orden) {
                if (!actor.estaVivo()) continue;
                boolean aturdido = actor.tieneEfecto(TipoEfecto.ATURDIDO);
                actor.tickEfectos();
                if (!actor.estaVivo()) continue;
                if (aturdido) continue;
                if (actor instanceof Enemigo enemigo) {
                    if (enemigo instanceof Jefe jefe) jefe.comprobarFase();
                    turnoEnemigo(enemigo, heroes, enemigos, dificultad);
                    if (enemigo instanceof Jefe jefe && jefe.enFaseDos() && enemigo.estaVivo())
                        turnoEnemigo(enemigo, heroes, enemigos, dificultad);
                } else turnoHeroe(actor, heroes, enemigos);
                if (vivos(heroes) == 0 || vivos(enemigos) == 0) break;
            }
        }
        int supervivientes = vivos(heroes);
        double vidaActual = heroes.stream().mapToDouble(Personaje::getVida).sum();
        double vidaMaxima = heroes.stream().mapToDouble(Personaje::getVidaMax).sum();
        return new Resultado(vivos(enemigos) == 0, ronda, supervivientes, vidaActual / vidaMaxima);
    }

    private static void turnoHeroe(Personaje heroe, List<Personaje> heroes, List<Enemigo> enemigos) {
        heroe.setRecurso(heroe.getRecurso() + heroe.getRegenRecurso());
        for (Habilidad h : heroe.getHabilidades()) if (h.getCooldownActual() > 0) h.reducirCooldown();
        Habilidad elegida = elegirHabilidad(heroe, heroes, enemigos);
        if (elegida == null) {
            heroe.setRecurso(heroe.getRecurso() + heroe.getRecursoMax() * 0.4);
            heroe.curar(heroe.getVidaMax() * 0.10);
            return;
        }
        heroe.setRecurso(heroe.getRecurso() - elegida.getCoste());
        elegida.activarCooldown();
        if (elegida.esSobreSi()) {
            Personaje objetivo = elegida.esSobreAliado() ? masHerido(heroes) : heroe;
            objetivo.aplicarEfecto(elegida.getEfecto(), elegida.getDuracionEfecto(), elegida.getPotenciaEfecto());
            return;
        }
        List<Enemigo> alcanzables = objetivos(elegida, enemigos);
        if (elegida.esArea()) for (Enemigo enemigo : alcanzables) golpear(heroe, elegida, enemigo);
        else golpear(heroe, elegida, alcanzables.stream()
                .min(Comparator.comparingDouble(Personaje::getVida)).orElseThrow());
    }

    private static Habilidad elegirHabilidad(Personaje heroe, List<Personaje> heroes, List<Enemigo> enemigos) {
        Personaje herido = masHerido(heroes);
        if (herido.getVida() / herido.getVidaMax() < 0.65) {
            Habilidad apoyo = heroe.getHabilidades().stream()
                    .filter(h -> h.esSobreAliado() && h.disponible(heroe)).findFirst().orElse(null);
            if (apoyo != null) return apoyo;
        }
        return heroe.getHabilidades().stream().filter(h -> h.disponible(heroe) && !h.esSobreSi())
                .filter(h -> !objetivos(h, enemigos).isEmpty())
                .max(Comparator.comparingDouble(h -> h.getMultiplicador() * (h.esArea() ? objetivos(h, enemigos).size() : 1)))
                .orElseGet(() -> heroe.getHabilidades().stream()
                        .filter(h -> h.disponible(heroe) && h.esSobreSi()).findFirst().orElse(null));
    }

    private static List<Enemigo> objetivos(Habilidad habilidad, List<Enemigo> enemigos) {
        List<Enemigo> resultado = new ArrayList<>();
        List<Enemigo> vivos = enemigos.stream().filter(Personaje::estaVivo).toList();
        for (int i = 0; i < vivos.size(); i++)
            for (int fila : habilidad.getFilas()) if (fila == i + 1) { resultado.add(vivos.get(i)); break; }
        return resultado;
    }

    private static void golpear(Personaje heroe, Habilidad habilidad, Enemigo enemigo) {
        if (!enemigo.estaVivo() || azar.probabilidad(enemigo.esquivaActual())) return;
        double danio = heroe.ataqueBase() * habilidad.getMultiplicador() * heroe.modDanioSaliente() * azar.variacion();
        if (azar.probabilidad(heroe.criticoActual() + habilidad.getBonusCritico())) danio *= 1.6;
        double real = habilidad.getMultiplicador() > 0 ? enemigo.recibirDanio(danio, false) : 0;
        if (habilidad.getRoboVida() > 0) heroe.curar(real * habilidad.getRoboVida());
        if (habilidad.getEfecto() != null && enemigo.estaVivo() && azar.probabilidad(habilidad.getProbabilidadEfecto()))
            enemigo.aplicarEfecto(habilidad.getEfecto(), habilidad.getDuracionEfecto(),
                    habilidad.getPotenciaEfecto() > 0 ? habilidad.getPotenciaEfecto() : 3 + heroe.getNivel());
    }

    private static void turnoEnemigo(Enemigo enemigo, List<Personaje> heroes,
                                     List<Enemigo> enemigos, Dificultad dificultad) {
        List<Personaje> disponibles = heroes.stream().filter(Personaje::estaVivo).toList();
        if (disponibles.isEmpty()) return;
        Personaje objetivo = azar.elegir(disponibles);
        int fila = enemigos.stream().filter(Personaje::estaVivo).toList().indexOf(enemigo) + 1;
        MovimientoEnemigo mov = enemigo.elegirMovimiento(Math.max(1, fila));
        if (mov.seCura()) { enemigo.curar(enemigo.getVidaMax() * 0.15); return; }
        if (mov.esSobreSi()) {
            enemigo.aplicarEfecto(mov.getEfecto(), mov.getDuracionEfecto(), mov.getPotenciaEfecto());
            return;
        }
        if (mov.getMultiplicador() > 0 && !azar.probabilidad(objetivo.esquivaActual())) {
            double multDificultad = dificultad == Dificultad.DIFICIL ? 1.10 : 1.0;
            double danio = enemigo.getDanioBase() * mov.getMultiplicador() * enemigo.multFase()
                    * multDificultad * enemigo.modDanioSaliente() * azar.variacion();
            if (azar.probabilidad(8)) danio *= 1.6;
            objetivo.recibirDanio(danio, false);
            if (mov.getEfecto() != null && azar.probabilidad(mov.getProbabilidadEfecto()))
                objetivo.aplicarEfecto(mov.getEfecto(), mov.getDuracionEfecto(), mov.getPotenciaEfecto());
        }
        if (mov.getEstres() > 0) objetivo.sufrirEstres(mov.getEstres());
    }

    private static Personaje masHerido(List<Personaje> heroes) {
        return heroes.stream().filter(Personaje::estaVivo)
                .min(Comparator.comparingDouble(h -> h.getVida() / h.getVidaMax())).orElseThrow();
    }

    private static int vivos(List<? extends Personaje> grupo) {
        return (int) grupo.stream().filter(Personaje::estaVivo).count();
    }
}
