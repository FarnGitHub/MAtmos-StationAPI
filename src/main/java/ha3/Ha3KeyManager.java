package ha3;

import java.util.HashMap;

import farn.matmos.MatmosMod;
import net.minecraft.client.option.KeyBinding;

public class Ha3KeyManager {
	MatmosMod mod;
	public HashMap<KeyBinding, Ha3KeyBinding> keys = new HashMap<>();

	public Ha3KeyManager(MatmosMod mod) {
		this.mod = mod;
	}

	public void addKeyBinding(Ha3KeyBinding hakey) {
		this.keys.put(hakey.key, hakey);
		hakey.setupMod(this.mod);
	}

	public void handleKeyDown(KeyBinding key) {
		try {
			this.keys.get(key).handleBefore();
		} catch (NullPointerException ignored) {
		}
	}

	public void handleRuntime() {
		for(Ha3KeyBinding keybind : this.keys.values())
			keybind.handle();
	}
}
