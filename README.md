# Analizador y validador de expresiones — Equipo 01

Proyecto final de Teoría de Lenguajes de Programación.

## Integrantes

- Gael Alberto Gonzalez Guzman — 261768
- Rafael Castillo Villalba — 257080
- Sergio Alejandro Solis Astorga — 262545
- Luis Angel Cruz Lopez — 257095

Programa que recibe expresiones aritméticas (números, `+`, `-`, `*`, `/` y paréntesis),
comprueba que estén bien formadas y las evalúa respetando la prioridad de operaciones.
Si hay un error (paréntesis, operadores, caracteres inválidos, división entre cero)
muestra un mensaje claro en lugar de un resultado.

La misma expresión se procesa con **dos implementaciones** para poder compararlas:

| Implementación | Archivos | Rol |
|---|---|---|
| Java | `src/analizador/Lexer.java`, `Parser.java`, `src/main/Main.java`, `src/token/*` | Interfaz de consola, análisis léxico y sintáctico por descenso recursivo |
| Prolog | `prolog/proyectoEquipo1.pl` | Reglas de validación y cálculo (archivo `.pl` obligatorio del curso) |

## Estructura

```
Proyecto Teoria/
├── src/
│   ├── analizador/   Lexer.java, Parser.java
│   ├── main/         Main.java
│   └── token/        Componentes.java, Token.java
├── prolog/
│   ├── proyectoEquipo1.pl   reglas centrales en Prolog
│   └── pruebas.pl           pruebas automáticas del .pl
├── bin/                     clases compiladas
└── README.md
```

## Requisitos

- JDK 8 o superior (probado con JDK 21).
- SWI-Prolog instalado y con el comando `swipl` disponible en el PATH.
  Si no está, el programa Java sigue funcionando y solo muestra
  `[Prolog] No se pudo ejecutar SWI-Prolog`.

## Cómo ejecutar

**Importante:** todos los comandos se ejecutan desde la carpeta raíz del proyecto
(la que contiene `src` y `prolog`), porque Java busca el archivo en `prolog/proyectoEquipo1.pl`.

### 1. Programa completo (Java + Prolog)

Desde el IDE: ejecutar `src/main/Main.java`.

Desde la terminal:

```
javac -encoding UTF-8 -d bin src/token/Componentes.java src/token/Token.java src/analizador/Lexer.java src/analizador/Parser.java src/main/Main.java
java -cp bin main.Main
```

Escribir una operación y presionar Enter. Se muestra el resultado de Java (`[Java]`)
y el de Prolog (`[Prolog]`). Para terminar, escribir `Salir`.

### 2. Solo Prolog, en la consola de SWI-Prolog

```
swipl prolog/proyectoEquipo1.pl
?- evaluar('(8 + 4) / 3', R).
R = 4.
?- evaluar('8 + (4 / 0)', R).

Error: No se puede dividir entre 0!
false.
```

### 3. Pruebas automáticas de Prolog

```
swipl -q -g correr_pruebas -t halt prolog/pruebas.pl
```

Imprime `[OK]` o `[FALLO]` por cada caso y un resumen al final.

## Ejemplos de ejecución

Ambas implementaciones dan el mismo resultado y los mismos mensajes.
(Solo cambia el formato del número: Java imprime `4.0` y Prolog `4`.)

| Expresión | Resultado / mensaje |
|---|---|
| `(8 + 4) / 3` | 4 |
| `2 + 3 * 4` | 14 |
| `(2 + 3) * 4` | 20 |
| `10 / 4` | 2.5 |
| `2 * (3 + 4) * 5` | 70 |
| `(((2)))` | 2 |
| `8 + (4 / 0)` | `No se puede dividir entre 0!` |
| `8 / (4 - 4)` | `No se puede dividir entre 0!` |
| `5 + * 2` | `Operadores consecutivos!` |
| `5 5` | `Falta un operador!` |
| `5 +` | `Se esperaba un numero o una expresion!` |
| `(5` | `Falta el parentesis derecho!` |
| `5)` | `Falta el parentesis izquierdo!` |
| `()` | `No se permiten parentesis vacios!` |
| `2 ^ 3` | `Caracter invalido: ^` |

La lista completa de casos (normales y límite) está en `prolog/pruebas.pl`.

## Gramática implementada

```
expresion -> termino  ( ('+' | '-') termino )*      menor prioridad
termino   -> factor   ( ('*' | '/') factor )*       mayor prioridad
factor    -> NUMERO | '(' expresion ')'             los paréntesis reinician la jerarquía
```

## Manejo de errores

- **Java:** `Lexer` y `Parser` lanzan `RuntimeException` con un mensaje; `Main` las atrapa con `try/catch`.
- **Prolog:** las reglas lanzan `throw(error(Mensaje))`; `evaluar/2` las atrapa con `catch/3` y las muestra.
