package net.minecraft;

import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public abstract class MAtCustomSheet {
	protected ArrayList<Integer> data;
	protected ArrayList<Integer> delta;

	MAtCustomSheet() {
		this.setSize(0);
	}

	public abstract String getName();

	public abstract boolean isUsingSmall();

	public abstract boolean isUsingLarge();

	public abstract boolean isUsingDelta();

	public int[] getLocators() {
		return null;
	}

	public Vec3d provideLocation(int var1) {
		return null;
	}

	public void load() {
	}

	public void unload() {
	}

	public void doFrequent() {
	}

	public void doRelaxed() {
	}

	public void doStartSmall() {
	}

	public void doBlockSmall(long var1, long var3, long var5) {
	}

	public void doEndSmall() {
	}

	public void doStartLarge() {
	}

	public void doBlockLarge(long var1, long var3, long var5) {
	}

	public void doEndLarge() {
	}

	ArrayList<Integer> getData() {
		return this.data;
	}

	ArrayList<Integer> getDelta() {
		return this.delta;
	}

	protected void setSize(int var1) {
		this.data = new ArrayList<>();
		if(this.isUsingDelta()) {
			this.delta = new ArrayList<>();
		}

		for(int var2 = 0; var2 < var1; ++var2) {
			this.data.add(0);
			if(this.isUsingDelta()) {
				this.delta.add(0);
			}
		}

	}

	protected void setValue(int var1, int var2) {
		if(var1 >= 0 && this.data.size() > var1) {
			if(this.isUsingDelta()) {
				int var3 = var2 - this.data.get(var1);
				this.delta.set(var1, var3);
			}

			this.data.set(var1, var2);
		}

	}
}
