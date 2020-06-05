package com.github.arielcarrera.undockerizer.model.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class RootFS {
	
	private String type;
	@JsonProperty("diff_ids")
	private List<String> diffIds = new ArrayList<String>();
	@JsonProperty("Layers")
	private List<String> layers = new ArrayList<String>();
}
