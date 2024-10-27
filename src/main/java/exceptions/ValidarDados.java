package exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidarDados extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	private Map<String,String> erros = new HashMap<>();
	
	public ValidarDados(String msg) {
		super(msg);
	}
	
	public Map<String,String> getError(){
		return erros;
	}
	
	public void addErros(String chave,String valor) {
		erros.put(chave, valor);
	}

}
