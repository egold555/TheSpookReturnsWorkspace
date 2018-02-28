package org.golde.java.gameengine3d.helpers;

public class GELog {

	public static void debug(String msg) {
		print("debug", msg, false);
	}
	
	public static void info(String msg) {
		print("info", msg, false);
	}
	
	public static void warning(String msg) {
		print("warning", msg, false);
	}
	
	public static void error(Exception e, String msg) {
		print("error", msg, true);
		e.printStackTrace();
	}
	
	public static void error(String msg) {
		print("error", msg, true);
	}
	
	private static void print(String prefix, String msg, boolean isError) {
		if(isError) {
			System.err.println("[" + prefix.toUpperCase() + "]" + format(new Exception()) + msg);
		}else {
			System.out.println("[" + prefix.toUpperCase() + "]" + format(new Exception()) + msg);
		}
	}
	
	private static String format(Exception e) {
		StackTraceElement ste = e.getStackTrace()[1];
		String s = "[";
		
		s = s + ste.getFileName().replace(".java", "");
		s = s + ":" + ste.getLineNumber();
		
		return s + "] ";
	}
	
}
