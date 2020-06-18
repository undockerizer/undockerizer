package com.github.arielcarrera.undockerizer.writer;

import java.io.IOException;

public class ShWriter extends AbstractWriter {

	private static final String DEFAULT_SHELL = "/bin/sh";
	private static final String COMMENT_PREFIX= "# ";
	private static final String ECHO_PREFIX= "echo ";
	protected final String customShell;
	
	public ShWriter(java.io.BufferedWriter writer, boolean interactive, boolean escapingDisabled) {
		super(writer, interactive, escapingDisabled);
		this.customShell = null;
	}
	
	public ShWriter(java.io.BufferedWriter writer, String customShell, boolean interactive, boolean escapingDisabled) {
		super(writer, interactive, escapingDisabled);
		this.customShell = customShell;
	}
	
	public String getShell() {
		return customShell == null ? DEFAULT_SHELL : customShell;
	}
	
	protected String getBeginBlock() {
		return "#!" + getShell()  + "\nexport UNDOCKERIZER_WORKDIR=\"$PWD\"";
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
		String lineWithSudo = writeWithSudo(s);
		if (interactive) {
			writer.write("read -p \"Line: " + escapePrint(lineWithSudo) + "\nAre you sure do you want to execute? \" -n 1 -r");
			writer.write(getLineSeparator());
			writer.write("printf \"\\n\"");
			writer.write(getLineSeparator());
			writer.write("if [[ $REPLY = \"\" || $REPLY =~ ^[Yy]$ ]]");
			writer.write(getLineSeparator());
			writer.write("then");
			writer.write(getLineSeparator());
		}
		writer.write(lineWithSudo);
		writer.write(getLineSeparator());
		writer.write("[ $? -eq 0 ]  || exit 10");
		writer.write(getLineSeparator());
		if (interactive) {
			writer.write("fi");
			writer.write(getLineSeparator());
		}
	}
	
	@Override
	public void writeCommand(String s, String variables) throws IOException {
		String lineWithSudo = writeWithSudo((!escapingDisabled ? escapeVar(variables) : variables) + ";" + s);
		if (interactive) {
			writer.write("read -p \"Line: " + escapePrint(lineWithSudo) + "\nAre you sure do you want to execute? \" -n 1 -r");
			writer.write(getLineSeparator());
			writer.write("printf \"\\n\"");
			writer.write(getLineSeparator());
			writer.write("if [[ $REPLY = \"\" || $REPLY =~ ^[Yy]$ ]]");
			writer.write(getLineSeparator());
			writer.write("then");
			writer.write(getLineSeparator());
		}
		writer.write(lineWithSudo);
		writer.write(getLineSeparator());
		writer.write("[ $? -eq 0 ]  || exit 10");
		writer.write(getLineSeparator());
		if (interactive) {
			writer.write("fi");
			writer.write(getLineSeparator());
		}
	}
	
	private String writeWithSudo(String s) {
		return "sudo -E -u " + user +  " " + customShell + " -c '" + (!escapingDisabled ? escape(s) : s) + "'";
	}
	
	private String escapeVar(String s) {
		if (s == null) return "";
		return s.replace("$", "\\$");
	}

	private String escape(String s) {
		if (s == null) return "";
		return s.replace("\'", "\'\"\'\"\'");
	}
	
	private String escapePrint(String s) {
		if (s == null) return "";
		return s.replace('"', '\'');
	}

	@Override
	public void writeEnvVar(String s, String value) throws IOException {
		writer.write("export " + s + "=" + (!escapingDisabled ? escapeVar(value) : value));
		writer.write(getLineSeparator());
		writer.write("[ $? -eq 0 ]  || exit 20");
		writer.write(getLineSeparator());
	}

	@Override
	public void writeVar(String s, String value) throws IOException {
		writer.write("export " + s + "=" + (!escapingDisabled ? escapeVar(value) : value));
		writer.write(getLineSeparator());
		writer.write("[ $? -eq 0 ]  || exit 20");
		writer.write(getLineSeparator());
	}
	
	@Override
	String getLineSeparator() {
		return "\n";
	}

}
