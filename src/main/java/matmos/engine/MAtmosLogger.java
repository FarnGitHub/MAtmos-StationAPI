package matmos.engine;

public class MAtmosLogger {
	static boolean isActive = true;

	public static void setActive(boolean var0) {
		isActive = var0;
	}

	public static void notice(String var0) {
		if(isActive)
			System.out.println(var0);
	}
}
