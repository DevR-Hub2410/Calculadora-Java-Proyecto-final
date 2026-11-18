package token;
/* Representa un token: la unidad minima con significado dentro de una expresion.
 * Un token une dos datos:
 *   - tipo:  la categoria a la que pertenece (ver enum Componentes)
 *   - valor: el texto original que lo genero (por ejemplo "12" o "+")
 */
public class Token {
	// Categoria del token (NUMEROS, SUMA, RESTA, etc.)
	private Componentes tipo;
	// Texto exacto del token tal como aparecio en la expresion
	private String valor;
	
	// Constructor: crea un token con su tipo y su valor
	public Token(Componentes tipo, String valor) {
		this.tipo = tipo;
		this.valor = valor;
	}
	
	// Devuelve la categoria del token
	public Componentes getTipo() {
		return tipo;
	}
	
	// Cambia la categoria del token
	public void setTipo(Componentes tipo) {
		this.tipo = tipo;
	}
	
	// Devuelve el texto del token
	public String getValor() {
		return valor;
	}
	
	// Cambia el texto del token
	public void setValor(String valor) {
		this.valor = valor;
	}
}