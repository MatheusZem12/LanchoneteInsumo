package br.com.insumo.lanchonete.exceptions;

public class EntityNotExistsException extends RuntimeException {

	public EntityNotExistsException() {
		super();
	}

	public EntityNotExistsException(String message) {
		super(message);
	}

	public EntityNotExistsException(String message, Throwable cause) {
		super(message, cause);
	}

}
