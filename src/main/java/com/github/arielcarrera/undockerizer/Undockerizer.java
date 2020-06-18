package com.github.arielcarrera.undockerizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arielcarrera.undockerizer.exceptions.ImageNotFoundException;
import com.github.arielcarrera.undockerizer.managers.ArchiveManager;
import com.github.arielcarrera.undockerizer.managers.ContentManager;
import com.github.arielcarrera.undockerizer.managers.ImageManager;
import com.github.arielcarrera.undockerizer.managers.WriterManagerFactory;
import com.github.arielcarrera.undockerizer.model.ContentData;
import com.github.arielcarrera.undockerizer.model.image.ConfigFile;
import com.github.arielcarrera.undockerizer.model.image.InspectData;
import com.github.arielcarrera.undockerizer.model.image.Manifest;
import com.github.arielcarrera.undockerizer.utils.FileUtil;
import com.github.arielcarrera.undockerizer.utils.OSUtil.OSFamily;
import com.github.arielcarrera.undockerizer.writer.Writer;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;

@Command(name = "Undockerizer", header = "%n@|green Welcome to Undockerizer.\nThe tool to convert a Docker image to a shell script installer.\n|@")
public class Undockerizer implements Callable<Integer> {

    @Option(names = {"-i", "--image"}, required = true, description = "The docker image.")
    String image;
    
    @Option(names = {"-o", "--output"}, required = false, description = "The output file name.", showDefaultValue = Visibility.ALWAYS)
    String outputfileStr;

    @Option(names = {"-od", "--outputDir"}, required = false, description = "Sets the output directory path.", defaultValue = "undockerizer", showDefaultValue = Visibility.ALWAYS)
    String outputDirPathStr;
    
    @Option(names = {"-sp", "--shellPath"}, required = false, description = "Sets the shell path.", defaultValue = "/bin/sh", showDefaultValue = Visibility.ALWAYS)
    String shellPathStr;
    
    @Option(names = {"-v", "--verbose"}, required = false, description = "Verbose mode.", defaultValue = "false")
    boolean verbose;
    
    @Option(names = {"-f", "--force"}, required = false, description = "Overwrite output file if exists", defaultValue = "false")
    boolean force;
    
    @Option(names = {"-c", "--cleanAll"}, required = false, description = "Clean all temp data", defaultValue = "false")
    boolean cleanAll;
    
    @Option(names = {"-fp", "--forcePull"}, required = false, description = "Force to pull image.", defaultValue = "false", showDefaultValue = Visibility.ALWAYS)
    boolean forcePull;
    
    @Option(names = {"-it", "--interactiveOutput"}, required = false, description = "Generate output file with interactive mode", defaultValue = "false")
    boolean interactiveOutput;
    
    @Option(names = {"-t", "--tar"}, required = false, description = "Create tar file.", defaultValue = "false", showDefaultValue = Visibility.ALWAYS)
    boolean archive;
    
    @Option(names = {"-de", "--disableEscaping"}, required = false, description = "Disable escaping of variables", defaultValue = "false", showDefaultValue = Visibility.ALWAYS)
    boolean escapingDisabled;
    
    private Set<Path> resourcesToArchive = new HashSet<Path>();
    
    private ArchiveManager archiveManager;
    private ImageManager imageManager;
    private ContentManager contentManager;
    
    private String interactiveOutputfileStr;
    
    private static final ObjectMapper mapper = new ObjectMapper()
    		.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
    		.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
    		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
	public static void main(String[] args) {
		int exitCode = new CommandLine(new Undockerizer()).execute(args);
		System.exit(exitCode);
	}
	
	@Override
	public Integer call() throws Exception {
		
		imageManager = new ImageManager(verbose, shellPathStr, outputDirPathStr, image, mapper);
		
		// check docker availability
		imageManager.checkDockerAvailability();
		
		contentManager = new ContentManager(verbose, mapper);
		
		// pull image first
		imageManager.pullDockerImage();
		
		// inspect docker image info
		InspectData info;
		try {
			info = imageManager.inspectDockerImageInfo();
		} catch (ImageNotFoundException e) {
			
			info = imageManager.inspectDockerImageInfo();
		}
		
		// first check (cache) if files exists in temp file
		Path outputDirPath = Paths.get(outputDirPathStr);
		
		// if cleanAll is active, skip check cache and do a clean
		ContentData tempData = contentManager.checkCacheData(outputDirPath, info, cleanAll, forcePull);
		
		// pull docker image
		if (tempData == null) {
			// read docker image
	    	Path tarFilePath = imageManager.saveImageTar(outputDirPath.toFile(), info.getContainer());
			
			// extracting tar file
			Path untarDirPath = getArchiveManager().extractImageTar(outputDirPath, tarFilePath, info.getContainer());
			
	        //opening manifest file
			Path manifestPath = untarDirPath.resolve("manifest.json");
	        Manifest manifest = contentManager.openManifest(manifestPath);
	        
	        //opening config file
	        Path configPath = untarDirPath.resolve(manifest.getConfigFile());
	        ConfigFile cfg = contentManager.openConfigFile(configPath, manifest);
	        
	        //remove temp tar file on exit
	        tarFilePath.toFile().deleteOnExit();

	        tempData = new ContentData(manifest, manifestPath, cfg, configPath);
		}
		if (archive) {
			resourcesToArchive.add(tempData.getConfigPath());
			resourcesToArchive.add(tempData.getManifesPath());
		}
		//opening output file
        process(tempData);
        
        return 0;
    }

	private void process(ContentData tempData) throws IOException, FileNotFoundException {
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
		
        File outputScript = FileUtil.openOutputFile(outputfileStr, force);
        if (archive) resourcesToArchive.add(outputScript.toPath());
        
        ScriptGenerator scriptGenerator = new ScriptGenerator(verbose, archive, shellPathStr, resourcesToArchive);
        
        if (interactiveOutput) {
        	interactiveOutputfileStr = getInteractiveOutputFileName(outputfileStr);
        	File interactiveOutputScript = FileUtil.openOutputFile(interactiveOutputfileStr, force);
        	if (archive) resourcesToArchive.add(interactiveOutputScript.toPath());
        	
        	try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputScript)));
        			BufferedWriter interactiveBw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(interactiveOutputScript)))) {
            	Writer w = WriterManagerFactory.create(OSFamily.UNIX, bw, interactiveBw, shellPathStr, escapingDisabled);
            	w.writeBegin();
            	scriptGenerator.generateScript(tempData.getManifest(), tempData.getConfig(), w);
            	w.writeEnd();
            }
        } else {
	        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputScript)))) {
	        	Writer w = WriterManagerFactory.create(OSFamily.UNIX, bw, shellPathStr, escapingDisabled);
	        	w.writeBegin();
	        	scriptGenerator.generateScript(tempData.getManifest(), tempData.getConfig(), w);
	        	w.writeEnd();
	        }
        }
        System.out.println("Script generated successfully.");
        
        if (archive) {
	        getArchiveManager().generateTar(outputfileStr, force, resourcesToArchive);
        	System.out.println("Tar generated successfully.");
        }
	}


    public static String getInteractiveOutputFileName(String outputfileStr2) {
    	int lastIndexOf = outputfileStr2.lastIndexOf(".");
    	if (lastIndexOf > -1) {
    		return outputfileStr2.substring(0, lastIndexOf) +  "-it" + outputfileStr2.substring(lastIndexOf);
    	}
		return outputfileStr2 + "-it";
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
    
    private ArchiveManager getArchiveManager() {
    	if (archiveManager == null) archiveManager = new ArchiveManager(outputDirPathStr, verbose);
    	return archiveManager;
    }
}
