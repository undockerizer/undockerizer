package com.github.arielcarrera.undockerizer;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemOutRule;

public abstract class BaseUndockerizerTest {

	@Rule
	public final EnvironmentVariables environmentVariables = new EnvironmentVariables();
	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

	public BaseUndockerizerTest() {
		super();
	}
	
	public abstract String getImageName();

	public abstract String getTestFolderName();
	
	public abstract String getTestFileName();
	
	@Test
	public void testNoInteractive() throws IOException {
		exit.expectSystemExitWithStatus(0);
		exit.checkAssertionAfterwards(() ->
				assertTrue(compareFiles(Paths.get("src","test", "resources", getTestFolderName(), getTestFileName()),
						Paths.get("undockerizer", getTestFileName())))
		);
		Undockerizer.main(new String[] {"-i", getImageName(), "-v", "-f"});
	}

	@Test
	public void testInteractive() throws IOException {
		exit.expectSystemExitWithStatus(0);
		exit.checkAssertionAfterwards(() ->
			assertTrue(compareFiles(Paths.get("src","test", "resources", getTestFolderName() + "-it", getTestFileName()),
				Paths.get("undockerizer", getTestFileName())))
			);
		Undockerizer.main(new String[] {"-i", getImageName(), "-v", "-f", "-it"});
	}

	@Test
	public void testTar() throws IOException {
		exit.expectSystemExitWithStatus(0);
		exit.checkAssertionAfterwards(() -> {
			assertTrue(compareFiles(Paths.get("src","test", "resources", getTestFolderName() + "-it", getTestFileName()),
					Paths.get("undockerizer", getTestFileName())));
			assertTrue(Paths.get("undockerizer", getTestFileName() + ".tar.gz").toFile().exists());
		});
		Undockerizer.main(new String[] {"-i", getImageName(), "-v", "-f", "-it", "-t"});
	}

	private boolean compareFiles(Path origin, Path target) throws IOException {
		byte[] originContent = Files.readAllBytes(origin);
		byte[] targetContent = Files.readAllBytes(target);
		return Arrays.equals(originContent, targetContent);
	}

}