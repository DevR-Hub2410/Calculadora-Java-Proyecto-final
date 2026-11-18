% Pruebas automaticas del analizador de expresiones - EQUIPO 01
%
% Uso, desde la raiz del proyecto:
%     swipl -q -g correr_pruebas -t halt prolog/pruebas.pl
%
% o dentro de la consola de SWI-Prolog:
%     ?- [prolog/pruebas].
%     ?- correr_pruebas.
%
% Cada caso(Expresion, Esperado) indica lo que debe pasar:
%     ok(Valor)       -> la expresion es valida y da ese resultado
%     error(Mensaje)  -> la expresion es invalida y se debe reportar ese mensaje

:- ensure_loaded(proyectoEquipo1).

% ---------------------------------------------------------------------
% Casos normales
% ---------------------------------------------------------------------
caso('(8 + 4) / 3',      ok(4)).       % ejemplo de la demostracion sugerida
caso('2 + 3 * 4',        ok(14)).      % prioridad: * antes que +
caso('(2 + 3) * 4',      ok(20)).      % los parentesis cambian la prioridad
caso('10 / 4',           ok(2.5)).
caso('100 - 50 - 25',    ok(25)).      % asociatividad izquierda
caso('2 * (3 + 4) * 5',  ok(70)).
caso('(((2)))',          ok(2)).       % parentesis anidados
caso('1.5 + 2.5',        ok(4)).       % decimales
caso('.5 + 5.',          ok(5.5)).     % decimales sin parte entera / sin parte decimal
caso('  7   *   6  ',    ok(42)).      % espacios de sobra
caso('0 / 5',            ok(0)).       % cero como dividendo si es valido

% ---------------------------------------------------------------------
% Division entre cero
% ---------------------------------------------------------------------
caso('8 + (4 / 0)',      error('No se puede dividir entre 0!')).   % ejemplo de la demostracion
caso('5 / 0',            error('No se puede dividir entre 0!')).
caso('8 / (4 - 4)',      error('No se puede dividir entre 0!')).

% ---------------------------------------------------------------------
% Operadores
% ---------------------------------------------------------------------
caso('5 + * 2',          error('Operadores consecutivos!')).       % ejemplo de la demostracion
caso('3 +* 4',           error('Operadores consecutivos!')).
caso('5 5',              error('Falta un operador!')).             % antes daba 55
caso('2 3 + 4',          error('Falta un operador!')).
caso('(5)(3)',           error('Falta un operador!')).
caso('5 +',              error('Se esperaba un numero o una expresion!')).
caso('',                 error('Se esperaba un numero o una expresion!')).

% ---------------------------------------------------------------------
% Parentesis
% ---------------------------------------------------------------------
caso('(5',               error('Falta el parentesis derecho!')).
caso('((5)',             error('Falta el parentesis derecho!')).
caso('5)',               error('Falta el parentesis izquierdo!')).
caso('()',               error('No se permiten parentesis vacios!')).

% ---------------------------------------------------------------------
% Caracteres y numeros invalidos
% ---------------------------------------------------------------------
caso('abc',              error('Caracter invalido: a')).
caso('2 ^ 3',            error('Caracter invalido: ^')).
caso('1.2.3',            error('Numero invalido: 1.2.3')).
caso('.',                error('Numero invalido: .')).


% ---------------------------------------------------------------------
% Ejecucion de las pruebas
% ---------------------------------------------------------------------
correr_pruebas :-
    findall(Expresion-Esperado, caso(Expresion, Esperado), Casos),
    length(Casos, Total),
    probarTodos(Casos, 0, Fallos),
    Aprobadas is Total - Fallos,
    format('~nResumen: ~d de ~d pruebas correctas~n', [Aprobadas, Total]).

% Recorre los casos y cuenta cuantos fallaron
probarTodos([], Fallos, Fallos).
probarTodos([Expresion-Esperado | Resto], Acumulado, Fallos) :-
    (   probar(Expresion, Esperado)
    ->  NuevoAcumulado = Acumulado
    ;   NuevoAcumulado is Acumulado + 1
    ),
    probarTodos(Resto, NuevoAcumulado, Fallos).

% Evalua una expresion y compara con lo esperado; falla si no coincide
probar(Expresion, Esperado) :-
    (   obtener(Expresion, Obtenido)
    ->  true
    ;   Obtenido = sin_respuesta
    ),
    descripcion(Obtenido, TextoObtenido),
    (   coincide(Esperado, Obtenido)
    ->  format('[OK]    ~q  ->  ~w~n', [Expresion, TextoObtenido])
    ;   descripcion(Esperado, TextoEsperado),
        format('[FALLO] ~q  ->  ~w   (se esperaba: ~w)~n', [Expresion, TextoObtenido, TextoEsperado]),
        fail
    ).

% Ejecuta calcular/2 y convierte el resultado o la excepcion en ok(V) / error(M)
obtener(Expresion, Obtenido) :-
    catch(
        (   calcular(Expresion, Valor),
            Obtenido = ok(Valor)
        ),
        Excepcion,
        (   Excepcion = error(Mensaje)
        ->  Obtenido = error(Mensaje)
        ;   Obtenido = excepcion(Excepcion)
        )
    ).

% Compara lo esperado con lo obtenido
coincide(ok(Esperado), ok(Obtenido)) :-
    number(Obtenido),
    Obtenido =:= Esperado.
coincide(error(Esperado), error(Obtenido)) :-
    Esperado == Obtenido.

% Texto legible para mostrar en pantalla
descripcion(ok(Valor), Texto) :-
    !,
    format(atom(Texto), 'resultado ~w', [Valor]).
descripcion(error(Mensaje), Texto) :-
    !,
    format(atom(Texto), 'Error: ~w', [Mensaje]).
descripcion(Otro, Texto) :-
    format(atom(Texto), '~w', [Otro]).
