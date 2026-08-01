package leyendasolvidadas.aplicacion;

import leyendasolvidadas.dominio.campana.CapituloCampana;
import leyendasolvidadas.dominio.misiones.*;
import leyendasolvidadas.dominio.mundo.*;
import leyendasolvidadas.dominio.objetos.Amuleto;

import java.util.ArrayList;
import java.util.List;

/** Genera las ofertas ordinarias y narrativas disponibles en Valdesombra. */
public final class ServicioTablonMisiones {
    public record Tablon(List<Mision> ordinarias, List<Mision> especiales) {}

    public Tablon generar(EstadoJuego estado) {
        CapituloCampana capitulo = estado.getProgresoCampana().getCapitulo();
        Region[] regiones = regiones(capitulo);
        Dificultad[] dificultades = Dificultad.values();
        List<Mision> ordinarias = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Mision mision = capitulo == CapituloCampana.LIBRO_DE_LOS_NOMBRES
                    ? GestorMisiones.generarLibroNombres(regiones[i], estado.getCompania().nivelMedio(), dificultades[i])
                    : estado.getProgresoCampana().estaDesbloqueada(regiones[i])
                    ? GestorMisiones.generarRegional(regiones[i], estado.getCompania().nivelMedio(), dificultades[i])
                    : GestorMisiones.generar(estado.getCompania().nivelMedio(), dificultades[i]);
            ordinarias.add(mision);
        }
        return new Tablon(List.copyOf(ordinarias), List.copyOf(especiales(estado)));
    }

    private List<Mision> especiales(EstadoJuego estado) {
        List<Mision> misiones = new ArrayList<>();
        var progreso = estado.getProgresoCampana();
        if (progreso.getCapitulo() == CapituloCampana.ULTIMA_PROCESION && !estado.isCampanaGanada())
            misiones.add(new MisionJefe(Dificultad.DIFICIL, 500, 1000,
                    Amuleto.aleatorio(30), true).enRegion(Region.HOSPITAL_DEL_CAMINO_VIEJO));
        else if (progreso.haDecidido("cap1.simbolo_peregrinos_descubierto") && !progreso.haDecidido("cap1.rei_derrotado"))
            misiones.add(new MisionJefe(MisionId.REY_SOGAS, "El rey de las sogas",
                    "Seguir a Inés y abatir a O Rei dos Aforcados.", Dificultad.MEDIA, 220, 300,
                    Amuleto.aleatorio(20), false).enRegion(Region.BOSQUE_DE_LOS_AHORCADOS));
        if (progreso.haDecidido("cap2.branas.jefe_disponible") && !progreso.haDecidido("cap2.lavandeira_derrotada"))
            misiones.add(new MisionJefe(MisionId.SUDARIOS_ALDARA, "Los sudarios de Aldara",
                    "Derrotar a A Lavandeira Maior y recuperar las páginas sumergidas.", Dificultad.MEDIA,
                    260, 340, Amuleto.aleatorio(22), false).enRegion(Region.BRANAS_HUNDIDAS));
        if (progreso.haDecidido("cap2.camino.jefe_disponible") && !progreso.haDecidido("cap2.hospitalario_derrotado"))
            misiones.add(new MisionJefe(MisionId.PUERTAS_HOSPITAL, "Las puertas del hospital",
                    "Vencer al Hospitalario que cerró las puertas durante el incendio.", Dificultad.DIFICIL,
                    300, 400, Amuleto.aleatorio(25), false).enRegion(Region.CAMINO_DE_LOS_DIFUNTOS));
        if (progreso.haDecidido("cap3.minas.jefe_disponible") && !progreso.haDecidido("cap3.capataz_derrotado"))
            misiones.add(new MisionJefe(MisionId.CAMPANA_CAPATAZ, "La campana del capataz", "Romper las cadenas de O Capataz.",
                    Dificultad.MEDIA, 320, 440, Amuleto.aleatorio(25), false).enRegion(Region.MINAS_DE_SAN_LOURENZO));
        if (progreso.haDecidido("cap3.pazo.jefe_disponible") && !progreso.haDecidido("cap3.cripta_soutomaior_abierta"))
            misiones.add(new MisionJefe(MisionId.CRIPTA_SOUTOMAIOR, "La cripta de los Soutomaior",
                    "Entrar en la cripta donde se oculta la Falange.", Dificultad.DIFICIL,
                    360, 500, Amuleto.aleatorio(28), false).enRegion(Region.PAZO_DE_SOUTOMAIOR));
        if (new ServicioCapituloCuatro().puedeCelebrarRitual(estado))
            misiones.add(new MisionReliquia(MisionId.VIGILIA_CIENTO_DOCE, "La vigilia de los ciento doce",
                    "Llevar el Libro reconstruido hasta el osario y devolver los nombres a sus muertos.",
                    Dificultad.DIFICIL, 420, 650, Amuleto.aleatorio(30)).enRegion(Region.CAMINO_DE_LOS_DIFUNTOS));
        misiones.addAll(new ServicioMisionesPersonales().disponibles(estado));
        return misiones;
    }

    private static Region[] regiones(CapituloCampana capitulo) {
        return switch (capitulo) {
            case CAMINOS_DE_ANIMAS -> new Region[]{Region.BRANAS_HUNDIDAS, Region.CAMINO_DE_LOS_DIFUNTOS, Region.BRANAS_HUNDIDAS};
            case DEUDA_DE_LOS_VIVOS -> new Region[]{Region.MINAS_DE_SAN_LOURENZO, Region.PAZO_DE_SOUTOMAIOR, Region.MINAS_DE_SAN_LOURENZO};
            case LIBRO_DE_LOS_NOMBRES -> new Region[]{Region.BOSQUE_DE_LOS_AHORCADOS, Region.CAMINO_DE_LOS_DIFUNTOS, Region.PAZO_DE_SOUTOMAIOR};
            default -> new Region[]{Region.BOSQUE_DE_LOS_AHORCADOS, Region.BOSQUE_DE_LOS_AHORCADOS, Region.BOSQUE_DE_LOS_AHORCADOS};
        };
    }
}
