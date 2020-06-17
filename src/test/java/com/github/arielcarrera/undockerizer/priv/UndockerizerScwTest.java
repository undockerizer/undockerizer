package com.github.arielcarrera.undockerizer.priv;

import com.github.arielcarrera.undockerizer.BaseUndockerizerTest;

import lombok.Getter;

public class UndockerizerScwTest extends BaseUndockerizerTest {

	@Getter
	private final String 
		imageName = "releases-nexus.pjn.gov.ar/pjn/scw-api:3.0.2", 
		testFolderName = "priv/scw-api",
		testFileName = "undockerizer-270bda774f64d9b20afec4254fc44b6c.sh";
	
	
}
