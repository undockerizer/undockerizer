package com.github.arielcarrera.undockerizer.writer;

public class BashWriter extends ShWriter {

	private static final String BEGIN_BLOCK= "#!/bin/bash";
	
	public BashWriter(java.io.BufferedWriter writer) {
		super(writer);
	}
	
	public BashWriter(java.io.BufferedWriter writer, String customShell) {
		super(writer, customShell);
	}
	
	protected String getBeginBlock() {
		return (customShell == null ? BEGIN_BLOCK : "#!" + customShell);
	}
	
}
