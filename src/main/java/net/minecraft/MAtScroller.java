package net.minecraft;

import farn.matmos.MatmosMod;
import ha3.Ha3Scroller;
import net.minecraft.client.util.ScreenScaler;

public class MAtScroller extends Ha3Scroller {
	final String msgup = "Volume+";
	final String msgdown = "Volume-";
	float lastPitch = -720.0F;
	boolean musicSet;

	MAtScroller(MatmosMod var1) {
		super(var1);
	}

	public void start(boolean var1) {
		this.musicSet = var1;
		this.start();
	}

	protected void drawBefore() {
		String var1;
		String var2;
		if(!this.musicSet) {
			var1 = "Sound Volume:";
			var2 = "" + (int)(((MAtmos)this.mod).getSoundManager().getCustomSoundVolume() * 100.0F);
		} else {
			var1 = "Music Volume:";
			var2 = "" + (int)(((MAtmos)this.mod).getSoundManager().getCustomMusicVolume() * 100.0F);
		}

		ScreenScaler var3 = new ScreenScaler(this.mc.options, this.mc.displayWidth, this.mc.displayHeight);
		int var4 = var3.getScaledWidth();
		int var5 = var3.getScaledHeight();
		int var6 = this.mc.textRenderer.getWidth(var1);
		this.mc.textRenderer.drawWithShadow(var1, (var4 - var6) / 2, var5 / 2 + 10, 16777215);
		int var7 = this.mc.textRenderer.getWidth(var2);
		this.mc.textRenderer.drawWithShadow(var2, (var4 - var7) / 2, var5 / 2 + 10 + var5 / 32, 16776960);
		int var8 = this.mc.textRenderer.getWidth("Volume+");
		this.mc.textRenderer.drawWithShadow("Volume+", (var4 - var8) / 2, var5 / 2 + 10 - var5 / 8, 16776960);
		int var9 = this.mc.textRenderer.getWidth("Volume-");
		this.mc.textRenderer.drawWithShadow("Volume-", (var4 - var9) / 2, var5 / 2 + 10 + var5 / 8, 16776960);
		String var10;
		int var11;
		if(((MAtmos)this.mod).tutorial_shouldShowUseVolumeHint()) {
			var10 = "Move your mouse around while holding F7!";
			var11 = this.mc.textRenderer.getWidth(var10);
			this.mc.textRenderer.drawWithShadow(var10, (var4 - var11) / 2, 10 + var5 / 16, 16776960);
		} else if(((MAtmos)this.mod).tutorial_shouldShowMusicHint()) {
			var10 = "Open your inventory and hold F7 down to tweak MAtmos Music volume.";
			var11 = this.mc.textRenderer.getWidth(var10);
			this.mc.textRenderer.drawWithShadow(var10, (var4 - var11) / 2, var5 / 32, 16777215);
		}

	}

	protected void drawAfter() {
	}

	protected void routineBefore() {
		float var1 = this.pitchGlobal;
		if(var1 > -5.0F && var1 < 5.0F) {
			var1 = 0.0F;
		}

		float var2 = -(var1 / 90.0F) + 1.0F;
		var2 *= var2;
		if(var2 < 0.05F) {
			var2 = 0.0F;
		}

		if(!this.musicSet) {
			((MAtmos)this.mod).getSoundManager().setCustomSoundVolume(var2);
		} else {
			((MAtmos)this.mod).getSoundManager().setCustomMusicVolume(var2);
		}

		if(Math.floor((this.lastPitch / 10.0F)) != Math.floor((var1 / 10.0F))) {
			float var3 = (float)Math.pow(2.0D, -Math.floor((this.lastPitch / 10.0F)) / 12.0D);
			if(!this.musicSet) {
				((MAtmos)this.mod).getSoundManager().playSoundEffect("random.click", var2, var3);
			} else {
				((MAtmos)this.mod).getSoundManager().playSoundEffect("note.pling", var2, var3);
			}
		}

		this.lastPitch = var1;
	}

	protected void routineAfter() {
	}
}
