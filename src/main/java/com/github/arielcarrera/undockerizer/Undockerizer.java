package com.github.arielcarrera.undockerizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
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
    
    @Option(names = {"-o", "--output"}, required = false, description = "The output file name.", showDefaultValue = Visibility.ALWAYS)
    String outputfileStr;

    @Option(names = {"-od", "--outputDir"}, required = false, description = "Sets the output directory path.", defaultValue = "undockerizer", showDefaultValue = Visibility.ALWAYS)
    String outputDirPathStr;
    
    @Option(names = {"-sp", "--shellPath"}, required = false, description = "Sets the shell path.", defaultValue = "/bin/sh", showDefaultValue = Visibility.ALWAYS)
    String shellPathStr;
    
    @Option(names = {"-v", "--verbose"}, required = false, description = "Verbose mode.", defaultValue = "false")
    boolean verbose;
    
    @Option(names = {"-sl", "--saveLogs"}, required = false, description = "Save log files.", defaultValue = "false")
    boolean trace;

    @Option(names = {"-f", "--force"}, required = false, description = "Overwrite output file if exists", defaultValue = "false")
    boolean force;
    
    @Option(names = {"-c", "--cleanAll"}, required = false, description = "Clean all temp data", defaultValue = "false")
    boolean cleanAll;
    
    @Option(names = {"-fp", "--forcePull"}, required = false, description = "Force to pull image.", defaultValue = "false", showDefaultValue = Visibility.ALWAYS)
    boolean forcePull;
    
    @Option(names = {"-it", "--interactiveOutput"}, required = false, description = "Generate output file with interactive mode", defaultValue = "false")
    boolean interactiveOutput;
    
    @Option(names = {"-t", "--tar"}, required = false, description = "Create tar file.", defaultValue = "false", showDefaultValue = Visibility.ALWAYS)
    boolean createTar;
    
    private Set<Path> resourcesToArchive = new HashSet<Path>();
    
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
		Path outputDirPath = Paths.get(outputDirPathStr);
		
		// if cleanAll is active, skip check cache and do a clean
		TempData tempData = checkCacheData(outputDirPath, info, cleanAll);
		
		// pull docker image
		if (tempData == null) {
			pullDockerImage();

			// read docker image
	    	Path tarFilePath = saveImageTar(outputDirPath.toFile(), info.getContainer());
			
			// extracting tar file
			Path untarDirPath = extractImageTar(outputDirPath, tarFilePath, info.getContainer());
			
	        //opening manifest file
			Path manifestPath = untarDirPath.resolve("manifest.json");
	        Manifest manifest = openManifest(manifestPath);
	        
	        //opening config file
	        Path configPath = untarDirPath.resolve(manifest.getConfigFile());
	        ConfigFile cfg = openConfigFile(configPath, manifest);
	        
	        //remove temp tar file on exit
	        tarFilePath.toFile().deleteOnExit();

	        tempData = new TempData(manifest, manifestPath, cfg, configPath);
		}
		if (createTar) {
			//TODO arreglar las urls
			resourcesToArchive.add(tempData.getConfigPath());
			resourcesToArchive.add(tempData.getManifesPath());
		}
		//opening output file
        process(tempData);
        
        return 0;
    }

	@Data @AllArgsConstructor
	private static class TempData {
		Manifest manifest;
		Path manifesPath;
		ConfigFile config;
		Path configPath;
	}
	
	private TempData checkCacheData(Path tempDirPath, InspectData info, boolean cleanAll) throws IOException {
		File tempDirFile = tempDirPath.toFile();
		if (verbose) System.out.println("Checking cache...");
		if (!checkDirectoryExists(tempDirFile)) {
			if (verbose && cleanAll) System.out.println("Temp directory not exists.");
			if (verbose) System.out.println("Creating temp directory...");
    		// create temp dir
    		tempDirFile.mkdirs();
			if (verbose) System.out.println("Temp directory created.");
    	} else {
    		// if we have to clean all...
    		if (cleanAll) {
    			cleanDirectory(tempDirFile);
    		} else {
    			String created = info.getCreated();
	    		String container = info.getContainer();
	    		// check manifest and config file
    			Path contentDirPath = tempDirPath.resolve(getContentFolderName(info));
    			
    			if (forcePull) {
    				//if we have to force Pull the current image data...
    				if (checkDirectoryExists(contentDirPath)) {
    					cleanDirectory(contentDirPath.toFile());
    				}
    			} else {
    				//if we have to open the cached image data...
    				try {
    					//opening cached manifest file
    					Path manifestPath = contentDirPath.resolve("manifest.json");
    	    			Manifest manifest = openManifest(manifestPath);
    	    			if (manifest == null) {
    		            	throw new RuntimeException("Error reading Manifest: Manifest data is required");
    		            }
    	    			//opening cached config file
    	    			Path configPath = contentDirPath.resolve(manifest.getConfigFile());
    	    	        ConfigFile cfg = openConfigFile(configPath, manifest);
    	    	        
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
    	    	        return new TempData(manifest, manifestPath, cfg, configPath);
    	    		} catch(Exception e) {
    	    			//clean temp folder
    	    			if (verbose) System.out.println("Image data not valid/found in cache.");
    	    			cleanDirectory(contentDirPath.toFile());
    	    		}
    			}
    		} 
    	}
		return null;
	}

	private boolean checkDirectoryExists(Path dirPath) {
		File tempDirFile = dirPath.toFile();
		return checkDirectoryExists(tempDirFile);
	}
	
	private boolean checkDirectoryExists(File dirFile) {
		return dirFile.exists() && dirFile.isDirectory();
	}

	private void cleanDirectory(File tempDirFile) throws IOException {
		if (verbose) System.out.println("Cleaning cache data (all)...");
		if (tempDirFile == null ) {
			throw new IllegalArgumentException("Error cleaning directory. Directory path is required");
			
		}
		if (tempDirFile.exists() && tempDirFile.isDirectory()) {
			FileUtils.cleanDirectory(tempDirFile);
		}
		if (verbose) System.out.println("Cleaning cache data (all). Done.");
	}

	private String getContentFolderName(InspectData info) {
		return getContentFolderName(info.getContainer());
	}
	
	private String getContentFolderName(String container) {
		return container.substring(0, Math.abs(container.length()/2)) + "-content";
	}

	private InspectData inspectDockerImageInfo() throws IOException, InterruptedException, ExecutionException, TimeoutException {
		
		if (verbose) System.out.println("Getting docker image info...");
		ProcessBuilder builder = new ProcessBuilder();
		if (OSUtil.isWindows()) {
		    builder.command("cmd.exe", "/c", "docker", "inspect", dockerImage);
		} else {
		    builder.command(shellPathStr, "-c", "docker", "inspect", dockerImage);
		}
		if (trace) builder.redirectOutput(Paths.get(outputDirPathStr, "inspect.log").toFile());
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
		if (outputfileStr == null) {
			String container = tempData.getConfig().getContainer();
			outputfileStr = outputDirPathStr + "/" + "undockerizer-" +  container.substring(0, Math.abs(container.length()/2)) + ".sh";
		} else {
			if (outputfileStr.contains("\\") || outputfileStr.contains("/")){
				if (verbose) System.out.println("Output filename cannot be a path: " + outputfileStr);
				throw new IllegalArgumentException("Output filename cannot be a path");
			}
			outputfileStr = Paths.get(outputDirPathStr, outputfileStr).toString();
		}

		if (verbose) System.out.println("Opening output file: " + outputfileStr);
		
        File output = openOutputFile(outputfileStr, force);
        if (createTar) resourcesToArchive.add(output.toPath());
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)))) {
        	//TODO change OSFamily
        	Writer w = WriterFactory.create(OSFamily.UNIX, bw, shellPathStr, interactiveOutput);
        	w.writeBegin();
        	generateScript(tempData.getManifest(), tempData.getConfig(), w);
        	w.writeEnd();
        	System.out.println("Script generated successfully.");
        }
        if (createTar) {
        	String outputTarGzFile = outputfileStr + ".tar.gz";
	        File outputTarGz = openOutputFile(outputTarGzFile, force);
        	generateTar(outputTarGz, resourcesToArchive);
        	System.out.println("Tar generated successfully.");
	        
        }
	}

	private void generateTar(File output, Set<Path> resources) throws IOException {
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

	private ConfigFile openConfigFile(Path configPath, Manifest manifest) throws IOException {
		
        if (verbose) System.out.println("Loading config file: " + configPath);
        ConfigFile cfg = readConfigFile(configPath);
        if (cfg == null) {
        	throw new RuntimeException("Error reading Config file: Config data is required");
        }
        if (verbose) System.out.println("-- Docker Config file loaded --");
		return cfg;
	}

	private Manifest openManifest(Path manifestPath) throws IOException {
        if (verbose) System.out.println("Loading manifest file: " + manifestPath);
        Manifest manifest = readManifest(manifestPath);
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

	private Path extractImageTar(Path tempDirPath, Path tarFilePath, String container) throws IOException {
		
		if (verbose) System.out.println("Opening docker image: " + dockerImage);
		Path untarDirPath = tempDirPath.resolve(getContentFolderName(container));
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

	private Path saveImageTar(File tempDirFile, String container) throws IOException, InterruptedException, ExecutionException, TimeoutException {
		
		if (verbose) System.out.println("Reading docker image: " + dockerImage);
    	// read docker image -> create tar file from image
    	Path tarFilePath = Paths.get(outputDirPathStr, container + ".tar");
    	ProcessBuilder builder = new ProcessBuilder();
    	if (trace) builder.redirectOutput(Paths.get(outputDirPathStr, "download.log").toFile());
    	
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
		if (trace) builder.redirectOutput(Paths.get(outputDirPathStr, "pull.log").toFile());
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
		if (trace) builder.redirectOutput(Paths.get(outputDirPathStr, "check-docker.log").toFile());
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
	
	private static final String REGEX_CMD = "(([\\w\\/\\\\.]*\\s*-c\\s)#\\(nop\\))\\s*CMD\\s";

	private static final String REGEX_ENV = "([\\w_-]*)(?:=(.*))?";

	private void generateScript(Manifest manifest, ConfigFile cfg, Writer w) throws IOException {
		AttachmentManager attachManager = new AttachmentManager(manifest, cfg);
		// TODO guardar endpoint, combinarlo con este archivo y ejecutarle
		String lastCommandSentence = null;
		List<History> hist = cfg.getHistory();
		boolean firstCommandFound = false;
		String noOpsPrefix = null, instructionprefix = null;
		Pattern cmdPattern = Pattern.compile(REGEX_CMD);
		Pattern envPattern = Pattern.compile(REGEX_ENV);
		for (Iterator<History> iterator = hist.iterator(); iterator.hasNext();) {
			History history = iterator.next();
			if (verbose) System.out.println("Processing line: " +  history.getCreatedBy());
			
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
							if (verbose) System.out.println("--> First command found. noOpsPrefix: " +  noOpsPrefix + ", instructionPrefix: " + instructionprefix);
						}
					}
				} else {
					if (history.isEmptyLayer()) {
						// docker instructions
						if (line.startsWith(noOpsPrefix)) {
							// remove prefix and empty spaces at beginning
							String sentence = lTrim(line.substring(noOpsPrefix.length()));
							if (sentence.startsWith("LABEL ")) {
								String value = sentence.substring(5);
								w.writeComment(value);
								if (verbose) System.out.println("--> Label added: " + value);
								
							} else if (sentence.startsWith("ENV ")) {
								Matcher matcher = envPattern.matcher(sentence.substring(4));
								if (matcher.find()) {
									String key = matcher.group(1);
									String value;
									if (matcher.groupCount() > 1) {
										value = matcher.group(2);
									} else {
										value = "";
									}
									w.writeEnvVar(key, value);
									if (verbose) System.out.println("--> Environment variable added. key: " + key + ", value: " + value);
								} else {
									System.err.println("--> Error parsing ENV: not found");
								}
							} else if (sentence.startsWith("ARG ")) {
								Matcher matcher = envPattern.matcher(sentence.substring(4));
								if (matcher.find()) {
									String key = matcher.group(1);
									String value;
									if (matcher.groupCount() > 1) {
										value = matcher.group(2);
									} else {
										value = "";
									}
									if (value != null) {										
										w.writeVar(key, value);
									} else {
										w.writeComment("ARG var without default value: " +  key);
									}
									if (verbose) System.out.println("-->Local variable added. key: " + key + ", value: " + value);
								} else {
									System.err.println("--> Error parsing ARG: not found");
								}
							} else if (sentence.startsWith("USER ")) {
								String value = sentence.substring(5);
								w.writeChangeUser(value);
								if (verbose) System.out.println("--> Set user added: " + value);
							} else if (sentence.startsWith("WORKDIR ")) {
								String value = sentence.substring(8);
								w.writeCommand("mkdir -p " + value + " & cd " + value);
								if (verbose) System.out.println("--> Set workdir added: " + value);
							} else if (sentence.startsWith("EXPOSE ")) {
								String value = sentence.substring(7);
								w.writeComment("Expose Ports: " + value);
								if (verbose) System.out.println("--> Comment: Expose ports " + value);
							} else if (sentence.startsWith("CMD ")) {
								String cmd = sentence.trim();
								if (cmd.charAt(4) == '[' && cmd.charAt(5) == '"' && cmd.endsWith("\"]")) {
									cmd = cmd.substring(6, cmd.length() - 2); //remove also first " and last "
									StringTokenizer tokenizer = new StringTokenizer(cmd, "\" \"");
									StringBuilder builder = new StringBuilder();
									boolean appendSpace = false;
									while (tokenizer.hasMoreElements()) {
										String object = (String) tokenizer.nextElement();
										if (appendSpace) builder.append(" ");
										builder.append(object);
										appendSpace = true;
									}
									lastCommandSentence = builder.toString();
									if (verbose) System.out.println("--> CMD line saved: " + lastCommandSentence);
								} else {
									System.err.println("Error processing CMD line (skiped): " + sentence);
								}
							}
						}
					} else {
						// shell commands
						if (line.startsWith(noOpsPrefix)) {
							// remove prefix and empty spaces at beginning
							String sentence = lTrim(line.substring(noOpsPrefix.length()));
							if (sentence.startsWith("ADD ")) {
								String value = sentence.substring(4);
								processAdd(w, value, attachManager.getAttachmentPath(line), cfg.getContainer());
							} else if (sentence.startsWith("COPY ")) {
								String value = sentence.substring(5);
								processAdd(w, value, attachManager.getAttachmentPath(line), cfg.getContainer());
							} 
						} else if (line.startsWith(instructionprefix)) {
							String value = lTrim(line.substring(instructionprefix.length()));
							w.writeCommand(value);
							if (verbose) System.out.println("--> Command line added: " + line);
						} else if (line.startsWith("|") && line.length() > 1) {
							String value = null;
							int i = 1;
							for (; i < line.length(); i++) {
								char charAt = line.charAt(i);
								if (!Character.isDigit(charAt)){
									value = line.substring(i);
									break;
								}
							}
							if (value != null) {
								value = lTrim(value);
								writeCommandLineWithReplacements(w, value, line.substring(1, i), instructionprefix);
								if (verbose) System.out.println("--> Command line added: " + value);
							} else {
								System.err.println("Error processing command line (skiped): " + line);
							}
						} else {
							System.err.println("Error processing line (skiped): " + line);
						}
					}
				}
			}
		}
	}

	private void writeCommandLineWithReplacements(Writer w, String value, String numberOfParams, String instructionprefix) throws IOException {
		int number = Integer.parseInt(numberOfParams);
		int ocurrences = 0;
		int pos = 0;
		while (ocurrences <= number) {
			int indexOf = value.indexOf('=', pos);
			if (indexOf > -1) {
				ocurrences++;
			} else {
				throw new RuntimeException("Error processing command line with arguments.");
			}
			pos++;
		}
		int indexOf = value.indexOf(" -c ", pos);
		if (indexOf < 0) throw new RuntimeException("Error processing command line with arguments (-c parameter not found)");
		String preParam = value.substring(0, indexOf);
		int lastIndexOf = preParam.lastIndexOf(shellPathStr);
		if (lastIndexOf < 0) throw new RuntimeException("Error processing command line with arguments (shell not found)");
		w.writeCommand(value.substring(indexOf + 4), escapeVars(value.substring(0, lastIndexOf)));
	}

	private String escapeVars(String s) {
		s = s.trim();
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (int i = 0; i < s.length(); i++) {
			//TODO improve this logic to support = inside vars
			char c = s.charAt(i);
			if ( c == '=') {
				if (!first) {
					for (int j = i; j >= 0; j--) {
						char c2 = s.charAt(j);
						if (c2 == ' ') {
							builder.insert(builder.length() - (i - j), "'");
							break;
						}
					}
				}
				builder.append("='");
				first = false;
			} else {
				builder.append(c);
			}
		}
		builder.append("'");
		return builder.toString();
	}

	private void processAdd(Writer w, String value, String filePath, String container) throws IOException {
		String filename = null, target = null, user = null, group = null;
		int indexOfIn = value.indexOf(" ");
		if (indexOfIn < 0) {
			System.err.println("Error processing ADD instruction (parsing in clause).");
		} else 	{
			if (value.startsWith("--chown=")) {
				value = value.substring(8, indexOfIn);
				int indexOfColon = value.indexOf(":");
				if (indexOfColon < 0) {
					System.err.println("Error processing ADD instruction (parsing user).");
				} else {
					int indexOfColon2 = value.indexOf(":", indexOfColon + 1);
					if (indexOfColon2 < 0) {
						// (no group defined) userfile:filename
						user = value.substring(0, indexOfColon - 4);
						filename = value.substring(indexOfColon + 1);
					} else {
						// user:groupfile:filename
						user = value.substring(0, indexOfColon);
						group = value.substring(indexOfColon + 1, indexOfColon2 - 4);
						filename = value.substring(indexOfColon2 + 1);
					}
				}
			} else {
				if (value.startsWith("file:")) {
					filename = value.substring(5, indexOfIn);
				}
			}
			if (value.startsWith(" in ", indexOfIn)) {
				target = value.substring(indexOfIn +4);
			} else {
				System.err.println("Error processing ADD instruction (in clause).");
			}
			if (filename != null && target != null) {
				writeAdd(w, container, filePath, target, user, group);
				if (verbose) System.out.println("--> Files added: " + value);
			}
		}
	}

	private void writeAdd(Writer w, String container, String filename, String target, String user, String group) throws IOException {
		filename = getLocalFilePath(filename);
		String tmpDir = "$UNDOCKERIZER_WORKDIR/" + container + "/" + filename;
		String sourceDir = "$UNDOCKERIZER_WORKDIR/" + getContentFolderName(container) + "/" + filename;
		w.writeCommand("mkdir -p " + tmpDir + " && tar -xvf " + sourceDir + " -C " + tmpDir);
		if (createTar) resourcesToArchive.add(Paths.get(getContentFolderName(container) + "/" + filename));
		if (user != null) {
			if (group != null) {
				w.writeCommand("chown -R " + user + ":" + group + " " + tmpDir + "/");
			} else {
				w.writeCommand("chown -R " + user + " " + tmpDir + "/");
			}
		}
		w.writeCommand("cp -r " + tmpDir + "/* / && rm -rf " + tmpDir);
	}

	private String getLocalFilePath(String filename) {
		// TODO Implementar este metodo obteniendo la url local
		return filename;
	}

	private Manifest readManifest(Path manifestPath) throws IOException {

		List<Manifest> list = mapper.readValue(manifestPath.toFile(), new TypeReference<List<Manifest>>(){});
		return !list.isEmpty() ? list.get(0) : null;
	}
	
	private InspectData readInspectData(String json) throws IOException {
		
		List<InspectData> list = mapper.readValue(json, new TypeReference<List<InspectData>>(){});
		return !list.isEmpty() ? list.get(0) : null;
	}
	
	private ConfigFile readConfigFile(Path configPath) throws IOException {
		
		return mapper.readValue(configPath.toFile(), ConfigFile.class);
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
    
    public static String lTrim(String value) {
    	if (value == null) return null;
    	
    	int i = 0;
    	while (i < value.length() && Character.isWhitespace(value.charAt(i))) {
    	    i++;
    	}
    	return value.substring(i);
    }
}
