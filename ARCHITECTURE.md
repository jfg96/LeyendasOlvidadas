# Arquitectura

Leyendas Olvidadas separa las reglas del juego de sus mecanismos de entrada,
salida y almacenamiento. La dirección permitida de dependencias es:

```text
interfaz/consola ───┐
                    ├──> aplicacion ──> dominio
infraestructura ──┘
```

El dominio no puede importar la consola ni la infraestructura. La aplicación
coordina casos de uso y define puertos; los adaptadores externos implementan
esos puertos.

## Dominio

- `dominio.combate`: personajes, clases, habilidades, estados y enemigos.
- `dominio.campana`: capítulos y progreso narrativo persistente.
- `dominio.compania`: plantilla, formación, tesorería e inventario.
  Incluye la identidad narrativa persistente de cada mercenario.
- `dominio.objetos`: armas, armaduras, amuletos, consumibles y rarezas.
- `dominio.misiones`: objetivos y progreso de encargos.
- `dominio.mundo`: regiones, habitaciones, dificultad, bestiario y contratos de luz.
- `dominio.azar`: generador reproducible empleado por reglas y simulaciones.
- `dominio.eventos`: mensajes semánticos sin colores ni widgets.

Las clases de dominio no leen teclado, no imprimen, no conocen ANSI y no abren
archivos. `BusEventos` publica hechos narrativos mediante `EventoDominio`; cada
interfaz decide cómo representarlos.

## Aplicación

- `EstadoJuego`: estado persistente de la partida, incluido `ProgresoCampana`.
- `ServicioCompania`: contratar, despedir y preparar la formación.
- `ServicioAldea`: curación, calma, compra, venta y forja.
- `ServicioPrologo`: transiciones, decisiones y consecuencias del prólogo.
- `ServicioCapituloUno`: presentación y primera relación con Padre Tomé.
- `ServicioCapituloDos`: progreso paralelo de regiones, jefes, Libro de los
  Nombres y decisión sobre la verdad de la matanza.
- `Combate`: motor completo de iniciativa, acciones, IA y recompensas.
- `VistaCombate`: puerto de decisiones y representación; permite ejecutar el
  mismo motor desde consola, JavaFX o una prueba automática.
- `RepositorioPartidas`: puerto de almacenamiento.
- `ResultadoAccion`: respuesta neutral de un caso de uso.

Los servicios se pueden ejecutar directamente en pruebas o desde cualquier
interfaz sin arrancar la consola.

Cada `Mision` puede declarar una `Region`. La expedición consume ese dato para
seleccionar ambientación, riesgos y grupos enemigos sin consultar el capítulo
ni introducir reglas narrativas en la interfaz.

## Infraestructura

`GuardarCargar` implementa `RepositorioPartidas`. `CodecPartida` usa el formato
versionado LOSV y nunca serializa clases Java, por lo que mover o renombrar una
clase no altera automáticamente los archivos guardados. Cada versión dispone
de una ruta explícita de lectura o migración; LOSV v3 todavía acepta v1 y v2.

## Interfaz de consola

Contiene el arranque, la representación ANSI y los controladores interactivos de
aldea, expedición, eventos e inventario. `VistaCombateConsola` adapta el motor
de aplicación a la terminal. Esta es una adaptación del
juego a terminal, no una dependencia del dominio.

Una futura interfaz JavaFX deberá:

1. Conectar un receptor propio a `BusEventos`.
2. Consumir `EstadoJuego` y los servicios de aplicación.
3. Implementar o reutilizar un adaptador para `RepositorioPartidas`.
4. Implementar `VistaCombate` para representar sus decisiones sin introducir
   JavaFX en los paquetes internos.

## Comprobación automática

`ArquitecturaTest` inspecciona las fuentes internas y falla si dominio,
aplicación o infraestructura vuelven a importar la consola, usar `UI`, acceder
a `System.in/out` o crear un `Scanner`.
