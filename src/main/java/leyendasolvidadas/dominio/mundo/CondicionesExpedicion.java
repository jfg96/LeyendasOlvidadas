package leyendasolvidadas.dominio.mundo;

import leyendasolvidadas.dominio.azar.FuenteAzar;
import leyendasolvidadas.dominio.combate.Personaje;
import leyendasolvidadas.dominio.combate.TipoEfecto;

import java.util.List;

/** Luz, desgaste y riesgos ambientales durante el desplazamiento. */
public final class CondicionesExpedicion implements ContextoCombate {
    public record Paso(Personaje infectado, boolean derrumbe, int danoDerrumbe) {}

    private final Region region;
    private final int nivelZona;
    private final FuenteAzar azar;
    private int luz = 100;

    public CondicionesExpedicion(Region region, int nivelZona, FuenteAzar azar) {
        if (azar == null) throw new IllegalArgumentException("La fuente de azar es obligatoria");
        this.region = region;
        this.nivelZona = nivelZona;
        this.azar = azar;
    }

    @Override public int getLuz() { return luz; }
    @Override public void subirLuz(int cantidad) { luz = Math.min(100, luz + Math.max(0, cantidad)); }
    @Override public double getMultBotin() { return luz >= 75 ? 1.0 : luz >= 40 ? 1.1 : luz >= 15 ? 1.3 : 1.6; }
    @Override public int getBonusRareza() { return luz >= 75 ? 0 : luz >= 40 ? 4 : luz >= 15 ? 10 : 18; }

    public int numeroSegmentos() { return azar.entre(2, 3); }
    public int tirarEventoPasillo() { return azar.entre(1, 100); }
    public boolean hayEmboscada() { return azar.probabilidad(probabilidadEmboscada()); }

    public Paso avanzarSegmento(List<Personaje> heroesVivos) {
        bajarLuz(consumoLuz());
        int estres = estresPorOscuridad() + (region == Region.CAMINO_DE_LOS_DIFUNTOS ? 2 : 0);
        if (estres > 0) for (Personaje heroe : heroesVivos) heroe.sufrirEstresAmbiental(estres, region);

        Personaje infectado = null;
        if (region == Region.BRANAS_HUNDIDAS && !heroesVivos.isEmpty() && azar.probabilidad(8)) {
            infectado = azar.elegir(heroesVivos);
            infectado.aplicarEfecto(TipoEfecto.VENENO, 2, 2 + nivelZona / 2.0);
        }
        boolean derrumbe = region == Region.MINAS_DE_SAN_LOURENZO && azar.probabilidad(7);
        int dano = derrumbe ? 5 + nivelZona : 0;
        if (derrumbe) for (Personaje heroe : heroesVivos) heroe.recibirDanio(dano, true);
        return new Paso(infectado, derrumbe, dano);
    }

    private void bajarLuz(int cantidad) { luz = Math.max(0, luz - cantidad); }
    private int consumoLuz() {
        return region == Region.MINAS_DE_SAN_LOURENZO ? 10
                : region == Region.BOSQUE_DE_LOS_AHORCADOS ? 7 : 5;
    }
    private int probabilidadEmboscada() { return luz >= 75 ? 4 : luz >= 40 ? 10 : luz >= 15 ? 18 : 30; }
    private int estresPorOscuridad() { return luz >= 75 ? 0 : luz >= 40 ? 1 : luz >= 15 ? 2 : 4; }
}
