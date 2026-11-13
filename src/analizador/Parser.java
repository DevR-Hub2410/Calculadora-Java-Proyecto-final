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
	
	private Token obtenerTokenActual() {
		if (posicion >= tokens.size()) {
	        throw new RuntimeException("Expresion incompleta!");
	    }
		return tokens.get(posicion);
	}
	
	private void avanzarToken() {
		posicion++;
	}
	
	public double analizarExpresion() {
		double resultado = analizarSumaResta();
		if(posicion < tokens.size()) {
	        if(obtenerTokenActual().getTipo() == Componentes.PARENTESIS_DERECHO) {
	            throw new RuntimeException("Falta el parentesis izquierdo!");
	        }
	        throw new RuntimeException("Falta un operador!");
	    }
		return resultado;
	}
	
	public double analizarSumaResta() {
		double resultado = analizarMultiplicacionDivision();
		
		while(posicion < tokens.size()) {
			Token token = obtenerTokenActual();
			
			if(token.getTipo() == Componentes.SUMA) {
				avanzarToken();
				resultado += analizarMultiplicacionDivision();
			}else if(token.getTipo() == Componentes.RESTA) {
				avanzarToken();
				resultado -= analizarMultiplicacionDivision();
			}else {
				break;
			}
		}
		
		return resultado;
	}
	
	private double analizarMultiplicacionDivision() {
		double resultado = analizarNumero();
		
		while(posicion < tokens.size()) {
			Token token = obtenerTokenActual();
			
			if(token.getTipo() == Componentes.MULTIPLICACION) {
				avanzarToken();
				resultado *= analizarNumero();
			}else if(token.getTipo() == Componentes.DIVISION) {
				avanzarToken();
				double valor = analizarNumero();
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
	
	private double analizarNumero() {
		Token token = obtenerTokenActual();
		
		if(token.getTipo() == Componentes.SUMA || token.getTipo() == Componentes.RESTA || token.getTipo() == Componentes.MULTIPLICACION || token.getTipo() == Componentes.DIVISION) {
			throw new RuntimeException("Operadores consecutivos!");
		}
		
		if(token.getTipo() == Componentes.NUMEROS) {
			avanzarToken();
			return Double.parseDouble(token.getValor());		
		}
		
		if(token.getTipo() == Componentes.PARENTESIS_IZQUIERDO) {
			avanzarToken();
			
			if (obtenerTokenActual().getTipo() == Componentes.PARENTESIS_DERECHO) {
		        throw new RuntimeException("No se permiten parentesis vacios!");
		    }

		    double resultado = analizarSumaResta();

		    if (obtenerTokenActual().getTipo() != Componentes.PARENTESIS_DERECHO) {
		        throw new RuntimeException("Falta el parentesis derecho!");
		    }
			
			avanzarToken();
			
			return resultado;
		}
		
		throw new RuntimeException("Se esperaba un numero o una expresion!");
	}

}
