package leyendasolvidadas.aplicacion;

import leyendasolvidadas.dominio.combate.Personaje;
import leyendasolvidadas.dominio.compania.Inventario;
import leyendasolvidadas.dominio.objetos.Item;
import leyendasolvidadas.dominio.mundo.EdificioAldea;
import leyendasolvidadas.dominio.compania.HeridaPersistente;

/** Reglas economicas y de recuperacion de Valdesombra. */
public class ServicioAldea {
    public ResultadoAccion sanar(EstadoJuego estado, Personaje personaje) {
        if (estado.getEstadoAldea().estaDanado(EdificioAldea.ERMITA)) return ResultadoAccion.error("La ermita está dañada.");
        int coste = costeSanar(estado, personaje);
        if (!estado.getCompania().getInventario().gastarOro(coste)) return ResultadoAccion.error("No te llega el oro.");
        personaje.setVida(personaje.getVidaMax());
        personaje.limpiarEfectosNegativos();
        return ResultadoAccion.exito("Las heridas de " + personaje.getNombre() + " se cierran.");
    }

    public ResultadoAccion calmar(EstadoJuego estado, Personaje personaje, int coste, int alivio) {
        if (!estado.getCompania().getInventario().gastarOro(coste)) return ResultadoAccion.error("No te llega el oro.");
        personaje.aliviarEstres(alivio);
        return ResultadoAccion.exito(personaje.getNombre() + " recupera el sosiego (-" + alivio + " estres).");
    }

    public ResultadoAccion tratarHerida(EstadoJuego estado, Personaje personaje, HeridaPersistente herida) {
        if (estado.getEstadoAldea().estaDanado(EdificioAldea.ERMITA)) return ResultadoAccion.error("La ermita está dañada.");
        if (herida == null || !personaje.getHeridas().contains(herida)) return ResultadoAccion.error("Esa secuela ya no requiere tratamiento.");
        int coste = costeTratar(estado, personaje);
        if (!estado.getCompania().getInventario().gastarOro(coste)) return ResultadoAccion.error("No te llega el oro.");
        personaje.tratarHerida(herida);
        return ResultadoAccion.exito(personaje.getNombre() + " supera " + herida.name().toLowerCase().replace('_', ' ') + ".");
    }

    public ResultadoAccion comprar(EstadoJuego estado, Item item) {
        if (estado.getEstadoAldea().estaDanado(EdificioAldea.HERRERIA)) return ResultadoAccion.error("La herrería está dañada.");
        if (item == null || !estado.getOfertasHerreria().contains(item))
            return ResultadoAccion.error("Ese objeto ya no esta disponible.");
        Inventario inventario = estado.getCompania().getInventario();
        if (!inventario.gastarOro(item.getValorOro())) return ResultadoAccion.error("No te llega el oro.");
        if (!inventario.anadir(item)) {
            inventario.ganarOro(item.getValorOro());
            return ResultadoAccion.error("La mochila esta llena.");
        }
        estado.getOfertasHerreria().remove(item);
        return ResultadoAccion.exito("Compras " + item.getNombre() + ".");
    }

    public ResultadoAccion forjar(EstadoJuego estado, Personaje personaje) {
        if (estado.getEstadoAldea().estaDanado(EdificioAldea.HERRERIA)) return ResultadoAccion.error("La herrería está dañada.");
        if (personaje.getArma() == null) return ResultadoAccion.error("No hay arma que forjar.");
        int coste = costeForjar(estado, personaje);
        if (!estado.getCompania().getInventario().gastarOro(coste)) return ResultadoAccion.error("No te llega el oro.");
        personaje.getArma().mejorar();
        return ResultadoAccion.exito("El martillo canta: " + personaje.getArma().getNombre()
                + " ahora hace +" + (int) personaje.getArma().getDanio() + " de dano.");
    }

    public ResultadoAccion vender(EstadoJuego estado, Item item) {
        Inventario inventario = estado.getCompania().getInventario();
        if (item == null || !inventario.getItems().remove(item))
            return ResultadoAccion.error("Ese objeto no esta en la mochila.");
        int precio = item.getValorOro() / 2;
        inventario.ganarOro(precio);
        return ResultadoAccion.exito("Vendes " + item.getNombre() + " por " + precio + " reales.");
    }

    public ResultadoAccion reparar(EstadoJuego estado, EdificioAldea edificio) {
        if (!estado.getEstadoAldea().estaDanado(edificio)) return ResultadoAccion.error("El edificio no necesita reparación.");
        int coste = 80 + estado.getEstadoAldea().nivel(edificio) * 20;
        if (!estado.getCompania().getInventario().gastarOro(coste)) return ResultadoAccion.error("No hay fondos para la reparación.");
        estado.getEstadoAldea().reparar(edificio);
        return ResultadoAccion.exito(edificio.getNombre() + " vuelve a prestar servicio.");
    }

    public int costeMejora(EstadoJuego estado, EdificioAldea edificio) {
        return 100 + estado.getEstadoAldea().nivel(edificio) * 80;
    }
    public int costeSanar(EstadoJuego estado, Personaje p) {
        return Math.max(5, 15 + p.getNivel() * 5 - estado.getEstadoAldea().nivel(EdificioAldea.ERMITA) * 4);
    }
    public int costeTratar(EstadoJuego estado, Personaje p) {
        return Math.max(15, 35 + p.getNivel() * 10 - estado.getEstadoAldea().nivel(EdificioAldea.ERMITA) * 6);
    }
    public int costeForjar(EstadoJuego estado, Personaje p) {
        return p.getArma() == null ? 0 : Math.max(20, 50 * (p.getArma().getMejoras() + 1)
                - estado.getEstadoAldea().nivel(EdificioAldea.HERRERIA) * 8);
    }

    public ResultadoAccion mejorar(EstadoJuego estado, EdificioAldea edificio) {
        if (estado.getEstadoAldea().estaDanado(edificio)) return ResultadoAccion.error("Primero hay que reparar el edificio.");
        if (estado.getEstadoAldea().nivel(edificio) >= 3) return ResultadoAccion.error("El edificio ya ha alcanzado su máximo.");
        int coste = costeMejora(estado, edificio);
        if (!estado.getCompania().getInventario().gastarOro(coste)) return ResultadoAccion.error("No hay fondos para la mejora.");
        estado.getEstadoAldea().mejorar(edificio);
        return ResultadoAccion.exito(edificio.getNombre() + " alcanza el nivel " + estado.getEstadoAldea().nivel(edificio) + ".");
    }
}
