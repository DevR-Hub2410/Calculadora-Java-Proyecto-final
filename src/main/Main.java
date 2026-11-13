package main;

import java.util.List;
import java.util.Scanner;

import analizador.Lexer;
import analizador.Parser;
import token.Token;

public class Main {
	
	private static void resultado(String operacion, boolean vertokens) {
		try {
			Lexer lexer = new Lexer(operacion);
			List<Token> tokens = lexer.analizador();
			Parser parser = new Parser(tokens);
			double resultado = parser.analizarExpresion();
			
			if(vertokens) {
				System.out.println("TOKENS: ");
				
				for(Token token : tokens) {
					System.out.println(token.getTipo());
				}
			}
			
			System.out.println(operacion + " = " + resultado);
		}catch(RuntimeException e) {
			System.out.println("--Error: " + e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		String operacion;
		
		while(true) {
			System.out.print("Ingresa una operacion: ");
			operacion = s.nextLine();
			
			if(operacion.contains("salir")) {
				break;
			}
			
			resultado(operacion, false);
		}
		
		s.close();
	}

}
