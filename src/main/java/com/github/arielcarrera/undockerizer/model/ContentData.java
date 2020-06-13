package com.github.arielcarrera.undockerizer.model;

import java.nio.file.Path;

import com.github.arielcarrera.undockerizer.model.image.ConfigFile;
import com.github.arielcarrera.undockerizer.model.image.Manifest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class ContentData {
	Manifest manifest;
	Path manifesPath;
	ConfigFile config;
	Path configPath;
}