# Leyendas Olvidadas: La Compañía

> La niebla ha devorado los caminos. Las campanas doblan solas.  
> Alguien tiene que llevar la vela... y devolverla.

**Leyendas Olvidadas** es un RPG de terror folclórico para terminal, escrito en
Java puro e inspirado en la tensión, el desgaste y las decisiones de riesgo y
recompensa de *Darkest Dungeon*. La aventura transcurre alrededor de
Valdesombra, una aldea asediada por criaturas del folclore ibérico: ánimas,
meigas, trasgos, lobisomes, cuélebres y la temida Santa Compaña.

Elige una leyenda, acepta encargos, adéntrate en parajes generados de forma
procedural y regresa con vida. La oscuridad ofrece mejores recompensas, pero
también enemigos más peligrosos, emboscadas y una mente cada vez más cerca de
quebrarse.

## Características

- **Compañía de tres héroes.** Crea a tu protagonista, contrata aventureros en
  Valdesombra y prepara una formación con habilidades complementarias.
- **Aventureros con identidad.** Los candidatos tienen origen, personalidad,
  rasgos, defectos, motivaciones y una voz propia antes de ser contratados.
- **Combate por turnos, filas e iniciativa.** Cada integrante actúa según su
  velocidad y cada habilidad alcanza objetivos concretos, aliados o enemigos.
- **Nueve clases jugables.** Cada una dispone de atributos, recurso y cuatro
  habilidades propias.
- **Expediciones procedurales.** Explora mapas conectados con niebla de guerra,
  salas especiales, pasillos, trampas, cofres, mímicos y campamentos.
- **Luz y oscuridad.** La antorcha se consume al avanzar. Una luz baja aumenta
  el estrés, las emboscadas y la fuerza enemiga, pero mejora el botín.
- **Cordura y aflicciones.** Al alcanzar el límite de estrés, el personaje
  afronta una prueba de determinación: puede hallar una virtud o sucumbir a la
  paranoia y la desesperación.
- **Estados alterados.** Sangrado, veneno, quemadura, regeneración, aturdimiento,
  protección, marca y otros efectos cambian el curso del combate.
- **Equipo y botín.** Armas, armaduras, amuletos y consumibles aparecen en
  cuatro rarezas: común, rara, épica y legendaria.
- **Aldea como centro de operaciones.** Contrata y organiza la compañía, visita
  la ermita, la taberna y la herrería, acepta encargos o guarda la partida.
- **Campaña por capítulos.** El prólogo y las decisiones persistentes abren
  regiones y conducen progresivamente hacia la última procesión.
- **Prólogo narrativo.** La llegada a Valdesombra, la desaparición de una niña
  y el primer encuentro con un Sin Rostro introducen el misterio y el combate.

## Clases jugables

| Clase | Estilo | Recurso |
| --- | --- | --- |
| **Alabardero** | Vanguardia resistente, defensa y control | Aguante |
| **Animero** | Daño sobrenatural, quemadura y robo de vida | Maná |
| **Bandolero** | Críticos, esquiva, sangrado y marcas | Energía |
| **Meiga** | Regeneración, veneno y autosuficiencia | Fe |
| **Montero** | Ataques a distancia, remates y trampas | Pulso |
| **Gaitero** | Fortaleza, recuperación y control de la moral | Aliento |
| **Lobishome** | Vanguardia agresiva, sangrado y drenaje | Furia |
| **Zahorí** | Debilitación, aturdimiento y control de campo | Presagio |
| **Fraile** | Resistencia, fuego sagrado y protección | Fervor |

## Requisitos

- **JDK 17 o posterior**.
- **Apache Maven 3.8.6 o posterior**.
- Una terminal compatible con UTF-8.

Puedes comprobar la versión instalada con:

```bash
java -version
mvn -version
```

## Compilar y jugar

Desde la raíz del proyecto:

```bash
mvn clean package
java -jar target/leyendas-olvidadas-3.1.0-SNAPSHOT.jar
```

Si la terminal no representa correctamente los colores ANSI:

```bash
java -jar target/leyendas-olvidadas-3.1.0-SNAPSHOT.jar --sin-color
```

En Windows se recomienda usar Windows Terminal y activar UTF-8 antes de
ejecutar el juego:

```powershell
chcp 65001
mvn clean package
java -jar target\leyendas-olvidadas-3.1.0-SNAPSHOT.jar
```

## Cómo se juega

La partida alterna entre dos espacios:

1. **Valdesombra:** crea y conserva a tu protagonista, contrata hasta completar
   una plantilla de seis, escoge tres miembros activos, compra o forja equipo,
   acepta una misión y guarda el progreso.
2. **La expedición:** explora el mapa, administra la antorcha y los objetos,
   supera encuentros y cumple el objetivo antes de regresar a la entrada.

Los encargos pueden exigir cazar criaturas, cartografiar una zona, recuperar
una reliquia o derrotar a un jefe. Abandonar permite conservar la vida, mientras
que morir devuelve al personaje a la aldea con la mitad de su oro.

Durante el capítulo I, los encargos conducen al Bosque de los Ahorcados, donde
la visibilidad consume más antorcha y aparecen criaturas propias de la región.
Investigar sus sogas conduce hasta Inés y O Rei dos Aforcados; las decisiones
del desenlace abren los caminos del capítulo II.

En el capítulo II, la compañía debe recorrer tanto las Brañas Hundidas como el
Camino de los Difuntos. Cada región tiene encargos, riesgos, criaturas, eventos
y jefe propios; ambas mitades del Libro de los Nombres son necesarias para
descubrir qué ocurrió con los ciento trece peregrinos.

El capítulo III abre las Minas y el Pazo, introduce a Don Gonzalo y permite que
la procesión dañe edificios de Valdesombra. Los servicios afectados permanecen
inutilizables hasta que la compañía financia su reparación.

El protagonista debe participar en todas las expediciones junto a dos
acompañantes. El oro y la mochila de 24 espacios pertenecen a toda la compañía;
cada integrante conserva su nivel, cordura, habilidades y equipo.

Todos los menús se controlan introduciendo la opción numérica indicada. Durante
el combate, cada héroe puede usar una habilidad, abrir la mochila compartida,
recuperar el aliento o intentar una huida conjunta.

## Guardado

La partida se guarda desde la aldea en `partida.sav`, dentro del directorio
desde el que se haya iniciado el juego. El prólogo también crea puntos de
guardado automáticos entre escenas. Al arrancar, el menú principal permite
continuar esa partida o comenzar una nueva.

El archivo utiliza el formato binario versionado LOSV v4, independiente de los
nombres de las clases Java. La versión actual puede migrar partidas LOSV v1, v2 y v3;
las partidas experimentales creadas con la antigua serialización nativa no son
compatibles.

## Estructura del proyecto

```text
LeyendasOlvidadas/
├── src/
│   ├── main/java/leyendasolvidadas/
│   │   ├── aplicacion/          # Casos de uso y flujo de partida
│   │   ├── dominio/
│   │   │   ├── azar/            # Azar reproducible
│   │   │   ├── campana/         # Capítulos y decisiones persistentes
│   │   │   ├── combate/         # Personajes, habilidades y combate
│   │   │   ├── compania/        # Plantilla, formación e inventario
│   │   │   ├── eventos/         # Mensajes semánticos de dominio
│   │   │   ├── misiones/        # Encargos y progreso
│   │   │   ├── mundo/           # Expediciones, salas y bestiario
│   │   │   └── objetos/         # Equipo, consumibles y rarezas
│   │   ├── infraestructura/     # Persistencia LOSV
│   │   └── interfaz/consola/    # Entrada, salida y punto de arranque
│   └── test/java/leyendasolvidadas/pruebas/
├── pom.xml
├── CHANGELOG.md
└── README.md
```

El código emplea una jerarquía común para personajes, fábricas para enemigos
y misiones, enumeraciones para estados y tipos, y un generador de azar
centralizado. Maven administra la compilación reproducible, el empaquetado y
las pruebas JUnit 5 con la estructura estándar de directorios.

Las decisiones y reglas internas no dependen de la terminal. Consulta
[`ARCHITECTURE.md`](ARCHITECTURE.md) para conocer los límites entre dominio,
aplicación, infraestructura e interfaces, y cómo añadir una futura versión JavaFX.

## Estado del proyecto

El juego es funcional y se encuentra en desarrollo. Incluye el bucle completo
de aldea, contratación, expedición, combate por compañías, progresión y jefe final; consulta
[`CHANGELOG.md`](CHANGELOG.md) para ver las incorporaciones más recientes.

## Pruebas y equilibrio

La suite automatizada usa JUnit 5. Desde la raíz del proyecto:

```bash
mvn test
```

El simulador de equilibrio, más costoso que la suite habitual, se ejecuta de
forma explícita:

```bash
mvn test-compile exec:java \
  -Dexec.mainClass=leyendasolvidadas.pruebas.SimuladorEquilibrio \
  -Dexec.classpathScope=test
```

El simulador ejecuta miles de expediciones reproducibles con composiciones,
niveles, dificultades y jefes distintos. Además de presentar tasas de victoria,
rondas y desgaste, falla si alguno de esos escenarios sale de las franjas de
equilibrio definidas para el proyecto.
