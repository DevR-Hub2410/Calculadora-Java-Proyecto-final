/* Proyecto final Teoria de Lenguajes de Programación
 * EQUIPO 01  Analizador y validador de expresiones 
 * Integrantes:
 * 		Gael Alberto Gonzalez Guzman - 261768
 * 		Rafael Castillo Villalba - 257080
 * 		Sergio Alejandro Solis Astorga - 262545
 * 		Luis Angel Cruz Lopez - 257095
 */

package main;
 
import analizador.Lexer;
import analizador.Parser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;
import token.Token;

/* Clase principal: interfaz de consola de la calculadora.
 * Pide expresiones al usuario en un ciclo, las procesa con el Lexer y el Parser,
 * y muestra el resultado o un mensaje de error. Se termina escribiendo "salir".
 */
public class Main {
	
	 /* Procesa una operacion completa: Lexer -> Parser -> resultado.
	 * Atrapa las excepciones (RuntimeException) que lanzan el Lexer y el Parser
	 * para mostrar un mensaje claro en lugar de cerrar el programa.
	 *
	 * @param operacion texto escrito por el usuario
	 * @param vertokens si es true, tambien imprime la lista de tokens generados
	 */
	private static void resultado(String operacion, boolean vertokens) {
		try {
			// Etapa 1: analisis lexico (texto -> tokens)
			Lexer lexer = new Lexer(operacion);
			List<Token> tokens = lexer.analizador();
			// Etapa 2: analisis sintactico y evaluacion (tokens -> numero)
			Parser parser = new Parser(tokens);
			double resultado = parser.analizarExpresion();
			
			// Modo depuracion opcional: muestra los tipos de token reconocidos
			if(vertokens) {
				System.out.println("TOKENS: ");
				
				for(Token token : tokens) {
					System.out.println(token.getTipo());
				}
			}
			
			// Muestra la operacion junto con su resultado
			System.out.println(operacion + " = " + resultado);
		}catch(RuntimeException e) {
			// Manejo de errores: caracter invalido, parentesis, operadores, division entre 0, etc.
			System.out.println("--Error: " + e.getMessage());
		}
	}
	 /* Evalua la operacion con Prolog: ejecuta SWI-Prolog como proceso externo,
	 * le pasa la expresion como argumento y muestra lo que Prolog imprime.
	 * Las reglas de validacion y calculo viven en proyectoEquipo1.pl.
	 */
	private static void resultadoProlog(String operacion) {
		try {
			ProcessBuilder pb = new ProcessBuilder("swipl", "-q", "-g", "principal", "-t", "halt", "prolog/proyectoEquipo1.pl", operacion);
			// Une la salida de errores con la salida normal para leerlas juntas
			pb.redirectErrorStream(true);
			Process proceso = pb.start();
			
			// Lee todo lo que Prolog imprimio
			BufferedReader lector = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
			String linea;
			boolean huboSalida = false;
			while((linea = lector.readLine()) != null) {
				if(!linea.trim().isEmpty()) {
					System.out.println("[Prolog] " + linea.trim());
					huboSalida = true;
				}
			}
			proceso.waitFor();
			
			// Si Prolog no imprimio nada, la expresion no fue valida
			if(!huboSalida) {
				System.out.println("[Prolog] Error: expresion invalida");
			}
		}catch(Exception e) {
			// Si no se encuentra con el codigo pl
			System.out.println("[Prolog] No se pudo ejecutar SWI-Prolog: " + e.getMessage());
		}
	}
	
	// Punto de entrada del programa
	public static void main(String[] args) {
		// Scanner para leer lo que el usuario escribe en consola
		Scanner s = new Scanner(System.in);
		String operacion;
		System.out.println("Bienvenido!");
		
		System.out.print("Nota: Para poner fin al programa introduce 'Salir'.");

		// Ciclo infinito: se repite hasta que el usuario escriba "salir"
		while(true) {
			System.out.print("Ingresa una operacion: ");
			operacion = s.nextLine();
			
			// Condicion de salida
			if((operacion.contains("salir")) || operacion.contains("Salir")) {
				System.out.println("Saliendo del programa...");
				break;
			}
			
			// Procesa la expresion con Java (false = no mostrar tokens)
			System.out.print("[Java]   ");
			resultado(operacion, false);
			// Procesa la misma expresion con Prolog para comparar
			resultadoProlog(operacion);
		}
		// Libera el recurso de entrada
		s.close();
	}
}
