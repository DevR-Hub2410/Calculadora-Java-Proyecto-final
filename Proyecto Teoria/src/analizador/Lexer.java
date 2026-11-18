package analizador;
 
import java.util.ArrayList;
import java.util.List;
 
import token.Componentes;
import token.Token;
 
 /* ANALIZADOR LEXICO (Lexer).
 * Primera etapa del procesamiento: recibe el texto que escribio el usuario y lo
 * descompone en una lista de tokens. Por ejemplo, "(8 + 4)" se convierte en:
 *   PARENTESIS_IZQUIERDO, NUMEROS(8), SUMA, NUMEROS(4), PARENTESIS_DERECHO, FIN
 * 
 * Aqui se detectan los caracteres invalidos (letras, simbolos raros, etc.).
 */
public class Lexer {
	// Texto completo de la expresion que se va a analizar
	private String valor;
	// Indice del caracter que se esta leyendo en este momento
	private int posicion;
	
	// Constructor: guarda la expresion y coloca el cursor al inicio
	public Lexer(String valor) {
		this.valor = valor;
		this.posicion = 0;
	}
	/* Recorre la expresion caracter por caracter y devuelve la lista de tokens.
	 * Lanza RuntimeException si encuentra un caracter que no pertenece al lenguaje.
	 */
	public List<Token> analizador(){
		// Lista donde se iran acumulando los tokens reconocidos
		List<Token> tokens = new ArrayList<>();
		
		// Se repite hasta haber leido todos los caracteres de la expresion
		while(posicion < valor.length()) {
			// Caracter actual
			char caracter = valor.charAt(posicion);
			
			// Los espacios en blanco se ignoran: se avanza sin generar token
			if(Character.isWhitespace(caracter)) {
				posicion++;
				continue;
			}
			
			// Si el caracter es un digito (o un punto), comienza un numero
			if(Character.isDigit(caracter) || caracter == '.') {
				// Acumula los digitos consecutivos que forman un solo numero (ej. "125")
				StringBuilder numero = new StringBuilder();
				
				// Lee mientras siga habiendo caracteres
				while(posicion < valor.length()) {
					caracter = valor.charAt(posicion);
					
					// Si es digito o punto decimal, se agrega al numero y se avanza
					if(Character.isDigit(caracter) || caracter == '.') {
						numero.append(caracter);
						posicion++;
					}else {
						// Un caracter que no es digito termina el numero
						break;
					}
				}
				
				// Se crea el token numerico con el texto acumulado
				tokens.add(new Token(Componentes.NUMEROS, numero.toString()));
				// Se vuelve al inicio del ciclo principal sin pasar por el switch
				continue;
			}
			
			// Para el resto de caracteres se clasifica cada simbolo en su tipo de token
			switch(caracter) {
			case '+':
				tokens.add(new Token(Componentes.SUMA, "+"));
				break;
			case '-':
				tokens.add(new Token(Componentes.RESTA, "-"));
				break;
			case '*':
				tokens.add(new Token(Componentes.MULTIPLICACION, "*"));
				break;
			case '/':
				tokens.add(new Token(Componentes.DIVISION, "/"));
				break;
			case '(':
				tokens.add(new Token(Componentes.PARENTESIS_IZQUIERDO, "("));
				break;
			case ')':
				tokens.add(new Token(Componentes.PARENTESIS_DERECHO, ")"));
				break;
			default:
				// Cualquier otro caracter no es valido: se reporta el error
				throw new RuntimeException("Caracter invalido: " + caracter);
			}
			// Se avanza al siguiente caracter despues de procesar el simbolo
			posicion++;
		}
		
		// Se agrega el token FIN para que el Parser sepa donde termina la expresion
		tokens.add(new Token(Componentes.FIN, ""));
		return tokens;
	}
 
}