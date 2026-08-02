# Registro de cambios

Este documento resume la evolución funcional y técnica de Leyendas Olvidadas.
Las entradas se han reconstruido a partir del historial de Git y de la
documentación mantenida durante el desarrollo.

## 3.1.0-SNAPSHOT — En desarrollo

### Interfaz y accesibilidad

- Conversión de la consola en una interfaz de pantalla completa con búfer
  alternativo, cursor controlado y restauración segura al salir o fallar.
- Incorporación de detección de capacidades y dimensiones mediante JLine, con
  validación del mínimo de 80 × 24 y degradación segura fuera de una terminal.
- Sustitución de la presentación acumulativa por una composición visual estable,
  adaptable entre 60 y 100 columnas, con cabeceras, secciones, acciones y pies
  de navegación consistentes en aldea, expedición y combate.
- Separación efectiva entre color y limpieza de pantalla: `--sin-color` ya no
  provoca que se acumulen las vistas del juego.
- Incorporación de `--modo-lineal` para lectores de pantalla, registros y
  consolas de IDE, manteniendo `--sin-limpiar` como alias compatible.
- Empaquetado de las dependencias de terminal en el JAR ejecutable y nueva ayuda
  de línea de comandos mediante `--ayuda`.
- Reorganización del menú principal de Valdesombra por áreas funcionales.
- Rediseño de la interfaz de consola con secciones, alineación y mensajes
  contextuales consistentes.
- Visualización de acciones no disponibles y del motivo de su bloqueo.
- Indicador del personaje activo, alcance de habilidades e intenciones
  enemigas durante el combate.
- Distinción entre salas visitadas, descubiertas y desconocidas.
- Validación explícita de confirmaciones y opciones numéricas.
- Incorporación de ayuda contextual dentro de la aldea.
- Incorporación de las opciones `--sin-color` y `--sin-limpiar`.

### Campaña y contenido

- Sustitución de las comparaciones por título por identificadores estables de
  misión para proteger el progreso narrativo frente a cambios de redacción.
- Incorporación de un prólogo jugable, persistente y reanudable.
- Incorporación de cinco capítulos completos y una fase final.
- Incorporación de seis regiones con contenido, riesgos y enemigos propios.
- Incorporación de decisiones persistentes que condicionan capítulos,
  alianzas y desenlaces.
- Incorporación de cinco epílogos disponibles según el progreso acumulado.
- Incorporación de un diario persistente de campaña.
- Incorporación de un bestiario desbloqueable.
- Incorporación de eventos regionales y jefes específicos de campaña.

### Compañía y personajes

- Sustitución del modelo de héroe único por una compañía persistente.
- Plantilla máxima de seis personajes y formación activa de tres.
- Protagonista obligatorio en todas las expediciones.
- Incorporación de contratación semanal y gestión de formación.
- Incorporación de nueve clases jugables.
- Incorporación de trasfondos con origen, motivación, rasgo, defecto y voz.
- Incorporación de rasgos y defectos con efectos mecánicos.
- Incorporación de lealtad individual y afinidad bilateral.
- Incorporación de heridas persistentes y tratamiento en la aldea.
- Incorporación de cinco mercenarios únicos no repetibles.
- Incorporación de diez misiones personales con consecuencias persistentes.

### Aldea, economía y progresión

- Separación del tablón de encargos en un servicio de aplicación y un
  controlador de consola específicos, con pruebas de disponibilidad narrativa.
- Conversión del oro y el inventario en recursos compartidos por la compañía.
- Ampliación de la mochila compartida de 14 a 24 espacios.
- Ajuste del capital inicial para permitir formar el primer grupo.
- Incorporación de seis edificios mejorables hasta nivel tres.
- Incorporación de daños y reparaciones persistentes en los edificios.
- Aplicación de recompensas, experiencia y penalizaciones a toda la formación.
- Devolución del equipo al inventario al despedir a un aventurero.

### Combate y expediciones

- Extracción de luz, desgaste, emboscadas y riesgos regionales a un modelo
  independiente con una fuente de azar inyectable y pruebas deterministas.
- Extracción de la selección de encuentros y del descanso en campamento, con
  pruebas del ciclo de caza, exploración, reliquia, jefes y recuperación.
- Extracción de la generación procedural a un modelo de mapa independiente de
  consola, con pruebas de tamaño, conectividad, enlaces y posición del objetivo.
- Adaptación del combate a formaciones de tres héroes.
- Incorporación de iniciativa individual y selección de objetivos aliados o
  enemigos.
- Adaptación de expediciones, trampas y campamentos al grupo activo.
- Incorporación del efecto de regeneración.
- Ajuste de grupos enemigos y jefes a la economía de acciones de la compañía.
- Incorporación de segunda fase y acción adicional para los jefes.
- Reserva del enfrentamiento final para el capítulo correspondiente.
- Corrección del aturdimiento de un turno para impedir efectivamente la acción.
- Ajuste de niveles por dificultad y de habilidades de apoyo.
- Incorporación de un simulador Monte Carlo reproducible de equilibrio.

### Persistencia

- Incorporación de escritura histórica controlada y pruebas completas de
  migración con especímenes LOSV v1-v6.
- Incorporación de guardado seguro mediante archivo temporal y sustitución
  atómica cuando el sistema de archivos lo permite.
- Incorporación de una copia de seguridad automática y recuperación desde ella
  cuando la partida principal está dañada.
- Incorporación de rutas de guardado configurables para aislar perfiles y
  pruebas.
- Sustitución de la serialización nativa de Java por el formato binario LOSV.
- Incorporación de cabecera, versión e identificadores estables.
- Evolución del formato desde LOSV v1 hasta LOSV v7.
- Migración automática de partidas LOSV v1-v6.
- Persistencia de campaña, regiones, trasfondos, compañía, relaciones,
  edificios, mercenarios únicos, diario y bestiario.
- Incorporación de carga de partida desde el menú de la aldea.
- Incorporación de guardado automático entre escenas del prólogo.

### Arquitectura y calidad

- Incorporación de un documento de propuestas de diseño para futuras iteraciones.
- Incorporación de fuentes de azar aisladas e inyectables, con semillas locales
  para evitar interferencias entre mapas y pruebas.
- Propagación de la fuente de azar de cada partida a personajes, enemigos,
  combate y expediciones.
- Eliminación del generador aleatorio global; las ofertas, mapas, encuentros,
  actores, pruebas y simulaciones reciben una fuente aislada y reproducible.
- Sustitución del receptor global de eventos por publicadores inyectables desde
  el punto de entrada, con aislamiento entre partidas y pruebas.
- Extracción de las recompensas, penalizaciones, convivencia y progreso tras
  una expedición a un servicio de aplicación independiente de consola.
- Incorporación de un resultado neutral que comunica a cada interfaz las
  decisiones narrativas pendientes.
- Reorganización del código en paquetes de aplicación, dominio,
  infraestructura e interfaz.
- Separación de las reglas internas respecto de la entrada y salida de consola.
- Incorporación del puerto `RepositorioPartidas` para la persistencia.
- Incorporación del puerto `VistaCombate` para ejecutar el motor sin consola.
- Eliminación del singleton global de juego y de la persistencia estática.
- Incorporación de eventos de dominio con representación independiente.
- Migración a la estructura estándar de Maven y Java 17.
- Incorporación de JUnit 5 y una suite automatizada para las reglas críticas.
- Incorporación de pruebas de arquitectura, campaña, compañía, combate,
  persistencia y servicios de aplicación.
- Incorporación de una prueba integral desde el prólogo hasta el epílogo.
- Revisión de documentación técnica, ortografía y comentarios Javadoc.

## 3.0 — Versión inicial

- Creación de Valdesombra como centro de operaciones.
- Incorporación de expediciones procedurales.
- Incorporación de combate por turnos y filas.
- Incorporación de antorcha, estrés, cordura y aflicciones.
- Incorporación de armas, armaduras, amuletos, consumibles y rarezas.
- Incorporación de misiones de caza, exploración, reliquia y jefe.
- Incorporación de tres clases iniciales: Guerrero, Mago y Pícaro.
- Incorporación de la Santa Compaña como enfrentamiento final inicial.
- Incorporación del primer sistema de guardado mediante serialización Java.
