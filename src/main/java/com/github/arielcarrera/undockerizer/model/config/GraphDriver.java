package com.github.arielcarrera.undockerizer.model.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class GraphDriver {
	
	@JsonProperty("Data")
	private GraphDriverData data;
	@JsonProperty("Name")
	private String name;
}
