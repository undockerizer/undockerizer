package com.github.arielcarrera.undockerizer.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.arielcarrera.undockerizer.model.image.ConfigFile;
import com.github.arielcarrera.undockerizer.model.image.Manifest;
import com.github.arielcarrera.undockerizer.model.image.config.History;

/**
 * Attachment Manager
 * @author Ariel Carrera
 *
 */
public class AttachmentManager {

	private final Map<String, String> map = new HashMap<>();
	
	public AttachmentManager(Manifest manifest, ConfigFile config) {
		String[] layersArray = manifest.getLayers().toArray(new String[manifest.getLayers().size()]);
		List<History>  filteredHistories = config.getHistory().stream().filter(h -> !h.isEmptyLayer()).collect(Collectors.toList());
		if (layersArray.length != filteredHistories.size()) throw new RuntimeException("Error preparing attachment data map");
		
		for (int i = 0; i < layersArray.length; i++) {
			map.put(filteredHistories.get(i).getCreatedBy(), layersArray[i]);
		}
		
	}
	
	public String getAttachmentPath(String createdBy) {
		return map.get(createdBy);
	}
}