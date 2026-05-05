package net.minecraft;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;
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

	public void cacheSound(String var1) {
		this.getSound(var1);
	}

	String getSound(String var1) {
		if(this.soundequivalences.containsKey(var1)) {
			return this.soundequivalences.get(var1);
		} else {
			int var6 = var1.indexOf("/");
			int var7 = var1.indexOf(".");
			String var8 = var1.substring(var6 + 1, var7);
			String var9 = var8.replaceAll("/", ".");
			var9 = var9.replaceAll("0", "");
			var9 = var9.replaceAll("1", "");
			var9 = var9.replaceAll("2", "");
			var9 = var9.replaceAll("3", "");
			var9 = var9.replaceAll("4", "");
			var9 = var9.replaceAll("5", "");
			var9 = var9.replaceAll("6", "");
			var9 = var9.replaceAll("7", "");
			var9 = var9.replaceAll("8", "");
			var9 = var9.replaceAll("9", "");
			var9 = "matmos:" + var9;
			this.soundequivalences.put(var1, var9);
			return var9;
		}
	}


	public void playSound(String var1, float var2, float var3, int var4) {
		float var5 = (float)this.mc.player.x;
		float var6 = (float)this.mc.player.y;
		float var7 = (float)this.mc.player.z;
		String var8 = this.getSound(var1);
		float var9 = var2 == 0.0F ? (this.musicVolUsesMinecraft ? this.settingsMusicVolume : this.getCustomMusicVolume()) : var2 * this.getCustomSoundVolume();
		if(var9 != 0.0F) {
			if(var4 > 0) {
				double var10 = (double)(this.random.nextFloat() * 2.0F) * Math.PI;
				var5 += (float)(Math.cos(var10) * (double)var4);
				var6 = var6 + this.random.nextFloat() * (float)var4 * 0.2F - (float)var4 * 0.01F;
				var7 += (float)(Math.sin(var10) * (double)var4);
				this.playSoundFXProxy(var8, var5, var6, var7, var9, var3, 0, 0.0F);
			} else if(var4 < 0) {
				MAtCustomSheet var12 = this.locators.get(-var4);
				if(var12 != null) {
					Vec3d var11 = var12.provideLocation(-var4);
					if(var11 != null) {
						this.playSoundFXProxy(var8, var5, var6, var7, var9, var3, 0, 0.0F);
					}
				} else {
					System.out.println("(MAtmos) Error, Couldn't find locator:" + -var4);
				}
			} else {
				var6 += 2048.0F;
				this.playSoundFXProxy(var8, var5, var6, var7, var9, var3, 0, 0.0F);
			}
		}

	}

	public void playSoundFXProxy(String id, float x, float y, float z, float volume, float pitch, int var7, float var8) {
		if(this.settingsVolume != 0.0F) {
			Sound var9 = Minecraft.INSTANCE.soundManager.sounds.get(id);
			if(var9 != null) {
				this.soundProxyInteger = (this.soundProxyInteger + 1) % 128;
				String var10 = "MATMOS_SPX_" + this.soundProxyInteger;
				getSoundSystem().removeSource(var10);
				getSoundSystem().newSource(false, var10, var9.soundFile, var9.id, false, x, y, z, var7, var8);
				if(volume > 1.0F) {
					volume = 1.0F;
				}

				volume *= 0.25F;
				getSoundSystem().setTemporary(var10, true);
				getSoundSystem().setPitch(var10, pitch);
				getSoundSystem().setVolume(var10, volume * this.settingsVolume);
				getSoundSystem().play(var10);
			}
		}
	}

	public SoundSystem getSoundSystem() {
		return SoundManager.soundSystem;
	}

	public void updateSettingsVolume() {
		boolean var1 = this.settingsVolume != this.mc.options.soundVolume;
		if(var1) {
			this.settingsVolume = this.mc.options.soundVolume;
		}

		if(this.musicVolUsesMinecraft) {
			float var2 = this.mc.options.musicVolume;
			if(var1 || var2 != this.settingsMusicVolume) {

                for (String s : this.sourcesAsMusic) {
					getSoundSystem().setVolume(s, var2 * this.settingsVolume);
                }
			}

			this.settingsMusicVolume = var2;
		} else {
			if(var1 || this.getCustomMusicVolume() != this.previousMusicVolume) {

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
			System.out.println("No sound found for " + var2);
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

	public void playSoundEffect(String id, float volume, float pitch) {
		float x = (float)this.mc.player.x;
		float y = (float)this.mc.player.y + 2048.0F;
		float z = (float)this.mc.player.z;
		this.playSoundFXProxy(id, x, y, z, volume, pitch, 0, 0.0F);
	}
}
