# Leyendas Olvidadas: La Compañía

RPG de terror folclórico para terminal desarrollado en Java. El jugador dirige
una compañía de aventureros, prepara expediciones, combate criaturas del
folclore ibérico y toma decisiones persistentes a lo largo de una campaña de
cinco capítulos.

## Estado

El proyecto se encuentra en desarrollo. La campaña principal es jugable desde
el prólogo hasta cinco epílogos, e incluye el ciclo completo de aldea,
contratación, preparación, expedición, combate, progresión y guardado.

Versión actual: `3.1.0-SNAPSHOT`.

## Funcionalidades

- Compañía con una plantilla máxima de seis personajes y una formación activa
  de tres.
- Nueve clases jugables con atributos, recursos y habilidades propios.
- Combate por turnos con iniciativa, filas, selección de objetivos, estados e
  intenciones enemigas visibles.
- Expediciones procedurales con niebla de guerra, trampas, cofres, campamentos,
  eventos y encuentros regionales.
- Sistemas de antorcha, estrés, cordura, aflicciones, equipo y botín.
- Campaña de cinco capítulos con regiones, decisiones y epílogos persistentes.
- Mercenarios con trasfondo, rasgos, defectos, lealtad, afinidad y heridas.
- Cinco mercenarios únicos con cadenas de misiones personales.
- Aldea con contratación, formación, servicios y seis edificios mejorables.
- Diario de campaña y bestiario desbloqueable.
- Guardado binario versionado con migración desde LOSV v1 hasta v6.
- Suite de pruebas JUnit 5 y simulador reproducible de equilibrio.

## Clases jugables

| Clase | Función principal | Recurso |
| --- | --- | --- |
| Alabardero | Defensa y control de vanguardia | Aguante |
| Animero | Daño sobrenatural y robo de vida | Maná |
| Bandolero | Críticos, esquiva y sangrado | Energía |
| Meiga | Regeneración y veneno | Fe |
| Montero | Ataques a distancia y trampas | Pulso |
| Gaitero | Apoyo y control de la moral | Aliento |
| Lobishome | Daño de vanguardia y drenaje | Furia |
| Zahorí | Debilitación y control de campo | Presagio |
| Fraile | Protección, aturdimiento y fuego | Fervor |

## Requisitos

- JDK 17 o posterior.
- Apache Maven 3.8.6 o posterior.
- Terminal compatible con UTF-8.

## Compilación y ejecución

Desde la raíz del proyecto:

```bash
mvn clean package
java -jar target/leyendas-olvidadas-3.1.0-SNAPSHOT.jar
```

Opciones disponibles:

- `--sin-color`: desactiva los colores ANSI.
- `--modo-lineal`: evita la pantalla completa y conserva un historial continuo,
  indicado para lectores de pantalla, registros y consolas de IDE.
- `--sin-limpiar`: alias compatible de `--modo-lineal`.
- `--ayuda`: muestra las opciones sin iniciar el juego.

En una terminal interactiva, el juego utiliza una pantalla alternativa: la
interfaz no se acumula en el historial y al salir se recuperan el contenido y
el cursor anteriores. La interfaz requiere un tamaño mínimo de 80 × 24.

Ejemplo:

```bash
java -jar target/leyendas-olvidadas-3.1.0-SNAPSHOT.jar --sin-color --modo-lineal
```

En Windows puede ser necesario activar UTF-8 antes de ejecutar el juego:

```powershell
chcp 65001
mvn clean package
java -jar target\leyendas-olvidadas-3.1.0-SNAPSHOT.jar
```

## Funcionamiento general

La partida alterna entre la aldea y las expediciones:

1. En Valdesombra se gestiona la plantilla, la formación, el equipo, los
   servicios, los edificios y los encargos.
2. En una expedición se explora un mapa, se administra la antorcha y el
   inventario, se resuelven encuentros y se completa o abandona la misión.
3. Al regresar se aplican recompensas, experiencia, relaciones, heridas y
   progreso de campaña.

El protagonista debe participar en todas las expediciones. El oro y la mochila
de 24 espacios pertenecen a la compañía; cada integrante conserva su nivel,
estado mental, desarrollo y equipo.

Los menús se controlan mediante opciones numéricas. Durante el combate, el
indicador `AHORA ACTÚA` identifica al personaje activo y cada habilidad muestra
su alcance antes de seleccionar el objetivo.

## Guardado

La partida se guarda en `partida.sav`, dentro del directorio desde el que se
inicia el juego. Puede guardarse manualmente desde la aldea y el prólogo crea
puntos de guardado automáticos entre escenas.

Cada actualización conserva la versión anterior en `partida.sav.bak`. Si el
archivo principal está dañado, el juego intenta cargar automáticamente esa
copia de seguridad.

El formato actual es LOSV v7 y puede migrar partidas LOSV v1-v6. Los archivos
experimentales creados con la antigua serialización nativa de Java no son
compatibles.

## Estructura

```text
src/
├── main/java/leyendasolvidadas/
│   ├── aplicacion/          Casos de uso y coordinación
│   ├── dominio/             Reglas y modelos del juego
│   │   ├── azar/
│   │   ├── campana/
│   │   ├── combate/
│   │   ├── compania/
│   │   ├── eventos/
│   │   ├── misiones/
│   │   ├── mundo/
│   │   └── objetos/
│   ├── infraestructura/     Persistencia LOSV
│   └── interfaz/consola/    Interfaz y punto de entrada
└── test/java/leyendasolvidadas/pruebas/
```

Las reglas internas no dependen de la terminal. Los límites entre dominio,
aplicación, infraestructura e interfaz están descritos en
[`ARCHITECTURE.md`](ARCHITECTURE.md).

## Pruebas

Para ejecutar la suite automatizada:

```bash
mvn test
```

Para ejecutar el simulador de equilibrio:

```bash
mvn test-compile exec:java \
  -Dexec.mainClass=leyendasolvidadas.pruebas.SimuladorEquilibrio \
  -Dexec.classpathScope=test
```

El simulador evalúa composiciones, niveles, dificultades y jefes mediante
expediciones reproducibles y comprueba que los resultados permanezcan dentro
de las franjas definidas.

## Documentación

- [`ARCHITECTURE.md`](ARCHITECTURE.md): organización y reglas de dependencia.
- [`CHANGELOG.md`](CHANGELOG.md): evolución funcional y técnica.
- [`PROJECT_STATUS.md`](PROJECT_STATUS.md): estado de continuidad del desarrollo.
