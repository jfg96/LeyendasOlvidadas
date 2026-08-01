# Registro de cambios

Este documento resume la evolución funcional y técnica de Leyendas Olvidadas.
Las entradas se han reconstruido a partir del historial de Git y de la
documentación mantenida durante el desarrollo.

## 3.1.0-SNAPSHOT — En desarrollo

### Interfaz y accesibilidad

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

- Conversión del oro y el inventario en recursos compartidos por la compañía.
- Ampliación de la mochila compartida de 14 a 24 espacios.
- Ajuste del capital inicial para permitir formar el primer grupo.
- Incorporación de seis edificios mejorables hasta nivel tres.
- Incorporación de daños y reparaciones persistentes en los edificios.
- Aplicación de recompensas, experiencia y penalizaciones a toda la formación.
- Devolución del equipo al inventario al despedir a un aventurero.

### Combate y expediciones

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

- Sustitución de la serialización nativa de Java por el formato binario LOSV.
- Incorporación de cabecera, versión e identificadores estables.
- Evolución del formato desde LOSV v1 hasta LOSV v7.
- Migración automática de partidas LOSV v1-v6.
- Persistencia de campaña, regiones, trasfondos, compañía, relaciones,
  edificios, mercenarios únicos, diario y bestiario.
- Incorporación de carga de partida desde el menú de la aldea.
- Incorporación de guardado automático entre escenas del prólogo.

### Arquitectura y calidad

- Reorganización del código en paquetes de aplicación, dominio,
  infraestructura e interfaz.
- Separación de las reglas internas respecto de la entrada y salida de consola.
- Incorporación del puerto `RepositorioPartidas` para la persistencia.
- Incorporación del puerto `VistaCombate` para ejecutar el motor sin consola.
- Eliminación del singleton global de juego y de la persistencia estática.
- Incorporación de eventos de dominio con representación independiente.
- Migración a la estructura estándar de Maven y Java 17.
- Incorporación de JUnit 5 y una suite automatizada de 25 pruebas.
- Incorporación de pruebas de arquitectura, campaña, compañía, combate,
  persistencia y servicios de aplicación.
- Incorporación de una prueba integral desde el prólogo hasta el epílogo.
- Revisión de documentación técnica y comentarios Javadoc.

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
