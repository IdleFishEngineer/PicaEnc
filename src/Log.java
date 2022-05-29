import java.io.IOException;

public class Log {

	private static final String VERBOSE_FIX = "ver:";
	private static final String ERROR_FIX = "err:";
	private static final String WARNING_FIX = "war:";
	
	public static void V(Class clazz,String msg) {
		log(clazz, VERBOSE_FIX + msg);
	}
	
	public static void V(Class clazz,String msg,boolean show) {
		if(show) log(clazz, VERBOSE_FIX + msg);
	}
	
	public static void E(Class clazz,String msg) {
		log(clazz, ERROR_FIX + msg);
	}
	
	public static void E(Class clazz,String msg,boolean show) {
		if(show) log(clazz, ERROR_FIX + msg);
	}
	
	public static void W(Class clazz,String msg) {
		log(clazz, WARNING_FIX + msg);
	}
	
	public static void W(Class clazz,String msg,boolean show) {
		if(show) log(clazz, WARNING_FIX + msg);
	}
	
	public static void F(Class clazz,String msg) {
		log(clazz,msg);
	}

	public static void log(Class clazz,String msg) {
		System.out.println(clazz.getSimpleName() + ">" + msg);
	}

}
