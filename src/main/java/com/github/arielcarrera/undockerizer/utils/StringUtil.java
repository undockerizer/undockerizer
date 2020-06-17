package com.github.arielcarrera.undockerizer.utils;

/**
 * String Utils
 * @author Ariel Carrera
 *
 */
public class StringUtil {

	/**
	 * Left Trim
	 * 
	 * @param value
	 * @return String trimmed by left
	 */
    public static String lTrim(String value) {
    	if (value == null) return null;
    	
    	int i = 0;
    	while (i < value.length() && Character.isWhitespace(value.charAt(i))) {
    	    i++;
    	}
    	return value.substring(i);
    }
    
    /**
     * Escape an string with variables
     * 
     * @param s
     * @return string
     */
	public static String escapeVars(String s) {
		s = s.trim();
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if ( c == '=') {
				if (!first) {
					for (int j = i; j >= 0; j--) {
						char c2 = s.charAt(j);
						if (c2 == ' ') {
							builder.insert(builder.length() - (i - j), "\"");
							break;
						}
					}
				}
				builder.append("=\"");
				first = false;
			} else {
				builder.append(c);
			}
		}
		builder.append("\"");
		return builder.toString();
	}
	
}