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

public class UndockerizerTest {

	@Rule
	public final EnvironmentVariables environmentVariables = new EnvironmentVariables();
	
	@Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();
	
	private final static String test1 = "undockerizer-36a3e8de4c6b4a982d9d66a50b6242de.sh";
	
	@Test
	public void testMinimal() throws IOException {
		exit.expectSystemExitWithStatus(0);
		Undockerizer.main(new String[] {"-i","jboss/wildfly:19.1.0.Final", "-v", "-f", "-it"});
		
		assertTrue(compareFiles(Paths.get("src","test", "resources", test1),
				Paths.get("undockerizer", test1)));
	}
	
	@Test
	public void testTar() throws IOException {
		exit.expectSystemExitWithStatus(0);
		Undockerizer.main(new String[] {"-i","jboss/wildfly:19.1.0.Final", "-v", "-f", "-it", "-t"});
		
		assertTrue(compareFiles(Paths.get("src","test", "resources", test1),
				Paths.get("undockerizer", test1)));
		
	}
	
	@Test
	public void testNoInteractiveTar() throws IOException {
		exit.expectSystemExitWithStatus(0);
		Undockerizer.main(new String[] {"-i","jboss/wildfly:19.1.0.Final", "-v", "-f", "-t"});
	}
	
	
	private boolean compareFiles(Path origin, Path target) throws IOException {
		byte[] originContent = Files.readAllBytes(origin);
		byte[] targetContent = Files.readAllBytes(target);
		return Arrays.equals(originContent, targetContent);
	}
}
