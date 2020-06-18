package com.github.arielcarrera.undockerizer.managers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.arielcarrera.undockerizer.writer.Writer;

public class WriterManager implements Writer {

	List<Writer> listeners = new ArrayList<Writer>();
	
	public WriterManager(Writer...listeners) {
		super();
		if (listeners == null || listeners.length == 0) throw new IllegalArgumentException("Writer is required");
		for (int i = 0; i < listeners.length; i++) {
			this.listeners.add(listeners[i]);
		}
	}
	
	public WriterManager(List<Writer> listeners) {
		super();
		if (listeners == null || listeners.isEmpty()) throw new IllegalArgumentException("Writer is required");
		this.listeners.addAll(listeners);
	}

	public void setCurrentUser(String user) {
		listeners.forEach(l -> l.setCurrentUser(user));
	}

	public void writeBegin() throws IOException {
		for (Writer writer : listeners) {
			writer.writeBegin();
		}
	}

	public void writeComment(String s) throws IOException {
		for (Writer writer : listeners) {
			writer.writeComment(s);
		}
	}

	public void writeMessage(String s) throws IOException {
		for (Writer writer : listeners) {
			writer.writeMessage(s);
		}
	}

	public void writeCommand(String s) throws IOException {
		for (Writer writer : listeners) {
			writer.writeCommand(s);
		}
	}

	public void writeCommand(String s, String variables) throws IOException {
		for (Writer writer : listeners) {
			writer.writeCommand(s, variables);
		}
	}

	public void writeEnvVar(String s, String value) throws IOException {
		for (Writer writer : listeners) {
			writer.writeEnvVar(s, value);
		}
	}

	public void writeVar(String s, String value) throws IOException {
		for (Writer writer : listeners) {
			writer.writeVar(s, value);
		}
	}

	public void writeEnd() throws IOException {
		for (Writer writer : listeners) {
			writer.writeEnd();
		}
	}

	public void writeChangeUser(String user) throws IOException {
		for (Writer writer : listeners) {
			writer.writeChangeUser(user);
		}
	}

	public void writeFileExists(String path, String errorMessage) throws IOException {
		for (Writer writer : listeners) {
			writer.writeFileExists(path, errorMessage);
		}
	}
}
