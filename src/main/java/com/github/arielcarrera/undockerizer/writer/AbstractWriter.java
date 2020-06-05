package com.github.arielcarrera.undockerizer.writer;

import java.io.IOException;

public abstract class AbstractWriter implements Writer {

	protected java.io.BufferedWriter writer;
	
	abstract String getBeginBlock();
	abstract String getCommentPrefix();
	abstract String getEchoPrefix();
	abstract String getEndBlock();
	
	public AbstractWriter(java.io.BufferedWriter writer) {
		if (writer == null) throw new IllegalArgumentException("Writer is required");
		this.writer = writer;
	}
	
	@Override
	public void writeBegin() throws IOException {
		writer.write(getBeginBlock());
		writer.newLine();
	}
	
	@Override
	public void writeComment(String s) throws IOException {
		writer.write(getCommentPrefix() + s);
		writer.newLine();
	}
	
	@Override
	public void writeMessage(String s) throws IOException {
		writer.write(getEchoPrefix() + s);
		writer.newLine();
	}
	
	@Override
	public abstract void writeCommand(String s) throws IOException ;
	
	@Override
	public void writeEnd() throws IOException {
		if (getEndBlock() != null) {
			writer.write(getEndBlock());
			writer.newLine();
		}
	}
}
