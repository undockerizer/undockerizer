package com.github.arielcarrera.undockerizer.writer;

import java.io.IOException;

public class WindowsWriter extends AbstractWriter {

	private static final String BEGIN_BLOCK= "@echo off";
	private static final String COMMENT_PREFIX= "rem ";
	private static final String ECHO_PREFIX= "echo ";
	private static final String SET_PREFIX= "set ";
	
	public WindowsWriter(java.io.BufferedWriter writer) {
		super(writer);
	}
	
	protected String getBeginBlock() {
		return BEGIN_BLOCK;
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
		writer.write(SET_PREFIX + s + "=" + value);
		writer.newLine();
	}

	@Override
	public void writeVar(String s, String value) throws IOException {
		writer.write(SET_PREFIX + s + "=" + value);
		writer.newLine();
	}
	
}
