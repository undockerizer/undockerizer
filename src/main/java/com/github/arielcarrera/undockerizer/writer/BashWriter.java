package com.github.arielcarrera.undockerizer.writer;

public class BashWriter extends ShWriter {

	private static final String DEFAULT_SHELL = "/bin/bash";
	
	public BashWriter(java.io.BufferedWriter writer, boolean interactive) {
		super(writer, interactive);
	}
	
	public BashWriter(java.io.BufferedWriter writer, String customShell, boolean interactive) {
		super(writer, customShell, interactive);
	}
	
	public String getShell() {
		return customShell == null ? DEFAULT_SHELL : customShell;
	}
	

	
}
