package matmos.engine;

import net.minecraft.MAtmos;

public class MAtmosLogger {

	public static void notice(String var0) {
		if(MAtmos.INSTANCE.showMAtmosLogger)
			System.out.println(var0);
	}
}
