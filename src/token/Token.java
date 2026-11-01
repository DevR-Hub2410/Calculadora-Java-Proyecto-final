package token;

public class Token {
	private Componentes tipo;
	private String valor;
	
	public Token(Componentes tipo, String valor) {
		this.tipo = tipo;
		this.valor = valor;
	}
	
	public Componentes getTipo() {
		return tipo;
	}
	
	public void setTipo(Componentes tipo) {
		this.tipo = tipo;
	}
	
	public String getValor() {
		return valor;
	}
	
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	

}
