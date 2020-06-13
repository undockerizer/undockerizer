package com.github.arielcarrera.undockerizer;

import lombok.Getter;

public class UndockerizerW19Test extends BaseUndockerizerTest {

	@Getter
	private final String 
		imageName = "jboss/wildfly:19.1.0.Final", 
		testFolderName = "wildfly19",
		testFileName = "undockerizer-36a3e8de4c6b4a982d9d66a50b6242de.sh";

}
