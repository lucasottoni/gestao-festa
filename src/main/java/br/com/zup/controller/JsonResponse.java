package br.com.zup.controller;

/**
 * Classe que representa os retornos de operações 'void' e mensagens de erro
 * @author Lucas
 *
 */
public class JsonResponse {

	private String status = "";
	private String errorMessage = "";

	public JsonResponse(String status, String errorMessage) {
		this.status = status;
		this.errorMessage = errorMessage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}