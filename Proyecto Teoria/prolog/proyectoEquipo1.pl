% Proyecto final Teoria de Lenguajes de Programación
% EQUIPO 01  Analizador y validador de expresiones
% Integrantes:
% 		Gael Alberto Gonzalez Guzman - 261768
% 		Rafael Castillo Villalba - 257080
% 		Sergio Alejandro Solis Astorga - 262545
% 		Luis Angel Cruz Lopez - 257095

% =====================================================================
% FLUJO GENERAL (igual que la version en Java: Lexer -> Parser)
%
%   texto --tokenizar/2--> lista de tokens --analizarTokens/2--> resultado
%
% Los tokens son:  '+'  '-'  '*'  '/'  '('  ')'   y   num(Valor) para los numeros.
% Todos los errores se reportan con  throw(error(Mensaje)),  con los mismos
% mensajes que usa la version en Java.
% =====================================================================

% Regla principal: recibe el texto de la operacion, evalua su resultado numerico
% y atrapa cualquier error de sintaxis o division por cero para mostrarlo.
evaluar(ExpresionTexto, ResultadoFinal) :-
    catch(
        calcular(ExpresionTexto, ResultadoFinal),
        Error,
        (mostrarError(Error), fail)
    ).

% Nucleo: convierte el texto en tokens, valida la sintaxis y calcula el resultado.
% No atrapa los errores: los lanza para que evaluar/2 (o las pruebas) los atrapen.
calcular(ExpresionTexto, Resultado) :-
    % 1. Convierte el texto de entrada en una lista de caracteres individuales
    atom_string(AtomoTexto, ExpresionTexto),
    atom_chars(AtomoTexto, Caracteres),
    % 2. Analisis lexico: caracteres -> tokens (aqui se detectan los caracteres invalidos)
    tokenizar(Caracteres, Tokens),
    % 3. Analisis sintactico y evaluacion: tokens -> numero
    analizarTokens(Tokens, Resultado).

% Muestra en la consola el mensaje detallado cuando se atrapa una excepcion.
% La segunda regla cubre errores inesperados de Prolog (por ejemplo, un desbordamiento
% numerico) para que nunca fallen en silencio.
mostrarError(error(Mensaje)) :-
    !,
    format('~nError: ~w~n', [Mensaje]).
mostrarError(Otro) :-
    format('~nError inesperado: ~w~n', [Otro]).


% =====================================================================
% ANALISIS LEXICO: texto -> tokens
% =====================================================================

% Recorre la lista de caracteres y produce la lista de tokens.
% Los espacios en blanco se ignoran pero SEPARAN tokens: "5 5" produce dos tokens
% num(5) num(5), y mas adelante el analisis sintactico detecta que falta un operador.
tokenizar([], []).

% Espacios, tabuladores, etc.: no generan token
tokenizar([Caracter | Resto], Tokens) :-
    char_type(Caracter, space),
    !,
    tokenizar(Resto, Tokens).

% Operadores y parentesis: el propio caracter es el token
tokenizar([Caracter | Resto], [Caracter | Tokens]) :-
    member(Caracter, ['+', '-', '*', '/', '(', ')']),
    !,
    tokenizar(Resto, Tokens).

% Digitos o punto decimal: comienza un numero
tokenizar([Caracter | Resto], [num(Numero) | Tokens]) :-
    caracterNumerico(Caracter),
    !,
    consumirDigitos([Caracter | Resto], DigitosCaracteres, Sobrante),
    convertirNumero(DigitosCaracteres, Numero),
    tokenizar(Sobrante, Tokens).

% Cualquier otro caracter no pertenece al lenguaje
tokenizar([Caracter | _], _) :-
    format(atom(Mensaje), 'Caracter invalido: ~w', [Caracter]),
    throw(error(Mensaje)).

% Un caracter numerico es un digito o el punto decimal
caracterNumerico(Caracter) :-
    (   char_type(Caracter, digit(_))
    ;   Caracter == '.'
    ),
    !.

% Junta los digitos o el punto decimal que forman un numero
consumirDigitos([Cabeza | ColaCaracteres], [Cabeza | DigitosRestantes], CaracteresSalida) :-
    caracterNumerico(Cabeza),
    !,
    consumirDigitos(ColaCaracteres, DigitosRestantes, CaracteresSalida).

% Termina cuando se acaba el numero
consumirDigitos(CaracteresEntrada, [], CaracteresEntrada).

% Convierte los caracteres de un numero a su valor numerico.
% Valida que tenga a lo mucho un punto y al menos un digito ("1.2.3" y "." son invalidos)
convertirNumero(Caracteres, Numero) :-
    atom_chars(TextoOriginal, Caracteres),
    (   numeroBienFormado(Caracteres),
        completarNumero(Caracteres, Completo),
        atom_chars(AtomoNumero, Completo),
        atom_number(AtomoNumero, ValorNumerico)
    ->  Numero = ValorNumerico
    ;   format(atom(Mensaje), 'Numero invalido: ~w', [TextoOriginal]),
        throw(error(Mensaje))
    ).

% Bien formado: a lo mucho un punto decimal y al menos un digito
numeroBienFormado(Caracteres) :-
    include(==('.'), Caracteres, Puntos),
    length(Puntos, CantidadPuntos),
    CantidadPuntos =< 1,
    exclude(==('.'), Caracteres, Digitos),
    Digitos \== [].

% Agrega un cero al inicio (".5" -> "0.5") o al final ("5." -> "5.0") si hace falta
completarNumero(Caracteres, Completo) :-
    (   Caracteres = ['.' | _]
    ->  Inicio = ['0' | Caracteres]
    ;   Inicio = Caracteres
    ),
    (   last(Inicio, '.')
    ->  append(Inicio, ['0'], Completo)
    ;   Completo = Inicio
    ).


% =====================================================================
% ANALISIS SINTACTICO Y JERARQUIA DE OPERACIONES: tokens -> resultado
% =====================================================================

% Punto de entrada: evalua la expresion completa y despues verifica que no sobre nada
analizarTokens(Tokens, Resultado) :-
    expresion(Resultado, Tokens, TokensRestantes),
    verificarFin(TokensRestantes).

% Despues de evaluar solo debe quedar la lista vacia; si queda algo, esta mal formada
verificarFin([]) :- !.
verificarFin([')' | _]) :-
    !,
    throw(error('Falta el parentesis izquierdo!')).
verificarFin(_) :-
    throw(error('Falta un operador!')).


% NIVEL 1: Expresion (Maneja Sumas '+' y Restas '-' - Menor prioridad)
expresion(Resultado, TokensEntrada, TokensSalida) :-
    termino(Termino1, TokensEntrada, TokensRestantes),
    expresionTail(Termino1, Resultado, TokensRestantes, TokensSalida).

% Suma los siguientes valores al acumulador
expresionTail(Acumulador, Resultado, ['+' | ColaTokens], TokensSalida) :-
    !,
    termino(Termino2, ColaTokens, TokensRestantes),
    AcumuladorNuevo is Acumulador + Termino2,
    expresionTail(AcumuladorNuevo, Resultado, TokensRestantes, TokensSalida).

% Resta los siguientes valores al acumulador
expresionTail(Acumulador, Resultado, ['-' | ColaTokens], TokensSalida) :-
    !,
    termino(Termino2, ColaTokens, TokensRestantes),
    AcumuladorNuevo is Acumulador - Termino2,
    expresionTail(AcumuladorNuevo, Resultado, TokensRestantes, TokensSalida).

% Termina cuando ya no hay mas sumas ni restas
expresionTail(Acumulador, Acumulador, TokensEntrada, TokensEntrada).


% NIVEL 2: Termino (Maneja Multiplicaciones '*' y Divisiones '/' - Mayor prioridad)
termino(Resultado, TokensEntrada, TokensSalida) :-
    factor(Factor1, TokensEntrada, TokensRestantes),
    terminoTail(Factor1, Resultado, TokensRestantes, TokensSalida).

% Multiplica por el siguiente factor
terminoTail(Acumulador, Resultado, ['*' | ColaTokens], TokensSalida) :-
    !,
    factor(Factor2, ColaTokens, TokensRestantes),
    AcumuladorNuevo is Acumulador * Factor2,
    terminoTail(AcumuladorNuevo, Resultado, TokensRestantes, TokensSalida).

% Divide entre el siguiente factor validando que no sea cero
terminoTail(Acumulador, Resultado, ['/' | ColaTokens], TokensSalida) :-
    !,
    factor(Factor2, ColaTokens, TokensRestantes),
    (   Factor2 =:= 0
    ->  throw(error('No se puede dividir entre 0!'))
    ;   AcumuladorNuevo is Acumulador / Factor2,
        terminoTail(AcumuladorNuevo, Resultado, TokensRestantes, TokensSalida)
    ).

% Termina cuando ya no hay mas multiplicaciones ni divisiones
terminoTail(Acumulador, Acumulador, TokensEntrada, TokensEntrada).


% NIVEL 3: Factor (Maneja Parentesis '(' ')' recursivamente y numeros base)
% Resuelve primero lo que esta dentro de los parentesis
factor(Resultado, ['(' | ColaTokens], TokensSalida) :-
    !,
    (   ColaTokens = [')' | _]
    ->  throw(error('No se permiten parentesis vacios!'))
    ;   true
    ),
    expresion(Resultado, ColaTokens, TokensRestantes),
    (   TokensRestantes = [')' | TokensSalida]
    ->  true
    ;   throw(error('Falta el parentesis derecho!'))
    ).

% Lee un numero normal
factor(Numero, [num(Valor) | TokensSalida], TokensSalida) :-
    !,
    Numero = Valor.

% Donde se esperaba un operando aparece un operador (ej. "5 + * 2")
factor(_, [Operador | _], _) :-
    member(Operador, ['+', '-', '*', '/']),
    !,
    throw(error('Operadores consecutivos!')).

% Cualquier otra cosa (fin de la entrada o un ")" inesperado) no es un operando valido
factor(_, _, _) :-
    throw(error('Se esperaba un numero o una expresion!')).


% =====================================================================
% PUNTO DE ENTRADA PARA JAVA
% =====================================================================
% Java ejecuta:  swipl -q -g principal -t halt proyectoEquipo1.pl "expresion"
% Lee la expresion recibida como argumento, la evalua con evaluar/2 e imprime
% el resultado. Si hay error, evaluar/2 ya imprime el mensaje por si misma.
principal :-
    current_prolog_flag(argv, Argumentos),
    last(Argumentos, Expresion),
    (   evaluar(Expresion, Resultado)
    ->  format('~w = ~w~n', [Expresion, Resultado])
    ;   true
    ).
