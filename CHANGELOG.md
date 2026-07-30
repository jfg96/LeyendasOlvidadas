# Registro de cambios

Todos los cambios notables de *Leyendas Olvidadas: La Compañía* se anotan aquí.
Formato basado en [Keep a Changelog](https://keepachangelog.com/es-ES/).

## [Sin publicar]

### Añadido
- Modelo persistente **`Compania`**: conserva un protagonista obligatorio,
  admite una plantilla de hasta seis personajes y valida una formación activa
  de hasta tres integrantes. Incluye compatibilidad inicial con los guardados
  que todavía almacenan un único jugador.
- Pruebas automáticas sin dependencias para las invariantes de contratación,
  despido y formación de la compañía.
- Fábrica común de héroes y generación semanal de tres candidatos de nivel
  acorde al progreso de la compañía, con coste de contratación escalable.
- Gestión de compañía desde la aldea: contratar, despedir y escoger los dos
  acompañantes que formarán junto al protagonista el grupo activo.
- Opción **Cargar partida** en el menú de la aldea, con confirmación antes de
  descartar el progreso actual y mensajes específicos cuando no existe un
  guardado o no se puede recuperar.
- **6 clases jugables nuevas** (de 3 a 9): Meiga (sanadora), Montero
  (ballestero), Gaitero (soporte), Lobishome (bruiser de sangrado), Zahorí
  (control) y Fraile (exorcista). Un archivo por clase, mismo patrón que las
  existentes.
- Efecto de estado **`REGENERACION`**: curación por turno, procesada en
  `Personaje.tickEfectos`. Sostiene a las clases de soporte y deja preparada
  la curación para el futuro sistema de compañía.

### Cambiado
- Capital inicial aumentado de 40 a 180 reales para poder contratar dos
  acompañantes y preparar la primera expedición sin vaciar la tesorería.
- Menú de creación de personaje ampliado de 3 a 9 opciones (`Juego`).
- Renombradas las 3 clases originales por nombres de folclore ibérico:
  Guerrero → **Alabardero**, Mago → **Animero**, Pícaro → **Bandolero**
  (renombrado completo de clase, no solo la etiqueta).

## [3.0] — Leyendas Olvidadas: La Compañía
- Versión base: aldea de Valdesombra, expediciones procedurales, combate por
  filas, antorcha, cordura, botín con rarezas, misiones y la Santa Compaña
  como jefe final. 3 clases (Guerrero, Mago, Pícaro).
