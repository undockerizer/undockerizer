package com.github.arielcarrera.undockerizer.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.io.FileUtils;

/**
 * File Utils
 * @author Ariel Carrera
 *
 */
public class FileUtil {

	/**
	 * Open a file for read/write operations
	 * 
	 * @param filePath path to the file
	 * @param overwrite enable overwrite if exists
	 * @return File reference
	 */
	public static File openOutputFile(String filePath, boolean overwrite) {
		File file = Paths.get(filePath).toFile();
		if (file.isDirectory()) throw new IllegalArgumentException("Invalid output path: " + filePath + " (the path is a directory)");
		
		File parent = file.getParentFile();
		if (parent != null && !parent.isDirectory()) throw new IllegalArgumentException("Invalid output path: " + filePath + " (the parent output directory must exists)");

		if (!overwrite) {
			if (file.exists()) throw new IllegalArgumentException("Invalid output path: " + filePath + " (the file exists)");
		} else if (file.exists() && !file.canWrite())  {
			throw new IllegalArgumentException("Invalid output path: " + filePath + " (the file exist must be writable)");
		}
		return file;
	}
	
	public static String fileName(File targetDir, ArchiveEntry entry) {
		return targetDir.getAbsolutePath() + File.separator + entry.getName();
	}

	//TODO use this method when open a file
	public static File openFile(String filePath) throws FileNotFoundException {
		File file = Paths.get(filePath).toFile();
		return checkFile(file);
	}
	
	public static File checkFile(File file) throws FileNotFoundException {
		if (!file.exists()) throw new IllegalArgumentException("Invalid path: " + file.getPath() + " (the file does not exists)");
		if (file.isDirectory()) throw new IllegalArgumentException("Invalid path: " + file.getPath() + " (the path is a directory)");
		if (!file.canRead()) throw new IllegalArgumentException("Invalid path: " + file.getPath() + " (the file cannot be read)");

		return file;
	}

	public static boolean checkDirectoryExists(Path dirPath) {
		File tempDirFile = dirPath.toFile();
		return checkDirectoryExists(tempDirFile);
	}
	
	public static boolean checkDirectoryExists(File dirFile) {
		return dirFile.exists() && dirFile.isDirectory();
	}

	public static void cleanDirectory(File tempDirFile, boolean verbose) throws IOException {
		if (verbose) System.out.println("Cleaning cache data (all)...");
		if (tempDirFile == null ) {
			throw new IllegalArgumentException("Error cleaning directory. Directory path is required");
			
		}
		if (tempDirFile.exists() && tempDirFile.isDirectory()) {
			FileUtils.cleanDirectory(tempDirFile);
		}
		if (verbose) System.out.println("Cleaning cache data (all). Done.");
	}
}