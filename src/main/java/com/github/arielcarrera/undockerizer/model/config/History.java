package com.github.arielcarrera.undockerizer.model.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class History {
	
	@JsonProperty("created")
	private String created;
	@JsonProperty("created_by")
	private String createdBy;
	@JsonProperty(value="empty_layer", defaultValue="false")
	private boolean emptyLayer;
}