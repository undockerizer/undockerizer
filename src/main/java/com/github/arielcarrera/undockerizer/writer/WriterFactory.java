package com.github.arielcarrera.undockerizer.writer;

import java.io.BufferedWriter;

import com.github.arielcarrera.undockerizer.OSUtil.OSFamily;

public class WriterFactory {

	public static Writer create(OSFamily os, BufferedWriter bw, String customShell, boolean interactive) {
		if (os == null) {
			throw new IllegalArgumentException("OS is required to create a Writer");
		}
		switch (os) {
		case WINDOWS:
			return new WindowsWriter(bw, interactive);
		case MAC_OS:
		case UNIX:
			return new ShWriter(bw, customShell, interactive);
		case UNIX_BASH:
			return new BashWriter(bw, customShell, interactive);
		case SOLARIS:
		case OTHER:
		default:
			return new ShWriter(bw, customShell, interactive);
		}
	}

}
