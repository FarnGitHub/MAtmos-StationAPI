package matmos.minecraft;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;

import farn.matmos.MatmosStationAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.util.math.Vec3d;
import paulscode.sound.SoundSystem;

public class MAtSoundManagerDefault extends MAtSoundManagerBase {
	float settingsVolume = 0.0F;
	float settingsMusicVolume = 0.0F;
	float previousMusicVolume = 0.0F;
	Minecraft mc = Minecraft.INSTANCE;
	HashMap<String, String> soundequivalences = new HashMap<>();
	Random random = new Random(System.currentTimeMillis());
	int nbTokens = 0;
	int soundProxyInteger = 0;
	ArrayList<String> tokenPaths = new ArrayList<>();
	ArrayList<Boolean> tokenSetFirst = new ArrayList<>();
	ArrayList<Float> tokenVolume = new ArrayList<>();
	ArrayList<URL> tokenURL = new ArrayList<>();
	ArrayList<String> sourcesAsMusic = new ArrayList<>();
	HashMap<String, Float> paulsCodeBug_markForFadeIn = new HashMap<>();

	public void initialize() {

	}

	public void cacheSound(String filePath) {
		this.getSound(filePath);
	}

	public String getSound(String filePath) {
		if(this.soundequivalences.containsKey(filePath)) {
			return this.soundequivalences.get(filePath);
		} else {
			int firstSlash = filePath.indexOf("/");
			int fileFormat = filePath.indexOf(".");
			String preSoundPath = filePath.substring(firstSlash + 1, fileFormat);
			String soundId = preSoundPath.replace("/", ".");
			soundId = "matmos:" + soundId;
			for(int number = 0; number < 10; ++number) {
				soundId = soundId.replace(String.valueOf(number), "");
			}
			this.soundequivalences.put(filePath, soundId);
			return soundId;
		}
	}


	public void playSound(String soundPath, float vol, float pitch, int meta) {
		float x = (float)this.mc.player.x;
		float y = (float)this.mc.player.y;
		float z = (float)this.mc.player.z;
		String soundId = this.getSound(soundPath);
		float volume = vol == 0.0F ? (this.musicVolUsesMinecraft ? this.settingsMusicVolume : this.getCustomMusicVolume()) : vol * this.getCustomSoundVolume();
		if(volume != 0.0F) {
			if(meta > 0) {
				double randEffect = (double)(this.random.nextFloat() * 2.0F) * Math.PI;
				x += (float)(Math.cos(randEffect) * (double)meta);
				y = y + this.random.nextFloat() * (float)meta * 0.2F - (float)meta * 0.01F;
				z += (float)(Math.sin(randEffect) * (double)meta);
				this.playSoundFXProxy(soundId, x, y, z, volume, pitch, 0, 0.0F);
			} else if(meta < 0) {
				MAtCustomSheet sheet = this.locators.get(-meta);
				if(sheet != null) {
					Vec3d location = sheet.provideLocation(-meta);
					if(location != null) {
						this.playSoundFXProxy(soundId, x, y, z, volume, pitch, 0, 0.0F);
					}
				} else {
                    MatmosStationAPI.LOGGER.error("Couldn't find locator:{}", -meta);
				}
			} else {
				y += 2048.0F;
				this.playSoundFXProxy(soundId, x, y, z, volume, pitch, 0, 0.0F);
			}
		}

	}

	public void playSoundFXProxy(String id, float x, float y, float z, float volume, float pitch, int var7, float var8) {
		if(this.settingsVolume != 0.0F) {
			Sound var9 = Minecraft.INSTANCE.soundManager.sounds.get(id);
			if(var9 != null) {
				this.soundProxyInteger = (this.soundProxyInteger + 1) % 128;
				String soundId = "MATMOS_SPX_" + this.soundProxyInteger;
				getSoundSystem().removeSource(soundId);
				getSoundSystem().newSource(false, soundId, var9.soundFile, var9.id, false, x, y, z, var7, var8);
				if(volume > 1.0F) {
					volume = 1.0F;
				}

				volume *= 0.25F;
				getSoundSystem().setTemporary(soundId, true);
				getSoundSystem().setPitch(soundId, pitch);
				getSoundSystem().setVolume(soundId, volume * this.settingsVolume);
				getSoundSystem().play(soundId);
			}
		}
	}

	public SoundSystem getSoundSystem() {
		return SoundManager.soundSystem;
	}

	public void updateSettingsVolume() {
		boolean volumeChanged = this.settingsVolume != this.mc.options.soundVolume;
		if(volumeChanged) {
			this.settingsVolume = this.mc.options.soundVolume;
		}

		if(this.musicVolUsesMinecraft) {
			float musicVol = this.mc.options.musicVolume;
			if(volumeChanged || musicVol != this.settingsMusicVolume) {
                for (String sound : this.sourcesAsMusic) {
					getSoundSystem().setVolume(sound, musicVol * this.settingsVolume);
                }
			}

			this.settingsMusicVolume = musicVol;
		} else {
			if(volumeChanged || this.getCustomMusicVolume() != this.previousMusicVolume) {

                for (String s : this.sourcesAsMusic) {
					getSoundSystem().setVolume(s, this.getCustomMusicVolume() * this.settingsVolume);
                }
			}

			this.previousMusicVolume = this.getCustomMusicVolume();
		}

	}

	public void routine() {
		if(this.getSoundSystem() != null) {
			this.updateSettingsVolume();
			if(!this.paulsCodeBug_markForFadeIn.isEmpty()) {

                for (Entry<String, Float> entry : this.paulsCodeBug_markForFadeIn.entrySet()) {
					getSoundSystem().setVolume(entry.getKey(), entry.getValue() * this.settingsVolume);
                }

				this.paulsCodeBug_markForFadeIn.clear();
			}
		}

	}

	@SuppressWarnings("unused")
	public float getSettingsVolume() {
		return this.settingsVolume;
	}

	public int getNewStreamingToken() {
		int var1 = this.nbTokens++;
		this.tokenPaths.add("");
		this.tokenSetFirst.add(false);
		this.tokenURL.add(null);
		this.tokenVolume.add(0.0F);
		return var1;
	}

	public void setupStreamingToken(int var1, String var2, float var3, float var4) {
		String var5 = "MATMOS_SRM_" + var1;
		this.tokenPaths.set(var1, var2);
		this.cacheSound(var2);
		String var6 = this.getSound(var2);
		Sound var7 = Minecraft.INSTANCE.soundManager.sounds.get(var6);
		if(var7 != null && getSoundSystem() != null) {
			if(var3 == 0.0F) {
				this.sourcesAsMusic.add(var5);
			}

			this.tokenURL.set(var1, var7.soundFile);
			this.tokenVolume.set(var1, var3);
			getSoundSystem().newStreamingSource(true, var5, var7.soundFile, var2, true, 0.0F, 0.0F, 0.0F, 0, 0.0F);
			getSoundSystem().setTemporary(var5, false);
			getSoundSystem().setPitch(var5, var4);
			getSoundSystem().setLooping(var5, true);
		} else {
            MatmosStationAPI.LOGGER.error("No sound found for {}", var2);
		}

	}

	public void startStreaming(int var1, float var2, int var3) {
		String var4 = "MATMOS_SRM_" + var1;
		if(!this.tokenSetFirst.get(var1)) {
			this.tokenSetFirst.set(var1, true);
			getSoundSystem().setLooping(var4, var3 == 0);
		}

		float var5 = this.tokenVolume.get(var1);
		float var6;
		if(var5 == 0.0F) {
			var6 = this.settingsVolume * (this.musicVolUsesMinecraft ? this.settingsMusicVolume : this.getCustomMusicVolume());
		} else {
			var6 = var5 * this.settingsVolume * this.getCustomSoundVolume();
		}

		if(var2 == 0.0F) {
			getSoundSystem().setVolume(var4, var6);
			getSoundSystem().play(var4);
		} else {
			String var7 = this.tokenPaths.get(var1);
			this.paulsCodeBug_markForFadeIn.put(var4, var6);
			getSoundSystem().setVolume(var4, 0.0F);
			getSoundSystem().play(var4);
			getSoundSystem().fadeOutIn(var4, this.tokenURL.get(var1), var7, 1L, (long)var2 * 1000L);
		}

	}

	public void stopStreaming(int var1, float var2) {
		String var3 = "MATMOS_SRM_" + var1;
		if(var2 == 0.0F) {
			getSoundSystem().stop(var3);
		} else {
			getSoundSystem().fadeOut(var3, null, (long)var2 * 1000L);
		}

	}

	public void pauseStreaming(int var1, float var2) {
		String var3 = "MATMOS_SRM_" + var1;
		getSoundSystem().pause(var3);
	}

	public void eraseStreamingToken(int var1) {
		String var2 = "MATMOS_SRM_" + var1;
		this.stopStreaming(var1, 0.0F);
		getSoundSystem().removeSource(var2);
		this.sourcesAsMusic.remove(var2);
	}

}
