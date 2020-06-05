package com.github.arielcarrera.undockerizer.writer;

import java.io.IOException;

public interface Writer {

	void writeBegin() throws IOException;
	void writeComment(String s) throws IOException;
	void writeMessage(String s) throws IOException;
	void writeCommand(String s) throws IOException;
	void writeEnvVar(String s, String value) throws IOException;
	void writeVar(String s, String value) throws IOException;
	void writeEnd() throws IOException;

}
