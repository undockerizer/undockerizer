package com.github.arielcarrera.undockerizer.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @AllArgsConstructor @EqualsAndHashCode(callSuper=false)
public class ImageNotFoundException extends Exception {
	private static final long serialVersionUID = -1250700098669029910L;
	String message;
	String image;
}
