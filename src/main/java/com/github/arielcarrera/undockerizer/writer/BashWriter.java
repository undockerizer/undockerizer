package com.github.arielcarrera.undockerizer.writer;

public class BashWriter extends ShWriter {

	private static final String BEGIN_BLOCK= "#!/bin/bash";
	
	public BashWriter(java.io.BufferedWriter writer, boolean interactive) {
		super(writer, interactive);
	}
	
	public BashWriter(java.io.BufferedWriter writer, String customShell, boolean interactive) {
		super(writer, customShell, interactive);
	}
	
	protected String getBeginBlock() {
		return (customShell == null ? BEGIN_BLOCK : "#!" + customShell)  + "\nUNDOCKERIZER_WORKDIR=\"$PWD\"";
	}
	
}
