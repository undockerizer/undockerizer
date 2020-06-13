package com.github.arielcarrera.undockerizer;

import lombok.Getter;

public class UndockerizerKc10Test extends BaseUndockerizerTest {

	@Getter
	private final String 
		imageName = "jboss/keycloak:10.0.2", 
		testFolderName = "keycloak10",
		testFileName = "undockerizer-449d9b2ea51b37a106fda8f92980c37f.sh";

}