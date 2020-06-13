package com.github.arielcarrera.undockerizer.model.image;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.arielcarrera.undockerizer.model.image.config.Config;
import com.github.arielcarrera.undockerizer.model.image.config.GraphDriver;
import com.github.arielcarrera.undockerizer.model.image.config.RootFS;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Docker Inspect Data
 * @author Ariel Carrera
 *
 */
@Data @NoArgsConstructor
public class InspectData {
	
	@JsonProperty("Id")
	private String id;
	@JsonProperty("RepoTags")
	private List<String> repoTags;
	@JsonProperty("RepoDigests")
	private List<String> repoDigests;	
	@JsonProperty("Parent")
	private String parent;
	@JsonProperty("Comment")
	private String comment;
	@JsonProperty("Created")
	private String created;
	@JsonProperty("Container")
	private String container;
	@JsonProperty("ContainerConfig")
	private Config containerConfig;
	@JsonProperty("DockerVersion")
	private String dockerVersion;
	@JsonProperty("Author")
	private String author;
	@JsonProperty("Config")
	private Config config;
	@JsonProperty("Architecture")
	private String architecture;
	@JsonProperty("Os")
	private String os;
	@JsonProperty("Size")
	private Long size;
	@JsonProperty("VirtualSize")
	private Long virtualSize;
	@JsonProperty("GraphDriver")
	private GraphDriver graphDriver;
	@JsonProperty("RootFS")
	private RootFS rootFS;
	@JsonProperty("Metadata")
	private Map<String, Object> metadata;
	
}