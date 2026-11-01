package analizador;

import java.util.List;

import token.Componentes;
import token.Token;

public class Parser {
	private List<Token> tokens;
	private int posicion = 0;
	
	public Parser(List<Token> tokens) {
		this.tokens = tokens;
	}
	
	private Token actual() {
		return tokens.get(posicion);
	}
	
	private void siguiente() {
		posicion++;
	}
	
	public double parse() {
		double resultado = operacion();
		if(posicion < tokens.size()) {
	        if(actual().getTipo() == Componentes.PARENTESIS_DERECHO) {
	            throw new RuntimeException("Falta el parentesis izquierdo!");
	        }
	    }
		return resultado;
	}
	
	// Se evalua al final suma o resta
	public double operacion() {
		double resultado = muldiv();
		
		while(posicion < tokens.size()) {
			Token token = actual();
			
			if(token.getTipo() == Componentes.SUMA) {
				siguiente();
				resultado += muldiv();
			}else if(token.getTipo() == Componentes.RESTA) {
				siguiente();
				resultado -= muldiv();
			}else {
				break;
			}
		}
		
		return resultado;
	}
	
	// Se evalua primero si hay multiplicacion o division
	private double muldiv() {
		double resultado = numero();
		
		while(posicion < tokens.size()) {
			Token token = actual();
			
			if(token.getTipo() == Componentes.MULTIPLICACION) {
				siguiente();
				resultado *= numero();
			}else if(token.getTipo() == Componentes.DIVISION) {
				siguiente();
				double valor = numero();
				if(valor == 0) {
					throw new RuntimeException("No se puede dividir entre 0!");
				}
				resultado /= valor;
			}else {
				break;
			}
		}
		
		return resultado;
	}
	
	private double numero() {
		Token token = actual();
		
		if(token.getTipo() == Componentes.SUMA || token.getTipo() == Componentes.RESTA || token.getTipo() == Componentes.MULTIPLICACION || token.getTipo() == Componentes.DIVISION) {
			throw new RuntimeException("Operadores consecutivos!");
		}
		
		if(token.getTipo() == Componentes.NUMEROS) {
			siguiente();
			return Double.parseDouble(token.getValor());		
		}
		
		// Parentesis primero
		if(token.getTipo() == Componentes.PARENTESIS_IZQUIERDO) {
			siguiente();
			
			double resultado = operacion();
			if(actual().getTipo() != Componentes.PARENTESIS_DERECHO) {
				throw new RuntimeException("Falta el parentesis derecho!");
			}
			
			siguiente();
			
			return resultado;
		}
		
		throw new RuntimeException("Se esperaba un numero o una expresion!");
	}

}
