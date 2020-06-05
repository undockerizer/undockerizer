package com.github.arielcarrera.undockerizer.exceptions;


public class RequiredEnvironmentVariableException extends Exception {
	private static final long serialVersionUID = -1250700098669029910L;
	String message;

	public RequiredEnvironmentVariableException(String message) {
		super();
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
