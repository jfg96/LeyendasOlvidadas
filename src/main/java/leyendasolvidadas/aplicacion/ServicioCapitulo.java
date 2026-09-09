package leyendasolvidadas.aplicacion;

import leyendasolvidadas.dominio.campana.CapituloCampana;

/** Base común de los capítulos: validan capítulo activo, presentación y cierre del capítulo. */
public abstract class ServicioCapitulo {

    public boolean enCapitulo(EstadoJuego estado) {
        return estado.getProgresoCampana().getCapitulo() == capitulo();
    }

    public boolean requierePresentacion(EstadoJuego estado) {
        return enCapitulo(estado) && !estaPresentado(estado);
    }

    protected void comprobarPresentacion(EstadoJuego estado, String yaRealizada) {
        if (!requierePresentacion(estado)) throw new IllegalStateException(yaRealizada);
    }

    protected void cerrar(EstadoJuego estado, CapituloCampana siguiente, String resumen) {
        estado.getProgresoCampana().avanzarA(siguiente);
        estado.getRegistroCampana().anotar(resumen);
    }

    protected abstract CapituloCampana capitulo();

    protected abstract boolean estaPresentado(EstadoJuego estado);
}