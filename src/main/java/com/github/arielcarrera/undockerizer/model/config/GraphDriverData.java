package com.github.arielcarrera.undockerizer.model.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class GraphDriverData {
	
	@JsonProperty("LowerDir")
	private String lowerDir;
	@JsonProperty("MergedDir")
	private String mergedDir;
	@JsonProperty("UpperDir")
	private String upperDir;
	@JsonProperty("WorkDir")
	private String workDir;
}
