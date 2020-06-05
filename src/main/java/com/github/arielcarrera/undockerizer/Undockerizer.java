package com.github.arielcarrera.undockerizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arielcarrera.undockerizer.OSUtil.OSFamily;
import com.github.arielcarrera.undockerizer.model.ConfigFile;
import com.github.arielcarrera.undockerizer.model.InspectData;
import com.github.arielcarrera.undockerizer.model.Manifest;
import com.github.arielcarrera.undockerizer.model.config.History;
import com.github.arielcarrera.undockerizer.writer.Writer;
import com.github.arielcarrera.undockerizer.writer.WriterFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;

@Command(name = "Undockerizer", header = "%n@|green Welcome to Undockerizer.\nThe tool to convert a Docker image to a shell script installer.\n|@")
public class Undockerizer implements Callable<Integer> {

    @Option(names = {"-i", "--image"}, required = true, description = "The docker image.")
    String dockerImage;
    
    @Option(names = {"-o", "--output"}, required = true, description = "The output file path.", defaultValue = "undockerized.sh", showDefaultValue = Visibility.ALWAYS)
    String outputfileStr;
    
    @Option(names = {"-v", "--verbose"}, required = false, description = "Verbose mode.", defaultValue = "false")
    boolean verbose;
    
    @Option(names = {"-sl", "--saveLogs"}, required = false, description = "Save all logs.", defaultValue = "false")
    boolean trace;

    @Option(names = {"-f", "--force"}, required = false, description = "Overwrite output file if exists", defaultValue = "false")
    boolean force;
    
    @Option(names = {"-sp", "--shellPath"}, required = false, description = "Sets the shell path.", defaultValue = "/bin/bash", showDefaultValue = Visibility.ALWAYS)
    String shellPathStr;
    
    @Option(names = {"-t", "--tempDir"}, required = false, description = "Sets the temp directory path.", defaultValue = "undockerizer-temp", showDefaultValue = Visibility.ALWAYS)
    String tempDirPathStr;
    
    @Option(names = {"-fp", "--forcePull"}, required = false, description = "Force to pull image.", defaultValue = "false", showDefaultValue = Visibility.ALWAYS)
    boolean skipPull;
    
    @Option(names = {"-fc", "--forceClean"}, required = false, description = "Force to clean temp/cache data", defaultValue = "false")
    boolean forceClean;
    
    
    private static boolean dockerAvailable = false;
    
    private static boolean dockerPullSuccessful = false;
    
    private static final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true).configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    
	public static void main(String[] args) {
		int exitCode = new CommandLine(new Undockerizer()).execute(args);
		System.exit(exitCode);
	}
	
	@Override
	public Integer call() throws Exception {
		// check docker availability
		checkDockerAvailability();
		
		// inspect docker image info
		InspectData info = inspectDockerImageInfo();
		
		// first check (cache) if files exists in temp file
		Path tempDirPath = Paths.get(tempDirPathStr);
		TempData tempData = checkCacheData(tempDirPath, info);
		
		// pull docker image
		if (tempData == null) {
			pullDockerImage();

			// read docker image
	    	Path tarFilePath = saveImageTar(tempDirPath.toFile());
			
			// extracting tar file
			Path untarDirPath = extractImageTar(tempDirPath, tarFilePath);
			
	        //opening manifest file
	        Manifest manifest = openManifest(untarDirPath);
	        
	        //opening config file
	        ConfigFile cfg = openConfigFile(untarDirPath, manifest);

	        tempData = new TempData(manifest, cfg);
		}
		//opening output file
        process(tempData);
        
        return 0;
    }

	@Data @AllArgsConstructor
	private static class TempData {
		Manifest manifest;
		ConfigFile config;
	}
	
	private TempData checkCacheData(Path tempDirPath, InspectData info) throws IOException {
		File tempDirFile = tempDirPath.toFile();
		if (verbose) System.out.println("Checking cache...");
		// read docker image -> check temp dir if not exists
		if (!tempDirFile.exists()) {
    		// create temp dir
    		tempDirFile.mkdirs();
    	} else {
    		if (!forceClean) {
	    		String created = info.getCreated();
	    		String container = info.getContainer();
	    		// check manifest and config file
	    		try {
	    			Path untarDirPath = tempDirPath.resolve("untar");
	    			//opening cached manifest file
	    			Manifest manifest = openManifest(untarDirPath);
	    			if (manifest == null) {
		            	throw new RuntimeException("Error reading Manifest: Manifest data is required");
		            }
	    			//opening cached config file
	    	        ConfigFile cfg = openConfigFile(untarDirPath, manifest);
	    	        
	    	        if (created != null) {
	    	        	if (!created.equals(cfg.getCreated())) {
	    	        		throw new RuntimeException("No cache data found");
	    	        	}
	    	        }
	    	        if (container != null) {
	    	        	if (!container.equals(cfg.getContainer())) {
	    	        		throw new RuntimeException("No cache data found");
	    	        	}
	    	        }
	    	        if (container == null && created == null) {
	    	        	throw new RuntimeException("No cache data found");
	    	        }
	    	        if (verbose) System.out.println("Image data found in cache");
	    	        return new TempData(manifest, cfg);
	    		} catch(Exception e) {
	    			//clean temp folder
	    			if (verbose) System.out.println("Image data not found in cache");
	    		}
	    		if (verbose) System.out.println("Cleaning cache data...");
	    		boolean proceed = promptToUser("Cleaning cache data (path: " + tempDirFile.getAbsolutePath() + "). Do you want to proceed? [Y][N]");
	    		if (proceed) {
	    			FileUtils.cleanDirectory(tempDirFile);
	    		} else {
	    			if (verbose) System.out.println("Exit...");
	    			throw new RuntimeException("Cleaning cache: No proceed selected");
	    		}
    		} else {
	    		if (verbose) System.out.println("Cleaning cache data (force)...");
	    		FileUtils.cleanDirectory(tempDirFile);
    		}
    		
    	}
		return null;
	}

	private InspectData inspectDockerImageInfo() throws IOException, InterruptedException, ExecutionException, TimeoutException {
		
		if (verbose) System.out.println("Getting docker image info...");
		ProcessBuilder builder = new ProcessBuilder();
		if (OSUtil.isWindows()) {
		    builder.command("cmd.exe", "/c", "docker", "inspect", dockerImage);
		} else {
		    builder.command(shellPathStr, "-c", "docker", "inspect", dockerImage);
		}
		if (trace) builder.redirectOutput(Paths.get(tempDirPathStr, "inspect.log").toFile());
		Process process = builder.start();
		StringBuilder strBuilder = new StringBuilder();
		StreamOutput stream = new StreamOutput(process.getInputStream(), line -> Undockerizer.readAndlogResponse(strBuilder, line, verbose));
		Future<?> submit = Executors.newSingleThreadExecutor().submit(stream);
		int exitCode = process.waitFor();
		if (exitCode != 0) {
			if (!verbose) System.out.println("Error inspecting docker image info. Please run in verbose mode for more details (-v).");
			throw new IllegalStateException("Error inspecting Docker image info: " + dockerImage);
		};
		submit.get(10, TimeUnit.SECONDS);
		InspectData info = readInspectData(strBuilder.toString());
		if (verbose) System.out.println("-- Docker image info read --");

		return info;
	}

	private void process(TempData tempData) throws IOException, FileNotFoundException {
		
		if (verbose) System.out.println("Opening output file: " + outputfileStr);
        File output = openOutputFile(outputfileStr, force);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)))) {
        	//TODO detect shell first
        	Writer w = WriterFactory.create(OSFamily.UNIX, bw, shellPathStr);
        	generateScript(tempData.getManifest(), tempData.getConfig(), w);
        }
	}

	private ConfigFile openConfigFile(Path untarDirPath, Manifest manifest) throws IOException {
		
		File configFile = untarDirPath.resolve(manifest.getConfigFile()).toFile();
        if (verbose) System.out.println("Loading config file: " + configFile.getPath());
        ConfigFile cfg = readConfigFile(configFile);
        if (cfg == null) {
        	throw new RuntimeException("Error reading Config file: Config data is required");
        }
        if (verbose) System.out.println("-- Docker Config file loaded --");
		return cfg;
	}

	private Manifest openManifest(Path untarDirPath) throws IOException {
		
		File manifestFile = untarDirPath.resolve("manifest.json").toFile();
        if (verbose) System.out.println("Loading manifest file: " + manifestFile.getPath());
        Manifest manifest = readManifest(manifestFile);
        if (manifest == null) {
        	throw new RuntimeException("Error reading Manifest: Manifest data is required");
        }
        
        if (manifest.getConfigFile() == null || manifest.getConfigFile().trim().isEmpty()) {
        	throw new RuntimeException("Manifest: Config file is required");
        }
        
        if (manifest.getLayers() == null || manifest.getLayers().isEmpty()) {
        	throw new RuntimeException("Manifest: Layers are required");
        }
        if (verbose) System.out.println("-- Docker Manifest file loaded --");
		return manifest;
	}

	private Path extractImageTar(Path tempDirPath, Path tarFilePath) throws IOException {
		
		if (verbose) System.out.println("Opening docker image: " + dockerImage);
		Path untarDirPath = tempDirPath.resolve("untar");
		File untarDirFile = untarDirPath.toFile();
		try(ArchiveInputStream i = new TarArchiveInputStream(Files.newInputStream(tarFilePath))){
		    ArchiveEntry entry = null;
		    while ((entry = i.getNextEntry()) != null) {
		        if (!i.canReadEntryData(entry)) {
		            // log something?
		            continue;
		        }
		        String name = fileName(untarDirFile, entry);
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
		if (verbose) System.out.println("-- Docker image opened --");
		return untarDirPath;
	}

	private Path saveImageTar(File tempDirFile) throws IOException, InterruptedException, ExecutionException, TimeoutException {
		
		if (verbose) System.out.println("Reading docker image: " + dockerImage);
    	// read docker image -> create tar file from image
    	Path tarFilePath = Paths.get(tempDirPathStr, "temp.tar");
    	ProcessBuilder builder = new ProcessBuilder();
    	if (trace) builder.redirectOutput(Paths.get(tempDirPathStr, "download.log").toFile());
    	
		if (OSUtil.isWindows()) {
		    builder.command("cmd.exe", "/c", "docker", "save", dockerImage, "-o", tarFilePath.toString());
		} else {
		    builder.command(shellPathStr, "-c", "docker", "save", dockerImage, "-o", tarFilePath.toString());
		}
		Process process = builder.start();
		StreamOutput stream = new StreamOutput(process.getInputStream(), line -> Undockerizer.logResponse(line, verbose));
		Future<?> submit = Executors.newSingleThreadExecutor().submit(stream);
		int exitCode = process.waitFor();
		if (exitCode != 0) {
			if (!verbose) System.out.println("Error processing docker command. Please run in verbose mode for more details (-v).");
			throw new IllegalStateException("Error processing docker command");
		}
		submit.get(10, TimeUnit.SECONDS);
		if (verbose) System.out.println("-- Docker image downloaded --");
		return tarFilePath;
	}

	private void pullDockerImage() throws IOException, InterruptedException, ExecutionException, TimeoutException {
		
		if (verbose) System.out.println("Checking docker runtime availability...");
		ProcessBuilder builder = new ProcessBuilder();
		if (OSUtil.isWindows()) {
		    builder.command("cmd.exe", "/c", "docker", "pull", dockerImage);
		} else {
		    builder.command(shellPathStr, "-c", "docker", "pull", dockerImage);
		}
		if (trace) builder.redirectOutput(Paths.get(tempDirPathStr, "pull.log").toFile());
		Process process = builder.start();
		StreamOutput stream = new StreamOutput(process.getInputStream(), line -> Undockerizer.checkDockerPull(line, dockerImage, verbose));
		Future<?> submit = Executors.newSingleThreadExecutor().submit(stream);
		int exitCode = process.waitFor();
		if (exitCode != 0) {
			if (!verbose) System.out.println("Error pulling docker image. Please run in verbose mode for more details (-v).");
			throw new IllegalStateException("Error pulling Docker image: " + dockerImage);
		};
		submit.get(10, TimeUnit.SECONDS);
		if (verbose) {
			if (dockerPullSuccessful) System.out.println("-- Docker image pulled --");
			else System.err.println("WARN: Docker image pulling cannot be validated");
		}
	}

	private void checkDockerAvailability() throws IOException, InterruptedException, ExecutionException, TimeoutException {
		if (verbose) System.out.println("Checking docker runtime availability...");
		ProcessBuilder builder = new ProcessBuilder();
		if (OSUtil.isWindows()) {
		    builder.command("cmd.exe", "/c", "docker", "--version");
		} else {
		    builder.command(shellPathStr, "-c", "docker", "--version");
		}
		if (trace) builder.redirectOutput(Paths.get(tempDirPathStr, "check-docker.log").toFile());
		Process process = builder.start();
		StreamOutput stream = new StreamOutput(process.getInputStream(), line -> Undockerizer.checkDockerVersion(line, verbose));
		Future<?> submit = Executors.newSingleThreadExecutor().submit(stream);
		int exitCode = process.waitFor();
		if (exitCode != 0) {
			if (!verbose) System.out.println("Error checking docker availability. Please run in verbose mode for more details (-v).");
			throw new IllegalStateException("Docker is required");
		};
		submit.get(10, TimeUnit.SECONDS);
	}
	
	private static final String REGEX_CMD = "(([\\w\\/\\\\.]*\\s*-c\\s)#\\(nop\\)\\s*)CMD\\s";

	private static final String REGEX_ENV = "([\\w_-]*)=(.*)";

	private void generateScript(Manifest manifest, ConfigFile cfg, Writer w) throws IOException {

		List<History> hist = cfg.getHistory();
		boolean firstCommandFound = false;
		String noOpsPrefix = null, instructionprefix = null;
		Pattern cmdPattern = Pattern.compile(REGEX_CMD);
		Pattern envPattern = Pattern.compile(REGEX_ENV);
		for (Iterator<History> iterator = hist.iterator(); iterator.hasNext();) {
			History history = iterator.next();
			String line = history.getCreatedBy();
			if (line != null) {
				//check first command
				if (!firstCommandFound) {
					if (history.isEmptyLayer()) {
						Matcher matcher = cmdPattern.matcher(line);
						if (matcher.find()) {
							firstCommandFound = true;
							noOpsPrefix = matcher.group(1);
							instructionprefix = matcher.group(2);
						}
					}
				} else {
					if (history.isEmptyLayer()) {
						// docker instructions
						if (line.startsWith(noOpsPrefix)) {
							String sentence = line.substring(noOpsPrefix.length());
							if (sentence.startsWith("LABEL ")) {
								w.writeComment(sentence.substring(5));
							} else if (sentence.startsWith("ENV ")) {
								Matcher matcher = envPattern.matcher(sentence.substring(4));
								if (matcher.find()) {
									String key = matcher.group(1);
									String val;
									if (matcher.groupCount() > 1) {
										val = matcher.group(2);
									} else {
										val = "";
									}
									w.writeEnvVar(key, val);
								} else {
									System.err.println("Error parsing ENV: not found");
								}
							}
						}
					} else {
						// shell commands
						if (line.startsWith(instructionprefix)) {
							w.writeCommand(line.substring(instructionprefix.length()));
						}
					}
				}
			}
		}
	}

	private Manifest readManifest(File manifestFile) throws IOException {
		
		List<Manifest> list = mapper.readValue(manifestFile, new TypeReference<List<Manifest>>(){});
		return !list.isEmpty() ? list.get(0) : null;
	}
	
	private InspectData readInspectData(String json) throws IOException {
		
		List<InspectData> list = mapper.readValue(json, new TypeReference<List<InspectData>>(){});
		return !list.isEmpty() ? list.get(0) : null;
	}
	
	private ConfigFile readConfigFile(File configFile) throws IOException {
		
		return mapper.readValue(configFile, ConfigFile.class);
	}

	private String fileName(File targetDir, ArchiveEntry entry) {
		return targetDir.getAbsolutePath() + File.separator + entry.getName();
	}

	static File openFile(String filePath) throws FileNotFoundException {
		File file = Paths.get(filePath).toFile();
		return checkFile(file);
	}
	static File checkFile(File file) throws FileNotFoundException {
		if (!file.exists()) throw new IllegalArgumentException("Invalid path: " + file.getPath() + " (the file does not exists)");
		if (file.isDirectory()) throw new IllegalArgumentException("Invalid path: " + file.getPath() + " (the path is a directory)");
		if (!file.canRead()) throw new IllegalArgumentException("Invalid path: " + file.getPath() + " (the file cannot be read)");

		return file;
	}
	
	static File openOutputFile(String filePath, boolean overwrite) {
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

	private static void checkDockerPull(String line, String dockerImage, boolean verbose) {
		if (verbose) System.out.println("result: " + line);
		if (!dockerPullSuccessful) {			
			dockerPullSuccessful = line.startsWith("Status: Downloaded") || line.startsWith("Status: Image is up to date");
		}
	}
	
	private static void checkDockerVersion(String line, boolean verbose) {
		if (verbose) System.out.println("result: " + line);
		if (!dockerAvailable) {
			dockerAvailable = line.startsWith("Docker version");
			if (verbose && dockerAvailable) System.out.println("-- Docker available --");
		}
	}
	
	private static void readAndlogResponse(StringBuilder builder, String line, boolean verbose) {
		if (verbose) System.out.println("result: " + line);
		builder.append(line);
	}
	private static void logResponse(String line, boolean verbose) {
		if (verbose) System.out.println("result: " + line);
	}
	

    public static boolean promptToUser(String message) {
        try (Scanner scanner = new Scanner(System.in)){
	        String value;
	        boolean isTrue = false, isFalse = false;
	        do {
	            System.out.print(message);
	            value = scanner.next().trim().toUpperCase();
	            isTrue = "Y".equalsIgnoreCase(value);
	            isFalse = "N".equalsIgnoreCase(value);
	        } while (!isTrue && !isFalse);
	        
	        return isTrue;
        }
    }
}
