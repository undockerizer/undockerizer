package com.github.arielcarrera.undockerizer.managers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arielcarrera.undockerizer.StreamOutput;
import com.github.arielcarrera.undockerizer.exceptions.ImageNotFoundException;
import com.github.arielcarrera.undockerizer.model.image.ConfigFile;
import com.github.arielcarrera.undockerizer.model.image.InspectData;
import com.github.arielcarrera.undockerizer.model.image.Manifest;
import com.github.arielcarrera.undockerizer.utils.OSUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * Image Manager
 * 
 * @author Ariel Carrera
 *
 */
@AllArgsConstructor
public class ImageManager {

	private boolean verbose;

	@NonNull
	private String shellPathStr;

	@NonNull
	private String outputDirPathStr;

	@NonNull
	private String image;

	@NonNull
	private ObjectMapper mapper;

	@Getter
	private static boolean imageAvailable = false;
	@Getter
	private static boolean pulled = false;
	@Getter
	private static boolean dockerAvailable = false;

	public void checkDockerAvailability()
			throws IOException, InterruptedException, ExecutionException, TimeoutException {
		if (verbose)
			System.out.println("Checking docker runtime availability...");
		ProcessBuilder builder = new ProcessBuilder();
		if (OSUtil.isWindows()) {
			builder.command("cmd.exe", "/c", "docker --version");
		} else {
			builder.command(shellPathStr, "-c", "docker --version");
		}
		Process process = builder.start();
		StreamOutput stream = new StreamOutput(process.getInputStream(),
				line -> ImageManager.logAndCheckVersion(line, verbose));
		StringBuilder errStrBuilder = new StringBuilder();
		StreamOutput errorStream = new StreamOutput(process.getErrorStream(),
				line -> ImageManager.readAndlogErrorResponse(errStrBuilder, line));
		Future<?> result = Executors.newSingleThreadExecutor().submit(stream);
		Future<?> errors = Executors.newSingleThreadExecutor().submit(errorStream);

		result.get(10, TimeUnit.SECONDS);
		errors.get(10, TimeUnit.SECONDS);

		int exitCode = process.waitFor();
		if (exitCode != 0) {
			if (!verbose)
				System.out.println(
						"Error checking docker availability. Please run in verbose mode for more details (-v).");
			throw new IllegalStateException("Docker is required");
		}
	}

	public void pullDockerImage() throws IOException, InterruptedException, ExecutionException, TimeoutException {
		pullDockerImage(false);
	}
	
	public void pullDockerImage(boolean force) throws IOException, InterruptedException, ExecutionException, TimeoutException {
		if (verbose) {
			System.out.println("Pulling docker image...");
		}
		if (pulled) {
			if (force) {
				System.out.println("WARN: Image pull forced");
			} else {
				if (verbose) {
					System.out.println("Skipped.");
				}
				return;
			}
		}

		pulled = true;

		ProcessBuilder builder = new ProcessBuilder();
		if (OSUtil.isWindows()) {
			builder.command("cmd.exe", "/c", "docker pull " + image);
		} else {
			builder.command(shellPathStr, "-c", "docker pull " + image);
		}
		Process process = builder.start();

		StreamOutput stream = new StreamOutput(process.getInputStream(),
				line -> ImageManager.logAndCheckPull(line, verbose));

		StringBuilder errStrBuilder = new StringBuilder();
		StreamOutput errorStream = new StreamOutput(process.getErrorStream(),
				line -> ImageManager.readAndlogErrorResponse(errStrBuilder, line));
		Future<?> result = Executors.newSingleThreadExecutor().submit(stream);
		Future<?> errors = Executors.newSingleThreadExecutor().submit(errorStream);

		result.get(5, TimeUnit.MINUTES);
		errors.get(5, TimeUnit.MINUTES);

		int exitCode = process.waitFor();
		if (exitCode != 0) {
			if (!verbose)
				System.out.println("Error pulling docker image. Please run in verbose mode for more details (-v).");
			throw new IllegalStateException("Error pulling Docker image: " + image);
		}

		if (verbose) {
			if (imageAvailable)
				System.out.println("-- Docker image pulled --");
			else
				System.err.println("WARN: Docker image pulling cannot be validated");
		}
	}

	public InspectData inspectDockerImageInfo()
			throws IOException, InterruptedException, ExecutionException, TimeoutException, ImageNotFoundException {

		if (verbose)
			System.out.println("Getting docker image info...");
		ProcessBuilder builder = new ProcessBuilder();
		if (OSUtil.isWindows()) {
			builder.command("cmd.exe", "/c", "docker inspect " + image);
		} else {
			builder.command(shellPathStr, "-c", "docker inspect " + image);
		}
		Process process = builder.start();
		StringBuilder strBuilder = new StringBuilder();
		StreamOutput stream = new StreamOutput(process.getInputStream(),
				line -> ImageManager.readAndlogResponse(strBuilder, line, verbose));

		StringBuilder errStrBuilder = new StringBuilder();
		StreamOutput errorStream = new StreamOutput(process.getErrorStream(),
				line -> ImageManager.readAndlogErrorResponse(errStrBuilder, line));
		Future<?> result = Executors.newSingleThreadExecutor().submit(stream);
		Future<?> errors = Executors.newSingleThreadExecutor().submit(errorStream);

		result.get(30, TimeUnit.SECONDS);
		errors.get(30, TimeUnit.SECONDS);

		if (!errStrBuilder.toString().isEmpty()) {
			String s = errStrBuilder.toString();
			if (s.startsWith("Error: No such image:")) {
				throw new ImageNotFoundException("Image not found locally", image);
			}
		}
		int exitCode = process.waitFor();
		if (exitCode != 0) {
			if (!verbose)
				System.out.println(
						"Error inspecting docker image info. Please run in verbose mode for more details (-v).");
			throw new IllegalStateException("Error inspecting Docker image info: " + image);
		}
		;
		InspectData info = readInspectData(strBuilder.toString());
		if (verbose)
			System.out.println("-- Docker image info read --");

		return info;
	}

	public Path saveImageTar(File tempDirFile, String container)
			throws IOException, InterruptedException, ExecutionException, TimeoutException {

		if (verbose)
			System.out.println("Reading docker image: " + image);
		// read docker image -> create tar file from image
		Path tarFilePath = Paths.get(outputDirPathStr, container + ".tar");
		ProcessBuilder builder = new ProcessBuilder();
		if (OSUtil.isWindows()) {
			builder.command("cmd.exe", "/c", "docker save " + image + " -o " + tarFilePath.toString());
		} else {
			builder.command(shellPathStr, "-c", "docker save " + image + " -o " + tarFilePath.toString());
		}
		Process process = builder.start();

		StringBuilder strBuilder = new StringBuilder();
		StreamOutput stream = new StreamOutput(process.getInputStream(),
				line -> ImageManager.readAndlogResponse(strBuilder, line, verbose));

		StringBuilder errStrBuilder = new StringBuilder();
		StreamOutput errorStream = new StreamOutput(process.getErrorStream(),
				line -> ImageManager.readAndlogErrorResponse(errStrBuilder, line));
		Future<?> result = Executors.newSingleThreadExecutor().submit(stream);
		Future<?> errors = Executors.newSingleThreadExecutor().submit(errorStream);

		result.get(120, TimeUnit.SECONDS);
		errors.get(120, TimeUnit.SECONDS);

		int exitCode = process.waitFor();
		if (exitCode != 0) {
			if (!verbose)
				System.out
						.println("Error processing docker command. Please run in verbose mode for more details (-v).");
			throw new IllegalStateException("Error processing docker command");
		}
		if (verbose)
			System.out.println("-- Docker image downloaded --");
		return tarFilePath;
	}

	public ConfigFile openConfigFile(Path configPath, Manifest manifest) throws IOException {

		if (verbose)
			System.out.println("Loading config file: " + configPath);
		ConfigFile cfg = readConfigFile(configPath);
		if (cfg == null) {
			throw new RuntimeException("Error reading Config file: Config data is required");
		}
		if (verbose)
			System.out.println("-- Docker Config file loaded --");
		return cfg;
	}

	public Manifest openManifest(Path manifestPath) throws IOException {
		if (verbose)
			System.out.println("Loading manifest file: " + manifestPath);
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
		if (verbose)
			System.out.println("-- Docker Manifest file loaded --");
		return manifest;
	}

	private Manifest readManifest(Path manifestPath) throws IOException {
		List<Manifest> list = mapper.readValue(manifestPath.toFile(), new TypeReference<List<Manifest>>() {
		});
		return !list.isEmpty() ? list.get(0) : null;
	}

	private ConfigFile readConfigFile(Path configPath) throws IOException {
		return mapper.readValue(configPath.toFile(), ConfigFile.class);
	}

	private InspectData readInspectData(String json) throws IOException {
		if (json.trim().isEmpty())
			throw new RuntimeException("Insufficient permissions to run docker command");
		List<InspectData> list = mapper.readValue(json, new TypeReference<List<InspectData>>() {
		});
		return !list.isEmpty() ? list.get(0) : null;
	}

	private static void readAndlogResponse(StringBuilder builder, String line, boolean verbose) {
		if (verbose)
			System.out.println("result: " + line);
		builder.append(line);
	}

	private static void readAndlogErrorResponse(StringBuilder builder, String line) {
		System.err.println("result: " + line);
		builder.append(line);
	}

	private static void logAndCheckPull(String line, boolean verbose) {
		if (verbose)
			System.out.println("result: " + line);
		if (!imageAvailable) {
			imageAvailable = line.startsWith("Status: Downloaded") || line.startsWith("Status: Image is up to date");
		}
	}

	private static void logAndCheckVersion(String line, boolean verbose) {
		if (verbose)
			System.out.println("result: " + line);
		if (!dockerAvailable) {
			dockerAvailable = line.startsWith("Docker version");
			if (verbose && dockerAvailable)
				System.out.println("-- Docker available --");
		}
	}
}