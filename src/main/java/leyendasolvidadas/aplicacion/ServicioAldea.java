package leyendasolvidadas.aplicacion;

import leyendasolvidadas.dominio.combate.Personaje;
import leyendasolvidadas.dominio.compania.Inventario;
import leyendasolvidadas.dominio.objetos.Item;

/** Reglas economicas y de recuperacion de Valdesombra. */
public class ServicioAldea {
    public ResultadoAccion sanar(EstadoJuego estado, Personaje personaje) {
        int coste = 15 + personaje.getNivel() * 5;
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

    public ResultadoAccion comprar(EstadoJuego estado, Item item) {
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
        if (personaje.getArma() == null) return ResultadoAccion.error("No hay arma que forjar.");
        int coste = 50 * (personaje.getArma().getMejoras() + 1);
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
}
