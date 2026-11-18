package analizador;

import java.util.List;

import token.Componentes;
import token.Token;
 
 /* analizador sintatico (Parser) y evaluador.
 * Segunda etapa: recibe la lista de tokens del Lexer, verifica que la expresion
 * este bien formada y calcula su resultado al mismo tiempo.
 *
 * Usa descenso recursivo, cada nivel de prioridad es un metodo, y cada metodo llama
 * al de mayor prioridad. La gramatica implementada es:
 *
 *   expresion  -> sumaResta FIN
 *   sumaResta  -> multDiv  ( (+ | -) multDiv )*     (menor prioridad)
 *   multDiv    -> numero   ( (* | /) numero )*      (mayor prioridad)
 *   numero     -> NUMEROS | ( sumaResta )           (parentesis reinician la jerarquia)
 *
 * Como "* y /" se resuelven mas adentro que "+ y -", se respeta la prioridad
 * de operaciones sin necesidad de codigo adicional.
 */
public class Parser {
	// Lista de tokens producida por el Lexer
	private List<Token> tokens;
	// Indice del token que se esta analizando
	private int posicion = 0;
	
	// Constructor: recibe los tokens a analizar
	public Parser(List<Token> tokens) {
		this.tokens = tokens;
	}
	
	// Devuelve el token en la posicion actual sin avanzar.
	// Si ya se acabaron los tokens, la expresion esta incompleta.
	private Token obtenerTokenActual() {
		if (posicion >= tokens.size()) {
	        throw new RuntimeException("Expresion incompleta!");
	    }
		return tokens.get(posicion);
	}
	
	// Consume el token actual: mueve el cursor al siguiente
	private void avanzarToken() {
		posicion++;
	}
	 /* punto de entrada del Parser. Analiza toda la expresion y devuelve su resultado.
	 * Despues de evaluar, el unico token que debe quedar es FIN; si queda otra cosa,
	 * significa que la expresion estaba mal formada.
	 */
	public double analizarExpresion() {
		// Evalua la expresion completa empezando por el nivel de menor prioridad
		double resultado = analizarSumaResta();
	    // Caso correcto: se llego al final de la entrada
	    if(obtenerTokenActual().getTipo() == Componentes.FIN) {
	        avanzarToken();
	        return resultado;
	    }
	    // Sobro un ")" que no tiene su "(" de apertura
	    if(obtenerTokenActual().getTipo() == Componentes.PARENTESIS_DERECHO) {
	        throw new RuntimeException("Falta el parentesis izquierdo!");
	    }
 
	    // Sobro cualquier otro token (por ejemplo dos numeros seguidos: "5 5")
	    throw new RuntimeException("Falta un operador!");
	}
	 /* nivel 1 (menor prioridad): sumas y restas.
	 * Resuelve primero un termino y luego va acumulando los siguientes + o -
	 * de izquierda a derecha.
	 */
	public double analizarSumaResta() {
		// El primer operando es un termino (que puede incluir * y /)
		double resultado = analizarMultiplicacionDivision();
		
		// Mientras el siguiente token sea + o -, se sigue acumulando
		while(posicion < tokens.size()) {
			Token token = obtenerTokenActual();
			
			if(token.getTipo() == Componentes.SUMA) {
				avanzarToken(); // consume el "+"
				resultado += analizarMultiplicacionDivision();
			}else if(token.getTipo() == Componentes.RESTA) {
				avanzarToken(); // consume el "-"
				resultado -= analizarMultiplicacionDivision();
			}else {
				// Ya no hay + ni -: termina este nivel
				break;
			}
		}
		
		return resultado;
	}
	/* nivel 2 (mayor prioridad): multiplicaciones y divisiones.
	 * Igual que el nivel anterior, pero con * y /, y valida la division entre cero.
	 */
	private double analizarMultiplicacionDivision() {
		// El primer operando es un numero o una expresion entre parentesis
		double resultado = analizarNumero();
		
		// Mientras el siguiente token sea * o /, se sigue acumulando
		while(posicion < tokens.size()) {
			Token token = obtenerTokenActual();
			
			if(token.getTipo() == Componentes.MULTIPLICACION) {
				avanzarToken(); // consume el "*"
				resultado *= analizarNumero();
			}else if(token.getTipo() == Componentes.DIVISION) {
				avanzarToken(); // consume el "/"
				double valor = analizarNumero();
				// Error controlado: no se permite dividir entre cero
				if(valor == 0) {
					throw new RuntimeException("No se puede dividir entre 0!");
				}
				resultado /= valor;
			}else {
				// Ya no hay * ni /: termina este nivel
				break;
			}
		}
		
		return resultado;
	}
	/* nivel 3 (base): numeros y parentesis.
	 * Aqui se leen los operandos. Si encuentra un "(" vuelve a empezar la jerarquia
	 * completa con analizarSumaResta() (recursividad), por eso lo que esta entre
	 * parentesis se resuelve primero.
	 */
	private double analizarNumero() {
		Token token = obtenerTokenActual();
		
		// Si en lugar de un operando aparece un operador, hay operadores consecutivos (ej. "5 + * 2")
		if(token.getTipo() == Componentes.SUMA || token.getTipo() == Componentes.RESTA || token.getTipo() == Componentes.MULTIPLICACION || token.getTipo() == Componentes.DIVISION) {
			throw new RuntimeException("Operadores consecutivos!");
		}
		
		// Caso base: un numero. Se consume el token y se convierte su texto a double
		if(token.getTipo() == Componentes.NUMEROS) {
			avanzarToken();
			return Double.parseDouble(token.getValor());		
		}
		
		// Caso recursivo: una subexpresion entre parentesis
		if(token.getTipo() == Componentes.PARENTESIS_IZQUIERDO) {
			avanzarToken(); // consume el "("
			
			// Valida que los parentesis no esten vacios: "()"
			if (obtenerTokenActual().getTipo() == Componentes.PARENTESIS_DERECHO) {
		        throw new RuntimeException("No se permiten parentesis vacios!");
		    }
 
		    // Evalua lo de adentro reiniciando desde el nivel de menor prioridad
		    double resultado = analizarSumaResta();
 
		    // Despues de la subexpresion debe venir obligatoriamente el ")"
		    if (obtenerTokenActual().getTipo() != Componentes.PARENTESIS_DERECHO) {
		        throw new RuntimeException("Falta el parentesis derecho!");
		    }
			
			avanzarToken(); // consume el ")"
			
			return resultado;
		}
		
		// Cualquier otro token (por ejemplo FIN o un ")" inesperado) no es un operando valido
		throw new RuntimeException("Se esperaba un numero o una expresion!");
	}
}