package matmos.engine;

import matmos.minecraft.MAtmos;

public class MAtmosLogger {

	public static void notice(String var0) {
		if(MAtmos.showMAtmosLogger)
			System.out.println(var0);
	}
}
