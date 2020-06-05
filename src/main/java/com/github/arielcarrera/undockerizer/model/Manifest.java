package com.github.arielcarrera.undockerizer.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Docker Manifest file
 * @author Ariel Carrera
 *
 */
@Data @NoArgsConstructor
public class Manifest {
	
	@JsonProperty("Config")
	private String configFile;
	@JsonProperty("RepoTags")
	private List<String> repoTags;
	@JsonProperty("Layers")
	private List<String> layers;
}