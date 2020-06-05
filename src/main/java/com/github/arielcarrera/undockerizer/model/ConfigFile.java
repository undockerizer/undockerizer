package com.github.arielcarrera.undockerizer.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.arielcarrera.undockerizer.model.config.Config;
import com.github.arielcarrera.undockerizer.model.config.History;
import com.github.arielcarrera.undockerizer.model.config.RootFS;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class ConfigFile {
	
	private String architecture;
	private Config config;
	private String container;
	@JsonProperty("container_config")
	private Config containerConfig;
	private String created;
	@JsonProperty("docker_version")
	private String dockerVersion;
	private List<History> history = new ArrayList<History>();
	private String os;
	private RootFS rootFS;
}
