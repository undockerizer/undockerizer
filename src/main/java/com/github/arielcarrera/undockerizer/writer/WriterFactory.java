package com.github.arielcarrera.undockerizer.writer;

import java.io.BufferedWriter;

import com.github.arielcarrera.undockerizer.utils.OSUtil.OSFamily;

public class WriterFactory {

	public static Writer create(OSFamily os, BufferedWriter bw, String customShell, boolean interactive, boolean escapingDisabled) {
		if (os == null) {
			throw new IllegalArgumentException("OS is required to create a Writer");
		}
		switch (os) {
		case WINDOWS:
			return new WindowsWriter(bw, interactive, escapingDisabled);
		case MAC_OS:
		case UNIX:
			return new ShWriter(bw, customShell, interactive, escapingDisabled);
		case UNIX_BASH:
			return new BashWriter(bw, customShell, interactive, escapingDisabled);
		case SOLARIS:
		case OTHER:
		default:
			return new ShWriter(bw, customShell, interactive, escapingDisabled);
		}
	}

}
