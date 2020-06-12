package com.github.arielcarrera.undockerizer.model.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class Config {
	
	@JsonProperty("Hostname")
	private String hostname;
	@JsonProperty("Domainname")
	private String domainname;
	@JsonProperty("User")
	private String user;
	@JsonProperty("AttachStdin")
	private boolean AttachStdin;
	@JsonProperty("AttachStdout")
	private boolean AttachStdout;
	@JsonProperty("AttachStderr")
	private boolean AttachStderr;
	@JsonProperty("ExposedPorts")
	private Map<String,Object> exposedPorts = new HashMap<String, Object>();
	@JsonProperty("Tty")
	private boolean tty;
	@JsonProperty("OpenStdin")
	private boolean openStdin;
	@JsonProperty("StdinOnce")
	private boolean stdinOnce;
	@JsonProperty("Env")
	private List<String> env = new ArrayList<String>();
	@JsonProperty("Cmd")
	private List<String> cmd = new ArrayList<String>();
	@JsonProperty("Image")
	private String image;
	@JsonProperty("Volumes")
	private String volumes;
	@JsonProperty("WorkingDir")
	private String workingDir;
	@JsonProperty("Entrypoint")
	private String entrypoint;
	@JsonProperty("OnBuild")
	private String onBuild;
	@JsonProperty("Labels")
	private Map<String, String> labels;
}