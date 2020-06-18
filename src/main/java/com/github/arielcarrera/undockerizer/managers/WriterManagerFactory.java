package com.github.arielcarrera.undockerizer.managers;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import com.github.arielcarrera.undockerizer.utils.OSUtil.OSFamily;
import com.github.arielcarrera.undockerizer.writer.BashWriter;
import com.github.arielcarrera.undockerizer.writer.ShWriter;
import com.github.arielcarrera.undockerizer.writer.WindowsWriter;
import com.github.arielcarrera.undockerizer.writer.Writer;

public class WriterManagerFactory {

	public static WriterManager create(OSFamily os, BufferedWriter bw, String customShell, boolean escapingDisabled) {
		return WriterManagerFactory.create(os, bw, null, customShell, escapingDisabled);
	}
	
	public static WriterManager create(OSFamily os, BufferedWriter bw, BufferedWriter interactiveBw, String customShell, boolean escapingDisabled) {
		if (os == null) {
			throw new IllegalArgumentException("OS is required to create a WriterManager");
		}
		if (bw == null) {
			throw new IllegalArgumentException("Writer is required to create a WriterManager");
		}
		List<Writer> writers = new ArrayList<Writer>();
		switch (os) {
		case WINDOWS:
			writers.add(new WindowsWriter(bw, false, escapingDisabled));
			if (interactiveBw != null) writers.add(new WindowsWriter(interactiveBw, true, escapingDisabled));
			break;
		case UNIX_BASH:
			writers.add(new BashWriter(bw, customShell, false, escapingDisabled));
			if (interactiveBw != null) writers.add(new BashWriter(interactiveBw, customShell, true, escapingDisabled));
			break;
		case SOLARIS:
		case OTHER:
		case MAC_OS:
		case UNIX:
		default:
			writers.add(new ShWriter(bw, customShell, false, escapingDisabled));
			if (interactiveBw != null) writers.add(new ShWriter(interactiveBw, customShell, true, escapingDisabled));
			break;
		}
		return new WriterManager(writers);
	}

}
