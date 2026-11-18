package token;
/* Enumeracion con todos los tipos de token que reconoce la calculadora.
 * Cada simbolo valido de una expresion aritmetica pertenece a una de estas categorias.
 * El Lexer clasifica los caracteres con estos tipos y el Parser decide con base en ellos.
 */
public enum Componentes {
	NUMEROS,              // Secuencia de digitos, por ejemplo: 8, 42, 100
	SUMA,                 // Operador +
	RESTA,                // Operador -
	MULTIPLICACION,       // Operador *
	DIVISION,             // Operador /
	PARENTESIS_IZQUIERDO, // Simbolo (
	PARENTESIS_DERECHO,   // Simbolo )
	FIN                   // Marca artificial que indica que ya no hay mas entrada
}
 