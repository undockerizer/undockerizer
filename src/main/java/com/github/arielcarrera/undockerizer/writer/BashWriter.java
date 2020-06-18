package com.github.arielcarrera.undockerizer.writer;

public class BashWriter extends ShWriter {

	private static final String DEFAULT_SHELL = "/bin/bash";
	
	public BashWriter(java.io.BufferedWriter writer, boolean interactive, boolean escapingDisabled) {
		super(writer, interactive, escapingDisabled);
	}
	
	public BashWriter(java.io.BufferedWriter writer, String customShell, boolean interactive, boolean escapingDisabled) {
		super(writer, customShell, interactive, escapingDisabled);
	}
	
	public String getShell() {
		return customShell == null ? DEFAULT_SHELL : customShell;
	}
	

	
}
