package analizador;

import java.util.ArrayList;
import java.util.List;

import token.Componentes;
import token.Token;

public class Lexer {
	private String valor;
	private int posicion;
	
	public Lexer(String valor) {
		this.valor = valor;
		this.posicion = 0;
	}
	
	public List<Token> analizador(){
		List<Token> tokens = new ArrayList<>();
		
		while(posicion < valor.length()) {
			char caracter = valor.charAt(posicion);
			
			if(Character.isWhitespace(caracter)) {
				posicion++;
				continue;
			}
			
			if(Character.isDigit(caracter) || caracter == '.') {
				StringBuilder numero = new StringBuilder();
				
				while(posicion < valor.length()) {
					caracter = valor.charAt(posicion);
					
					if(Character.isDigit(caracter)) {
						numero.append(caracter);
						posicion++;
					}else {
						break;
					}
				}
				
				tokens.add(new Token(Componentes.NUMEROS, numero.toString()));
				continue;
			}
			
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
				throw new RuntimeException("Caracter invalido: " + caracter);
			}
			posicion++;
		}
		
		tokens.add(new Token(Componentes.FIN, ""));
		return tokens;
	}

}
