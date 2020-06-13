package com.github.arielcarrera.undockerizer.managers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arielcarrera.undockerizer.model.ContentData;
import com.github.arielcarrera.undockerizer.model.image.ConfigFile;
import com.github.arielcarrera.undockerizer.model.image.InspectData;
import com.github.arielcarrera.undockerizer.model.image.Manifest;
import com.github.arielcarrera.undockerizer.utils.FileUtil;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Content Manager
 * @author Ariel Carrera
 *
 */
@AllArgsConstructor
public class ContentManager {
	
	private boolean verbose;
	
	@NonNull
	private ObjectMapper mapper;
	
	public static String getContentFolderName(InspectData info) {
		return getContentFolderName(info.getContainer());
	}
	
	public static String getContentFolderName(String container) {
		return container.substring(0, Math.abs(container.length()/2)) + "-content";
	}
	
	public ContentData checkCacheData(Path tempDirPath, InspectData info, boolean cleanAll, boolean forcePull) throws IOException {
		File tempDirFile = tempDirPath.toFile();
		if (verbose) System.out.println("Checking cache...");
		if (!FileUtil.checkDirectoryExists(tempDirFile)) {
			if (verbose && cleanAll) System.out.println("Temp directory not exists.");
			if (verbose) System.out.println("Creating temp directory...");
    		// create temp dir
    		tempDirFile.mkdirs();
			if (verbose) System.out.println("Temp directory created.");
    	} else {
    		// if we have to clean all...
    		if (cleanAll) {
    			FileUtil.cleanDirectory(tempDirFile, verbose);
    		} else {
    			String created = info.getCreated();
	    		String container = info.getContainer();
	    		// check manifest and config file
    			Path contentDirPath = tempDirPath.resolve(ContentManager.getContentFolderName(info));
    			
    			if (forcePull) {
    				//if we have to force Pull the current image data...
    				if (FileUtil.checkDirectoryExists(contentDirPath)) {
    					FileUtil.cleanDirectory(contentDirPath.toFile(), verbose);
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
    	    	        return new ContentData(manifest, manifestPath, cfg, configPath);
    	    		} catch(Exception e) {
    	    			//clean temp folder
    	    			if (verbose) System.out.println("Image data not valid/found in cache.");
    	    			FileUtil.cleanDirectory(contentDirPath.toFile(), verbose);
    	    		}
    			}
    		} 
    	}
		return null;
	}

	
	public ConfigFile openConfigFile(Path configPath, Manifest manifest) throws IOException {
		
        if (verbose) System.out.println("Loading config file: " + configPath);
        ConfigFile cfg = readConfigFile(configPath);
        if (cfg == null) {
        	throw new RuntimeException("Error reading Config file: Config data is required");
        }
        if (verbose) System.out.println("-- Docker Config file loaded --");
		return cfg;
	}

	public Manifest openManifest(Path manifestPath) throws IOException {
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
	
	private Manifest readManifest(Path manifestPath) throws IOException {
		List<Manifest> list = mapper.readValue(manifestPath.toFile(), new TypeReference<List<Manifest>>(){});
		return !list.isEmpty() ? list.get(0) : null;
	}
	
	private ConfigFile readConfigFile(Path configPath) throws IOException {
		return mapper.readValue(configPath.toFile(), ConfigFile.class);
	}
	
}