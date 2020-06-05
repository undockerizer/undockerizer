package com.github.arielcarrera.undockerizer.writer;

import java.io.IOException;

public class ShWriter extends AbstractWriter {

	private static final String BEGIN_BLOCK= "#!/bin/sh";
	private static final String COMMENT_PREFIX= "# ";
	private static final String ECHO_PREFIX= "echo ";
	protected final String customShell;
	
	public ShWriter(java.io.BufferedWriter writer) {
		super(writer);
		this.customShell = null;
	}
	
	public ShWriter(java.io.BufferedWriter writer, String customShell) {
		super(writer);
		this.customShell = customShell;
	}
	
	protected String getBeginBlock() {
		return (customShell == null ? BEGIN_BLOCK : "#!" + customShell);
	}
	protected String getCommentPrefix() {
		return COMMENT_PREFIX;
	}
	protected String getEchoPrefix() {
		return ECHO_PREFIX;
	}
	protected String getEndBlock() {
		return null;
	}
	
	@Override
	public void writeCommand(String s) throws IOException {
		writer.write(s);
		writer.newLine();
	}

	@Override
	public void writeEnvVar(String s, String value) throws IOException {
		writer.write(s + "=" + value);
		writer.newLine();
	}

	@Override
	public void writeVar(String s, String value) throws IOException {
		writer.write(s + "=" + value);
		writer.newLine();
	}
	
}
