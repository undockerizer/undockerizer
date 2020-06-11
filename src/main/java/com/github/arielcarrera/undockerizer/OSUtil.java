package com.github.arielcarrera.undockerizer;

/**
 * Operative System Utility
 * @author Ariel Carrera
 *
 */
public class OSUtil {

	public enum OSFamily {
		WINDOWS, MAC_OS, UNIX, UNIX_BASH, SOLARIS, OTHER
	}

	private static final String WIN = "WINDOWS", MAC = "MAC_OS", UNIX = "NIX", LINUX = "LINUX", AIX = "AIX", SOLARIS = "SUNOS";
	
    private static final String OSNAME = System.getProperty("os.name").toUpperCase();

    public static boolean isWindows() {
        return OSNAME.contains(WIN);
    }

    public static boolean isMac() {
        return OSNAME.contains(MAC);
    }

    public static boolean isUnix() {
        return OSNAME.contains(UNIX) || OSNAME.contains(LINUX) || OSNAME.contains(AIX);
    }

    public static boolean isLinux() {
        return OSNAME.contains(LINUX);
    }
    
    public static boolean isSolarisFamily() {
        return OSNAME.contains(SOLARIS);
    }
    
    public static OSFamily getFamily(){
        if (isWindows()) {
            return OSFamily.WINDOWS;
        } else if (isUnix()) {
            return OSFamily.UNIX;
        } else if (isMac()) {
            return OSFamily.MAC_OS;
        } else if (isSolarisFamily()) {
            return OSFamily.SOLARIS;
        }
        
        return OSFamily.OTHER;
    }

}