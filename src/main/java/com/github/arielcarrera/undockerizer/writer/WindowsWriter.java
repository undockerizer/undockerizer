package com.github.arielcarrera.undockerizer.writer;

import java.io.IOException;

public class WindowsWriter extends AbstractWriter {

	private static final String BEGIN_BLOCK= "@echo off";
	private static final String COMMENT_PREFIX= "rem ";
	private static final String ECHO_PREFIX= "echo ";
	private static final String SET_PREFIX= "set ";
	
	public WindowsWriter(java.io.BufferedWriter writer, boolean interactive) {
		super(writer, interactive);
	}
	
	protected String getBeginBlock() {
		return BEGIN_BLOCK + "\n\rset UNDOCKERIZER_WORKDIR=%cd%";
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
		writer.write(getLineSeparator());
	}

	@Override
	public void writeCommand(String s, String variables) throws IOException {
		//TODO implement
		writer.write(s);
		writer.write(getLineSeparator());
	}
	
	@Override
	public void writeEnvVar(String s, String value) throws IOException {
		writer.write(SET_PREFIX + s + "=" + value);
		writer.write(getLineSeparator());
		envVarSet.add(s);
	}

	@Override
	public void writeVar(String s, String value) throws IOException {
		writer.write(SET_PREFIX + s + "=" + value);
		writer.write(getLineSeparator());
	}

	@Override
	String getLineSeparator() {
		return "\n\r";
	}



}
