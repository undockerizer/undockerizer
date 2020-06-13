package com.github.arielcarrera.undockerizer;

import lombok.Getter;

public class UndockerizerKafkaTest extends BaseUndockerizerTest {

	@Getter
	private final String 
		imageName = "confluentinc/cp-kafka:5.0.4", 
		testFolderName = "kafka5",
		testFileName = "undockerizer-93545591f1925bdc98103f5bbc846ff2.sh";

}
