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
- **Campaña y modo libre.** Tras cuatro expediciones victoriosas se desbloquea
  el enfrentamiento final contra la Santa Compaña. La partida puede continuar
  después de la victoria.

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
- Una terminal compatible con UTF-8.
- No requiere Maven, Gradle ni librerías externas.

Puedes comprobar la versión instalada con:

```bash
java -version
javac -version
```

## Compilar y jugar

Desde la raíz del proyecto:

```bash
mkdir -p out
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

Si la terminal no representa correctamente los colores ANSI:

```bash
java -cp out Main --sin-color
```

En Windows se recomienda usar Windows Terminal y activar UTF-8 antes de
ejecutar el juego:

```powershell
chcp 65001
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
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

El protagonista debe participar en todas las expediciones junto a dos
acompañantes. El oro y la mochila de 24 espacios pertenecen a toda la compañía;
cada integrante conserva su nivel, cordura, habilidades y equipo.

Todos los menús se controlan introduciendo la opción numérica indicada. Durante
el combate, cada héroe puede usar una habilidad, abrir la mochila compartida,
recuperar el aliento o intentar una huida conjunta.

## Guardado

La partida se guarda desde la aldea en `partida.sav`, dentro del directorio
desde el que se haya iniciado el juego. Al arrancar, el menú principal permite
continuar esa partida o comenzar una nueva.

El archivo utiliza serialización nativa de Java. Conviene conservar una copia de
seguridad antes de cambiar entre versiones del juego, ya que no se garantiza la
compatibilidad de partidas antiguas.

## Estructura del proyecto

```text
LeyendasOlvidadas/
├── src/
│   ├── Main.java              # Punto de entrada
│   ├── Juego.java             # Flujo principal de la partida
│   ├── Aldea.java             # Centro de operaciones
│   ├── Expedicion.java        # Exploración y mapa procedural
│   ├── Combate.java           # Motor de combate
│   ├── Personaje.java         # Base de héroes y enemigos
│   ├── Bestiario.java         # Fábrica de criaturas y jefes
│   ├── GestorMisiones.java    # Creación y progreso de encargos
│   ├── EstadoJuego.java       # Estado persistente
│   ├── GuardarCargar.java     # Lectura y escritura de partidas
│   └── UI.java                # Interfaz de terminal
├── CHANGELOG.md
└── README.md
```

El código emplea una jerarquía común para personajes, fábricas para enemigos
y misiones, enumeraciones para estados y tipos, y un generador de azar
centralizado. Todo vive en el paquete por defecto para mantener una compilación
directa y sin configuración adicional.

## Estado del proyecto

El juego es funcional y se encuentra en desarrollo. Incluye el bucle completo
de aldea, contratación, expedición, combate por compañías, progresión y jefe final; consulta
[`CHANGELOG.md`](CHANGELOG.md) para ver las incorporaciones más recientes.
