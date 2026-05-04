package ha3;

import farn.matmos.MatmosMod;
import net.minecraft.client.Minecraft;

public abstract class Ha3Scroller {
	protected MatmosMod mod;
	protected Minecraft mc;
	protected boolean isRunning;
	protected float pitchBase;
	protected float pitchGlobal;
	protected float pitchGlobalNormalized;
	protected float pitchDifference;
	protected float pitchDifferenceNormalized;

	public Ha3Scroller(MatmosMod var1) {
		this.mod = var1;
		this.mc = Minecraft.INSTANCE;
		this.isRunning = false;
	}

	public void draw() {
		if(this.isRunning) {
			this.drawBefore();
			this.drawAfter();
		}

	}

	public void routine() {
		if(this.isRunning) {
			this.routineBefore();
			this.pitchGlobal = this.mc.player.pitch;
			this.pitchGlobalNormalized = -this.pitchGlobal / 90.0F + 1.0F;
			this.pitchDifference = this.pitchGlobal - this.pitchBase;
			this.pitchDifferenceNormalized = -this.pitchDifference / 90.0F + 1.0F;
			this.routineAfter();
		}

	}

	protected abstract void drawBefore();

	protected abstract void drawAfter();

	protected abstract void routineBefore();

	protected abstract void routineAfter();

	public void start() {
		if(!this.isRunning) {
			this.isRunning = true;
			this.pitchBase = this.mc.player.pitch;
		}

	}

	public void stop() {
		if(this.isRunning) {
			this.isRunning = false;
		}

	}

	public boolean isRunning() {
		return this.isRunning;
	}
}
