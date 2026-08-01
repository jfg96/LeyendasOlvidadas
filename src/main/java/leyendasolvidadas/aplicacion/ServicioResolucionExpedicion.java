package leyendasolvidadas.aplicacion;

import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.combate.Personaje;
import leyendasolvidadas.dominio.compania.Compania;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.objetos.Item;
import leyendasolvidadas.dominio.mundo.EdificioAldea;
import leyendasolvidadas.dominio.mundo.Region;

import java.util.List;

/** Aplica las consecuencias del regreso de una expedición sin depender de la interfaz. */
public final class ServicioResolucionExpedicion {
    public record Resolucion(
            ResultadoExpedicion resultado,
            int oroRecibido,
            int experienciaRecibida,
            Item itemRecibido,
            int oroPerdido,
            int progresoCapituloUno,
            int progresoCapituloDos,
            int progresoCapituloTres,
            int fragmentosLibro,
            boolean requiereFinal,
            boolean requiereDesenlacePersonal,
            boolean requiereCierreCapituloUno,
            boolean requiereCierreCapituloDos,
            boolean requiereCierreCapituloTres,
            boolean requiereCierreCapituloCuatro) {}

    public Resolucion resolver(EstadoJuego estado, Mision mision, ResultadoExpedicion resultado) {
        if (estado == null || mision == null || resultado == null)
            throw new IllegalArgumentException("Estado, misión y resultado son obligatorios");

        estado.avanzarSemana();
        estado.renovarHerreria();
        estado.renovarContratacion();

        return switch (resultado) {
            case VICTORIA -> resolverVictoria(estado, mision);
            case ABANDONO -> resolverAbandono(estado, mision);
            case DERROTA -> resolverDerrota(estado, mision);
        };
    }

    private Resolucion resolverVictoria(EstadoJuego estado, Mision mision) {
        Compania compania = estado.getCompania();
        List<Personaje> grupo = compania.getFormacionActiva();
        estado.registrarVictoria();
        estado.getRegistroCampana().anotar("Semana " + estado.getSemana() + ": victoria en «"
                + mision.getNombre() + "» (" + nombreRegion(mision) + ").");
        new ServicioCompania().registrarConvivencia(compania, ServicioCompania.ResultadoExpedicion.VICTORIA);

        compania.getInventario().ganarOro(mision.getOroRecompensa());
        int experiencia = (int) Math.round(mision.getXpRecompensa()
                * (1 + estado.getEstadoAldea().nivel(EdificioAldea.ARCHIVO) * 0.05));
        for (Personaje heroe : grupo) {
            heroe.ganarExperiencia(experiencia);
            heroe.aliviarEstres(20);
        }
        if (mision.getItemRecompensa() != null)
            compania.getInventario().anadir(mision.getItemRecompensa());

        int progresoUno = new ServicioCapituloUno().registrarVictoria(estado, mision.getRegion());
        int progresoDos = new ServicioCapituloDos().registrarVictoria(estado, mision.getRegion());
        int progresoTres = new ServicioCapituloTres().registrarVictoria(estado, mision.getRegion());
        int fragmentos = estado.getProgresoCampana().getCapitulo() == CapituloCampana.LIBRO_DE_LOS_NOMBRES
                ? new ServicioCapituloCuatro().registrarHallazgo(estado, mision.getRegion()) : 0;

        if (mision.getId() == MisionId.SUDARIOS_ALDARA)
            new ServicioCapituloDos().registrarJefe(estado, Region.BRANAS_HUNDIDAS);
        if (mision.getId() == MisionId.PUERTAS_HOSPITAL)
            new ServicioCapituloDos().registrarJefe(estado, Region.CAMINO_DE_LOS_DIFUNTOS);
        if (mision.getId() == MisionId.CAMPANA_CAPATAZ)
            new ServicioCapituloTres().registrarJefe(estado, Region.MINAS_DE_SAN_LOURENZO);
        if (mision.getId() == MisionId.CRIPTA_SOUTOMAIOR)
            new ServicioCapituloTres().registrarJefe(estado, Region.PAZO_DE_SOUTOMAIOR);

        boolean personal = mision instanceof MisionPersonal misionPersonal
                && new ServicioMisionesPersonales().registrarVictoria(estado, misionPersonal);
        boolean finalPendiente = mision instanceof MisionJefe jefe && jefe.esFinal();

        return new Resolucion(ResultadoExpedicion.VICTORIA, mision.getOroRecompensa(), experiencia,
                mision.getItemRecompensa(), 0, progresoUno, progresoDos, progresoTres, fragmentos,
                finalPendiente, personal, mision.getId() == MisionId.REY_SOGAS,
                new ServicioCapituloDos().puedeCerrar(estado)
                        && estado.getProgresoCampana().getCapitulo() == CapituloCampana.CAMINOS_DE_ANIMAS,
                new ServicioCapituloTres().puedeCerrar(estado)
                        && estado.getProgresoCampana().getCapitulo() == CapituloCampana.DEUDA_DE_LOS_VIVOS,
                mision.getId() == MisionId.VIGILIA_CIENTO_DOCE);
    }

    private Resolucion resolverAbandono(EstadoJuego estado, Mision mision) {
        estado.getRegistroCampana().anotar("Semana " + estado.getSemana()
                + ": la compañía abandonó «" + mision.getNombre() + "».");
        new ServicioCompania().registrarConvivencia(estado.getCompania(),
                ServicioCompania.ResultadoExpedicion.ABANDONO);
        return vacia(ResultadoExpedicion.ABANDONO, 0);
    }

    private Resolucion resolverDerrota(EstadoJuego estado, Mision mision) {
        Compania compania = estado.getCompania();
        estado.getRegistroCampana().anotar("Semana " + estado.getSemana()
                + ": derrota y nuevas secuelas en «" + mision.getNombre() + "».");
        new ServicioCompania().registrarConvivencia(compania, ServicioCompania.ResultadoExpedicion.DERROTA);
        int perdido = compania.getInventario().getOro() / 2;
        compania.getInventario().gastarOro(perdido);
        for (Personaje heroe : compania.getFormacionActiva()) {
            heroe.setVida(heroe.getVidaMax() * 0.5);
            heroe.limpiarEfectos();
            heroe.resetMental();
            heroe.aliviarEstres(30);
        }
        return vacia(ResultadoExpedicion.DERROTA, perdido);
    }

    private static Resolucion vacia(ResultadoExpedicion resultado, int oroPerdido) {
        return new Resolucion(resultado, 0, 0, null, oroPerdido, 0, 0, 0, 0,
                false, false, false, false, false, false);
    }

    private static String nombreRegion(Mision mision) {
        return mision.getRegion() == null ? "paraje desconocido" : mision.getRegion().getNombre();
    }
}
