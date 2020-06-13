package com.github.arielcarrera.undockerizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemOutRule;

public class UndockerizerScwTest {

	@Rule
	public final EnvironmentVariables environmentVariables = new EnvironmentVariables();
	
	@Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();
	
	
	@Test
	public void testMinimal() throws IOException {
		exit.expectSystemExitWithStatus(0);
		Undockerizer.main(new String[] {"-i","releases-nexus.pjn.gov.ar/pjn/scw-api:3.0.2", "-v", "-f", "-it"});
	}
	
	@Test
	public void testTar() throws IOException {
		exit.expectSystemExitWithStatus(0);
		Undockerizer.main(new String[] {"-i","releases-nexus.pjn.gov.ar/pjn/scw-api:3.0.2", "-v", "-f", "-it", "-t"});
	}
	
	@Test
	public void testNoInteractiveTar() throws IOException {
		exit.expectSystemExitWithStatus(0);
		Undockerizer.main(new String[] {"-i","releases-nexus.pjn.gov.ar/pjn/scw-api:3.0.2", "-v", "-f", "-t"});
	}
	
	
	private boolean compareFiles(Path origin, Path target) throws IOException {
		byte[] originContent = Files.readAllBytes(origin);
		byte[] targetContent = Files.readAllBytes(target);
		return Arrays.equals(originContent, targetContent);
	}
}
