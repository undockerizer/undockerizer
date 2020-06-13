package com.github.arielcarrera.undockerizer.model.image.config;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class GraphDriver {
	
	@JsonProperty("Data")
	private Map<String,String> data;
	
	@JsonProperty("Name")
	private String name;
}
