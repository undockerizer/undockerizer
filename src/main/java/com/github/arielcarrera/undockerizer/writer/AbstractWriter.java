package com.github.arielcarrera.undockerizer.writer;

import java.io.IOException;

public abstract class AbstractWriter implements Writer {

	protected java.io.BufferedWriter writer;
	protected boolean interactive;
	protected String user = "root";
	
	abstract String getBeginBlock();
	abstract String getCommentPrefix();
	abstract String getEchoPrefix();
	abstract String getEndBlock();
	abstract String getLineSeparator();
	
	public AbstractWriter(java.io.BufferedWriter writer, boolean interactive) {
		if (writer == null) throw new IllegalArgumentException("Writer is required");
		this.writer = writer;
		this.interactive = interactive;
	}
	
	@Override
	public void writeBegin() throws IOException {
		writer.write(getBeginBlock());
		writer.write(getLineSeparator());
	}
	
	@Override
	public void writeComment(String s) throws IOException {
		writer.write(getCommentPrefix() + s);
		writer.write(getLineSeparator());
	}
	
	@Override
	public void writeMessage(String s) throws IOException {
		writer.write(getEchoPrefix() + s);
		writer.write(getLineSeparator());
	}
	
	@Override
	public void writeEnd() throws IOException {
		if (getEndBlock() != null) {
			writer.write(getEndBlock());
			writer.write(getLineSeparator());
		}
	}
	
	@Override
	public void writeChangeUser(String user) throws IOException {
		writeComment("Changed user to: " +  user);
		setCurrentUser(user);
	}
	
	@Override
	public void setCurrentUser(String user) {
		this.user = user;
	}
}
