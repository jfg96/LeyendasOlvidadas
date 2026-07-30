package leyendasolvidadas.infraestructura;

import leyendasolvidadas.dominio.azar.*;
import leyendasolvidadas.dominio.campana.*;
import leyendasolvidadas.aplicacion.*;
import leyendasolvidadas.dominio.combate.*;
import leyendasolvidadas.dominio.compania.*;
import leyendasolvidadas.dominio.objetos.*;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Formato binario estable y versionado, independiente de la serializacion Java. */
public final class CodecPartida {
    public static final int MAGIC = 0x4C4F5356; // LOSV
    public static final int VERSION = 4;

    private CodecPartida() {}

    public static void escribir(DataOutputStream out, EstadoJuego estado) throws IOException {
        out.writeInt(MAGIC);
        out.writeInt(VERSION);
        out.writeInt(estado.getSemana());
        out.writeInt(estado.getExpedicionesGanadas());
        out.writeBoolean(estado.isCampanaGanada());
        escribirCompania(out, estado.getCompania());
        escribirItems(out, estado.getOfertasHerreria());
        out.writeInt(estado.getCandidatos().size());
        for (Personaje candidato : estado.getCandidatos()) escribirHeroe(out, candidato);
        escribirProgresoCampana(out, estado.getProgresoCampana());
        escribirTrasfondos(out, estado.getCompania(), estado.getCandidatos());
        escribirEstadoAldea(out, estado.getEstadoAldea());
    }

    public static EstadoJuego leer(DataInputStream in) throws IOException {
        if (in.readInt() != MAGIC) throw new IOException("Cabecera de partida desconocida");
        int version = in.readInt();
        if (version < 1 || version > VERSION)
            throw new IOException("Version de partida no compatible: " + version);
        int semana = in.readInt();
        int victorias = in.readInt();
        boolean campana = in.readBoolean();
        Compania compania = leerCompania(in);
        List<Item> ofertas = leerItems(in);
        int totalCandidatos = leerCantidad(in, Compania.MAX_PLANTILLA);
        List<Personaje> candidatos = new ArrayList<>();
        for (int i = 0; i < totalCandidatos; i++) candidatos.add(leerHeroe(in));
        ProgresoCampana progreso = version >= 2
                ? leerProgresoCampana(in) : migrarProgresoV1(campana);
        if (version >= 3) leerTrasfondos(in, compania, candidatos);
        else completarTrasfondosLegado(compania, candidatos);
        EstadoAldea estadoAldea = version >= 4 ? leerEstadoAldea(in) : new EstadoAldea();
        EstadoJuego estado = new EstadoJuego();
        estado.restaurarProgreso(semana, victorias, campana, compania, ofertas, candidatos, progreso);
        estado.restaurarEstadoAldea(estadoAldea);
        estado.prepararTrasCarga();
        return estado;
    }

    private static void escribirEstadoAldea(DataOutputStream out, EstadoAldea aldea) throws IOException {
        out.writeInt(EdificioAldea.values().length);
        for (EdificioAldea edificio : EdificioAldea.values()) {
            out.writeUTF(edificio.name()); out.writeInt(aldea.nivel(edificio));
            out.writeBoolean(aldea.estaDanado(edificio));
        }
    }

    private static EstadoAldea leerEstadoAldea(DataInputStream in) throws IOException {
        EstadoAldea aldea = new EstadoAldea();
        int total = leerCantidad(in, EdificioAldea.values().length);
        try {
            for (int i = 0; i < total; i++)
                aldea.restaurar(EdificioAldea.valueOf(in.readUTF()), in.readInt(), in.readBoolean());
        } catch (IllegalArgumentException e) { throw new IOException("Estado de aldea no válido", e); }
        return aldea;
    }

    private static void escribirTrasfondos(DataOutputStream out, Compania compania,
                                             List<Personaje> candidatos) throws IOException {
        out.writeInt(compania.getPlantilla().size());
        for (Personaje heroe : compania.getPlantilla()) escribirTrasfondo(out, heroe.getTrasfondo());
        out.writeInt(candidatos.size());
        for (Personaje candidato : candidatos) escribirTrasfondo(out, candidato.getTrasfondo());
    }

    private static void leerTrasfondos(DataInputStream in, Compania compania,
                                        List<Personaje> candidatos) throws IOException {
        int plantilla = leerCantidad(in, Compania.MAX_PLANTILLA);
        if (plantilla != compania.getPlantilla().size()) throw new IOException("Plantilla de trasfondos incoherente");
        for (Personaje heroe : compania.getPlantilla()) heroe.setTrasfondo(leerTrasfondo(in));
        int totalCandidatos = leerCantidad(in, Compania.MAX_PLANTILLA);
        if (totalCandidatos != candidatos.size()) throw new IOException("Candidatos de trasfondos incoherentes");
        for (Personaje candidato : candidatos) candidato.setTrasfondo(leerTrasfondo(in));
    }

    private static void escribirTrasfondo(DataOutputStream out, TrasfondoMercenario trasfondo) throws IOException {
        out.writeBoolean(trasfondo != null);
        if (trasfondo == null) return;
        out.writeUTF(trasfondo.origen());
        out.writeUTF(trasfondo.descripcion());
        out.writeUTF(trasfondo.rasgo());
        out.writeUTF(trasfondo.defecto());
        out.writeUTF(trasfondo.motivacion());
        out.writeUTF(trasfondo.frase());
    }

    private static TrasfondoMercenario leerTrasfondo(DataInputStream in) throws IOException {
        if (!in.readBoolean()) return null;
        try {
            return new TrasfondoMercenario(in.readUTF(), in.readUTF(), in.readUTF(),
                    in.readUTF(), in.readUTF(), in.readUTF());
        } catch (IllegalArgumentException e) {
            throw new IOException("Trasfondo de mercenario no valido", e);
        }
    }

    private static void completarTrasfondosLegado(Compania compania, List<Personaje> candidatos) {
        for (Personaje heroe : compania.getPlantilla())
            if (!compania.esProtagonista(heroe) && heroe.getTrasfondo() == null)
                heroe.setTrasfondo(TrasfondoMercenario.legado());
        for (Personaje candidato : candidatos)
            if (candidato.getTrasfondo() == null) candidato.setTrasfondo(TrasfondoMercenario.legado());
    }

    private static void escribirProgresoCampana(DataOutputStream out, ProgresoCampana progreso)
            throws IOException {
        out.writeUTF(progreso.getCapitulo().name());
        out.writeInt(progreso.getDecisiones().size());
        for (String decision : progreso.getDecisiones()) out.writeUTF(decision);
        out.writeInt(progreso.getRegionesDesbloqueadas().size());
        for (Region region : progreso.getRegionesDesbloqueadas()) out.writeUTF(region.name());
    }

    private static ProgresoCampana leerProgresoCampana(DataInputStream in) throws IOException {
        try {
            CapituloCampana capitulo = CapituloCampana.valueOf(in.readUTF());
            int totalDecisiones = leerCantidad(in, 512);
            Set<String> decisiones = new LinkedHashSet<>();
            for (int i = 0; i < totalDecisiones; i++)
                if (!decisiones.add(in.readUTF())) throw new IOException("Decision narrativa duplicada");
            int totalRegiones = leerCantidad(in, Region.values().length);
            Set<Region> regiones = new LinkedHashSet<>();
            for (int i = 0; i < totalRegiones; i++)
                if (!regiones.add(Region.valueOf(in.readUTF()))) throw new IOException("Region duplicada");
            return ProgresoCampana.restaurar(capitulo, decisiones, regiones);
        } catch (IllegalArgumentException e) {
            throw new IOException("Progreso narrativo no valido", e);
        }
    }

    private static ProgresoCampana migrarProgresoV1(boolean campanaGanada) {
        if (!campanaGanada) return new ProgresoCampana();
        return ProgresoCampana.restaurar(CapituloCampana.EPILOGO,
                Set.of("legacy.campana_completada"), Set.of());
    }

    private static void escribirCompania(DataOutputStream out, Compania compania) throws IOException {
        List<Personaje> plantilla = compania.getPlantilla();
        out.writeInt(plantilla.size());
        for (Personaje heroe : plantilla) escribirHeroe(out, heroe);
        out.writeInt(plantilla.indexOf(compania.getProtagonista()));
        out.writeInt(compania.getFormacionActiva().size());
        for (Personaje activo : compania.getFormacionActiva()) out.writeInt(plantilla.indexOf(activo));
        out.writeInt(compania.getInventario().getOro());
        escribirItems(out, compania.getInventario().getItems());
    }

    private static Compania leerCompania(DataInputStream in) throws IOException {
        int total = leerCantidad(in, Compania.MAX_PLANTILLA);
        if (total == 0) throw new IOException("La compania guardada no tiene protagonista");
        List<Personaje> plantilla = new ArrayList<>();
        for (int i = 0; i < total; i++) plantilla.add(leerHeroe(in));
        int indiceProtagonista = leerIndice(in, total);
        Compania compania = new Compania(plantilla.get(indiceProtagonista));
        for (Personaje heroe : plantilla) if (heroe != compania.getProtagonista()) compania.contratar(heroe);
        int totalActivos = leerCantidad(in, Compania.MAX_FORMACION);
        List<Personaje> activos = new ArrayList<>();
        for (int i = 0; i < totalActivos; i++) activos.add(plantilla.get(leerIndice(in, total)));
        compania.prepararFormacion(activos);
        int oro = in.readInt();
        compania.getInventario().restaurar(oro, leerItems(in));
        return compania;
    }

    private static void escribirHeroe(DataOutputStream out, Personaje heroe) throws IOException {
        out.writeUTF(idClase(heroe));
        out.writeUTF(heroe.getNombre());
        out.writeInt(heroe.getNivel());
        out.writeDouble(heroe.getVida());
        out.writeDouble(heroe.getRecurso());
        out.writeInt(heroe.getCordura());
        escribirNullable(out, heroe.getAflixion());
        out.writeInt(heroe.getExperiencia());
        escribirItemNullable(out, heroe.getArma());
        escribirItemNullable(out, heroe.getArmadura());
        escribirItemNullable(out, heroe.getAmuleto());
        out.writeInt(heroe.getEfectos().size());
        for (EfectoEstado efecto : heroe.getEfectos()) {
            out.writeUTF(efecto.getTipo().name());
            out.writeInt(efecto.getDuracion());
            out.writeDouble(efecto.getPotencia());
        }
        out.writeInt(heroe.getHabilidades().size());
        for (Habilidad habilidad : heroe.getHabilidades()) out.writeInt(habilidad.getCooldownActual());
    }

    private static Personaje leerHeroe(DataInputStream in) throws IOException {
        String clase = in.readUTF();
        String nombre = in.readUTF();
        int nivel = in.readInt();
        double vida = in.readDouble();
        double recurso = in.readDouble();
        int cordura = in.readInt();
        String aflixion = leerNullable(in);
        int experiencia = in.readInt();
        Personaje heroe = FabricaHeroes.crear(numeroClase(clase), nombre);
        heroe.prepararNivelInicial(nivel);
        heroe.setArma((Arma) leerItemNullable(in));
        heroe.setArmadura((Armadura) leerItemNullable(in));
        heroe.setAmuleto((Amuleto) leerItemNullable(in));
        int totalEfectos = leerCantidad(in, 32);
        List<EfectoEstado> efectos = new ArrayList<>();
        for (int i = 0; i < totalEfectos; i++) efectos.add(new EfectoEstado(
                TipoEfecto.valueOf(in.readUTF()), in.readInt(), in.readDouble()));
        int totalCooldowns = leerCantidad(in, 16);
        List<Integer> cooldowns = new ArrayList<>();
        for (int i = 0; i < totalCooldowns; i++) cooldowns.add(in.readInt());
        heroe.restaurarEstado(vida, recurso, cordura, aflixion, experiencia, efectos, cooldowns);
        return heroe;
    }

    private static void escribirItems(DataOutputStream out, List<Item> items) throws IOException {
        out.writeInt(items.size());
        for (Item item : items) escribirItem(out, item);
    }

    private static List<Item> leerItems(DataInputStream in) throws IOException {
        int total = leerCantidad(in, 128);
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < total; i++) items.add(leerItem(in));
        return items;
    }

    private static void escribirItemNullable(DataOutputStream out, Item item) throws IOException {
        out.writeBoolean(item != null);
        if (item != null) escribirItem(out, item);
    }

    private static Item leerItemNullable(DataInputStream in) throws IOException {
        return in.readBoolean() ? leerItem(in) : null;
    }

    private static void escribirItem(DataOutputStream out, Item item) throws IOException {
        if (item instanceof Arma arma) {
            out.writeByte(1); out.writeUTF(arma.getNombre()); out.writeUTF(arma.getRareza().name());
            out.writeDouble(arma.getDanioBase()); out.writeInt(arma.getMejoras());
        } else if (item instanceof Armadura armadura) {
            out.writeByte(2); out.writeUTF(armadura.getNombre()); out.writeUTF(armadura.getRareza().name());
            out.writeInt(armadura.getDefensa()); out.writeInt(armadura.getVidaExtra());
        } else if (item instanceof Amuleto amuleto) {
            out.writeByte(3); out.writeUTF(amuleto.getNombre()); out.writeUTF(amuleto.getRareza().name());
            out.writeUTF(amuleto.getDon().name()); out.writeInt(amuleto.getPotencia());
        } else if (item instanceof Pocion pocion) {
            out.writeByte(4); out.writeUTF(pocion.getNombre()); out.writeUTF(pocion.getTipo().name());
            out.writeDouble(pocion.getPotencia());
        } else throw new IOException("Tipo de objeto no soportado: " + item.getClass().getSimpleName());
    }

    private static Item leerItem(DataInputStream in) throws IOException {
        int tipo = in.readUnsignedByte();
        String nombre = in.readUTF();
        try {
            return switch (tipo) {
                case 1 -> {
                    Rareza rareza = Rareza.valueOf(in.readUTF());
                    Arma arma = new Arma(nombre, in.readDouble(), rareza);
                    int mejoras = in.readInt();
                    for (int i = 0; i < mejoras; i++) arma.mejorar();
                    yield arma;
                }
                case 2 -> {
                    Rareza rareza = Rareza.valueOf(in.readUTF());
                    yield Armadura.restaurar(nombre, in.readInt(), in.readInt(), rareza);
                }
                case 3 -> {
                    Rareza rareza = Rareza.valueOf(in.readUTF());
                    yield Amuleto.restaurar(nombre, Amuleto.Don.valueOf(in.readUTF()), in.readInt(), rareza);
                }
                case 4 -> new Pocion(nombre, TipoPocion.valueOf(in.readUTF()), in.readDouble());
                default -> throw new IOException("Identificador de objeto desconocido: " + tipo);
            };
        } catch (IllegalArgumentException e) {
            throw new IOException("Datos de objeto no validos", e);
        }
    }

    private static String idClase(Personaje p) {
        if (p instanceof Alabardero) return "ALABARDERO";
        if (p instanceof Animero) return "ANIMERO";
        if (p instanceof Bandolero) return "BANDOLERO";
        if (p instanceof Meiga) return "MEIGA";
        if (p instanceof Montero) return "MONTERO";
        if (p instanceof Gaitero) return "GAITERO";
        if (p instanceof Lobishome) return "LOBISHOME";
        if (p instanceof Zahori) return "ZAHORI";
        if (p instanceof Fraile) return "FRAILE";
        throw new IllegalArgumentException("No es un heroe jugable: " + p.getClass().getSimpleName());
    }

    private static int numeroClase(String id) throws IOException {
        return switch (id) {
            case "ALABARDERO" -> 1; case "ANIMERO" -> 2; case "BANDOLERO" -> 3;
            case "MEIGA" -> 4; case "MONTERO" -> 5; case "GAITERO" -> 6;
            case "LOBISHOME" -> 7; case "ZAHORI" -> 8; case "FRAILE" -> 9;
            default -> throw new IOException("Clase de heroe desconocida: " + id);
        };
    }

    private static int leerCantidad(DataInputStream in, int maximo) throws IOException {
        int valor = in.readInt();
        if (valor < 0 || valor > maximo) throw new IOException("Cantidad fuera de rango: " + valor);
        return valor;
    }

    private static int leerIndice(DataInputStream in, int limite) throws IOException {
        int valor = in.readInt();
        if (valor < 0 || valor >= limite) throw new IOException("Indice fuera de rango: " + valor);
        return valor;
    }

    private static void escribirNullable(DataOutputStream out, String valor) throws IOException {
        out.writeBoolean(valor != null);
        if (valor != null) out.writeUTF(valor);
    }

    private static String leerNullable(DataInputStream in) throws IOException {
        return in.readBoolean() ? in.readUTF() : null;
    }
}
