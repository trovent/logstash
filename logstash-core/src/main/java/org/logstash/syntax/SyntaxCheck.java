package org.logstash.syntax;

public class SyntaxCheck {
	
	private final boolean isOk;
	private final String error;
	
	public SyntaxCheck() {
		this.isOk = true;
		this.error = null;
	}
	
	public SyntaxCheck(String error) {
		this.isOk = false;
		this.error = error;
	}

	public boolean isOk() {
		return isOk;
	}

	public String getError() {
		return error;
	}

}
