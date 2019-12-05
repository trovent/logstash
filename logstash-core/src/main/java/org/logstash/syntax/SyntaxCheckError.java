package org.logstash.syntax;

public class SyntaxCheckError {
	
	private final String message;
	
	public SyntaxCheckError(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
