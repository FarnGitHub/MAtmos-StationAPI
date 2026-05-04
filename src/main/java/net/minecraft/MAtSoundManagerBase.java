package net.minecraft;

import java.util.HashMap;
import matmos.engine.MAtmosSoundManager;

public abstract class MAtSoundManagerBase implements MAtmosSoundManager {
	float soundVolume = 1.0F;
	float musicVolume = 1.0F;
	boolean musicVolUsesMinecraft = false;
	HashMap<Integer, MAtCustomSheet> locators = new HashMap<>();

	public abstract void initialize();

	public abstract void playSoundEffect(String key, float volume, float pitch);

	public void setCustomSoundVolume(float vol) {
		this.soundVolume = vol;
	}

	public float getCustomSoundVolume() {
		return this.soundVolume;
	}

	public void setCustomMusicVolume(float vol) {
		this.musicVolume = vol;
	}

	public float getCustomMusicVolume() {
		return this.musicVolume;
	}

	public void setMusicVolumeIsBasedOffMinecraft(boolean flag) {
		this.musicVolUsesMinecraft = flag;
	}

	public boolean getMusicVolumeIsBasedOffMinecraft() {
		return this.musicVolUsesMinecraft;
	}

	public void addLocator(int key, MAtCustomSheet sheet) {
		this.locators.put(key, sheet);
	}
}
