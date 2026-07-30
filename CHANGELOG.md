# Registro de cambios

Todos los cambios notables de *Leyendas Olvidadas: La Compañía* se anotan aquí.
Formato basado en [Keep a Changelog](https://keepachangelog.com/es-ES/).

## [Sin publicar]

### Añadido
- Capítulo II completo, **Los caminos de ánimas**, con presentación de Aldara y
  respuestas distintas según el destino dado a Inés al cerrar el Bosque.
- Tablón regional para Brañas Hundidas y Camino de los Difuntos, con encargos,
  progresión y culminaciones independientes.
- Enemigos de Brañas (Afogado y Lavandeira) y del Camino (Peregrino Quemado y
  Campanero Sin Rostro), además de eventos exclusivos de reflejos y nombres.
- Riesgos diferenciados: infección por el barro en las Brañas y estrés continuo
  causado por las campanas del Camino.
- Jefes **A Lavandeira Maior** y **El Hospitalario**, cada uno custodio de una
  parte del Libro de los Nombres.
- Reconstrucción del Libro, revelación de la matanza de los ciento trece
  peregrinos y decisión de revelar, ocultar o negociar con la verdad.
- Avance a `DEUDA_DE_LOS_VIVOS` y desbloqueo de Minas de San Lourenzo y Pazo de
  Soutomaior únicamente tras completar ambas rutas.
- Cierre completo del capítulo I: tras investigar tres veces el Bosque aparece
  Inés y se habilita la expedición culminante **El rey de las sogas**.
- Jefe regional **O Rei dos Aforcados**, con marca, sangrado, raíces y segunda
  fase, más una decisión final sobre proteger, interrogar o confiar Inés a Tomé.
- Eventos exclusivos del Bosque con voces imitadas y sogas sin cadáver, cada
  uno con elecciones de riesgo, estrés, conciencia y recompensa.
- Avance efectivo a `CAMINOS_DE_ANIMAS` tras derrotar al jefe, conservando la
  decisión sobre Inés y desbloqueando Brañas Hundidas y Camino de los Difuntos.
- Primer ciclo regional del **Bosque de los Ahorcados**, con tres encargos
  propios: Las sogas vacías, El sendero que regresa y La medalla del ahorcado.
- Enemigos regionales Ahorcado Verde y Corvo de Carne, junto a lobos y
  lobisomes, y mayor consumo de antorcha por la escasa visibilidad del Bosque.
- Progreso narrativo de las tres primeras victorias en el Bosque; la tercera
  revela el símbolo de los peregrinos y queda registrada para escenas futuras.
- Apertura del capítulo I con Padre Tomé, el Libro de los Nombres mutilado y
  una primera actitud persistente: confiar, desconfiar o presionarlo.
- Trasfondos completos para candidatos: origen, descripción, rasgo, defecto,
  motivación y frase característica, visibles antes de confirmar un contrato.
- Generación procedural de identidades y perfil de legado para aventureros
  procedentes de partidas anteriores.
- Prólogo jugable **La novena campanada**: carta del concejo, motivo personal
  para acudir a Valdesombra, funeral interrumpido, primera decisión sobre Lúa,
  encuentro tutorial en el cementerio y revelación sobre el robo de nombres.
- `ServicioPrologo` independiente de consola, con pasos validados, reanudación
  desde puntos intermedios y consecuencias distintas al vencer, huir o caer.
- Guardado automático entre escenas del prólogo y prueba completa de
  reanudación, derrota no bloqueante, finalización y desbloqueo del Bosque.
- Armazón narrativo persistente con prólogo, cinco capítulos, última procesión
  y epílogo; solo permite transiciones consecutivas para evitar estados de
  campaña imposibles.
- Banderas narrativas mediante identificadores estables y validados, preparadas
  para que eventos, personajes, precios y finales recuerden decisiones previas.
- Seis regiones de campaña con identidad y capítulo de desbloqueo: Bosque de los
  Ahorcados, Brañas Hundidas, Camino de los Difuntos, Minas de San Lourenzo,
  Pazo de Soutomaior y Hospital del Camino Viejo.
- Pruebas de las transiciones narrativas, desbloqueos, decisiones y migración de
  partidas anteriores.
- Construcción reproducible con Maven para Java 17, empaquetado ejecutable y
  ejecución del simulador de equilibrio mediante `exec-maven-plugin`.
- JUnit 5 como infraestructura de pruebas y ejecución unificada con `mvn test`.
- Servicios de aplicación independientes de UI para gestionar compañía,
  recuperación, compras, ventas y forja, con resultados neutrales reutilizables.
- Puerto `RepositorioPartidas` e implementación de archivo inyectada desde el
  punto de entrada; eliminados el singleton global de `Juego` y la persistencia
  estática.
- Puerto `VistaCombate` y adaptador `VistaCombateConsola`: el motor de combate
  completo se ejecuta ahora desde aplicación sin importar consola ni leer
  teclado. Su prueba de integración usa una vista automática headless.
- Eventos de dominio con intención semántica y adaptador de colores en consola.
- Prueba arquitectónica que impide dependencias de presentación en las capas
  internas, y documentación específica en `ARCHITECTURE.md`.
- Formato binario de guardado **LOSV v1**, con cabecera y versión explícitas,
  identificadores estables para clases y objetos y prueba completa de ida y
  vuelta. Ya no depende de los nombres ni de la ubicación de las clases Java.
- Simulador Monte Carlo reproducible de equilibrio: compara tres composiciones,
  tres niveles y tres dificultades en expediciones de cuatro encuentros, además
  de los jefes normales y la Santa Compaña. Sus franjas automáticas detectan
  combates triviales, injustos o excesivamente largos.
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
- Motor de combate por formaciones con iniciativa basada en velocidad, turnos
  individuales para cada integrante, objetivos enemigos y aliados, huida
  conjunta y reparto de experiencia a toda la formación.
- Expediciones adaptadas a grupos de tres: el estrés ambiental afecta a toda
  la formación, las trampas escogen una víctima, los campamentos recuperan al
  grupo y no se permite partir con plazas vacías.
- Migración de guardados de héroe único: crea la compañía, garantiza el capital
  mínimo para dos contratos y genera candidatos si son necesarios.
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
- La Santa Compaña ya no se desbloquea artificialmente tras cuatro expediciones:
  el enfrentamiento final queda reservado para `ULTIMA_PROCESION`.
- Guardado actualizado a **LOSV v3** para conservar la identidad narrativa de
  plantilla y candidatos, manteniendo lectura de LOSV v1 y v2.
- Guardado actualizado a **LOSV v2** para conservar capítulo, decisiones y
  regiones. El lector mantiene compatibilidad con LOSV v1 y marca de forma
  explícita las campañas antiguas que ya estaban completadas.
- Código principal y pruebas trasladados a la estructura estándar de Maven
  (`src/main/java` y `src/test/java`); las comprobaciones manuales anteriores
  son ahora casos JUnit detectados automáticamente.
- Menús interactivos de juego, aldea, expedición, combate, eventos e inventario
  clasificados como adaptadores de consola; las entidades ya no imprimen, leen
  opciones ni generan texto ANSI.
- Eliminada por completo la implementación de `Serializable` del modelo tras la
  adopción del códec LOSV explícito.
- Proyecto organizado bajo `leyendasolvidadas` en paquetes explícitos de
  aplicación, dominio (combate, compañía, objetos, misiones y mundo),
  infraestructura, interfaz de consola y pruebas. Las APIs de habilidades y
  movimientos sustituyen el acceso implícito que ofrecía el paquete por defecto.
- Los primeros guardados estables usaban LOSV v1. Las partidas experimentales creadas
  con la serialización nativa anterior no son compatibles con la nueva
  organización por paquetes.
- Saltos de nivel de Veterano y Pesadilla reducidos de `+2/+4` a `+1/+2`;
  la dificultad alta conserva grupos más numerosos en vez de depender de una
  diferencia de nivel desproporcionada.
- Jefes adaptados a la economía de tres acciones: 50 % más de vida y una
  segunda acción durante la fase dos. La Santa Compaña queda un nivel por encima
  de la zona, evitando sumar dos veces la dificultad.
- **Alborada** del Gaitero mejorada de 8 a 12 de regeneración y enfriamiento
  reducido de 4 a 3 turnos para que una formación de control tenga sostén real.
- Capital inicial aumentado de 40 a 180 reales para poder contratar dos
  acompañantes y preparar la primera expedición sin vaciar la tesorería.
- Recompensas, experiencia de misión, recuperación y penalizaciones de derrota
  aplicadas a la compañía completa; los grupos enemigos ahora contienen dos o
  tres criaturas para compensar la nueva economía de acciones.
- Mochila compartida ampliada de 14 a 24 espacios; al despedir un aventurero,
  su equipo vuelve al almacén de la compañía.

### Corregido
- El efecto **Aturdido** de un turno ya impide actuar antes de expirar. Antes se
  eliminaba al comenzar el turno y nunca llegaba a cancelar la acción.
- Menú de creación de personaje ampliado de 3 a 9 opciones (`Juego`).
- Renombradas las 3 clases originales por nombres de folclore ibérico:
  Guerrero → **Alabardero**, Mago → **Animero**, Pícaro → **Bandolero**
  (renombrado completo de clase, no solo la etiqueta).

## [3.0] — Leyendas Olvidadas: La Compañía
- Versión base: aldea de Valdesombra, expediciones procedurales, combate por
  filas, antorcha, cordura, botín con rarezas, misiones y la Santa Compaña
  como jefe final. 3 clases (Guerrero, Mago, Pícaro).
