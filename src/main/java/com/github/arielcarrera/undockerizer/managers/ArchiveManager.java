package com.github.arielcarrera.undockerizer.managers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import com.github.arielcarrera.undockerizer.utils.FileUtil;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Archive Manager
 * @author Ariel Carrera
 *
 */
@AllArgsConstructor
public class ArchiveManager {

	@NonNull
	private String outputDirPathStr;
	
	private boolean verbose;
	
	public void generateTar(String filePath, boolean force, Set<Path> resources) throws IOException {
		String fileTarGz = filePath + ".tar.gz";
        File output = FileUtil.openOutputFile(fileTarGz, force);
		this.generateTar(output, resources);
	}
	
	public void generateTar(File output, Set<Path> resources) throws IOException {
		if (verbose) System.out.println("Generating Tar file: " + output.getName());
		
		Set<File> filesToArchive = resources.stream().map(p -> p.toString().startsWith(outputDirPathStr) ? p : Paths.get(outputDirPathStr, p.toString()))
				.map(s -> s.toFile()).collect(Collectors.toSet());
		
		Map<String, String> filesToArchiveName = 
				filesToArchive.stream().collect(Collectors.toMap(f -> f.getPath(), f -> f.getPath().startsWith(outputDirPathStr) ? 
						f.getPath().substring(outputDirPathStr.length()) : f.getPath()));
		try (OutputStream fo = Files.newOutputStream(output.toPath()); 
				OutputStream gzo = new GzipCompressorOutputStream(fo);
				TarArchiveOutputStream o = new TarArchiveOutputStream(gzo)) {
			o.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
		    for (File f : filesToArchive) {
		    	if (f.isFile()) {
		    		ArchiveEntry entry = o.createArchiveEntry(f, filesToArchiveName.get(f.getPath()).toString());
		    		o.putArchiveEntry(entry);
		    		try (InputStream i = Files.newInputStream(f.toPath())) {
		                IOUtils.copy(i, o);
		            }
		            o.closeArchiveEntry();
		        } else if (f.isDirectory()) {
		        	Path path = Paths.get(f.getPath(), "layer.tar");
		        	File file = path.toFile();
		        	ArchiveEntry entry = o.createArchiveEntry(file, file.getPath());
		        	o.putArchiveEntry(entry);
		        	try (InputStream i = Files.newInputStream(f.toPath())) {
		                IOUtils.copy(i, o);
		            }
		            o.closeArchiveEntry();
		        }
		    }
		    o.finish();
		}
	}
	
	
	public Path extractImageTar(Path tempDirPath, Path tarFilePath, String container) throws IOException {
		if (verbose) System.out.println("Extracting docker image content: ");
		
		Path untarDirPath = tempDirPath.resolve(ContentManager.getContentFolderName(container));
		File untarDirFile = untarDirPath.toFile();
		try(ArchiveInputStream i = new TarArchiveInputStream(Files.newInputStream(tarFilePath))){
		    ArchiveEntry entry = null;
		    while ((entry = i.getNextEntry()) != null) {
		        if (!i.canReadEntryData(entry)) {
		            // log something?
		            continue;
		        }
		        String name = FileUtil.fileName(untarDirFile, entry);
		        File f = new File(name);
		        if (entry.isDirectory()) {
		            if (!f.isDirectory() && !f.mkdirs()) {
		                throw new IOException("failed to create directory " + f);
		            }
		        } else {
		            File parent = f.getParentFile();
		            if (!parent.isDirectory() && !parent.mkdirs()) {
		                throw new IOException("failed to create directory " + parent);
		            }
		            try (OutputStream o = Files.newOutputStream(f.toPath())) {
		                IOUtils.copy(i, o);
		            }
		        }
		    }
		}
		if (verbose) System.out.println("-- Docker image content extracted --");
		return untarDirPath;
	}
}